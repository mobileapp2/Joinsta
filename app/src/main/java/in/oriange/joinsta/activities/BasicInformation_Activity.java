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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import in.oriange.joinsta.R;
import in.oriange.joinsta.models.MasterModel;
import in.oriange.joinsta.models.PrimaryPublicMobileSelectionModel;
import in.oriange.joinsta.models.PrimarySelectionModel;
import in.oriange.joinsta.pojos.MasterPojo;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.MultipartUtility;
import in.oriange.joinsta.utilities.ParamsPojo;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.PermissionUtil.PERMISSION_ALL;
import static in.oriange.joinsta.utilities.PermissionUtil.doesAppNeedPermissions;

public class BasicInformation_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private CircleImageView imv_user;
    private MaterialEditText edt_fname, edt_mname, edt_lname, edt_bloodgroup, edt_education,
            edt_specify, edt_mobile, edt_landline, edt_email, edt_nativeplace;
    private RadioButton rb_male, rb_female;
    private LinearLayout ll_mobile, ll_landline, ll_email;
    private ImageButton ib_add_mobile, ib_add_landline, ib_add_email;
    private ArrayList<MasterModel> bloodGroupList, educationList;
    private ArrayList<LinearLayout> mobileLayoutsList, landlineLayoutsList, emailLayoutsList;

    private String userId, password, bloodGroupId, educationId, genderId, imageUrl, isActive, referralCode;
    private JsonArray mobileJSONArray, landlineJSONArray, emailJSONArray;

    private JSONArray mobileJsonArray, landlinesonArray, emailJsonArray;

    private ArrayList<PrimaryPublicMobileSelectionModel> mobileList;
    private ArrayList<PrimarySelectionModel> landlineList, emailList;
    private int lastSelectedMobilePrimary = -1, lastSelectedLandlinePrimary = -1, lastSelectedEmailPrimary = -1;

    private Uri photoURI;
    private final int CAMERA_REQUEST = 100;
    private final int GALLERY_REQUEST = 200;
    private File file, photoFileToUpload, profilPicFolder;
    private String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_information);

        init();
        setDefault();
        getSessionData();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = BasicInformation_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        imv_user = findViewById(R.id.imv_user);

        edt_fname = findViewById(R.id.edt_fname);
        edt_mname = findViewById(R.id.edt_mname);
        edt_lname = findViewById(R.id.edt_lname);
        edt_bloodgroup = findViewById(R.id.edt_bloodgroup);
        edt_education = findViewById(R.id.edt_education);
        edt_specify = findViewById(R.id.edt_specify);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_landline = findViewById(R.id.edt_landline);
        edt_email = findViewById(R.id.edt_email);
        edt_nativeplace = findViewById(R.id.edt_nativeplace);

        rb_male = findViewById(R.id.rb_male);
        rb_female = findViewById(R.id.rb_female);

        ll_mobile = findViewById(R.id.ll_mobile);
        ll_landline = findViewById(R.id.ll_landline);
        ll_email = findViewById(R.id.ll_email);

        ib_add_mobile = findViewById(R.id.ib_add_mobile);
        ib_add_landline = findViewById(R.id.ib_add_landline);
        ib_add_email = findViewById(R.id.ib_add_email);

        bloodGroupList = new ArrayList<>();
        educationList = new ArrayList<>();
        mobileLayoutsList = new ArrayList<>();
        landlineLayoutsList = new ArrayList<>();
        emailLayoutsList = new ArrayList<>();

        mobileJSONArray = new JsonArray();
        landlineJSONArray = new JsonArray();
        emailJSONArray = new JsonArray();

        mobileList = new ArrayList<>();
        landlineList = new ArrayList<>();
        emailList = new ArrayList<>();


        profilPicFolder = new File(Environment.getExternalStorageDirectory() + "/Joinsta/" + "Basic Info");
        if (!profilPicFolder.exists())
            profilPicFolder.mkdirs();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }
    }

    private void setDefault() {

    }

    private void getSessionData() {

        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);

            userId = json.getString("userid");
            password = json.getString("password");
            bloodGroupId = json.getString("blood_group_id");
            edt_bloodgroup.setText(json.getString("blood_group_description"));
            educationId = json.getString("education_id");
            edt_education.setText(json.getString("education_description"));
            edt_specify.setText(json.getString("specific_education"));
            edt_nativeplace.setText(json.getString("native_place"));
            edt_fname.setText(json.getString("first_name"));
            edt_mname.setText(json.getString("middle_name"));
            edt_lname.setText(json.getString("last_name"));
            genderId = json.getString("gender_id");
            imageUrl = json.getString("image_url");
            isActive = json.getString("is_active");
            referralCode = json.getString("referral_code");

            if (edt_bloodgroup.getText().toString().trim().equals("null"))
                edt_bloodgroup.setText("");

            if (edt_education.getText().toString().trim().equals("null"))
                edt_education.setText("");

            if (!imageUrl.equals("")) {
                Picasso.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.icon_userphoto)
                        .into(imv_user);
            }

            if (genderId.equals("1")) {
                rb_male.setChecked(true);
            } else if (genderId.equals("2")) {
                rb_female.setChecked(true);
            }

            mobileJsonArray = new JSONArray(json.getString("mobile_numbers"));
            landlinesonArray = new JSONArray(json.getString("landline_numbers"));
            emailJsonArray = new JSONArray(json.getString("email"));


            if (mobileJSONArray != null)
                if (mobileJsonArray.length() > 0) {
                    for (int i = 0; i < mobileJsonArray.length(); i++) {

                        if (i == (mobileJsonArray.length() - 1)) {
                            edt_mobile.setText(mobileJsonArray.getJSONObject(i).getString("mobile"));
                        } else {
                            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            final View rowView = inflater.inflate(R.layout.layout_add_mobile, null);
                            mobileLayoutsList.add((LinearLayout) rowView);
                            ll_mobile.addView(rowView, ll_mobile.getChildCount() - 1);
                            ((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).setText(mobileJsonArray.getJSONObject(i).getString("mobile"));
                        }
                    }
                }

            if (landlinesonArray != null)
                if (landlinesonArray.length() > 0) {
                    for (int i = 0; i < landlinesonArray.length(); i++) {

                        if (i == (landlinesonArray.length() - 1)) {
                            edt_landline.setText(landlinesonArray.getJSONObject(i).getString("landline_number").replace("-", ""));
//                        landlineLayoutsList.add(ll_landline);
                        } else {
                            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            final View rowView = inflater.inflate(R.layout.layout_add_landline, null);
                            landlineLayoutsList.add((LinearLayout) rowView);
                            ll_landline.addView(rowView, ll_landline.getChildCount() - 1);
                            ((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).setText(landlinesonArray.getJSONObject(i).getString("landline_number").replace("-", ""));
                        }
                    }
                }

            if (emailJsonArray != null)
                if (emailJsonArray.length() > 0) {
                    for (int i = 0; i < emailJsonArray.length(); i++) {

                        if (i == (emailJsonArray.length() - 1)) {
                            edt_email.setText(emailJsonArray.getJSONObject(i).getString("email"));
//                        emailLayoutsList.add(ll_email);
                        } else {
                            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            final View rowView = inflater.inflate(R.layout.layout_add_email, null);
                            emailLayoutsList.add((LinearLayout) rowView);
                            ll_email.addView(rowView, ll_email.getChildCount() - 1);
                            ((EditText) emailLayoutsList.get(i).findViewById(R.id.edt_email)).setText(emailJsonArray.getJSONObject(i).getString("email"));

                        }

                    }
                }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEventHandler() {

        imv_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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


        edt_bloodgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetBloodGroupList().execute();
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            }
        });

        edt_education.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetEducationList().execute();
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            }
        });

        ib_add_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.layout_add_mobile, null);
                LinearLayout ll = (LinearLayout) rowView;
                mobileLayoutsList.add(ll);
                ll_mobile.addView(rowView, ll_mobile.getChildCount() - 1);
            }
        });

        ib_add_landline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.layout_add_landline, null);
                LinearLayout ll = (LinearLayout) rowView;
                landlineLayoutsList.add(ll);
                ll_landline.addView(rowView, ll_landline.getChildCount() - 1);
            }
        });

        ib_add_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.layout_add_email, null);
                LinearLayout ll = (LinearLayout) rowView;
                emailLayoutsList.add(ll);
                ll_email.addView(rowView, ll_email.getChildCount() - 1);
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
                    file = new File(profilPicFolder, "doc_image.png");
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
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(BasicInformation_Activity.this);
            }
            if (requestCode == CAMERA_REQUEST) {
                CropImage.activity(photoURI).setGuidelines(CropImageView.Guidelines.ON).start(BasicInformation_Activity.this);
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
                + "Basic Info/" + "uplimg.png";

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

        photoFileToUpload = new File(destinationFile);
        new UploadUserImage().execute(photoFileToUpload);

    }

    private class UploadUserImage extends AsyncTask<File, Integer, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(File... params) {
            String res = "";
            try {
                MultipartUtility multipart = new MultipartUtility(ApplicationConstants.FILEUPLOADAPI, "UTF-8");

                multipart.addFormField("request_type", "uploadFile");
                multipart.addFormField("user_id", userId);
                multipart.addFilePart("document", params[0]);

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
            String type = "", message = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        JSONObject jsonObject = mainObj.getJSONObject("result");
                        imageUrl = jsonObject.getString("document_url");

                        if (!imageUrl.equals("")) {
                            Picasso.with(context)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.icon_userphoto)
                                    .into(imv_user);
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

    private class GetBloodGroupList extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "AllBloodGroup"));
            res = APICall.FORMDATAAPICall(ApplicationConstants.MASTERAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    MasterPojo pojoDetails = new Gson().fromJson(result, MasterPojo.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        bloodGroupList = pojoDetails.getResult();
                        if (bloodGroupList.size() > 0) {
                            showBloodGroupListDialog();
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

    private void showBloodGroupListDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Blood Group");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.list_row);

        for (int i = 0; i < bloodGroupList.size(); i++) {

            arrayAdapter.add(String.valueOf(bloodGroupList.get(i).getName()));
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
                edt_bloodgroup.setText(bloodGroupList.get(which).getName());
                bloodGroupId = bloodGroupList.get(which).getId();

            }
        });
        AlertDialog alertD = builderSingle.create();
        alertD.show();
    }

    private class GetEducationList extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "AllEducationList"));
            res = APICall.FORMDATAAPICall(ApplicationConstants.MASTERAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    MasterPojo pojoDetails = new Gson().fromJson(result, MasterPojo.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        educationList = pojoDetails.getResult();
                        if (educationList.size() > 0) {
                            showEducationDialog();
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

    private void showEducationDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Education");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.list_row);

        for (int i = 0; i < educationList.size(); i++) {

            arrayAdapter.add(String.valueOf(educationList.get(i).getName()));
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
                edt_education.setText(educationList.get(which).getName());
                educationId = educationList.get(which).getId();

            }
        });
        AlertDialog alertD = builderSingle.create();
        alertD.show();
    }

    public void removeMobileLayout(View view) {
        ll_mobile.removeView((View) view.getParent());
        mobileLayoutsList.remove(view.getParent());
    }

    public void removeLandlineLayout(View view) {
        ll_landline.removeView((View) view.getParent());
        landlineLayoutsList.remove(view.getParent());
    }

    public void removeEmailLayout(View view) {
        ll_email.removeView((View) view.getParent());
        emailLayoutsList.remove(view.getParent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menus_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                validateData();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void validateData() {

        mobileList = new ArrayList<>();
        landlineList = new ArrayList<>();
        emailList = new ArrayList<>();

        if (edt_fname.getText().toString().trim().isEmpty()) {
            edt_fname.setError("Please enter first name");
            edt_fname.requestFocus();
            return;
        }

//        if (edt_mname.getText().toString().trim().isEmpty()) {
//            edt_mname.setError("Please enter middle name");
//            edt_mname.requestFocus();
//            return;
//        }
//
//        if (edt_lname.getText().toString().trim().isEmpty()) {
//            edt_lname.setError("Please enter last name");
//            edt_lname.requestFocus();
//            return;
//        }

        String genderId = "";

        if (rb_male.isChecked()) {
            genderId = "1";
        } else if (rb_female.isChecked()) {
            genderId = "2";
        } else {
            Utilities.showMessage("Please select gender", context, 2);
            return;
        }

        if (edt_education.getText().toString().trim().isEmpty()) {
            edt_education.setError("Please select education");
            edt_education.requestFocus();
            return;
        }

        if (edt_specify.getText().toString().trim().isEmpty()) {
            edt_specify.setError("Please enter specification");
            edt_specify.requestFocus();
            return;
        }


        for (int i = 0; i < mobileLayoutsList.size(); i++) {
            if (!Utilities.isValidMobileno(((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim())) {
                ((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).setError("Please enter mobile number");
                (mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).requestFocus();
                return;
            }
        }

        if (!Utilities.isValidMobileno(edt_mobile.getText().toString().trim())) {
            edt_mobile.setError("Please enter valid mobile number");
            edt_mobile.requestFocus();
            return;
        }

        for (int i = 0; i < landlineLayoutsList.size(); i++) {
            if (!Utilities.isLandlineValid(((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).getText().toString().trim())) {
                ((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).setError("Please enter valid landline number");
                (landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).requestFocus();
                return;
            }
        }

        if (!Utilities.isLandlineValid(edt_landline.getText().toString().trim())) {
            edt_landline.setError("Please enter valid landline number");
            edt_landline.requestFocus();
            return;
        }


        for (int i = 0; i < emailLayoutsList.size(); i++) {
            if (!Utilities.isEmailValid(((EditText) emailLayoutsList.get(i).findViewById(R.id.edt_email)).getText().toString().trim())) {
                ((EditText) emailLayoutsList.get(i).findViewById(R.id.edt_email)).setError("Please enter valid email");
                (emailLayoutsList.get(i).findViewById(R.id.edt_email)).requestFocus();
                return;
            }
        }

        if (!Utilities.isEmailValid(edt_email.getText().toString().trim())) {
            edt_email.setError("Please enter valid email");
            edt_email.requestFocus();
            return;
        }

        if (edt_nativeplace.getText().toString().trim().isEmpty()) {
            edt_nativeplace.setError("Please enter native place");
            edt_nativeplace.requestFocus();
            return;
        }


        try {
            for (int i = 0; i < mobileLayoutsList.size(); i++) {
                if (!((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim().equals("")) {
                    if (i < mobileJsonArray.length() - 1) {
                        mobileList.add(new PrimaryPublicMobileSelectionModel(((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim(), "0", "0", mobileJsonArray.getJSONObject(i).getString("user_moblie_id")));
                    } else {
                        mobileList.add(new PrimaryPublicMobileSelectionModel(((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim(), "0", "0", "0"));

                    }
                }
            }

            if (mobileJsonArray != null)
                mobileList.add(new PrimaryPublicMobileSelectionModel(edt_mobile.getText().toString().trim(), "0", "0", mobileJsonArray.getJSONObject(mobileJsonArray.length() - 1).getString("user_moblie_id")));
            else
                mobileList.add(new PrimaryPublicMobileSelectionModel(edt_mobile.getText().toString().trim(), "0", "0", "0"));


            for (int i = 0; i < landlineLayoutsList.size(); i++) {
                if (!((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).getText().toString().trim().equals("")) {
                    if (i < landlinesonArray.length() - 1) {
                        landlineList.add(new PrimarySelectionModel(((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).getText().toString().trim(), "0", landlinesonArray.getJSONObject(i).getString("user_landline_id")));
                    } else {
                        landlineList.add(new PrimarySelectionModel(((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).getText().toString().trim(), "0", "0"));
                    }
                }
            }

            if (landlinesonArray != null)
                landlineList.add(new PrimarySelectionModel(edt_landline.getText().toString().trim(), "0", landlinesonArray.getJSONObject(landlinesonArray.length() - 1).getString("user_landline_id")));
            else
                landlineList.add(new PrimarySelectionModel(edt_landline.getText().toString().trim(), "0", "0"));

            for (int i = 0; i < emailLayoutsList.size(); i++) {
                if (!((EditText) emailLayoutsList.get(i).findViewById(R.id.edt_email)).getText().toString().trim().equals("")) {
                    if (i < emailJsonArray.length() - 1) {
                        emailList.add(new PrimarySelectionModel(((EditText) emailLayoutsList.get(i).findViewById(R.id.edt_email)).getText().toString().trim(), "0", emailJsonArray.getJSONObject(i).getString("user_email_id")));
                    } else {
                        emailList.add(new PrimarySelectionModel(((EditText) emailLayoutsList.get(i).findViewById(R.id.edt_email)).getText().toString().trim(), "0", "0"));
                    }
                }
            }

            if (emailJsonArray != null)
                emailList.add(new PrimarySelectionModel(edt_email.getText().toString().trim(), "0", emailJsonArray.getJSONObject(emailJsonArray.length() - 1).getString("user_email_id")));
            else
                emailList.add(new PrimarySelectionModel(edt_email.getText().toString().trim(), "0", "0"));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        showPrimaryMobileDialog();


    }

    private void showPrimaryMobileDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_primary_selection, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setTitle("Select Primary Mobile");
        alertDialogBuilder.setView(promptView);

        final RecyclerView rv_list = promptView.findViewById(R.id.rv_list);
        Button btn_next = promptView.findViewById(R.id.btn_next);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rv_list.setLayoutManager(layoutManager);
        rv_list.setAdapter(new MobilePrimaryAdapter());


        alertDialogBuilder.setCancelable(false);
        final AlertDialog alertD = alertDialogBuilder.create();

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < mobileList.size(); i++) {

                    MobilePrimaryAdapter.MyViewHolder myViewHolder =
                            (MobilePrimaryAdapter.MyViewHolder) rv_list.findViewHolderForAdapterPosition(i);

                    if (myViewHolder.rb_selectone.isChecked()) {
                        mobileList.get(i).setIsPrimary("1");
                    }
                }

                alertD.dismiss();
                showPublicMobileDialog();
            }
        });

        alertD.show();
    }

    private class MobilePrimaryAdapter extends RecyclerView.Adapter<MobilePrimaryAdapter.MyViewHolder> {

        public MobilePrimaryAdapter() {

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_primary, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.rb_selectone.setText(mobileList.get(position).getDetails());

            holder.rb_selectone.setChecked(lastSelectedMobilePrimary == position);
        }

        @Override
        public int getItemCount() {
            return mobileList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private RadioButton rb_selectone;

            public MyViewHolder(View view) {
                super(view);

                rb_selectone = view.findViewById(R.id.rb_selectone);

                rb_selectone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lastSelectedMobilePrimary = getAdapterPosition();
                        notifyDataSetChanged();
                    }
                });


            }
        }
    }

    private void showPublicMobileDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_primary_selection, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setTitle("Select Public Mobile");
        alertDialogBuilder.setView(promptView);

        final RecyclerView rv_list = promptView.findViewById(R.id.rv_list);
        Button btn_next = promptView.findViewById(R.id.btn_next);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rv_list.setLayoutManager(layoutManager);
        rv_list.setAdapter(new MobilePublicAdapter());

        alertDialogBuilder.setCancelable(false);
        final AlertDialog alertD = alertDialogBuilder.create();

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < mobileList.size(); i++) {

                    MobilePublicAdapter.MyViewHolder myViewHolder =
                            (MobilePublicAdapter.MyViewHolder) rv_list.findViewHolderForAdapterPosition(i);

                    if (myViewHolder.cb_selected.isChecked()) {
                        mobileList.get(i).setIsPublic("1");
                    }
                }

                alertD.dismiss();
                showPrimaryLandlineDialog();
            }
        });

        alertD.show();
    }

    private class MobilePublicAdapter extends RecyclerView.Adapter<MobilePublicAdapter.MyViewHolder> {

        public MobilePublicAdapter() {

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_public, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.cb_selected.setText(mobileList.get(position).getDetails());
        }

        @Override
        public int getItemCount() {
            return mobileList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private CheckBox cb_selected;

            public MyViewHolder(View view) {
                super(view);

                cb_selected = view.findViewById(R.id.cb_selected);

            }
        }
    }

    private void showPrimaryLandlineDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_primary_selection, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setTitle("Select Primary Landline");
        alertDialogBuilder.setView(promptView);

        final RecyclerView rv_list = promptView.findViewById(R.id.rv_list);
        Button btn_next = promptView.findViewById(R.id.btn_next);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rv_list.setLayoutManager(layoutManager);
        rv_list.setAdapter(new LandLinePrimaryAdapter());


        alertDialogBuilder.setCancelable(false);
        final AlertDialog alertD = alertDialogBuilder.create();

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < landlineList.size(); i++) {

                    LandLinePrimaryAdapter.MyViewHolder myViewHolder =
                            (LandLinePrimaryAdapter.MyViewHolder) rv_list.findViewHolderForAdapterPosition(i);

                    if (myViewHolder.rb_selectone.isChecked()) {
                        landlineList.get(i).setIsPrimary("1");
                    }
                }

                alertD.dismiss();
                showPrimaryEmailDialog();
            }
        });

        alertD.show();
    }

    private class LandLinePrimaryAdapter extends RecyclerView.Adapter<LandLinePrimaryAdapter.MyViewHolder> {

        public LandLinePrimaryAdapter() {

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_primary, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.rb_selectone.setText(landlineList.get(position).getDetails());

            holder.rb_selectone.setChecked(lastSelectedLandlinePrimary == position);
        }

        @Override
        public int getItemCount() {
            return landlineList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private RadioButton rb_selectone;

            public MyViewHolder(View view) {
                super(view);

                rb_selectone = view.findViewById(R.id.rb_selectone);

                rb_selectone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lastSelectedLandlinePrimary = getAdapterPosition();
                        notifyDataSetChanged();
                    }
                });


            }
        }
    }

    private void showPrimaryEmailDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_primary_selection, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setTitle("Select Primary Email");
        alertDialogBuilder.setView(promptView);

        final RecyclerView rv_list = promptView.findViewById(R.id.rv_list);
        Button btn_next = promptView.findViewById(R.id.btn_next);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rv_list.setLayoutManager(layoutManager);
        rv_list.setAdapter(new EmailPrimaryAdapter());


        alertDialogBuilder.setCancelable(false);
        final AlertDialog alertD = alertDialogBuilder.create();

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < emailList.size(); i++) {

                    EmailPrimaryAdapter.MyViewHolder myViewHolder =
                            (EmailPrimaryAdapter.MyViewHolder) rv_list.findViewHolderForAdapterPosition(i);

                    if (myViewHolder.rb_selectone.isChecked()) {
                        emailList.get(i).setIsPrimary("1");
                    }
                }

                alertD.dismiss();
                submitData();
            }
        });

        alertD.show();
    }

    private class EmailPrimaryAdapter extends RecyclerView.Adapter<EmailPrimaryAdapter.MyViewHolder> {

        public EmailPrimaryAdapter() {

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_primary, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.rb_selectone.setText(emailList.get(position).getDetails());

            holder.rb_selectone.setChecked(lastSelectedEmailPrimary == position);
        }

        @Override
        public int getItemCount() {
            return emailList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private RadioButton rb_selectone;

            public MyViewHolder(View view) {
                super(view);

                rb_selectone = view.findViewById(R.id.rb_selectone);

                rb_selectone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lastSelectedEmailPrimary = getAdapterPosition();
                        notifyDataSetChanged();
                    }
                });


            }
        }
    }

    private void submitData() {
        mobileJSONArray = new JsonArray();
        landlineJSONArray = new JsonArray();
        emailJSONArray = new JsonArray();

        JsonObject mainObj = new JsonObject();

        for (int i = 0; i < mobileList.size(); i++) {
            JsonObject mobileJSONObj = new JsonObject();
            mobileJSONObj.addProperty("mobile", mobileList.get(i).getDetails());
            mobileJSONObj.addProperty("is_primary", mobileList.get(i).getIsPrimary());
            mobileJSONObj.addProperty("is_public", mobileList.get(i).getIsPublic());
            mobileJSONObj.addProperty("id", mobileList.get(i).getId());
            mobileJSONArray.add(mobileJSONObj);
        }

        for (int i = 0; i < landlineList.size(); i++) {
            JsonObject landlineJSONObj = new JsonObject();
            landlineJSONObj.addProperty("landline_number", landlineList.get(i).getDetails());
            landlineJSONObj.addProperty("is_primary", landlineList.get(i).getIsPrimary());
            landlineJSONObj.addProperty("id", landlineList.get(i).getId());
            landlineJSONArray.add(landlineJSONObj);
        }

        for (int i = 0; i < emailList.size(); i++) {
            JsonObject emailJSONObj = new JsonObject();
            emailJSONObj.addProperty("email_id", emailList.get(i).getDetails());
            emailJSONObj.addProperty("is_primary", emailList.get(i).getIsPrimary());
            emailJSONObj.addProperty("id", emailList.get(i).getId());
            emailJSONArray.add(emailJSONObj);
        }

        mainObj.addProperty("type", "updateusers");
        mainObj.addProperty("first_name", edt_fname.getText().toString().trim());
        mainObj.addProperty("last_name", edt_lname.getText().toString().trim());
        mainObj.addProperty("middle_name", edt_mname.getText().toString().trim());
        mainObj.addProperty("gender_id", genderId);
        mainObj.addProperty("blood_group_id", bloodGroupId);
        mainObj.addProperty("education_id", educationId);
        mainObj.addProperty("specific_education", edt_specify.getText().toString().trim());
        mainObj.addProperty("referral_code", referralCode);
        mainObj.addProperty("is_active", isActive);
        mainObj.addProperty("password", password);
        mainObj.addProperty("image_url", imageUrl);
        mainObj.addProperty("native_place", edt_nativeplace.getText().toString().trim());
        mainObj.add("mobile1", mobileJSONArray);
        mainObj.add("landline_number", landlineJSONArray);
        mainObj.add("email", emailJSONArray);
        mainObj.addProperty("user_id", userId);

        Log.i("BASICINFOJSON", mainObj.toString());
        if (Utilities.isNetworkAvailable(context)) {
            new UpdateUser().execute(mainObj.toString());
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }

    }

    private class UpdateUser extends AsyncTask<String, Void, String> {

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
            res = APICall.JSONAPICall(ApplicationConstants.USERSAPI, params[0]);
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

                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                session.createUserLoginSession(jsonarr.toString());

                                LayoutInflater layoutInflater = LayoutInflater.from(context);
                                View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                                alertDialogBuilder.setView(promptView);

                                LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                                TextView tv_title = promptView.findViewById(R.id.tv_title);
                                Button btn_ok = promptView.findViewById(R.id.btn_ok);

                                animation_view.playAnimation();
                                tv_title.setText("User details updated successfully");
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
                            }
                        }


                    } else {
                        Utilities.showMessage("User details failed to update", context, 3);
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void askPermission() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
            return;
        } else {
            selectImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        switch (requestCode) {
            case 1: {
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
                            startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", context.getPackageName(), null)));
                        }
                    });
                    builder.create();
                    AlertDialog alertD = builder.create();
                    alertD.show();
                }
            }

        }
    }


}
