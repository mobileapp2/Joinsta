package in.oriange.joinsta.activities;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
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
import in.oriange.joinsta.fragments.Groups_Fragment;
import in.oriange.joinsta.models.GroupAdminsGroupsListModel;
import in.oriange.joinsta.models.GroupMessagesCountsModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.MultipartUtility;
import in.oriange.joinsta.utilities.ParamsPojo;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.PermissionUtil.PERMISSION_ALL;
import static in.oriange.joinsta.utilities.PermissionUtil.doesAppNeedPermissions;
import static in.oriange.joinsta.utilities.Utilities.hideSoftKeyboard;

public class GroupsSendMessage_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private TextView tv_sms_count, tv_email_count, tv_notifications_count;
    private MaterialEditText edt_groups, edt_subject, edt_attach_multidoc;
    private EditText edt_message;
    private ImageView imv_photo1, imv_photo2;
    private RadioButton rb_supervisor, rb_all;
    private Button btn_save, btn_sms, btn_email, btn_notification, btn_add_document;
    private CheckBox cb_canshare;
    private LinearLayout ll_attach_docs;

    private List<GroupAdminsGroupsListModel.ResultBean> groupsList;
    private ArrayList<LinearLayout> docsLayoutsList;

    private JsonArray selectedGroups;
    private String userId, imageName = "", groupId, groupName;

    private boolean isSmsPressed, isEmailPressed, isNotificationPressed = true;

    private Uri photoURI;
    private final int CAMERA_REQUEST = 100, GALLERY_REQUEST = 200, MESSAGE_REQUEST = 300;
    private File photoFileFolder;

    private String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_sendmessage);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = GroupsSendMessage_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        tv_sms_count = findViewById(R.id.tv_sms_count);
        tv_email_count = findViewById(R.id.tv_email_count);
        tv_notifications_count = findViewById(R.id.tv_notifications_count);
        edt_groups = findViewById(R.id.edt_groups);
        edt_subject = findViewById(R.id.edt_subject);
        edt_message = findViewById(R.id.edt_message);
        btn_sms = findViewById(R.id.btn_sms);
        btn_email = findViewById(R.id.btn_email);
        btn_notification = findViewById(R.id.btn_notification);
        imv_photo1 = findViewById(R.id.imv_photo1);
        imv_photo2 = findViewById(R.id.imv_photo2);
        btn_add_document = findViewById(R.id.btn_add_document);
        ll_attach_docs = findViewById(R.id.ll_attach_docs);
        rb_supervisor = findViewById(R.id.rb_supervisor);
        rb_all = findViewById(R.id.rb_all);
        cb_canshare = findViewById(R.id.cb_canshare);
        btn_save = findViewById(R.id.btn_save);

        groupsList = new ArrayList<>();
        docsLayoutsList = new ArrayList<>();
        selectedGroups = new JsonArray();

        photoFileFolder = new File(Environment.getExternalStorageDirectory() + "/Joinsta/" + "Message Images");
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
        groupName = getIntent().getStringExtra("groupName");

        btn_notification.setBackgroundResource(isNotificationPressed ? R.drawable.bbg_pressed : R.drawable.bg_button_selectable);

        edt_groups.setText(groupName);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.layout_add_document, null);
        LinearLayout ll = (LinearLayout) rowView;
        docsLayoutsList.add(ll);
        ll_attach_docs.addView(rowView, ll_attach_docs.getChildCount());

//        if (Utilities.isNetworkAvailable(context)) {
//            new GetMessageCount().execute();
//        } else {
//            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
//        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setEventHandler() {
//        edt_groups.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (groupsList.size() == 0) {
//                    if (Utilities.isNetworkAvailable(context)) {
//                        new GetGroupAdminsGroups().execute();
//                    } else {
//                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
//                    }
//                } else {
//                    showGroupsListDialog();
//                }
//            }
//        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });

        btn_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSmsPressed = !isSmsPressed;
                btn_sms.setBackgroundResource(isSmsPressed ? R.drawable.bbg_pressed : R.drawable.bg_button_selectable);
            }
        });

        btn_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEmailPressed = !isEmailPressed;
                btn_email.setBackgroundResource(isEmailPressed ? R.drawable.bbg_pressed : R.drawable.bg_button_selectable);
            }
        });

        btn_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNotificationPressed = !isNotificationPressed;
                btn_notification.setBackgroundResource(isNotificationPressed ? R.drawable.bbg_pressed : R.drawable.bg_button_selectable);
            }
        });

        imv_photo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edt_groups.getText().toString().trim().isEmpty()) {
                    edt_groups.setError("Please select atleast one group");
                    edt_groups.requestFocus();
                    return;
                }

                if (Utilities.isNetworkAvailable(context)) {
                    if (doesAppNeedPermissions()) {
                        askPermission();
                    } else {
                        selectImage();
                    }
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            }
        });

        imv_photo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edt_groups.getText().toString().trim().isEmpty()) {
                    edt_groups.setError("Please select atleast one group");
                    edt_groups.requestFocus();
                    return;
                }

                if (Utilities.isNetworkAvailable(context)) {
                    if (doesAppNeedPermissions()) {
                        askPermission();
                    } else {
                        selectImage();
                    }
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            }
        });

        edt_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context, GroupsSendMessageScroll_Activity.class)
                        .putExtra("message", edt_message.getText().toString().trim()), MESSAGE_REQUEST);

