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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
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
import java.util.regex.Matcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import in.oriange.joinsta.R;
import in.oriange.joinsta.models.ContryCodeModel;
import in.oriange.joinsta.models.GetTagsListModel;
import in.oriange.joinsta.models.MapAddressListModel;
import in.oriange.joinsta.models.MasterModel;
import in.oriange.joinsta.models.PublicOfficeModel;
import in.oriange.joinsta.models.PublicOfficeSubTypeModel;
import in.oriange.joinsta.models.PublicOfficeTypeModel;
import in.oriange.joinsta.models.TagsListModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.MultipartUtility;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.ApplicationConstants.IMAGE_LINK;
import static in.oriange.joinsta.utilities.PermissionUtil.PERMISSION_ALL;
import static in.oriange.joinsta.utilities.Utilities.loadJSONForCountryCode;
import static in.oriange.joinsta.utilities.Utilities.setPaddingForView;

public class EditPublicOffice_Activity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edt_name)
    MaterialEditText edtName;
    @BindView(R.id.edt_name_local_language)
    MaterialEditText edtNameLocalLanguage;
    @BindView(R.id.edt_type)
    MaterialEditText edtType;
    @BindView(R.id.edt_subtype)
    MaterialEditText edtSubtype;
    @BindView(R.id.tiedt_function_of_office)
    TextInputEditText tiedtFunctionOfOffice;
    @BindView(R.id.tiedt_other_information)
    TextInputEditText tiedtOtherInformation;
    @BindView(R.id.ll_attach_mobile)
    LinearLayout llAttachMobile;
    @BindView(R.id.btn_add_mobile)
    Button btnAddMobile;
    @BindView(R.id.ll_attach_landline)
    LinearLayout llAttachLandline;
    @BindView(R.id.btn_add_landline)
    Button btnAddLandline;
    @BindView(R.id.ll_attach_email)
    LinearLayout llAttachEmail;
    @BindView(R.id.btn_add_email)
    Button btnAddEmail;
    @BindView(R.id.ll_attach_fax)
    LinearLayout llAttachFax;
    @BindView(R.id.btn_add_fax)
    Button btnAddFax;
    @BindView(R.id.edt_website)
    MaterialEditText edtWebsite;
    @BindView(R.id.edt_select_area)
    MaterialEditText edtSelectArea;
    @BindView(R.id.edt_address)
    MaterialEditText edtAddress;
    @BindView(R.id.edt_pincode)
    MaterialEditText edtPincode;
    @BindView(R.id.edt_city)
    MaterialEditText edtCity;
    @BindView(R.id.edt_district)
    MaterialEditText edtDistrict;
    @BindView(R.id.edt_state)
    MaterialEditText edtState;
    @BindView(R.id.edt_country)
    MaterialEditText edtCountry;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.edt_tag)
    AutoCompleteTextView edtTag;
    @BindView(R.id.btn_add_tag)
    Button btnAddTag;
    @BindView(R.id.tag_container)
    TagContainerLayout tagContainer;
    @BindView(R.id.rv_images)
    RecyclerView rvImages;
    @BindView(R.id.ll_documents)
    LinearLayout llDocuments;
    @BindView(R.id.btn_add_document)
    Button btnAddDocument;

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private MaterialEditText edt_attach_doc_multi;
    private String userId, typeId = "", subTypeId = "", imageName = "", latitude = "", longitude = "";
    private Uri photoURI;
    private File officeFileFolder;
    private final int CAMERA_REQUEST = 100, GALLERY_REQUEST = 200, LOCATION_REQUEST = 300,
            OFFICE_FUNCTION_REQUEST = 400, OTHER_INFORMATION_REQUEST = 500, DOCUMENT_REQUEST = 600;
    private ArrayList<LinearLayout> mobileLayoutsList, landlineLayoutsList, emailLayoutsList, faxLayoutsList;
    private List<PublicOfficeModel.ResultBean.MobileNumberBean> mobileList;
    private List<PublicOfficeModel.ResultBean.LandlineNumberBean> landlineList;
    private List<PublicOfficeModel.ResultBean.EmailsBean> emailList;
    private List<PublicOfficeModel.ResultBean.FaxBean> faxList;
    private List<PublicOfficeModel.ResultBean.TagsBean> tagsList;
    private List<PublicOfficeModel.ResultBean.ImageUrlBean> documentList;
    private ArrayList<MasterModel> imageList;
    private ArrayList<GetTagsListModel.ResultBean> tagsListFromAPI;
    private ArrayList<TagsListModel> tagsListTobeSubmitted;
    private ArrayList<ContryCodeModel> countryCodeList;
    private ArrayList<LinearLayout> docsLayoutsList;
    private TextView tv_selected_forconcode = null;
    private AlertDialog countryCodeDialog;
    private PublicOfficeModel.ResultBean publicOfficeDetails;
    private int latestPosition;
    private String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_public_office);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = EditPublicOffice_Activity.this;
        session = new UserSessionManager(context);

        pd = new ProgressDialog(context, R.style.CustomDialogTheme);
        pd.setMessage("Please wait ...");
        pd.setCancelable(false);

        tagsListTobeSubmitted = new ArrayList<>();
        tagsListFromAPI = new ArrayList<>();
        mobileLayoutsList = new ArrayList<>();
        landlineLayoutsList = new ArrayList<>();
        emailLayoutsList = new ArrayList<>();
        faxLayoutsList = new ArrayList<>();
        countryCodeList = new ArrayList<>();
        mobileList = new ArrayList<>();
        landlineList = new ArrayList<>();
        emailList = new ArrayList<>();
        faxList = new ArrayList<>();
        tagsList = new ArrayList<>();
        documentList = new ArrayList<>();
        imageList = new ArrayList<>();
        docsLayoutsList = new ArrayList<>();

        officeFileFolder = new File(Environment.getExternalStorageDirectory() + "/Joinsta/" + "Public Office");
        if (!officeFileFolder.exists())
            officeFileFolder.mkdirs();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

        rvImages.setLayoutManager(new GridLayoutManager(context, 3));
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
    }

    private void setDefault() {

        publicOfficeDetails = (PublicOfficeModel.ResultBean) getIntent().getSerializableExtra("publicOfficeDetails");

        if (Utilities.isNetworkAvailable(context)) {
            new GetTagsList().execute("4");
        }

        edtName.setText(publicOfficeDetails.getName());
        edtNameLocalLanguage.setText(publicOfficeDetails.getLocal_name());
        edtType.setText(publicOfficeDetails.getType());
        edtSubtype.setText(publicOfficeDetails.getSub_type_name());
        tiedtFunctionOfOffice.setText(publicOfficeDetails.getDepartment_functions());
        tiedtOtherInformation.setText(publicOfficeDetails.getOther_info());
        edtWebsite.setText(publicOfficeDetails.getWebsite());
        edtAddress.setText(publicOfficeDetails.getAddress());
        edtPincode.setText(publicOfficeDetails.getPincode());
        edtCity.setText(publicOfficeDetails.getCity());
        edtDistrict.setText(publicOfficeDetails.getDistrict());
        edtState.setText(publicOfficeDetails.getState());
        edtCountry.setText(publicOfficeDetails.getCountry());

        mobileList = publicOfficeDetails.getMobile_number();
        landlineList = publicOfficeDetails.getLandline_number();
        emailList = publicOfficeDetails.getEmails();
        faxList = publicOfficeDetails.getFax();
        tagsList = publicOfficeDetails.getTags();
        documentList = publicOfficeDetails.getImage_url();

        typeId = publicOfficeDetails.getOffice_type_id();
        subTypeId = publicOfficeDetails.getOffice_sub_type_id();
        latitude = publicOfficeDetails.getLatitude();
        longitude = publicOfficeDetails.getLongitude();

        if (mobileList.size() > 0) {
            for (int i = 0; i < mobileList.size(); i++) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.layout_add_mobile, null);
                LinearLayout ll = (LinearLayout) rowView;
                mobileLayoutsList.add(ll);
                llAttachMobile.addView(rowView, llAttachMobile.getChildCount() - 1);
                ((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).setText(mobileList.get(i).getMobile());
                ((TextView) mobileLayoutsList.get(i).findViewById(R.id.tv_countrycode_mobile)).setText(mobileList.get(i).getCountry_code());
            }
        } else {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.layout_add_mobile, null);
            LinearLayout ll = (LinearLayout) rowView;
            mobileLayoutsList.add(ll);
            llAttachMobile.addView(rowView, llAttachMobile.getChildCount());
        }

        if (landlineList.size() > 0) {
            for (int i = 0; i < landlineList.size(); i++) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.layout_add_landline, null);
                LinearLayout ll = (LinearLayout) rowView;
                landlineLayoutsList.add(ll);
                llAttachLandline.addView(rowView, llAttachLandline.getChildCount() - 1);

                ((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).setText(landlineList.get(i).getLandlinenumbers());
                ((TextView) landlineLayoutsList.get(i).findViewById(R.id.tv_countrycode_landline)).setText(landlineList.get(i).getCountry_code());
            }
        } else {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.layout_add_landline, null);
            LinearLayout ll = (LinearLayout) rowView;
            landlineLayoutsList.add(ll);
            llAttachLandline.addView(rowView, llAttachLandline.getChildCount());
        }

        if (emailList.size() > 0) {
            for (int i = 0; i < emailList.size(); i++) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.layout_add_email, null);
                LinearLayout ll = (LinearLayout) rowView;
                emailLayoutsList.add(ll);
                llAttachEmail.addView(rowView, llAttachEmail.getChildCount() - 1);

                ((EditText) emailLayoutsList.get(i).findViewById(R.id.edt_email)).setText(emailList.get(i).getEmail());
            }
        } else {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.layout_add_email, null);
            LinearLayout ll = (LinearLayout) rowView;
            emailLayoutsList.add(ll);
            llAttachEmail.addView(rowView, llAttachEmail.getChildCount());
        }

        if (faxList.size() > 0) {
            for (int i = 0; i < faxList.size(); i++) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.layout_add_fax, null);
                LinearLayout ll = (LinearLayout) rowView;
                faxLayoutsList.add(ll);
                llAttachFax.addView(rowView, llAttachFax.getChildCount() - 1);

                ((EditText) faxLayoutsList.get(i).findViewById(R.id.edt_fax)).setText(faxList.get(i).getFax());
            }
        } else {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.layout_add_fax, null);
            LinearLayout ll = (LinearLayout) rowView;
            faxLayoutsList.add(ll);
            llAttachFax.addView(rowView, llAttachFax.getChildCount());
        }

        if (tagsList.size() > 0) {
            for (int i = 0; i < tagsList.size(); i++) {
                if (!tagsList.get(i).getTag_name().trim().equals("")) {
                    tagsListTobeSubmitted.add(new TagsListModel(tagsList.get(i).getTag_id(), tagsList.get(i).getTag_name(), tagsList.get(i).getIs_approved()));
                    tagContainer.addTag(tagsList.get(i).getTag_name());
                }
            }
        }

        for (int i = 0; i < documentList.size(); i++) {
            if (documentList.get(i).getDocument_type().equals("1")) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.layout_add_document, null);
                LinearLayout ll = (LinearLayout) rowView;
                docsLayoutsList.add(ll);
                llDocuments.addView(rowView, llDocuments.getChildCount() - 1);
                ((EditText) rowView.findViewById(R.id.edt_attach_doc)).setText(documentList.get(i).getImages());
            } else if (documentList.get(i).getDocument_type().equals("2")) {
                imageList.add(new MasterModel(documentList.get(i).getImages(), IMAGE_LINK + "office/image/" + documentList.get(i).getImages()));
            }
        }

        switch (imageList.size()) {
            case 0:
                imageList.add(new MasterModel("", ""));
                imageList.add(new MasterModel("", ""));
                imageList.add(new MasterModel("", ""));
                break;
            case 1:
                imageList.add(new MasterModel("", ""));
                imageList.add(new MasterModel("", ""));
                break;
            case 2:
                imageList.add(new MasterModel("", ""));
                break;
        }

        rvImages.setAdapter(new ImagesAdapter());
    }

    private void setEventHandler() {
        edtType.setOnClickListener(v -> {
            if (Utilities.isNetworkAvailable(context)) {
                new GetOfficeTypeList().execute();
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        });

        edtSubtype.setOnClickListener(v -> {
//            if (edtType.getText().toString().trim().isEmpty()) {
//                edtType.setError("Please select office type");
//                edtType.requestFocus();
//                edtType.getParent().requestChildFocus(edtType, edtType);
//                return;
//            }

            if (Utilities.isNetworkAvailable(context)) {
                new GetOfficeSubTypeList().execute(typeId);
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        });

        btnAddMobile.setOnClickListener(v -> {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.layout_add_mobile, null);
            LinearLayout ll = (LinearLayout) rowView;
            mobileLayoutsList.add(ll);
            llAttachMobile.addView(rowView, llAttachMobile.getChildCount());
        });

        btnAddLandline.setOnClickListener(v -> {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.layout_add_landline, null);
            LinearLayout ll = (LinearLayout) rowView;
            landlineLayoutsList.add(ll);
            llAttachLandline.addView(rowView, llAttachLandline.getChildCount());
        });

        btnAddEmail.setOnClickListener(v -> {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.layout_add_email, null);
            LinearLayout ll = (LinearLayout) rowView;
            emailLayoutsList.add(ll);
            llAttachEmail.addView(rowView, llAttachEmail.getChildCount());
        });

        btnAddFax.setOnClickListener(v -> {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.layout_add_fax, null);
            LinearLayout ll = (LinearLayout) rowView;
            faxLayoutsList.add(ll);
            llAttachFax.addView(rowView, llAttachFax.getChildCount());
        });

        btnAddDocument.setOnClickListener(v -> {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.layout_add_document, null);
            LinearLayout ll = (LinearLayout) rowView;
            docsLayoutsList.add(ll);
            llDocuments.addView(rowView, llDocuments.getChildCount());
        });

        btnAddTag.setOnClickListener(v -> {

            if (edtTag.getText().toString().trim().isEmpty()) {
                edtTag.setError("Please enter tag");
                edtTag.requestFocus();
                return;
            }

            boolean isTagSelected = false;

            for (TagsListModel tagObj : tagsListTobeSubmitted) {

                if (tagObj.getTag_name().equalsIgnoreCase(edtTag.getText().toString().trim())) {
                    isTagSelected = true;
                    break;

                }

            }

            if (!isTagSelected) {

                boolean isTagPresent = false;

                for (GetTagsListModel.ResultBean tagObj : tagsListFromAPI) {
                    if (tagObj.getTag_name().equalsIgnoreCase(edtTag.getText().toString().trim())) {

                        tagsListTobeSubmitted.add(new TagsListModel(tagObj.getTagid(), tagObj.getTag_name(), tagObj.getIs_approved()));

                        isTagPresent = true;

                        break;
                    }
                }

                if (!isTagPresent) {
                    tagsListTobeSubmitted.add(new TagsListModel("0", edtTag.getText().toString().trim(), "0"));
                }

                tagContainer.addTag(edtTag.getText().toString().trim());
            } else {
                Utilities.showMessage("Tag already added", context, 2);
            }

            edtTag.setText("");

        });

        tagContainer.setOnTagClickListener(new TagView.OnTagClickListener() {
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
                if (position < tagContainer.getChildCount()) {
                    tagContainer.removeTag(position);
                    tagsListTobeSubmitted.remove(position);
                }
            }
        });

        edtTag.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
            GetTagsListModel.ResultBean tagObj = (GetTagsListModel.ResultBean) arg0.getAdapter().getItem(arg2);


            boolean isTagSelected = false;

            for (TagsListModel tagObj1 : tagsListTobeSubmitted) {
                if (tagObj1.getTag_name().equalsIgnoreCase(edtTag.getText().toString().trim())) {
                    isTagSelected = true;
                    break;

                }
            }

            if (!isTagSelected) {
                tagsListTobeSubmitted.add(new TagsListModel(tagObj.getTagid(), tagObj.getTag_name(), tagObj.getIs_approved()));
                tagContainer.addTag(edtTag.getText().toString().trim());
            } else {
                Utilities.showMessage("Tag already added", context, 2);
            }

            edtTag.setText("");
        });

        edtSelectArea.setOnClickListener(v -> startActivityForResult(new Intent(context, PickMapLoaction_Activity.class), LOCATION_REQUEST));

        tiedtFunctionOfOffice.setOnClickListener(v -> startActivityForResult(new Intent(context, FullScreenTextEdit_Activity.class)
                .putExtra("message", tiedtFunctionOfOffice.getText().toString().trim()), OFFICE_FUNCTION_REQUEST));

        tiedtOtherInformation.setOnClickListener(v -> startActivityForResult(new Intent(context, FullScreenTextEdit_Activity.class)
                .putExtra("message", tiedtOtherInformation.getText().toString().trim()), OTHER_INFORMATION_REQUEST));

        btnSave.setOnClickListener(v -> {
            submitData();
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
                    File file = new File(officeFileFolder, "doc_image.png");
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
        public void onBindViewHolder(final MyViewHolder holder, int pos) {
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
                                File file = new File(officeFileFolder, "doc_image.png");
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

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private ImageView imv_image, imv_image_delete;

            private MyViewHolder(View view) {
                super(view);
                imv_image = view.findViewById(R.id.imv_image);
                imv_image_delete = view.findViewById(R.id.imv_image_delete);

            }
        }
    }

    public void selectContryCode(View view) {
        tv_selected_forconcode = (TextView) view;
        showCountryCodeDialog();
    }

    public void removeMobileLayout(View view) {
        llAttachMobile.removeView((View) view.getParent());
        mobileLayoutsList.remove(view.getParent());
    }

    public void removeLandlineLayout(View view) {
        llAttachLandline.removeView((View) view.getParent());
        landlineLayoutsList.remove(view.getParent());
    }

    public void removeEmailLayout(View view) {
        llAttachEmail.removeView((View) view.getParent());
        emailLayoutsList.remove(view.getParent());
    }

    public void removeFaxLayout(View view) {
        llAttachFax.removeView((View) view.getParent());
        faxLayoutsList.remove(view.getParent());
    }

    public void removeAttachDoc(View view) {
        llDocuments.removeView((View) view.getParent());
        docsLayoutsList.remove(view.getParent());
    }

    public void pickAttachDoc(View view) {
        if (Utilities.isNetworkAvailable(context)) {
            edt_attach_doc_multi = (MaterialEditText) view;
            Intent intent = new Intent(context, NormalFilePickActivity.class);
            intent.putExtra(Constant.MAX_NUMBER, 1);
            intent.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"});
            startActivityForResult(intent, DOCUMENT_REQUEST);
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }

    private void submitData() {
        JsonArray mobileJsonArray = new JsonArray();
        JsonArray landlineJsonArray = new JsonArray();
        JsonArray emailJsonArray = new JsonArray();
        JsonArray faxJsonArray = new JsonArray();
        JsonArray documentsArray = new JsonArray();
        JsonArray tagJSONArray = new JsonArray();

        if (edtName.getText().toString().trim().isEmpty()) {
            edtName.setError("Please enter office name");
            edtName.requestFocus();
            edtName.getParent().requestChildFocus(edtName, edtName);
            return;
        }

        if (edtNameLocalLanguage.getText().toString().trim().isEmpty()) {
            edtNameLocalLanguage.setError("Please enter office name in local language");
            edtNameLocalLanguage.requestFocus();
            edtNameLocalLanguage.getParent().requestChildFocus(edtNameLocalLanguage, edtNameLocalLanguage);
            return;
        }

//        if (edtType.getText().toString().trim().isEmpty()) {
//            edtType.setError("Please select office type");
//            edtType.requestFocus();
//            edtType.getParent().requestChildFocus(edtType, edtType);
//            return;
//        }

        if (tiedtFunctionOfOffice.getText().toString().trim().isEmpty()) {
            tiedtFunctionOfOffice.setError("Please enter function of office");
            tiedtFunctionOfOffice.requestFocus();
            tiedtFunctionOfOffice.getParent().requestChildFocus(tiedtFunctionOfOffice, tiedtFunctionOfOffice);
            return;
        }

        if (!edtWebsite.getText().toString().trim().isEmpty()) {
            if (!Utilities.isWebsiteValid(edtWebsite.getText().toString().trim())) {
                edtWebsite.setError("Please enter valid website");
                edtWebsite.requestFocus();
                edtWebsite.getParent().requestChildFocus(edtWebsite, edtWebsite);
                return;
            }
        }

        for (int i = 0; i < mobileLayoutsList.size(); i++) {
            if (!((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim().isEmpty()) {
                if (!Utilities.isValidMobileno(((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim())) {
                    ((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).setError("Please enter valid mobile number");
                    (mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).requestFocus();
                    return;
                }
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

        for (int i = 0; i < emailLayoutsList.size(); i++) {
            if (!((EditText) emailLayoutsList.get(i).findViewById(R.id.edt_email)).getText().toString().trim().isEmpty()) {
                if (!Utilities.isEmailValid(((EditText) emailLayoutsList.get(i).findViewById(R.id.edt_email)).getText().toString().trim())) {
                    ((EditText) emailLayoutsList.get(i).findViewById(R.id.edt_email)).setError("Please enter valid email");
                    (emailLayoutsList.get(i).findViewById(R.id.edt_email)).requestFocus();
                    return;
                }
            }
        }

        for (int i = 0; i < faxLayoutsList.size(); i++) {
            if (!((EditText) faxLayoutsList.get(i).findViewById(R.id.edt_fax)).getText().toString().trim().isEmpty()) {
                if (!Utilities.isValidMobileno(((EditText) faxLayoutsList.get(i).findViewById(R.id.edt_fax)).getText().toString().trim())) {
                    ((EditText) faxLayoutsList.get(i).findViewById(R.id.edt_fax)).setError("Please enter valid fax number");
                    (faxLayoutsList.get(i).findViewById(R.id.edt_fax)).requestFocus();
                    return;
                }
            }
        }

        for (int i = 0; i < mobileLayoutsList.size(); i++) {
            if (!((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim().isEmpty()) {
                if (i <= mobileList.size() - 1) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("mobile", ((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim());
                    jsonObject.addProperty("country_code", ((TextView) mobileLayoutsList.get(i).findViewById(R.id.tv_countrycode_mobile)).getText().toString().trim().replace("+", ""));
                    jsonObject.addProperty("id", mobileList.get(i).getId());
                    mobileJsonArray.add(jsonObject);
                } else {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("mobile", ((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim());
                    jsonObject.addProperty("country_code", ((TextView) mobileLayoutsList.get(i).findViewById(R.id.tv_countrycode_mobile)).getText().toString().trim().replace("+", ""));
                    jsonObject.addProperty("id", "0");
                    mobileJsonArray.add(jsonObject);
                }
            }
        }

        for (int i = 0; i < landlineLayoutsList.size(); i++) {
            if (!((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).getText().toString().trim().isEmpty()) {
                if (i <= landlineList.size() - 1) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("landlinenumbers", ((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).getText().toString().trim());
                    jsonObject.addProperty("country_code", ((TextView) landlineLayoutsList.get(i).findViewById(R.id.tv_countrycode_landline)).getText().toString().trim().replace("+", ""));
                    jsonObject.addProperty("id", landlineList.get(i).getId());
                    landlineJsonArray.add(jsonObject);
                } else {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("landlinenumbers", ((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).getText().toString().trim());
                    jsonObject.addProperty("country_code", ((TextView) landlineLayoutsList.get(i).findViewById(R.id.tv_countrycode_landline)).getText().toString().trim().replace("+", ""));
                    jsonObject.addProperty("id", "0");
                    landlineJsonArray.add(jsonObject);
                }
            }
        }

        for (int i = 0; i < emailLayoutsList.size(); i++) {
            if (!((EditText) emailLayoutsList.get(i).findViewById(R.id.edt_email)).getText().toString().trim().isEmpty()) {
                if (i <= emailList.size() - 1) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("email", ((EditText) emailLayoutsList.get(i).findViewById(R.id.edt_email)).getText().toString().trim());
                    jsonObject.addProperty("id", emailList.get(i).getId());
                    emailJsonArray.add(jsonObject);
                } else {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("email", ((EditText) emailLayoutsList.get(i).findViewById(R.id.edt_email)).getText().toString().trim());
                    jsonObject.addProperty("id", "0");
                    emailJsonArray.add(jsonObject);
                }
            }
        }

        for (int i = 0; i < faxLayoutsList.size(); i++) {
            if (!((EditText) faxLayoutsList.get(i).findViewById(R.id.edt_fax)).getText().toString().trim().isEmpty()) {
                if (i <= faxList.size() - 1) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("faxes", ((EditText) faxLayoutsList.get(i).findViewById(R.id.edt_fax)).getText().toString().trim());
                    jsonObject.addProperty("id", faxList.get(i).getId());
                    faxJsonArray.add(jsonObject);
                } else {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("faxes", ((EditText) faxLayoutsList.get(i).findViewById(R.id.edt_fax)).getText().toString().trim());
                    jsonObject.addProperty("id", "0");
                    faxJsonArray.add(jsonObject);
                }
            }
        }

        for (int i = 0; i < tagsListTobeSubmitted.size(); i++) {
            if (i <= tagsList.size() - 1) {
                JsonObject tagsJSONObj = new JsonObject();
                tagsJSONObj.addProperty("id", tagsList.get(i).getId());
                tagsJSONObj.addProperty("tag_id", tagsListTobeSubmitted.get(i).getTag_id());
                tagsJSONObj.addProperty("tag_name", tagsListTobeSubmitted.get(i).getTag_name());
                tagsJSONObj.addProperty("is_approved", tagsListTobeSubmitted.get(i).getIs_approved());
                tagJSONArray.add(tagsJSONObj);
            } else {
                JsonObject tagsJSONObj = new JsonObject();
                tagsJSONObj.addProperty("id", "0");
                tagsJSONObj.addProperty("tag_id", tagsListTobeSubmitted.get(i).getTag_id());
                tagsJSONObj.addProperty("tag_name", tagsListTobeSubmitted.get(i).getTag_name());
                tagsJSONObj.addProperty("is_approved", tagsListTobeSubmitted.get(i).getIs_approved());
                tagJSONArray.add(tagsJSONObj);
            }

        }

        if (!edtPincode.getText().toString().trim().isEmpty()) {
            if (edtPincode.getText().toString().trim().length() != 6) {
                edtPincode.setError("Please enter pincode");
                edtPincode.requestFocus();
                edtPincode.getParent().requestChildFocus(edtPincode, edtPincode);
                return;
            }
        }

        if (edtCity.getText().toString().trim().isEmpty()) {
            edtCity.setError("Please enter city");
            edtCity.requestFocus();
            edtCity.getParent().requestChildFocus(edtCity, edtCity);
            return;
        }

        for (int i = 0; i < docsLayoutsList.size(); i++) {
            if (!((EditText) docsLayoutsList.get(i).findViewById(R.id.edt_attach_doc)).getText().toString().trim().equals("")) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("document_type", "1");
                jsonObject.addProperty("images", ((EditText) docsLayoutsList.get(i).findViewById(R.id.edt_attach_doc)).getText().toString());
                documentsArray.add(jsonObject);
            }
        }

        for (int i = 0; i < imageList.size(); i++) {
            if (!imageList.get(i).getName().equals("")) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("document_type", "2");
                jsonObject.addProperty("images", imageList.get(i).getName());
                documentsArray.add(jsonObject);
            }
        }

        JsonObject mainObj = new JsonObject();
        mainObj.addProperty("type", "updateoffice");
        mainObj.addProperty("office_id", publicOfficeDetails.getId());
        mainObj.addProperty("address", edtAddress.getText().toString().trim());
        mainObj.addProperty("name", edtName.getText().toString().trim());
        mainObj.addProperty("district", edtDistrict.getText().toString().trim());
        mainObj.addProperty("state", edtState.getText().toString().trim());
        mainObj.addProperty("city", edtCity.getText().toString().trim());
        mainObj.addProperty("country", edtCountry.getText().toString().trim());
        mainObj.addProperty("pincode", edtPincode.getText().toString().trim());
        mainObj.addProperty("local_name", edtNameLocalLanguage.getText().toString().trim());
        mainObj.addProperty("office_type_id", typeId);
        mainObj.addProperty("office_sub_type_id", subTypeId);
        mainObj.addProperty("website", edtWebsite.getText().toString().trim());
        mainObj.addProperty("latitude", latitude);
        mainObj.addProperty("longitude", longitude);
        mainObj.addProperty("department_functions", tiedtFunctionOfOffice.getText().toString().trim());
        mainObj.addProperty("other_info", tiedtOtherInformation.getText().toString().trim());
        mainObj.addProperty("created_by", publicOfficeDetails.getCreated_by());
        mainObj.addProperty("updated_by", userId);
        mainObj.add("emails", emailJsonArray);
        mainObj.add("mobile_number", mobileJsonArray);
        mainObj.add("landline_number", landlineJsonArray);
        mainObj.add("fax", faxJsonArray);
        mainObj.add("image_url", documentsArray);
        mainObj.add("tag_name", tagJSONArray);

        if (Utilities.isNetworkAvailable(context)) {
            new UpdatePublicOffice().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }

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
                        edtTag.setAdapter(adapter);

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

    private class GetOfficeTypeList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getType");
            res = APICall.JSONAPICall(ApplicationConstants.OFFICEAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    PublicOfficeTypeModel pojoDetails = new Gson().fromJson(result, PublicOfficeTypeModel.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        List<PublicOfficeTypeModel.ResultBean> officeTypeList = new ArrayList<>();
                        officeTypeList = pojoDetails.getResult();
                        if (officeTypeList.size() > 0) {
                            showOfficeTypeListDialog(officeTypeList);
                        } else {
                            Utilities.showAlertDialog(context, "Office type list not available", false);
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

    private void showOfficeTypeListDialog(List<PublicOfficeTypeModel.ResultBean> officeTypeList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Office Type");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < officeTypeList.size(); i++) {
            arrayAdapter.add(officeTypeList.get(i).getType());
        }

        builderSingle.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
            PublicOfficeTypeModel.ResultBean type = officeTypeList.get(which);
            edtType.setText(type.getType());
            typeId = type.getId();

            edtSubtype.setText("");
            subTypeId = "";
        });
        builderSingle.show();
    }

    private class GetOfficeSubTypeList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getsubtype");
            obj.addProperty("type_id", params[0]);
            res = APICall.JSONAPICall(ApplicationConstants.OFFICEAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "";
            try {
                if (!result.equals("")) {
                    PublicOfficeSubTypeModel pojoDetails = new Gson().fromJson(result, PublicOfficeSubTypeModel.class);
                    type = pojoDetails.getType();
                    pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        List<PublicOfficeSubTypeModel.ResultBean> subCategoryList = pojoDetails.getResult();
                        if (subCategoryList.size() > 0) {
                            showOfficeSubTypeListDialog(subCategoryList);
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

    private void showOfficeSubTypeListDialog(final List<PublicOfficeSubTypeModel.ResultBean> subCategoryList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Subtype");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < subCategoryList.size(); i++) {
            arrayAdapter.add(subCategoryList.get(i).getSub_type_name());
        }

        builderSingle.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
            PublicOfficeSubTypeModel.ResultBean subCategoty = subCategoryList.get(which);
            edtSubtype.setText(subCategoty.getSub_type_name());
            subTypeId = subCategoty.getType_id();
        });
        builderSingle.show();
    }

    private void showCountryCodeDialog() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_countrycodes_list, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builder.setView(view);
        builder.setTitle("Select Country");
        builder.setCancelable(false);

        final RecyclerView rv_country = view.findViewById(R.id.rv_country);
        EditText edt_search = view.findViewById(R.id.edt_search);
        rv_country.setLayoutManager(new LinearLayoutManager(context));
        rv_country.setAdapter(new CountryCodeAdapter(countryCodeList));

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {

                if (query.toString().isEmpty()) {
                    rv_country.setAdapter(new CountryCodeAdapter(countryCodeList));
                    return;
                }

                if (countryCodeList.size() == 0) {
                    rv_country.setVisibility(View.GONE);
                    return;
                }

                if (!query.toString().equals("")) {
                    ArrayList<ContryCodeModel> searchedCountryList = new ArrayList<>();
                    for (ContryCodeModel countryDetails : countryCodeList) {

                        String countryToBeSearched = countryDetails.getName().toLowerCase();

                        if (countryToBeSearched.contains(query.toString().toLowerCase())) {
                            searchedCountryList.add(countryDetails);
                        }
                    }
                    rv_country.setAdapter(new CountryCodeAdapter(searchedCountryList));
                } else {
                    rv_country.setAdapter(new CountryCodeAdapter(countryCodeList));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        countryCodeDialog = builder.create();
        countryCodeDialog.show();
    }

    private class CountryCodeAdapter extends RecyclerView.Adapter<CountryCodeAdapter.MyViewHolder> {

        private ArrayList<ContryCodeModel> countryCodeList;

        public CountryCodeAdapter(ArrayList<ContryCodeModel> countryCodeList) {
            this.countryCodeList = countryCodeList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_1, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int pos) {
            final int position = holder.getAdapterPosition();

            holder.tv_name.setText(countryCodeList.get(position).getName());

            holder.tv_name.setOnClickListener(v -> {
                tv_selected_forconcode.setText(countryCodeList.get(position).getDial_code());
                countryCodeDialog.dismiss();
            });
        }

        @Override
        public int getItemCount() {
            return countryCodeList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView tv_name;

            MyViewHolder(@NonNull View view) {
                super(view);
                tv_name = view.findViewById(R.id.tv_name);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }

    private class UpdatePublicOffice extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            res = APICall.JSONAPICall(ApplicationConstants.OFFICEAPI, params[0]);
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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("MyPublicOffice_Activity"));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("ViewPublicOffice_Activity"));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("PublicOfficeApprovalRequestList_Activity"));

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Public office details updated successfully");
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
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(EditPublicOffice_Activity.this);
            }

            if (requestCode == CAMERA_REQUEST) {
                CropImage.activity(photoURI).setGuidelines(CropImageView.Guidelines.ON).start(EditPublicOffice_Activity.this);
            }

            if (requestCode == DOCUMENT_REQUEST) {
                ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                new UploadImageAndDocument().execute("uploadOfficeDoc", list.get(0).getPath(), "1");
            }

            if (requestCode == LOCATION_REQUEST) {
                MapAddressListModel addressList = (MapAddressListModel) data.getSerializableExtra("addressList");
                if (addressList != null) {
                    latitude = addressList.getMap_location_lattitude();
                    longitude = addressList.getMap_location_logitude();
                    edtAddress.setText(addressList.getAddress_line_one());
                    edtCountry.setText(addressList.getCountry());
                    edtState.setText(addressList.getState());
                    edtDistrict.setText(addressList.getDistrict());
                    edtPincode.setText(addressList.getPincode());
                    edtSelectArea.setText(addressList.getName());
                    edtCity.setText(addressList.getDistrict());
                } else {
                    Utilities.showMessage("Address not found, please try again", context, 3);
                }
            }

            if (requestCode == OFFICE_FUNCTION_REQUEST) {
                if (!data.getStringExtra("message").trim().equals("")) {
                    tiedtFunctionOfOffice.setError(null);
                    tiedtFunctionOfOffice.setText(data.getStringExtra("message"));
                }
            }

            if (requestCode == OTHER_INFORMATION_REQUEST) {
                tiedtOtherInformation.setText(data.getStringExtra("message"));
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
                + "Public Office/" + "uplimg.png";

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
        new UploadImageAndDocument().execute("uploadOfficeImage", photoFileToUpload.getPath(), "0");
    }

    private class UploadImageAndDocument extends AsyncTask<String, Integer, String> {

        private String TYPE = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            TYPE = params[2];
            String res = "";
            try {
                MultipartUtility multipart = new MultipartUtility(ApplicationConstants.FILEUPLOADAPI, "UTF-8");

                multipart.addFormField("request_type", params[0]);
                multipart.addFormField("user_id", userId);
                multipart.addFilePart("document", new File(params[1]));

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
                        if (TYPE.equals("1")) {
                            edt_attach_doc_multi.setText(jsonObject.getString("name"));
                        } else if (TYPE.equals("0")) {
                            imageList.set(latestPosition, new MasterModel(jsonObject.getString("name"), jsonObject.getString("document_url")));
                            rvImages.setAdapter(new ImagesAdapter());
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

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.icon_backarrow);
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    /////////////////////////////////Permission Code//////////////////////////////////////////////////

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
    }

}
