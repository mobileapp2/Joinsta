package in.oriange.joinsta.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.GetFeedTypesListModel;
import in.oriange.joinsta.models.MasterModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.MultipartUtility;
import in.oriange.joinsta.utilities.ParamsPojo;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.PermissionUtil.PERMISSION_ALL;
import static in.oriange.joinsta.utilities.Utilities.hideSoftKeyboard;
import static in.oriange.joinsta.utilities.Utilities.setPaddingForView;

public class AddGroupFeeds_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private MaterialEditText edt_feed_type, edt_title, edt_attach_multidoc;
    private EditText edt_description;
    private Button btn_save, btn_add_document;
    private LinearLayout ll_attach_docs;
    private RecyclerView rv_images;
    private CheckBox cb_canshare, cb_disclaimer;

    private String userId, groupId, isAdmin, typeId = "1";
    private List<GetFeedTypesListModel.ResultBean> feedTypeList;
    private ArrayList<LinearLayout> docsLayoutsList;

    private Uri photoURI;
    private int latestPosition;
    private final int CAMERA_REQUEST = 100, GALLERY_REQUEST = 200, DOCUMENT_REQUEST = 300, MESSAGE_REQUEST = 400;
    private File photoFileFolder;
    private ArrayList<MasterModel> imageList;

    private String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_feeds);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = AddGroupFeeds_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        edt_title = findViewById(R.id.edt_title);
        edt_feed_type = findViewById(R.id.edt_feed_type);
        edt_description = findViewById(R.id.edt_description);
        btn_add_document = findViewById(R.id.btn_add_document);
        cb_canshare = findViewById(R.id.cb_canshare);
        cb_disclaimer = findViewById(R.id.cb_disclaimer);
        ll_attach_docs = findViewById(R.id.ll_attach_docs);
        rv_images = findViewById(R.id.rv_images);
        rv_images.setLayoutManager(new GridLayoutManager(context, 3));
        btn_save = findViewById(R.id.btn_save);

        feedTypeList = new ArrayList<>();
        docsLayoutsList = new ArrayList<>();
        imageList = new ArrayList<>();

        photoFileFolder = new File(Environment.getExternalStorageDirectory() + "/Joinsta/" + "Posts");
        if (!photoFileFolder.exists())
            photoFileFolder.mkdirs();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }
    }

    private void getSessionDetails() {

        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            userId = json.getString("userid");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefault() {
        groupId = getIntent().getStringExtra("groupId");
        isAdmin = getIntent().getStringExtra("isAdmin");

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.layout_add_document, null);
        LinearLayout ll = (LinearLayout) rowView;
        docsLayoutsList.add(ll);
        ll_attach_docs.addView(rowView, ll_attach_docs.getChildCount());

        imageList.add(new MasterModel("", ""));
        imageList.add(new MasterModel("", ""));
        imageList.add(new MasterModel("", ""));
        rv_images.setAdapter(new ImagesAdapter());
    }

    private void setEventHandler() {
        edt_feed_type.setOnClickListener(v -> {
            if (Utilities.isNetworkAvailable(context)) {
                new GetFeedTypes().execute();
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        });

        edt_description.setOnClickListener(v -> startActivityForResult(new Intent(context, FullScreenTextEdit_Activity.class)
                .putExtra("message", edt_description.getText().toString().trim()), MESSAGE_REQUEST));

        btn_add_document.setOnClickListener(v -> {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.layout_add_document, null);
            LinearLayout ll = (LinearLayout) rowView;
            docsLayoutsList.add(ll);
            ll_attach_docs.addView(rowView, ll_attach_docs.getChildCount());
        });

        btn_save.setOnClickListener(v -> submitData());
    }

    private void selectImage() {
        final CharSequence[] options = {"Take a Photo", "Choose from Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builder.setCancelable(false);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take a Photo")) {
                    File file = new File(photoFileFolder, "doc_image.png");
                    photoURI = Uri.fromFile(file);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(intent, CAMERA_REQUEST);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, GALLERY_REQUEST);
                }
            }
        });
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertD = builder.create();
        alertD.show();
    }

    public void removeAttachDoc(View v) {
        ll_attach_docs.removeView((View) v.getParent());
        docsLayoutsList.remove(v.getParent());
    }

    public void pickAttachDoc(View v) {
        if (Utilities.isNetworkAvailable(context)) {
            edt_attach_multidoc = (MaterialEditText) v;
            Intent intent = new Intent(context, NormalFilePickActivity.class);
            intent.putExtra(Constant.MAX_NUMBER, 1);
            intent.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"});
            startActivityForResult(intent, DOCUMENT_REQUEST);
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }

    private class GetFeedTypes extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "giveFeedTypes"));
            res = APICall.FORMDATAAPICall(ApplicationConstants.FEEDSAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    feedTypeList = new ArrayList<>();
                    GetFeedTypesListModel pojoDetails = new Gson().fromJson(result, GetFeedTypesListModel.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        feedTypeList = pojoDetails.getResult();
                        if (feedTypeList.size() > 0) {
                            showFeedListDialog();
                        }
                    } else {
                        Utilities.showAlertDialog(context, message, false);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showAlertDialog(context, "Server Not Responding", false);
            }
        }
    }

    private class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.MyViewHolder> {

        ImagesAdapter() {

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.grid_row_images, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int pos) {
            final int position = holder.getAdapterPosition();

            if (!imageList.get(position).getId().isEmpty()) {
                Glide.with(context)
                        .load(imageList.get(position).getId())
                        .into(holder.imv_image);
                setPaddingForView(context, holder.imv_image, 0);
                holder.imv_image_delete.setVisibility(View.VISIBLE);
                holder.imv_image_delete.bringToFront();
            }

            holder.imv_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    latestPosition = position;

                    final CharSequence[] options = {"Take a Photo", "Choose from Gallery"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                    builder.setCancelable(false);
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals("Take a Photo")) {
                                File file = new File(photoFileFolder, "doc_image.png");
                                photoURI = Uri.fromFile(file);
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(intent, CAMERA_REQUEST);
                            } else if (options[item].equals("Choose from Gallery")) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(intent, GALLERY_REQUEST);
                            }
                        }
                    });
                    builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertD = builder.create();
                    alertD.show();
                }
            });

            holder.imv_image_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageList.set(position, new MasterModel("", ""));
                    rv_images.setAdapter(new ImagesAdapter());
                }
            });
        }

        @Override
        public int getItemCount() {
            return imageList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private ImageView imv_image, imv_image_delete;

            private MyViewHolder(View view) {
                super(view);
                imv_image = view.findViewById(R.id.imv_image);
                imv_image_delete = view.findViewById(R.id.imv_image_delete);

            }
        }
    }

    private void showFeedListDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Post Type");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < feedTypeList.size(); i++) {
            arrayAdapter.add(String.valueOf(feedTypeList.get(i).getType()));
        }

        builderSingle.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GetFeedTypesListModel.ResultBean feedType = feedTypeList.get(which);
                edt_feed_type.setText(feedType.getType());
                typeId = feedType.getId();
            }
        });
        builderSingle.show();
    }

    private void submitData() {
        if (edt_title.getText().toString().trim().isEmpty()) {
            edt_title.setError("Please enter title");
            edt_title.requestFocus();
            return;
        }

        JsonArray documentsArray = new JsonArray();

        for (int i = 0; i < docsLayoutsList.size(); i++) {
            if (!((EditText) docsLayoutsList.get(i).findViewById(R.id.edt_attach_doc)).getText().toString().trim().equals("")) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", "0");
                jsonObject.addProperty("document_type", "invitationdocument");
                jsonObject.addProperty("document", ((EditText) docsLayoutsList.get(i).findViewById(R.id.edt_attach_doc)).getText().toString());
                documentsArray.add(jsonObject);
            }
        }

        for (int i = 0; i < imageList.size(); i++) {
            if (!imageList.get(i).getName().equals("")) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", "0");
                jsonObject.addProperty("document_type", "invitationimage");
                jsonObject.addProperty("document", imageList.get(i).getName());
                documentsArray.add(jsonObject);
            }
        }

        String canShare = cb_canshare.isChecked() ? "1" : "0";
        String showDisclaimer = cb_disclaimer.isChecked() ? "1" : "0";

        JsonObject mainObj = new JsonObject();
        mainObj.addProperty("type", "addFeedDetails");
        mainObj.addProperty("user_id", userId);
        mainObj.addProperty("group_id", groupId);
        mainObj.addProperty("type_id", typeId);
        mainObj.addProperty("feed_title", edt_title.getText().toString().trim());
        mainObj.addProperty("feed_text", edt_description.getText().toString().trim());
        mainObj.addProperty("is_admin", isAdmin);
        mainObj.addProperty("can_share", canShare);
        mainObj.addProperty("show_disclaimer", showDisclaimer);
        mainObj.add("document", documentsArray);

        if (Utilities.isNetworkAvailable(context)) {
            new AddGroupFeed().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }

    }

    private class AddGroupFeed extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            res = APICall.JSONAPICall(ApplicationConstants.FEEDSAPI, params[0]);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    if (type.equalsIgnoreCase("success")) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("GroupFeeds_Activity"));

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Posted submitted successfully");
                        alertDialogBuilder.setCancelable(false);
                        final AlertDialog alertD = alertDialogBuilder.create();

                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertD.dismiss();
                                finish();
                            }
                        });

                        alertD.show();
                    } else {
                        Utilities.showMessage("Failed to submit the details", context, 3);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                Uri imageUri = data.getData();
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(AddGroupFeeds_Activity.this);
            }

            if (requestCode == CAMERA_REQUEST) {
                CropImage.activity(photoURI).setGuidelines(CropImageView.Guidelines.ON).start(AddGroupFeeds_Activity.this);
            }

            if (requestCode == DOCUMENT_REQUEST) {
                ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                new UploadImage().execute(list.get(0).getPath(), "1");
            }

            if (requestCode == MESSAGE_REQUEST) {
                edt_description.setText(data.getStringExtra("message"));
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                savefile(resultUri);
            }
        }
    }

    private void savefile(Uri sourceuri) {
        Log.i("sourceuri1", "" + sourceuri);
        String sourceFilename = sourceuri.getPath();
        String destinationFile = Environment.getExternalStorageDirectory() + "/Joinsta/"
                + "Banners/" + "uplimg.png";

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(sourceFilename));
            bos = new BufferedOutputStream(new FileOutputStream(destinationFile, false));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while (bis.read(buf) != -1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File photoFileToUpload = new File(destinationFile);
        new UploadImage().execute(photoFileToUpload.getPath(), "0");

    }

    private class UploadImage extends AsyncTask<String, Integer, String> {

        String TYPE;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            TYPE = params[1];
            StringBuilder res = new StringBuilder();
            try {
                MultipartUtility multipart = new MultipartUtility(ApplicationConstants.FILEUPLOADAPI, "UTF-8");

                multipart.addFormField("request_type", "uploadFeedFile");
                multipart.addFilePart("document", new File(params[0]));

                List<String> response = multipart.finish();
                for (String line : response) {
                    res.append(line);
                }
                return res.toString();
            } catch (IOException ex) {
                return ex.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    if (type.equalsIgnoreCase("success")) {
                        JSONObject jsonObject = mainObj.getJSONObject("result");
                        if (TYPE.equals("1")) {
                            edt_attach_multidoc.setText(jsonObject.getString("name"));
                        } else if (TYPE.equals("0")) {
                            imageList.set(latestPosition, new MasterModel(jsonObject.getString("name"), jsonObject.getString("document_url")));
                            rv_images.setAdapter(new ImagesAdapter());
                        }

                    } else {
                        Utilities.showMessage("Image upload failed", context, 3);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askPermission() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
        } else {
            selectImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setTitle("Alert");
                builder.setMessage("Please provide permission for Camera and Gallery");
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", context.getPackageName(), null)));
                    }
                });
                builder.create();
                AlertDialog alertD = builder.create();
                alertD.show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftKeyboard(AddGroupFeeds_Activity.this);
    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
