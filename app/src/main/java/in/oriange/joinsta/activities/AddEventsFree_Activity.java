package in.oriange.joinsta.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.EventTypeModel;
import in.oriange.joinsta.models.MasterModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.MultipartUtility;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.Utilities.changeDateFormat;
import static in.oriange.joinsta.utilities.Utilities.hideSoftKeyboard;
import static in.oriange.joinsta.utilities.Utilities.setPaddingForView;
import static in.oriange.joinsta.utilities.Utilities.yyyyMMddDate;

public class AddEventsFree_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private MaterialEditText edt_name, edt_type, edt_description, edt_organizer_name, edt_organizer_mobile, edt_date, edt_start_time, edt_end_time, edt_select_from_map,
            edt_address, edt_city, edt_remark,
            edt_attach_doc_multi;
    private CheckBox cb_confirmation_required, cb_online_event, cb_displayto_members, cb_displayin_city;
    private RecyclerView rv_images;
    private LinearLayout ll_documents;
    private Button btn_add_document, btn_add_image;
    private int latestPosition;

    private List<EventTypeModel.ResultBean> eventTypeList;
    private ArrayList<LinearLayout> docsLayoutsList;
    private ArrayList<MasterModel> imageList;
    private String userId, groupId, eventTypeId, eventDate, latitude = "", longitude = "";
    private File photoFileFolder;
    private Uri photoURI;
    private int mYear, mMonth, mDay, startHour, startMinutes;

    private final int DOCUMENT_REQUEST = 100, CAMERA_REQUEST = 200, GALLERY_REQUEST = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_events_free);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = AddEventsFree_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        edt_name = findViewById(R.id.edt_name);
        edt_type = findViewById(R.id.edt_type);
        edt_description = findViewById(R.id.edt_description);
        edt_organizer_name = findViewById(R.id.edt_organizer_name);
        edt_organizer_mobile = findViewById(R.id.edt_organizer_mobile);
        edt_date = findViewById(R.id.edt_date);
        edt_start_time = findViewById(R.id.edt_start_time);
        edt_end_time = findViewById(R.id.edt_end_time);
        edt_select_from_map = findViewById(R.id.edt_select_from_map);
        edt_address = findViewById(R.id.edt_address);
        edt_city = findViewById(R.id.edt_city);
        edt_remark = findViewById(R.id.edt_remark);
        ll_documents = findViewById(R.id.ll_documents);
        btn_add_document = findViewById(R.id.btn_add_document);
        btn_add_image = findViewById(R.id.btn_add_image);
        cb_confirmation_required = findViewById(R.id.cb_confirmation_required);
        cb_online_event = findViewById(R.id.cb_online_event);
        cb_displayto_members = findViewById(R.id.cb_displayto_members);
        cb_displayin_city = findViewById(R.id.cb_displayin_city);
        rv_images = findViewById(R.id.rv_images);
        rv_images.setLayoutManager(new GridLayoutManager(context, 3));

        eventTypeList = new ArrayList<>();
        docsLayoutsList = new ArrayList<>();
        imageList = new ArrayList<>();

        photoFileFolder = new File(Environment.getExternalStorageDirectory() + "/Joinsta/" + "Events");
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

        Calendar calendar = Calendar.getInstance();

        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.layout_add_document, null);
        LinearLayout ll = (LinearLayout) rowView;
        docsLayoutsList.add(ll);
        ll_documents.addView(rowView, ll_documents.getChildCount());

        imageList.add(new MasterModel("", ""));
        imageList.add(new MasterModel("", ""));
        imageList.add(new MasterModel("", ""));
        rv_images.setAdapter(new ImagesAdapter());
    }

    private void setEventHandler() {
        edt_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventTypeList.size() == 0) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new GetEventTypeList().execute();
                    } else {
                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                    }
                } else {
                    new GetEventTypeList().showEventTypeListDialog();
                }
            }
        });

        edt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        eventDate = yyyyMMddDate(dayOfMonth, month + 1, year);
                        edt_date.setText(changeDateFormat("yyyy-MM-dd", "dd-MM-yyyy", eventDate));

                        mYear = year;
                        mMonth = month;
                        mDay = dayOfMonth;
                    }
                }, mYear, mMonth, mDay);
                try {

                    dialog.getDatePicker().setMinDate(System.currentTimeMillis());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.show();
            }
        });

        edt_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        edt_end_time.setText("");
                        startMinutes = selectedMinute;
                        startHour = selectedHour;
                        edt_start_time.setText(selectedHour + ":" + selectedMinute + ":00");
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Event Start Time");
                mTimePicker.show();
            }
        });

        edt_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_start_time.getText().toString().trim().isEmpty()) {
                    edt_start_time.setError("Please select start date");
                    edt_start_time.requestFocus();
                    edt_start_time.getParent().requestChildFocus(edt_start_time, edt_start_time);
                }

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        if (selectedHour < startHour) {
                            Utilities.showMessage("End time cannot be before start time", context, 2);
                            return;
                        } else if ((selectedHour == startHour)) {
                            if (selectedMinute <= startMinutes) {
                                Utilities.showMessage("End time cannot be before start time", context, 2);
                                return;
                            }
                        }

                        edt_end_time.setText(selectedHour + ":" + selectedMinute + ":00");
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Event End Time");
                mTimePicker.show();
            }
        });

        edt_select_from_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(AddEventsFree_Activity.this), 10001);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        btn_add_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.layout_add_document, null);
                LinearLayout ll = (LinearLayout) rowView;
                docsLayoutsList.add(ll);
                ll_documents.addView(rowView, ll_documents.getChildCount());
            }
        });

        btn_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageList.add(new MasterModel("", ""));
                rv_images.setAdapter(new ImagesAdapter());
            }
        });
    }

    private void submitData() {
        if (edt_name.getText().toString().trim().isEmpty()) {
            edt_name.setError("Please enter name");
            edt_name.requestFocus();
            edt_name.getParent().requestChildFocus(edt_name, edt_name);
            return;
        }

        if (edt_type.getText().toString().trim().isEmpty()) {
            edt_type.setError("Please select type");
            edt_type.requestFocus();
            edt_type.getParent().requestChildFocus(edt_type, edt_type);
            return;
        }

        if (edt_description.getText().toString().trim().isEmpty()) {
            edt_description.setError("Please enter description");
            edt_description.requestFocus();
            edt_description.getParent().requestChildFocus(edt_description, edt_description);
            return;
        }

        if (edt_organizer_name.getText().toString().trim().isEmpty()) {
            edt_organizer_name.setError("Please enter organizer name");
            edt_organizer_name.requestFocus();
            edt_organizer_name.getParent().requestChildFocus(edt_organizer_name, edt_organizer_name);
            return;
        }

        if (!Utilities.isValidMobileno(edt_organizer_mobile.getText().toString().trim())) {
            edt_organizer_mobile.setError("Please enter valid mobile number");
            edt_organizer_mobile.requestFocus();
            edt_organizer_mobile.getParent().requestChildFocus(edt_organizer_mobile, edt_organizer_mobile);
            return;
        }

        if (edt_date.getText().toString().trim().isEmpty()) {
            edt_date.setError("Please select date");
            edt_date.requestFocus();
            edt_date.getParent().requestChildFocus(edt_date, edt_date);
            return;
        }

        if (edt_start_time.getText().toString().trim().isEmpty()) {
            edt_start_time.setError("Please select start time");
            edt_start_time.requestFocus();
            edt_start_time.getParent().requestChildFocus(edt_start_time, edt_start_time);
            return;
        }

        if (edt_end_time.getText().toString().trim().isEmpty()) {
            edt_end_time.setError("Please select end time");
            edt_end_time.requestFocus();
            edt_end_time.getParent().requestChildFocus(edt_end_time, edt_end_time);
            return;
        }

        if (edt_address.getText().toString().trim().isEmpty()) {
            edt_address.setError("Please enter address");
            edt_address.requestFocus();
            edt_address.getParent().requestChildFocus(edt_address, edt_address);
            return;
        }

        if (edt_city.getText().toString().trim().isEmpty()) {
            edt_city.setError("Please enter city");
            edt_city.requestFocus();
            edt_city.getParent().requestChildFocus(edt_city, edt_city);
            return;
        }

        String is_confirmation_required = cb_confirmation_required.isChecked() ? "1" : "0";
        String is_online_event = cb_online_event.isChecked() ? "1" : "0";
        String is_displaytomembers = cb_displayto_members.isChecked() ? "1" : "0";
        String display_in_city = cb_displayin_city.isChecked() ? "1" : "0";

        JsonArray documentsArray = new JsonArray();

        for (int i = 0; i < docsLayoutsList.size(); i++) {
            if (!((EditText) docsLayoutsList.get(i).findViewById(R.id.edt_attach_doc)).getText().toString().trim().equals("")) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("document_type", "invitationdocument");
                jsonObject.addProperty("document", ((EditText) docsLayoutsList.get(i).findViewById(R.id.edt_attach_doc)).getText().toString());
                documentsArray.add(jsonObject);
            }
        }

        for (int i = 0; i < imageList.size(); i++) {
            if (!imageList.get(i).getName().equals("")) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("document_type", "invitationimage");
                jsonObject.addProperty("document", imageList.get(i).getName());
                documentsArray.add(jsonObject);
            }
        }

        JsonObject mainObj = new JsonObject();

        mainObj.addProperty("type", "CreateFreeEvent");
        mainObj.addProperty("event_type_id", eventTypeId);
        mainObj.addProperty("name", edt_name.getText().toString().trim());
        mainObj.addProperty("description", edt_description.getText().toString().trim());
        mainObj.addProperty("organizer_name", edt_organizer_name.getText().toString().trim());
        mainObj.addProperty("mobile", edt_organizer_mobile.getText().toString().trim());
        mainObj.addProperty("event_date", eventDate);
        mainObj.addProperty("event_start_time", edt_start_time.getText().toString().trim());
        mainObj.addProperty("event_end_time", edt_end_time.getText().toString().trim());
        mainObj.addProperty("venue_address", edt_address.getText().toString().trim());
        mainObj.addProperty("venue_longitude", longitude);
        mainObj.addProperty("venue_latitude", latitude);
        mainObj.addProperty("is_confirmation_required", is_confirmation_required);
        mainObj.addProperty("is_online_event", is_online_event);
        mainObj.addProperty("is_displaytomembers", is_displaytomembers);
        mainObj.addProperty("event_city", edt_city.getText().toString().trim());
        mainObj.addProperty("remark", edt_remark.getText().toString().trim());
        mainObj.addProperty("display_in_city", display_in_city);
        mainObj.addProperty("event_category_id", "1");
        mainObj.addProperty("created_by", userId);
        mainObj.addProperty("updated_by", userId);
        mainObj.addProperty("group_id", groupId);
        mainObj.addProperty("is_active", "1");
        mainObj.add("document_path", documentsArray);

        if (Utilities.isNetworkAvailable(context)) {
            new AddFreeEvent().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 3);
        }
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

    public void removeAttachDoc(View view) {
        ll_documents.removeView((View) view.getParent());
        docsLayoutsList.remove(view.getParent());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 10001) {
                try {
                    Place place = PlacePicker.getPlace(context, data);
                    Geocoder gcd = new Geocoder(context, Locale.getDefault());
                    List<Address> addresses;
                    addresses = gcd.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                    place.getAddress();
                    if (addresses.size() != 0) {

                        latitude = String.valueOf(place.getLatLng().latitude);
                        longitude = String.valueOf(place.getLatLng().longitude);
                        edt_address.setText(place.getAddress());
                        edt_city.setText(addresses.get(0).getLocality());
                    } else {
                        Utilities.showMessage("Address not found, please try again", context, 3);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Utilities.showMessage("Address not found, please try again", context, 3);
                }
            }

            if (requestCode == GALLERY_REQUEST) {
                Uri imageUri = data.getData();
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(AddEventsFree_Activity.this);
            }

            if (requestCode == CAMERA_REQUEST) {
                CropImage.activity(photoURI).setGuidelines(CropImageView.Guidelines.ON).start(AddEventsFree_Activity.this);
            }

            if (requestCode == DOCUMENT_REQUEST) {
                ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                new UploadImageAndDocument().execute("uploadEventDoc", list.get(0).getPath(), "1");
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
                + "Events/" + "uplimg.png";

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
        new UploadImageAndDocument().execute("uploadEventImage", photoFileToUpload.getPath(), "0");
    }

    private class UploadImageAndDocument extends AsyncTask<String, Integer, String> {
        private String TYPE = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            TYPE = params[2];
            StringBuilder res = new StringBuilder();
            try {
                MultipartUtility multipart = new MultipartUtility(ApplicationConstants.FILEUPLOADAPI, "UTF-8");

                multipart.addFormField("request_type", "uploadFeedFile");
                multipart.addFilePart("document", new File(params[1]));

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
                            edt_attach_doc_multi.setText(jsonObject.getString("name"));
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

    private class GetEventTypeList extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "getAllEventTypes");
            res = APICall.JSONAPICall(ApplicationConstants.EVENTSTYPEAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    eventTypeList = new ArrayList<>();
                    EventTypeModel pojoDetails = new Gson().fromJson(result, EventTypeModel.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        eventTypeList = pojoDetails.getResult();
                        if (eventTypeList.size() > 0) {
                            showEventTypeListDialog();
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

        private void showEventTypeListDialog() {
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
            builderSingle.setTitle("Select Event Type");
            builderSingle.setCancelable(false);

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

            for (int i = 0; i < eventTypeList.size(); i++) {
                arrayAdapter.add(String.valueOf(eventTypeList.get(i).getEvent_type()));
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
                    EventTypeModel.ResultBean event = eventTypeList.get(which);
                    edt_type.setText(event.getEvent_type());
                    eventTypeId = event.getId();
                }
            });
            builderSingle.show();
        }
    }

    private class AddFreeEvent extends AsyncTask<String, Void, String> {

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
            res = APICall.JSONAPICall(ApplicationConstants.FREEEVENTSAPI, params[0]);
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
                    if (type.equalsIgnoreCase("success")) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("EventsFree_Fragment"));

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Event details submitted successfully");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menus_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_save) {
            submitData();
        }
        return super.onOptionsItemSelected(item);
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
        hideSoftKeyboard(AddEventsFree_Activity.this);
    }
}
