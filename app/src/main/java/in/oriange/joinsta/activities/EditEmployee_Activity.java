package in.oriange.joinsta.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Callback;
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
import java.util.Locale;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import de.hdodenhof.circleimageview.CircleImageView;
import in.oriange.joinsta.R;
import in.oriange.joinsta.fragments.Profile_Fragment;
import in.oriange.joinsta.models.CategotyListModel;
import in.oriange.joinsta.models.ContryCodeModel;
import in.oriange.joinsta.models.GetEmployeeModel;
import in.oriange.joinsta.models.GetTagsListModel;
import in.oriange.joinsta.models.SubCategotyListModel;
import in.oriange.joinsta.models.TagsListModel;
import in.oriange.joinsta.pojos.CategotyListPojo;
import in.oriange.joinsta.pojos.SubCategotyListPojo;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.MultipartUtility;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.ApplicationConstants.IMAGE_LINK;
import static in.oriange.joinsta.utilities.PermissionUtil.PERMISSION_ALL;
import static in.oriange.joinsta.utilities.PermissionUtil.doesAppNeedPermissions;
import static in.oriange.joinsta.utilities.Utilities.hideSoftKeyboard;
import static in.oriange.joinsta.utilities.Utilities.loadJSONForCountryCode;

public class EditEmployee_Activity extends AppCompatActivity {

    private static Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private ProgressBar progressBar;
    private CircleImageView imv_user;
    private RelativeLayout rl_profilepic;
    private MaterialEditText edt_name, edt_nature, edt_subtype, edt_designation, edt_mobile, edt_landline,
            edt_email, edt_website, edt_select_area, edt_address, edt_pincode, edt_city, edt_district, edt_state, edt_country;
    private AutoCompleteTextView edt_tag;
    private static LinearLayout ll_mobile, ll_landline;
    private TextView tv_countrycode_mobile, tv_countrycode_landline;
    private ImageButton ib_add_mobile, ib_add_landline;
    private TagContainerLayout tag_container;
    private Button btn_save, btn_add_tag;

    private ArrayList<CategotyListModel> categotyList;
    private ArrayList<GetTagsListModel.ResultBean> tagsListFromAPI;
    private ArrayList<TagsListModel> tagsListTobeSubmitted;
    private static ArrayList<LinearLayout> mobileLayoutsList, landlineLayoutsList;
    private ArrayList<String> mobileList, landlineList;
    private JsonArray mobileJSONArray, landlineJSONArray, tagJSONArray;
    private static ArrayList<ContryCodeModel> countryCodeList;

    private String userId, imageUrl = "", imageName = "", categoryId = "0", subCategoryId = "0", latitude = "", longitude = "";
    private Uri photoURI;
    private final int CAMERA_REQUEST = 100;
    private final int GALLERY_REQUEST = 200;
    private File file, photoFileToUpload, profilPicFolder;
    private String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private static TextView tv_selected_forconcode = null;

