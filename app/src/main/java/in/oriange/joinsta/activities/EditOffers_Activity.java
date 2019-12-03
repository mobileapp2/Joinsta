package in.oriange.joinsta.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
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
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.MyOffersListModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.MultipartUtility;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.ApplicationConstants.IMAGE_LINK;
import static in.oriange.joinsta.utilities.Utilities.changeDateFormat;
import static in.oriange.joinsta.utilities.Utilities.hideSoftKeyboard;
import static in.oriange.joinsta.utilities.Utilities.yyyyMMddDate;

public class EditOffers_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private MaterialEditText edt_title, edt_start_date, edt_end_date, edt_url, edt_promo_code;
    private EditText edt_description;
    private ImageView imv_image_one, imv_image_two, imv_image_three;
    private Button btn_save;

    private int mYear, mMonth, mDay, mYear1, mMonth1, mDay1;
    private String userId, startDate, endDate, detailId, categoryTypeId, categoryId, categoryTypeName;

    private File photoFileFolder;
    private Uri photoURI;
    private final int CAMERA_REQUEST = 100, GALLERY_REQUEST = 200;
    private int IMAGE_TYPE = 0;
    private String imageOneName = "", imageTwoName = "", imageThreeName = "";
    private MyOffersListModel.ResultBean offerDetails;

    private String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_offers);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = EditOffers_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        edt_title = findViewById(R.id.edt_title);
        edt_start_date = findViewById(R.id.edt_start_date);
        edt_end_date = findViewById(R.id.edt_end_date);
        edt_url = findViewById(R.id.edt_url);
        edt_promo_code = findViewById(R.id.edt_promo_code);
        edt_description = findViewById(R.id.edt_description);
        imv_image_one = findViewById(R.id.imv_image_one);
        imv_image_two = findViewById(R.id.imv_image_two);
        imv_image_three = findViewById(R.id.imv_image_three);
        btn_save = findViewById(R.id.btn_save);


        photoFileFolder = new File(Environment.getExternalStorageDirectory() + "/Joinsta/" + "Offer Images");
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
        offerDetails = (MyOffersListModel.ResultBean) getIntent().getSerializableExtra("offerDetails");

        detailId = getIntent().getStringExtra("detailId");
        categoryTypeId = getIntent().getStringExtra("categoryTypeId");
        categoryId = getIntent().getStringExtra("categoryId");
        categoryTypeName = getIntent().getStringExtra("categoryTypeName");

        Calendar calendar = Calendar.getInstance();

        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        mYear1 = calendar.get(Calendar.YEAR);
        mMonth1 = calendar.get(Calendar.MONTH);
        mDay1 = calendar.get(Calendar.DAY_OF_MONTH);


        edt_title.setText(offerDetails.getTitle());

        edt_description.setText(offerDetails.getDescription());

        startDate = offerDetails.getStart_date();
        edt_start_date.setText(changeDateFormat("yyyy-MM-dd", "dd-MM-yyyy", offerDetails.getStart_date()));

        endDate = offerDetails.getEnd_date();
        edt_end_date.setText(changeDateFormat("yyyy-MM-dd", "dd-MM-yyyy", offerDetails.getEnd_date()));

        edt_url.setText(offerDetails.getUrl());

        edt_promo_code.setText(offerDetails.getPromo_code());

        for (int i = 0; i < offerDetails.getDocuments().size(); i++) {

            switch (i) {
                case 0:
                    String imageOne = IMAGE_LINK + "offerdoc/business/" + offerDetails.getDocuments().get(0).getDocument();
                    Picasso.with(context)
                            .load(imageOne)
                            .resize(200, 200)
                            .into(imv_image_one);
                    imv_image_one.setPadding(0, 0, 0, 0);

                    break;
                case 1:
                    String imageTwo = IMAGE_LINK + "offerdoc/business/" + offerDetails.getDocuments().get(1).getDocument();
                    Picasso.with(context)
                            .load(imageTwo)
                            .resize(200, 200)
                            .into(imv_image_two);
                    imv_image_two.setPadding(0, 0, 0, 0);

                    break;
                case 2:
                    String imageThree = IMAGE_LINK + "offerdoc/business/" + offerDetails.getDocuments().get(2).getDocument();
                    Picasso.with(context)
                            .load(imageThree)
                            .resize(200, 200)
                            .into(imv_image_three);
                    imv_image_three.setPadding(0, 0, 0, 0);

                    break;

            }

        }


    }

    private void setEventHandler() {
        edt_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        edt_end_date.setText("");
                        startDate = yyyyMMddDate(dayOfMonth, month + 1, year);
                        edt_start_date.setText(changeDateFormat("yyyy-MM-dd", "dd-MM-yyyy", startDate));

                        mYear = year;
                        mMonth = month;
                        mDay = dayOfMonth;
                    }
                }, mYear, mMonth, mDay);
                try {
                    Calendar c = Calendar.getInstance();
                    c.set(mYear, mMonth + 1, mDay);

                    dialog.getDatePicker().setCalendarViewShown(false);
                    dialog.getDatePicker().setMinDate(System.currentTimeMillis());
                    dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.show();
            }
        });

        edt_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_start_date.getText().toString().trim().isEmpty()) {
                    edt_start_date.setError("Please select start date");
                    edt_start_date.requestFocus();
                    return;
                }

                DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        endDate = yyyyMMddDate(dayOfMonth, month + 1, year);
                        edt_end_date.setText(changeDateFormat("yyyy-MM-dd", "dd-MM-yyyy", endDate));

                        mYear1 = year;
                        mMonth1 = month;
                        mDay1 = dayOfMonth;
                    }
                }, mYear1, mMonth1, mDay1);
                try {
                    dialog.getDatePicker().setCalendarViewShown(false);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(mYear, mMonth, mDay);
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.set(mYear, mMonth + 1, mDay);
                    dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                    dialog.getDatePicker().setMaxDate(calendar1.getTimeInMillis());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.show();
            }
        });

        imv_image_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IMAGE_TYPE = 1;
                selectImage();
            }
        });

        imv_image_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IMAGE_TYPE = 2;
                selectImage();
            }
        });

        imv_image_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IMAGE_TYPE = 3;
                selectImage();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                Uri imageUri = data.getData();
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(EditOffers_Activity.this);
            }

            if (requestCode == CAMERA_REQUEST) {
                CropImage.activity(photoURI).setGuidelines(CropImageView.Guidelines.ON).start(EditOffers_Activity.this);
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
                + "Offer Images/" + "uplimg.png";

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
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder res = new StringBuilder();
            try {
                MultipartUtility multipart = new MultipartUtility(ApplicationConstants.FILEUPLOADAPI, "UTF-8");

                multipart.addFormField("request_type", "uploadOfferFile");
                multipart.addFormField("category_type", categoryTypeName);
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
            String type;
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    if (type.equalsIgnoreCase("success")) {
                        JSONObject jsonObject = mainObj.getJSONObject("result");

                        if (IMAGE_TYPE == 1) {
                            String imageUrl = jsonObject.getString("document_url");
                            imageOneName = jsonObject.getString("name");

                            if (!imageUrl.equals("")) {
                                Picasso.with(context)
                                        .load(imageUrl)
                                        .resize(100, 100)
                                        .into(imv_image_one);
                                imv_image_one.setPadding(0, 0, 0, 0);
                            }
                        } else if (IMAGE_TYPE == 2) {
                            String imageUrl = jsonObject.getString("document_url");
                            imageTwoName = jsonObject.getString("name");

                            if (!imageUrl.equals("")) {
                                Picasso.with(context)
                                        .load(imageUrl)
                                        .resize(100, 100)
                                        .into(imv_image_two);
                                imv_image_two.setPadding(0, 0, 0, 0);
                            }
                        } else if (IMAGE_TYPE == 3) {
                            String imageUrl = jsonObject.getString("document_url");
                            imageThreeName = jsonObject.getString("name");

                            if (!imageUrl.equals("")) {
                                Picasso.with(context)
                                        .load(imageUrl)
                                        .resize(100, 100)
                                        .into(imv_image_three);
                                imv_image_three.setPadding(0, 0, 0, 0);
                            }
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

    private void submitData() {
        if (edt_title.getText().toString().trim().isEmpty()) {
            edt_title.setError("Please enter offer title");
            edt_title.requestFocus();
            return;
        }

        if (edt_description.getText().toString().trim().isEmpty()) {
            edt_description.setError("Please enter offer description");
            edt_description.requestFocus();
            return;
        }

        if (edt_start_date.getText().toString().trim().isEmpty()) {
            edt_start_date.setError("Please select start date");
            edt_start_date.requestFocus();
            return;
        }

        if (edt_end_date.getText().toString().trim().isEmpty()) {
            edt_end_date.setError("Please select end date");
            edt_end_date.requestFocus();
            return;
        }

        if (!edt_url.getText().toString().trim().isEmpty()) {
            if (!Utilities.isWebsiteValid(edt_url.getText().toString().trim())) {
                edt_url.setError("Please enter valid offer URL");
                edt_url.requestFocus();
                return;
            }
        }

//        String document = "";

//        if (imageOneName.isEmpty() && imageTwoName.isEmpty() && imageThreeName.isEmpty()) {
//            document = "";
//        } else {
        JsonArray docsArray = new JsonArray();

        if (!imageOneName.isEmpty()) {
            docsArray.add(imageOneName);
        }

        if (!imageTwoName.isEmpty()) {
            docsArray.add(imageTwoName);
        }

        if (!imageThreeName.isEmpty()) {
            docsArray.add(imageThreeName);
        }

//            document = docsArray.toString();
//        }

        JsonObject mainObj = new JsonObject();
        mainObj.addProperty("type", "updateOfferDetails");
        mainObj.addProperty("edit_category_type_id", categoryTypeId);
        mainObj.addProperty("edit_record_id", detailId);
        mainObj.addProperty("user_id", userId);
        mainObj.addProperty("edit_title", edt_title.getText().toString().trim());
        mainObj.addProperty("edit_description", edt_description.getText().toString().trim());
        mainObj.addProperty("edit_start_date", startDate);
        mainObj.addProperty("edit_end_date", endDate);
        mainObj.addProperty("edit_url", edt_url.getText().toString().trim());
        mainObj.addProperty("edit_promo_code", edt_promo_code.getText().toString().trim());
        mainObj.add("edit_document", docsArray);
        mainObj.addProperty("offer_id", offerDetails.getId());

        if (Utilities.isNetworkAvailable(context)) {
            new EditOffer().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }

    }

    private class EditOffer extends AsyncTask<String, Void, String> {

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
            res = APICall.JSONAPICall(ApplicationConstants.OFFERSAPI, params[0]);
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
                        tv_title.setText("Offer added successfully");
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

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftKeyboard(EditOffers_Activity.this);
    }
}