//                Rect displayRectangle = new Rect();
//                Window window = GroupsSendMessage_Activity.this.getWindow();
//                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
//
//                LayoutInflater layoutInflater = LayoutInflater.from(context);
//                View promptView = layoutInflater.inflate(R.layout.dialog_message, null);
//
//                promptView.setMinimumWidth((int) (displayRectangle.width() * 0.9f));
//                promptView.setMinimumHeight((int) (displayRectangle.height() * 0.9f));
//
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
//                alertDialogBuilder.setTitle("Type your message here");
//                alertDialogBuilder.setView(promptView);
//
//                final EditText edt_text = promptView.findViewById(R.id.edt_text);
//                edt_text.setText(edt_message.getText().toString().trim());
//
//                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        edt_message.setText(edt_text.getText().toString().trim());
//                    }
//                });
//
//                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//
//
//                final AlertDialog alertD = alertDialogBuilder.create();
//                alertD.show();
            }
        });

        btn_add_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.layout_add_document, null);
                LinearLayout ll = (LinearLayout) rowView;
                docsLayoutsList.add(ll);
                ll_attach_docs.addView(rowView, ll_attach_docs.getChildCount());
            }
        });
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
            intent.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf", "txt"});
            startActivityForResult(intent, 1025);
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                Uri imageUri = data.getData();
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(GroupsSendMessage_Activity.this);
            }

            if (requestCode == CAMERA_REQUEST) {
                CropImage.activity(photoURI).setGuidelines(CropImageView.Guidelines.ON).start(GroupsSendMessage_Activity.this);
            }

            if (requestCode == 1025) {
                ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                new UploadImage().execute(list.get(0).getPath(), "1");
            }

            if (requestCode == MESSAGE_REQUEST) {
                edt_message.setText(data.getStringExtra("message"));
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
                + "Message Images/" + "uplimg.png";

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

    private class GetMessageCount extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res;
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "giveMsgCount"));
            param.add(new ParamsPojo("user_id", userId));
            res = APICall.FORMDATAAPICall(ApplicationConstants.GRPADMINMSGCOUNTAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type;
            try {
                if (!result.equals("")) {
                    GroupMessagesCountsModel pojoDetails = new Gson().fromJson(result, GroupMessagesCountsModel.class);
                    type = pojoDetails.getType();
                    if (type.equalsIgnoreCase("success")) {
                        tv_sms_count.setText("SMS (" + pojoDetails.getResult().getSmscount() + ")");
                        tv_email_count.setText("Email (" + pojoDetails.getResult().getEmailcount() + ")");
                        tv_notifications_count.setText("Notifications (" + pojoDetails.getResult().getNotificationcount() + ")");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class GetGroupAdminsGroups extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res;
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "giveAllGroupDetails"));
            param.add(new ParamsPojo("user_id", userId));
            res = APICall.FORMDATAAPICall(ApplicationConstants.GRPADMINMEMBERSSUPERVISORSAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    groupsList = new ArrayList<>();
                    GroupAdminsGroupsListModel pojoDetails = new Gson().fromJson(result, GroupAdminsGroupsListModel.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        groupsList = pojoDetails.getResult();
                        if (groupsList.size() > 0) {
                            showGroupsListDialog();
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

//    private void showGroupsListDialog() {
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View view = inflater.inflate(R.layout.dialog_groups_list, null);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
//        builder.setView(view);
//        builder.setTitle("Select Groups");
//        builder.setCancelable(false);
//
//        RecyclerView rv_groups = view.findViewById(R.id.rv_groups);
//        rv_groups.setLayoutManager(new LinearLayoutManager(context));
//        rv_groups.setAdapter(new GroupListAdapter());
//
//        builder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                edt_groups.setText("");
//                selectedGroups = new JsonArray();
//                StringBuilder selectedGroupsName = new StringBuilder();
//
//                for (GroupAdminsGroupsListModel.ResultBean grpDetails : groupsList) {
//                    if (grpDetails.isChecked()) {
//                        selectedGroups.add(grpDetails.getid());
//                        selectedGroupsName.append(grpDetails.getGroup_name()).append(", ");
//                    }
//                }
//
//                if (selectedGroupsName.toString().length() != 0) {
//                    String selectedGroupsNameStr = selectedGroupsName.substring(0, selectedGroupsName.toString().length() - 2);
//                    edt_groups.setText(selectedGroupsNameStr);
//                }
//            }
//        });
//
//        builder.create().show();
//    }

    private void showGroupsListDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Groups");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < groupsList.size(); i++) {
            arrayAdapter.add(String.valueOf(groupsList.get(i).getGroup_name()));
        }

        builderSingle.setNegativeButton(
                "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GroupAdminsGroupsListModel.ResultBean grpDetails = groupsList.get(which);

                selectedGroups = new JsonArray();
                selectedGroups.add(grpDetails.getId());
                edt_groups.setText(grpDetails.getGroup_name());
            }
        });
        builderSingle.show();
    }

    private class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_checklist, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
            final int position = holder.getAdapterPosition();

            holder.cb_select.setText(groupsList.get(position).getGroup_code() + " - " + groupsList.get(position).getGroup_name());

            if (groupsList.get(position).isChecked()) {
                holder.cb_select.setChecked(true);
            }

            holder.cb_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    groupsList.get(position).setChecked(isChecked);
                }
            });
        }

        @Override
        public int getItemCount() {
            return groupsList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private CheckBox cb_select;

            public MyViewHolder(@NonNull View view) {
                super(view);
                cb_select = view.findViewById(R.id.cb_select);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }

    private void submitData() {
        String receiverType = "";
        JsonArray messageTypes = new JsonArray();

        if (edt_groups.getText().toString().trim().isEmpty()) {
            edt_groups.setError("Please select atleast one group");
            edt_groups.requestFocus();
            return;
        }

        if (rb_supervisor.isChecked()) {
            receiverType = "group_supervisors";
        } else if (rb_all.isChecked()) {
            receiverType = "all";
        } else {
            Utilities.showMessage("Please select receiver type", context, 2);
            return;
        }

        if (edt_subject.getText().toString().trim().isEmpty()) {
            edt_subject.setError("Please enter subject");
            edt_subject.requestFocus();
            return;
        }

        if (edt_message.getText().toString().trim().isEmpty()) {
            edt_message.setError("Please enter message");
            edt_message.requestFocus();
            return;
        }

        if (!isSmsPressed && !isEmailPressed && !isNotificationPressed) {
            Utilities.showMessage("Please select message mode", context, 2);
            return;
        }

        if (isSmsPressed) {
            messageTypes.add("sms");
        }

        if (isEmailPressed) {
            messageTypes.add("email");
        }

        if (isNotificationPressed) {
            messageTypes.add("notification");
        }

        selectedGroups = new JsonArray();
        selectedGroups.add(groupId);

        JsonArray messageDocArray = new JsonArray();

        for (int i = 0; i < docsLayoutsList.size(); i++) {
            if (!((EditText) docsLayoutsList.get(i).findViewById(R.id.edt_attach_doc)).getText().toString().trim().equals("")) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("document", ((EditText) docsLayoutsList.get(i).findViewById(R.id.edt_attach_doc)).getText().toString());
                messageDocArray.add(jsonObject);
            }
        }

        String canShare = cb_canshare.isChecked() ? "1" : "0";

        JsonObject mainObject = new JsonObject();
        mainObject.addProperty("type", "sendMessage");
        mainObject.addProperty("user_id", userId);
        mainObject.addProperty("subject", edt_subject.getText().toString().trim());
        mainObject.addProperty("message", edt_message.getText().toString().trim());
        mainObject.add("groups", selectedGroups);
        mainObject.addProperty("document", imageName);
        mainObject.addProperty("receiver_type", receiverType);
        mainObject.addProperty("can_share", canShare);
        mainObject.add("message_types", messageTypes);
        mainObject.add("message_doc", messageDocArray);

        if (Utilities.isNetworkAvailable(context)) {
            new SendMessage().execute(mainObject.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }

    private class SendMessage extends AsyncTask<String, Void, String> {

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
            res = APICall.JSONAPICall(ApplicationConstants.GRPADMINSENDMESSAGEAPI, params[0]);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Message sent successfully");
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

                        new Groups_Fragment.GetMyGroupsList().execute();
                        new AllGroups_Activity.GetGroupsList().execute();
                    } else {
                        Utilities.showAlertDialog(context, message, false);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

                multipart.addFormField("request_type", "uploadGroupFile");
                multipart.addFormField("user_id", userId);
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

                        if (TYPE.equals("0")) {
                            String imageUrl = jsonObject.getString("document_url");
                            imageName = jsonObject.getString("name");

                            if (!imageUrl.equals("")) {
                                Picasso.with(context)
                                        .load(imageUrl)
                                        .into(imv_photo1);
                                imv_photo2.setVisibility(View.GONE);
                                imv_photo1.setVisibility(View.VISIBLE);
                            }
                        } else if (TYPE.equals("1")) {
                            edt_attach_multidoc.setText(jsonObject.getString("name"));
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
        hideSoftKeyboard(GroupsSendMessage_Activity.this);
    }
}