    private GetEmployeeModel.ResultBean searchDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_employee);

        init();
        getSessionDetails();
        setDefault();
        setEventListner();
        setUpToolbar();
    }

    private void init() {
        context = EditEmployee_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        rl_profilepic = findViewById(R.id.rl_profilepic);
        progressBar = findViewById(R.id.progressBar);
        imv_user = findViewById(R.id.imv_user);
        edt_name = findViewById(R.id.edt_name);
        edt_nature = findViewById(R.id.edt_nature);
        edt_subtype = findViewById(R.id.edt_subtype);
        edt_designation = findViewById(R.id.edt_designation);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_landline = findViewById(R.id.edt_landline);
        edt_email = findViewById(R.id.edt_email);
        edt_website = findViewById(R.id.edt_website);
        edt_select_area = findViewById(R.id.edt_select_area);
        edt_address = findViewById(R.id.edt_address);
        edt_pincode = findViewById(R.id.edt_pincode);
        edt_city = findViewById(R.id.edt_city);
        edt_district = findViewById(R.id.edt_district);
        edt_state = findViewById(R.id.edt_state);
        edt_country = findViewById(R.id.edt_country);
        edt_tag = findViewById(R.id.edt_tag);

        tag_container = findViewById(R.id.tag_container);
        tv_countrycode_mobile = findViewById(R.id.tv_countrycode_mobile);
        tv_countrycode_landline = findViewById(R.id.tv_countrycode_landline);

        ll_mobile = findViewById(R.id.ll_mobile);
        ll_landline = findViewById(R.id.ll_landline);

        ib_add_mobile = findViewById(R.id.ib_add_mobile);
        ib_add_landline = findViewById(R.id.ib_add_landline);

        btn_add_tag = findViewById(R.id.btn_add_tag);
        btn_save = findViewById(R.id.btn_save);

        categotyList = new ArrayList<>();
        tagsListTobeSubmitted = new ArrayList<>();
        tagsListFromAPI = new ArrayList<>();
        mobileLayoutsList = new ArrayList<>();
        landlineLayoutsList = new ArrayList<>();
        mobileJSONArray = new JsonArray();
        landlineJSONArray = new JsonArray();
        tagJSONArray = new JsonArray();
        mobileList = new ArrayList<>();
        landlineList = new ArrayList<>();

        profilPicFolder = new File(Environment.getExternalStorageDirectory() + "/Joinsta/" + "Employee");
        if (!profilPicFolder.exists())
            profilPicFolder.mkdirs();

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

        try {
            JSONArray m_jArry = new JSONArray(loadJSONForCountryCode(context));
            countryCodeList = new ArrayList<>();

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                countryCodeList.add(new ContryCodeModel(
                        jo_inside.getString("name"),
                        jo_inside.getString("dial_code"),
                        jo_inside.getString("code")
                ));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        searchDetails = (GetEmployeeModel.ResultBean) getIntent().getSerializableExtra("searchDetails");

        if (!searchDetails.getImage_url().trim().isEmpty()) {
            String url = IMAGE_LINK + "" + searchDetails.getCreated_by() + "/" + searchDetails.getImage_url();
            Picasso.with(context)
                    .load(url)
                    .placeholder(getResources().getDrawable(R.drawable.icon_userphoto))
                    .into(imv_user, new Callback() {
                        @Override
                        public void onSuccess() {
                            rl_profilepic.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            rl_profilepic.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        } else {
            rl_profilepic.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }

        edt_name.setText(searchDetails.getOrganization_name());
        edt_nature.setText(searchDetails.getType_description());
        edt_subtype.setText(searchDetails.getSubtype_description());
        edt_designation.setText(searchDetails.getDesignation());
        edt_email.setText(searchDetails.getEmail());
        edt_website.setText(searchDetails.getWebsite());
        edt_select_area.setText(searchDetails.getLandmark());
        edt_address.setText(searchDetails.getAddress());
        edt_pincode.setText(searchDetails.getPincode());
        edt_city.setText(searchDetails.getCity());
        edt_district.setText(searchDetails.getDistrict());
        edt_state.setText(searchDetails.getState());
        edt_country.setText(searchDetails.getCountry());

        ArrayList<GetEmployeeModel.ResultBean.TagBean> tagsList = new ArrayList<>();
        tagsList = searchDetails.getTag().get(0);

        if (tagsList != null)
            if (tagsList.size() > 0)
                for (int i = 0; i < tagsList.size(); i++) {

                    if (!tagsList.get(i).getTag_name().trim().equals("")) {
                        tagsListTobeSubmitted.add(new TagsListModel(tagsList.get(i).getTag_id(), tagsList.get(i).getTag_name(), tagsList.get(i).getIs_approved()));
                        tag_container.addTag(tagsList.get(i).getTag_name());
                    }
                }


        ArrayList<GetEmployeeModel.ResultBean.MobilesBean> mobilesList = new ArrayList<>();
        mobilesList = searchDetails.getMobiles().get(0);

        if (mobilesList != null)
            if (mobilesList.size() > 0)
                for (int i = 0; i < mobilesList.size(); i++) {
                    if (i == mobilesList.size() - 1) {
                        try {
                            if (mobilesList.get(i).getMobile_number().length() > 10) {
                                edt_mobile.setText(mobilesList.get(i).getMobile_number().substring(mobilesList.get(i).getMobile_number().length() - 10));
                                String code = mobilesList.get(i).getMobile_number().substring(0, mobilesList.get(i).getMobile_number().length() - 10);
                                if (!code.isEmpty())
                                    tv_countrycode_mobile.setText(code);
                            } else {
                                edt_mobile.setText(mobilesList.get(i).getMobile_number());
                                String code = "";
                                if (!code.isEmpty())
                                    tv_countrycode_mobile.setText(code);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            final View rowView = inflater.inflate(R.layout.layout_add_mobile3, null);
                            LinearLayout ll = (LinearLayout) rowView;
                            mobileLayoutsList.add(ll);
                            ll_mobile.addView(rowView, ll_mobile.getChildCount() - 1);
                            if (mobilesList.get(i).getMobile_number().length() > 10) {
                                ((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).setText(mobilesList.get(i).getMobile_number().substring(mobilesList.get(i).getMobile_number().length() - 10));
                                String code = mobilesList.get(i).getMobile_number().substring(0, mobilesList.get(i).getMobile_number().length() - 10);
                                if (!code.isEmpty())
                                    ((TextView) mobileLayoutsList.get(i).findViewById(R.id.tv_countrycode_mobile)).setText(code);
                            } else {
                                ((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).setText(mobilesList.get(i).getMobile_number());
                                String code = "";
                                if (!code.isEmpty())
                                    ((TextView) mobileLayoutsList.get(i).findViewById(R.id.tv_countrycode_mobile)).setText(code);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }


        ArrayList<GetEmployeeModel.ResultBean.LandlineBean> landlineList = new ArrayList<>();
        landlineList = searchDetails.getLandline().get(0);


        if (landlineList != null)
            if (landlineList.size() > 0)
                for (int i = 0; i < landlineList.size(); i++) {
                    if (i == landlineList.size() - 1) {
                        try {

                            if (landlineList.get(i).getLandline_numbers().length() > 10) {
                                edt_landline.setText(landlineList.get(i).getLandline_numbers().substring(landlineList.get(i).getLandline_numbers().length() - 10));
                                String code = landlineList.get(i).getLandline_numbers().substring(0, landlineList.get(i).getLandline_numbers().length() - 10);
                                if (!code.isEmpty())
                                    tv_countrycode_landline.setText(code);
                            } else {
                                edt_landline.setText(landlineList.get(i).getLandline_numbers());
                                String code = "";
                                if (!code.isEmpty())
                                    tv_countrycode_landline.setText(code);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            final View rowView = inflater.inflate(R.layout.layout_add_landline3, null);
                            LinearLayout ll = (LinearLayout) rowView;
                            landlineLayoutsList.add(ll);
                            ll_landline.addView(rowView, ll_landline.getChildCount() - 1);

                            if (landlineList.get(i).getLandline_numbers().length() > 10) {
                                ((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).setText(landlineList.get(i).getLandline_numbers().substring(landlineList.get(i).getLandline_numbers().length() - 10));
                                String code = landlineList.get(i).getLandline_numbers().substring(0, landlineList.get(i).getLandline_numbers().length() - 10);
                                if (!code.isEmpty())
                                    ((TextView) landlineLayoutsList.get(i).findViewById(R.id.tv_countrycode_landline)).setText(code);
                            } else {
                                ((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).setText(landlineList.get(i).getLandline_numbers());
                                String code = "";
                                if (!code.isEmpty())
                                    ((TextView) landlineLayoutsList.get(i).findViewById(R.id.tv_countrycode_landline)).setText(code);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }


        categoryId = searchDetails.getType_id();
        subCategoryId = searchDetails.getSub_type_id();
        latitude = searchDetails.getLatitude();
        longitude = searchDetails.getLongitude();

        if (!searchDetails.getImage_url().isEmpty()) {
            Uri uri = Uri.parse(searchDetails.getImage_url());
            imageName = uri.getLastPathSegment();
        }


        if (Utilities.isNetworkAvailable(context)) {
            new GetTagsList().execute("0");
        }

    }

    private void setEventListner() {
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

        edt_nature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categotyList.size() == 0) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new GetCategotyList().execute("0", "0", "2");
                    } else {
                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                    }
                } else {
                    showCategoryListDialog();
                }
            }
        });

        edt_subtype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edt_nature.getText().toString().trim().isEmpty()) {
                    edt_nature.setError("Please select the nature of employment");
                    edt_nature.requestFocus();
                    return;
                }

                if (Utilities.isNetworkAvailable(context)) {
                    new GetSubCategotyList().execute(categoryId, "1", "2");
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }

            }
        });

        tv_countrycode_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContryCodeDialog(countryCodeList, "1");
            }
        });

        tv_countrycode_landline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContryCodeDialog(countryCodeList, "2");
            }
        });

        ib_add_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.layout_add_mobile3, null);
                LinearLayout ll = (LinearLayout) rowView;
                mobileLayoutsList.add(ll);
                ll_mobile.addView(rowView, ll_mobile.getChildCount() - 1);
            }
        });

        ib_add_landline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.layout_add_landline3, null);
                LinearLayout ll = (LinearLayout) rowView;
                landlineLayoutsList.add(ll);
                ll_landline.addView(rowView, ll_landline.getChildCount() - 1);
            }
        });

        edt_select_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, PickMapLoaction_Activity.class);
