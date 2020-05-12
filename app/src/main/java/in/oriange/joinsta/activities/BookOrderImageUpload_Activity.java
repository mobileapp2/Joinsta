package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinsta.R;
import in.oriange.joinsta.models.BookOrderGetMyOrdersModel;
import in.oriange.joinsta.models.MasterModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.MultipartUtility;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.Utilities.setPaddingForView;

public class BookOrderImageUpload_Activity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_images)
    RecyclerView rvImages;
    @BindView(R.id.btn_add_image)
    Button btnAddImage;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.btn_skip)
    MaterialButton btnSkip;

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private LocalBroadcastManager localBroadcastManager;
    private ArrayList<MasterModel> imageList;

    private Uri photoURI;
    private File orderFileFolder;
    private int latestPosition;
    private final int CAMERA_REQUEST = 100, GALLERY_REQUEST = 200;
    private BookOrderGetMyOrdersModel.ResultBean orderDetails;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_order_image_upload);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = BookOrderImageUpload_Activity.this;
        session = new UserSessionManager(context);

        pd = new ProgressDialog(context, R.style.CustomDialogTheme);
        pd.setMessage("Please wait ...");
        pd.setCancelable(false);

        rvImages.setLayoutManager(new GridLayoutManager(context, 3));
        imageList = new ArrayList<>();

        imageList.add(new MasterModel("", ""));
        imageList.add(new MasterModel("", ""));
        imageList.add(new MasterModel("", ""));
        rvImages.setAdapter(new ImagesAdapter());

        orderFileFolder = new File(Environment.getExternalStorageDirectory() + "/Joinsta/" + "Book Order");
        if (!orderFileFolder.exists())
            orderFileFolder.mkdirs();

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
        orderDetails = (BookOrderGetMyOrdersModel.ResultBean) getIntent().getSerializableExtra("orderDetails");

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("BookOrderImageUpload_Activity");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setEventHandler() {
        btnAddImage.setOnClickListener(v -> {
            imageList.add(new MasterModel("", ""));
            rvImages.setAdapter(new ImagesAdapter());
        });

        btnSkip.setOnClickListener(v -> {
            startActivity(new Intent(context, BookOrderPurchaseTypeSelection_Activity.class)
                    .putExtra("businessOwnerId", orderDetails.getOwner_business_id())
                    .putExtra("orderType", "1")
                    .putExtra("orderImageArray", new JsonArray().toString())
                    .putExtra("orderText", "")
                    .putExtra("orderDetails", orderDetails));

        });

        btnSave.setOnClickListener(v -> submitData());
    }

    private void submitData() {
        boolean atleaseOnePhotoIsAvailable = false;
        JsonArray imageJsonArray = new JsonArray();

        for (int i = 0; i < imageList.size(); i++) {
            if (!imageList.get(i).getName().equals("")) {
                atleaseOnePhotoIsAvailable = true;
                break;
            }
        }

        if (!atleaseOnePhotoIsAvailable) {
            Utilities.showMessage("Please pick atleast one image", context, 2);
            return;
        }


        for (int i = 0; i < imageList.size(); i++) {
            if (!imageList.get(i).getName().equals("")) {
                imageJsonArray.add(imageList.get(i).getName());
            }
        }


        startActivity(new Intent(context, BookOrderPurchaseTypeSelection_Activity.class)
                .putExtra("businessOwnerId", orderDetails.getOwner_business_id())
                .putExtra("orderType", "1")
                .putExtra("orderImageArray", imageJsonArray.toString())
                .putExtra("orderText", "")
                .putExtra("orderDetails", orderDetails));

//        updateOrderWithNewProduct(imageJsonArray);
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
                    if (!Utilities.isNetworkAvailable(context)) {
                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                        return;
                    }

                    latestPosition = position;

                    final CharSequence[] options = {"Take a Photo", "Choose from Gallery"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                    builder.setCancelable(false);
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals("Take a Photo")) {
                                File file = new File(orderFileFolder, "doc_image.png");
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
                    rvImages.setAdapter(new ImagesAdapter());
                }
            });
        }

        @Override
        public int getItemCount() {
            return imageList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                Uri imageUri = data.getData();
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(BookOrderImageUpload_Activity.this);
            }

            if (requestCode == CAMERA_REQUEST) {
                CropImage.activity(photoURI).setGuidelines(CropImageView.Guidelines.ON).start(BookOrderImageUpload_Activity.this);
            }

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                savefile(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void savefile(Uri sourceuri) {
        Log.i("sourceuri1", "" + sourceuri);
        String sourceFilename = sourceuri.getPath();
        String destinationFile = Environment.getExternalStorageDirectory() + "/Joinsta/"
                + "Book Order/" + "uplimg.png";

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
        new UploadImage().execute(photoFileToUpload.getPath());
    }

    private class UploadImage extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "";
            try {
                MultipartUtility multipart = new MultipartUtility(ApplicationConstants.FILEUPLOADAPI, "UTF-8");

                multipart.addFormField("request_type", "uploadOrderImage");
                multipart.addFilePart("document", new File(params[0]));

                List<String> response = multipart.finish();
                for (String line : response) {
                    res = res + line;
                }
                return res;
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
                        imageList.set(latestPosition, new MasterModel(jsonObject.getString("name"), jsonObject.getString("document_url")));
                        rvImages.setAdapter(new ImagesAdapter());
                    } else {
                        Utilities.showMessage("Image upload failed", context, 3);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showMessage(e.toString(), context, 3);
            }
        }
    }

    private void updateOrderWithNewProduct(JsonArray orderImageJsonArray) {
        JsonObject mainObj = new JsonObject();

        JsonArray productsDetailsArray = new JsonArray();

        for (int i = 0; i < orderDetails.getProduct_details().size(); i++) {
            JsonObject productObject = new JsonObject();
            productObject.addProperty("id", orderDetails.getProduct_details().get(i).getId());
            productObject.addProperty("product_id", orderDetails.getProduct_details().get(i).getProduct_id());
            productObject.addProperty("quantity", orderDetails.getProduct_details().get(i).getQuantity());
            productObject.addProperty("amount", orderDetails.getProduct_details().get(i).getAmount());
            productsDetailsArray.add(productObject);
        }

        mainObj.addProperty("type", "updateOrder");
        mainObj.addProperty("id", orderDetails.getId());
        mainObj.addProperty("order_id", orderDetails.getOrder_id());
        mainObj.addProperty("owner_business_id", orderDetails.getOwner_business_id());
        mainObj.addProperty("order_type", "1");
        mainObj.addProperty("order_text", "");
        mainObj.add("product_details", productsDetailsArray);
        mainObj.addProperty("status", "1");    // status = 'IN CART'-2
        mainObj.addProperty("purchase_order_type", "1");
        mainObj.addProperty("business_id", "0");
        mainObj.add("order_image", orderImageJsonArray);
        mainObj.addProperty("user_id", userId);

        if (Utilities.isNetworkAvailable(context)) {
            new UpdateOrder().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }

    private class UpdateOrder extends AsyncTask<String, Void, String> {

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
            res = APICall.JSONAPICall(ApplicationConstants.BOOKORDERAPI, params[0]);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    BookOrderGetMyOrdersModel pojoDetails = new Gson().fromJson(result, BookOrderGetMyOrdersModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        List<BookOrderGetMyOrdersModel.ResultBean> ordersList = pojoDetails.getResult();

                        for (BookOrderGetMyOrdersModel.ResultBean orderDetail : ordersList) {
                            if (orderDetails.getId().equals(orderDetail.getId())) {
                                orderDetails = orderDetail;
                            }
                        }


                    } else {
                        Utilities.showMessage(message, context, 3);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.icon_backarrow);
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }
}