//                startActivityForResult(intent, 10001);

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(EditEmployee_Activity.this), 10001);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });


        btn_add_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edt_tag.getText().toString().trim().isEmpty()) {
                    edt_tag.setError("Please enter tag");
                    edt_tag.requestFocus();
                    return;
                }

                boolean isTagSelected = false;

                for (TagsListModel tagObj : tagsListTobeSubmitted) {

                    if (tagObj.getTag_name().equalsIgnoreCase(edt_tag.getText().toString().trim())) {
                        isTagSelected = true;
                        break;

                    }

                }

                if (!isTagSelected) {

                    boolean isTagPresent = false;

                    for (GetTagsListModel.ResultBean tagObj : tagsListFromAPI) {
                        if (tagObj.getTag_name().equalsIgnoreCase(edt_tag.getText().toString().trim())) {

                            tagsListTobeSubmitted.add(new TagsListModel(tagObj.getTagid(), tagObj.getTag_name(), tagObj.getIs_approved()));

                            isTagPresent = true;

                            break;
                        }
                    }

                    if (!isTagPresent) {
                        tagsListTobeSubmitted.add(new TagsListModel("0", edt_tag.getText().toString().trim(), "0"));
                    }

                    tag_container.addTag(edt_tag.getText().toString().trim());
                } else {
                    Utilities.showMessage("Tag aleady added", context, 2);
                }


                edt_tag.setText("");

            }
        });

        tag_container.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {

            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onSelectedTagDrag(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {
                if (position < tag_container.getChildCount()) {
                    tag_container.removeTag(position);
                    tagsListTobeSubmitted.remove(position);
                }
            }
        });

        edt_tag.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                GetTagsListModel.ResultBean tagObj = (GetTagsListModel.ResultBean) arg0.getAdapter().getItem(arg2);


                boolean isTagSelected = false;

                for (TagsListModel tagObj1 : tagsListTobeSubmitted) {
                    if (tagObj1.getTag_name().equalsIgnoreCase(edt_tag.getText().toString().trim())) {
                        isTagSelected = true;
                        break;

                    }
                }

                if (!isTagSelected) {
                    tagsListTobeSubmitted.add(new TagsListModel(tagObj.getTagid(), tagObj.getTag_name(), tagObj.getIs_approved()));
                    tag_container.addTag(edt_tag.getText().toString().trim());
                } else {
                    Utilities.showMessage("Tag aleady added", context, 2);
                }

                edt_tag.setText("");
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

    public void removeMobileLayoutEmp(View v) {
        ll_mobile.removeView((View) v.getParent());
        mobileLayoutsList.remove(v.getParent());
    }

    public void removeLandlineLayoutEmp(View view) {
        ll_landline.removeView((View) view.getParent());
        landlineLayoutsList.remove(view.getParent());
    }

    public static void selectContryCode(View v) {
        tv_selected_forconcode = (TextView) v;
        showContryCodeForSelectedDialog(countryCodeList);
    }

    private class GetTagsList extends AsyncTask<String, Void, String> {

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
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getTags");
            obj.addProperty("category_type_id", params[0]);
            res = APICall.JSONAPICall(ApplicationConstants.TAGSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    tagsListFromAPI = new ArrayList<>();
                    GetTagsListModel pojoDetails = new Gson().fromJson(result, GetTagsListModel.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        tagsListFromAPI = pojoDetails.getResult();

                        ArrayAdapter<GetTagsListModel.ResultBean> adapter = new ArrayAdapter<GetTagsListModel.ResultBean>(
                                context, R.layout.list_row, tagsListFromAPI);
                        edt_tag.setAdapter(adapter);

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

    private class GetCategotyList extends AsyncTask<String, Void, String> {

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
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getcategory");
            obj.addProperty("parent_id", params[0]);
            obj.addProperty("level", params[1]);
            obj.addProperty("category_type_id", params[2]);
            res = APICall.JSONAPICall(ApplicationConstants.CATEGORYAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    categotyList = new ArrayList<>();
                    CategotyListPojo pojoDetails = new Gson().fromJson(result, CategotyListPojo.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        categotyList = pojoDetails.getResult();
                        if (categotyList.size() > 0) {
                            showCategoryListDialog();
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

    private void showCategoryListDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Nature of Employment");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < categotyList.size(); i++) {
            arrayAdapter.add(String.valueOf(categotyList.get(i).getName()));
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
                CategotyListModel categoty = categotyList.get(which);
                edt_nature.setText(categoty.getName());
                categoryId = categoty.getId();
                edt_subtype.setText("");
            }
        });
        builderSingle.show();
    }

    public class GetSubCategotyList extends AsyncTask<String, Void, String> {

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
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getcategory");
            obj.addProperty("parent_id", params[0]);
            obj.addProperty("level", params[1]);
            obj.addProperty("category_type_id", params[2]);
            res = APICall.JSONAPICall(ApplicationConstants.CATEGORYAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    SubCategotyListPojo pojoDetails = new Gson().fromJson(result, SubCategotyListPojo.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        ArrayList<SubCategotyListModel> subCategoryList = pojoDetails.getResult();
                        if (subCategoryList.size() > 0) {
                            showSubCategoryListDialog(subCategoryList);
                        }
                    } else {
                        Utilities.showAlertDialog(context, "Subtype not available", false);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showAlertDialog(context, "Server Not Responding", false);
            }
        }
    }

    private void showSubCategoryListDialog(final ArrayList<SubCategotyListModel> subCategoryList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Subtype");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < subCategoryList.size(); i++) {
            arrayAdapter.add(String.valueOf(subCategoryList.get(i).getName()));
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
                SubCategotyListModel subCategoty = subCategoryList.get(which);
                edt_subtype.setText(subCategoty.getName());
                subCategoryId = subCategoty.getId();
            }
        });
        builderSingle.show();
    }

    private void showContryCodeDialog(final ArrayList<ContryCodeModel> contryCodeList, final String type) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Country Code");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < contryCodeList.size(); i++) {
            arrayAdapter.add(String.valueOf(contryCodeList.get(i).getName()));
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
                ContryCodeModel countryCode = contryCodeList.get(which);

                if (type.equals("1")) {
                    tv_countrycode_mobile.setText(countryCode.getDial_code());
                } else if (type.equals("2")) {
                    tv_countrycode_landline.setText(countryCode.getDial_code());
                }
            }
        });
        builderSingle.show();
    }

    private static void showContryCodeForSelectedDialog(final ArrayList<ContryCodeModel> contryCodeList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Country Code");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < contryCodeList.size(); i++) {
            arrayAdapter.add(String.valueOf(contryCodeList.get(i).getName()));
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
                ContryCodeModel countryCode = contryCodeList.get(which);
                tv_selected_forconcode.setText(countryCode.getDial_code());
            }
        });
        builderSingle.show();
    }

    private void submitData() {
        mobileList = new ArrayList<>();
        landlineList = new ArrayList<>();
        mobileJSONArray = new JsonArray();
        landlineJSONArray = new JsonArray();
        tagJSONArray = new JsonArray();

        if (edt_name.getText().toString().trim().isEmpty()) {
            edt_name.setError("Please enter organization name");
            edt_name.requestFocus();
            return;
        }

        if (edt_nature.getText().toString().trim().isEmpty()) {
            edt_nature.setError("Please select the nature of employment");
            edt_nature.requestFocus();
            return;
        }

//        if (edt_subtype.getText().toString().trim().isEmpty()) {
//            edt_subtype.setError("Please select subtype");
//            edt_subtype.requestFocus();
//            return;
//        }

        if (edt_designation.getText().toString().trim().isEmpty()) {
            edt_designation.setError("Please enter designation");
            edt_designation.requestFocus();
            return;
        }

        for (int i = 0; i < mobileLayoutsList.size(); i++) {
            if (!((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim().isEmpty()) {
                if (!Utilities.isValidMobileno(((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim())) {
                    ((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).setError("Please enter mobile number");
                    (mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).requestFocus();
                    return;
                }
            }
        }

        if (!edt_mobile.getText().toString().trim().isEmpty()) {
            if (!Utilities.isValidMobileno(edt_mobile.getText().toString().trim())) {
                edt_mobile.setError("Please enter valid mobile number");
                edt_mobile.requestFocus();
                return;
            }
        }

        for (int i = 0; i < landlineLayoutsList.size(); i++) {
            if (!((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).getText().toString().trim().isEmpty()) {
                if (!Utilities.isLandlineValid(((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).getText().toString().trim())) {
                    ((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).setError("Please enter valid landline number");
                    (landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).requestFocus();
                    return;
                }
            }
        }

        if (!edt_landline.getText().toString().trim().isEmpty()) {
            if (!Utilities.isLandlineValid(edt_landline.getText().toString().trim())) {
                edt_landline.setError("Please enter valid landline number");
                edt_landline.requestFocus();
                return;
            }
        }

        if (!edt_email.getText().toString().trim().isEmpty()) {
            if (!Utilities.isEmailValid(edt_email.getText().toString().trim())) {
                edt_email.setError("Please enter valid email");
                edt_email.requestFocus();
                return;
            }
        }

//        if (edt_select_area.getText().toString().trim().isEmpty()) {
//            edt_select_area.setError("Please select area");
//            edt_select_area.requestFocus();
//            return;
//        }

        if (!edt_pincode.getText().toString().trim().isEmpty()) {
            if (edt_pincode.getText().toString().trim().length() != 6) {
                edt_pincode.setError("Please enter pincode");
                edt_pincode.requestFocus();
                return;
            }
        }

//        if (edt_city.getText().toString().trim().isEmpty()) {
//            edt_city.setError("Please select area");
//            edt_city.requestFocus();
//            return;
//        }

        for (int i = 0; i < mobileLayoutsList.size(); i++) {
            if (!((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim().equals("")) {
                mobileList.add(((TextView) mobileLayoutsList.get(i).findViewById(R.id.tv_countrycode_mobile)).getText().toString() + "" +
                        ((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim());
            }
        }

        if (!edt_mobile.getText().toString().trim().isEmpty()) {
            mobileList.add(tv_countrycode_mobile.getText().toString() + "" + edt_mobile.getText().toString().trim());
        }

        for (int i = 0; i < landlineLayoutsList.size(); i++) {
            if (!((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).getText().toString().trim().equals("")) {
                landlineList.add(((TextView) landlineLayoutsList.get(i).findViewById(R.id.tv_countrycode_landline)).getText().toString() + "" +
                        ((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).getText().toString().trim());
            }
        }

        if (!edt_landline.getText().toString().trim().isEmpty()) {
            landlineList.add(tv_countrycode_landline.getText().toString() + "" + edt_landline.getText().toString().trim());
        }

        JsonObject mainObj = new JsonObject();

        for (int i = 0; i < mobileList.size(); i++) {
            JsonObject mobileJSONObj = new JsonObject();
            mobileJSONObj.addProperty("mobile", mobileList.get(i));
            mobileJSONArray.add(mobileJSONObj);
        }

        for (int i = 0; i < landlineList.size(); i++) {
            JsonObject landlineJSONObj = new JsonObject();
            landlineJSONObj.addProperty("landlinenumbers", landlineList.get(i));
            landlineJSONArray.add(landlineJSONObj);
        }

        for (int i = 0; i < tagsListTobeSubmitted.size(); i++) {
            JsonObject tagsJSONObj = new JsonObject();
            tagsJSONObj.addProperty("tag_id", tagsListTobeSubmitted.get(i).getTag_id());
            tagsJSONObj.addProperty("tag_name", tagsListTobeSubmitted.get(i).getTag_name());
            tagsJSONObj.addProperty("is_approved", tagsListTobeSubmitted.get(i).getIs_approved());
            tagJSONArray.add(tagsJSONObj);
        }

        mainObj.addProperty("type", "updateemployee");
        mainObj.addProperty("address", edt_address.getText().toString().trim());
        mainObj.addProperty("district", edt_district.getText().toString().trim());
        mainObj.addProperty("state", edt_state.getText().toString().trim());
        mainObj.addProperty("city", edt_city.getText().toString().trim());
        mainObj.addProperty("country", edt_country.getText().toString().trim());
        mainObj.addProperty("pincode", edt_pincode.getText().toString().trim());
        mainObj.addProperty("longitude", longitude);
        mainObj.addProperty("latitude", latitude);
        mainObj.addProperty("landmark", edt_select_area.getText().toString().trim());
        mainObj.addProperty("locality", edt_city.getText().toString().trim());
        mainObj.addProperty("email", edt_email.getText().toString().trim());
        mainObj.addProperty("designation", edt_designation.getText().toString().trim());
        mainObj.addProperty("organization_name", edt_name.getText().toString().trim());
        mainObj.addProperty("record_status_id", "0");
        mainObj.addProperty("website", edt_website.getText().toString().trim());
        mainObj.addProperty("is_active", "0");
        mainObj.addProperty("other_details", "");
        mainObj.addProperty("image_url", imageName);
//        mainObj.addProperty("type_description", edt_nature.getText().toString().trim());
//        mainObj.addProperty("subtype_description", edt_subtype.getText().toString().trim());
//        mainObj.addProperty("cat_id", "1");
        mainObj.addProperty("type_id", categoryId);
        mainObj.addProperty("sub_type_id", subCategoryId);
        mainObj.addProperty("created_by", userId);
        mainObj.addProperty("updated_by", userId);
        mainObj.addProperty("emp_id", searchDetails.getId());
        mainObj.add("mobile_number", mobileJSONArray);
        mainObj.add("landline_numbers", landlineJSONArray);
        mainObj.add("tag_name", tagJSONArray);

        Log.i("EDITEMPLOYEE", mainObj.toString());


        if (Utilities.isNetworkAvailable(context)) {
            new EditEmployee().execute(mainObj.toString());
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
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(EditEmployee_Activity.this);
            }

            if (requestCode == CAMERA_REQUEST) {
                CropImage.activity(photoURI).setGuidelines(CropImageView.Guidelines.ON).start(EditEmployee_Activity.this);
            }

            if (requestCode == 10001) {
                try {
                    Place place = PlacePicker.getPlace(context, data);
                    Geocoder gcd = new Geocoder(context, Locale.getDefault());
                    List<Address> addresses = null;
                    addresses = gcd.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);

                    if (addresses.size() != 0) {

                        latitude = String.valueOf(place.getLatLng().latitude);
                        longitude = String.valueOf(place.getLatLng().longitude);
                        edt_address.setText(addresses.get(0).getAddressLine(0));
                        edt_country.setText(addresses.get(0).getCountryName());
                        edt_state.setText(addresses.get(0).getAdminArea());
                        edt_district.setText(addresses.get(0).getSubAdminArea());
                        edt_pincode.setText(addresses.get(0).getPostalCode());
                        edt_select_area.setText(addresses.get(0).getFeatureName());
                        edt_city.setText(addresses.get(0).getLocality());
                    } else {
                        Utilities.showMessage("Address not found, please try again", context, 3);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Utilities.showMessage("Address not found, please try again", context, 3);
                }
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
                + "Employee/" + "uplimg.png";

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
        new UploadImage().execute(photoFileToUpload);

    }

    private class UploadImage extends AsyncTask<File, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                        imageName = jsonObject.getString("name");

                        if (!imageUrl.equals("")) {
                            Picasso.with(context)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.icon_userphoto)
                                    .into(imv_user);

                            imv_user.setVisibility(View.VISIBLE);
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

    private class EditEmployee extends AsyncTask<String, Void, String> {

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
            res = APICall.JSONAPICall(ApplicationConstants.EMPLOYEEAPI, params[0]);
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

                        new Profile_Fragment.GetEmployee().execute();

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Employment details updated successfully");
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askPermission() {
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
        hideSoftKeyboard(EditEmployee_Activity.this);
    }


}
