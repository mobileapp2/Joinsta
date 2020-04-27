package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.lujun.androidtagview.TagContainerLayout;
import de.hdodenhof.circleimageview.CircleImageView;
import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.PublicOfficeEmailAdapter;
import in.oriange.joinsta.adapters.PublicOfficeFaxAdapter;
import in.oriange.joinsta.adapters.PublicOfficeLandlineAdapter;
import in.oriange.joinsta.adapters.PublicOfficeMobileAdapter;
import in.oriange.joinsta.models.ContryCodeModel;
import in.oriange.joinsta.models.PublicOfficeModel;
import in.oriange.joinsta.models.PublicRatingAndReviewModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static android.Manifest.permission.CALL_PHONE;
import static android.view.View.GONE;
import static in.oriange.joinsta.utilities.ApplicationConstants.IMAGE_LINK;
import static in.oriange.joinsta.utilities.Utilities.loadJSONForCountryCode;
import static in.oriange.joinsta.utilities.Utilities.provideCallPremission;

public class ViewPublicOffice_Activity extends AppCompatActivity {

    @BindView(R.id.ll_nopreview)
    LinearLayout llNopreview;
    @BindView(R.id.imv_image)
    ImageView imvImage;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.anim_toolbar)
    Toolbar animToolbar;
    @BindView(R.id.imb_edit)
    ImageButton imbEdit;
    @BindView(R.id.imb_delete)
    ImageButton imbDelete;
    @BindView(R.id.imb_back)
    ImageButton imbBack;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.ll_direction)
    LinearLayout llDirection;
    @BindView(R.id.ll_mobile)
    LinearLayout llMobile;
    @BindView(R.id.ll_landline)
    LinearLayout llLandline;
    @BindView(R.id.ll_email)
    LinearLayout llEmail;
    @BindView(R.id.tv_total_rating)
    TextView tvTotalRating;
    @BindView(R.id.rb_feedback_stars)
    RatingBar rbFeedbackStars;
    @BindView(R.id.tv_total_reviews)
    TextView tvTotalReviews;
    @BindView(R.id.rl_rating)
    RelativeLayout rlRating;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_name_local_language)
    TextView tvNameLocalLanguage;
    @BindView(R.id.tv_function_of_office)
    ReadMoreTextView tvFunctionOfOffice;
    @BindView(R.id.cv_function_of_office)
    CardView cvFunctionOfOffice;
    @BindView(R.id.tv_other_information)
    ReadMoreTextView tvOtherInformation;
    @BindView(R.id.tv_website)
    TextView tvWebsite;
    @BindView(R.id.cv_other_information)
    CardView cvOtherInformation;
    @BindView(R.id.rv_mobile)
    RecyclerView rvMobile;
    @BindView(R.id.cv_mobile)
    CardView cvMobile;
    @BindView(R.id.rv_landline)
    RecyclerView rvLandline;
    @BindView(R.id.cv_landline)
    CardView cvLandline;
    @BindView(R.id.rv_email)
    RecyclerView rvEmail;
    @BindView(R.id.cv_email)
    CardView cvEmail;
    @BindView(R.id.rv_fax)
    RecyclerView rvFax;
    @BindView(R.id.cv_fax)
    CardView cvFax;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.cv_address)
    CardView cvAddress;
    @BindView(R.id.cv_report_issue)
    CardView cvReportIssue;
    @BindView(R.id.rb_post_rating)
    RatingBar rbPostRating;
    @BindView(R.id.cv_post_review)
    CardView cvPostReview;
    @BindView(R.id.cv_assign_to_other_user)
    CardView cvAssignToOtherUser;
    @BindView(R.id.tv_first_to_rate)
    TextView tvFirstToRate;
    @BindView(R.id.tv_report_issue)
    TextView tvReportIssue;
    @BindView(R.id.tv_type_subtype)
    TextView tvTypeSubtype;
    @BindView(R.id.container_tags)
    TagContainerLayout containerTags;
    @BindView(R.id.cv_tabs)
    CardView cvTabs;
    @BindView(R.id.imv_user)
    CircleImageView imvUser;
    @BindView(R.id.tv_creater_name)
    TextView tvCreaterName;
    @BindView(R.id.tv_creater_mobile)
    TextView tvCreaterMobile;
    @BindView(R.id.btn_approve)
    Button btnApprove;
    @BindView(R.id.btn_reject)
    Button btnReject;
    @BindView(R.id.cv_created_by)
    CardView cvCreatedBy;

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private String userId;
    private ArrayList<ContryCodeModel> countryCodeList;
    private PublicOfficeModel.ResultBean publicOfficeDetails;
    private AlertDialog countryCodeDialog;
    private TextView tv_countrycode;
    private int callType;                       //    1 == My Public Office,  2 == Location-wise Public Office  3 == Approval Request
    private AlertDialog assignToOtherDialog;
    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_public_office);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = ViewPublicOffice_Activity.this;
        session = new UserSessionManager(context);

        pd = new ProgressDialog(context, R.style.CustomDialogTheme);
        pd.setMessage("Please wait ...");
        pd.setCancelable(false);

        rvMobile.setLayoutManager(new LinearLayoutManager(context));
        rvLandline.setLayoutManager(new LinearLayoutManager(context));
        rvEmail.setLayoutManager(new LinearLayoutManager(context));
        rvFax.setLayoutManager(new LinearLayoutManager(context));

        countryCodeList = new ArrayList<>();
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

        publicOfficeDetails = (PublicOfficeModel.ResultBean) getIntent().getSerializableExtra("publicOfficeDetails");
        callType = getIntent().getIntExtra("callType", 2);

        List<PublicOfficeModel.ResultBean.ImageUrlBean> imagesList = publicOfficeDetails.getImage_url();

        if (imagesList != null)
            if (imagesList.size() != 0)
                if (!imagesList.get(0).getImages().equals("")) {
                    String url = IMAGE_LINK + "office/image/" + imagesList.get(0).getImages();
                    Picasso.with(context)
                            .load(url)
                            .into(imvImage, new Callback() {
                                @Override
                                public void onSuccess() {
                                    llNopreview.setVisibility(GONE);
                                    imvImage.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(GONE);
                                }

                                @Override
                                public void onError() {
                                    imageNotAvailable();
                                }
                            });
                } else
                    imageNotAvailable();
            else
                imageNotAvailable();
        else
            imageNotAvailable();

        if (!publicOfficeDetails.getName().trim().equals(""))
            tvName.setText(publicOfficeDetails.getName());
        else
            tvName.setVisibility(GONE);

        if (!publicOfficeDetails.getLocal_name().trim().equals(""))
            tvNameLocalLanguage.setText(publicOfficeDetails.getLocal_name());
        else
            tvNameLocalLanguage.setVisibility(GONE);

        if (!publicOfficeDetails.getTypeSubType().equals(""))
            tvTypeSubtype.setText(publicOfficeDetails.getTypeSubType());
        else
            tvTypeSubtype.setVisibility(GONE);

        if (!publicOfficeDetails.getDepartment_functions().trim().equals(""))
            tvFunctionOfOffice.setText(publicOfficeDetails.getDepartment_functions());
        else
            cvFunctionOfOffice.setVisibility(GONE);

        if (!publicOfficeDetails.getOther_info().trim().equals(""))
            tvOtherInformation.setText(publicOfficeDetails.getOther_info());
        else
            cvOtherInformation.setVisibility(GONE);

        if (!publicOfficeDetails.getWebsite().trim().equals(""))
            tvWebsite.setText(publicOfficeDetails.getWebsite());
        else
            tvWebsite.setVisibility(GONE);

        List<PublicOfficeModel.ResultBean.TagsBean> tagsList = publicOfficeDetails.getTags();

        if (tagsList != null)
            if (tagsList.size() > 0)
                for (int i = 0; i < tagsList.size(); i++) {
                    if (!tagsList.get(i).getTag_name().trim().equals("")) {
                        containerTags.addTag(tagsList.get(i).getTag_name());
                    }
                }
            else
                cvTabs.setVisibility(GONE);
        else
            cvTabs.setVisibility(GONE);

        List<PublicOfficeModel.ResultBean.MobileNumberBean> mobileList = publicOfficeDetails.getMobile_number();

        if (mobileList != null)
            if (mobileList.size() != 0)
                rvMobile.setAdapter(new PublicOfficeMobileAdapter(context, mobileList));
            else
                cvMobile.setVisibility(GONE);
        else
            cvMobile.setVisibility(GONE);

        List<PublicOfficeModel.ResultBean.LandlineNumberBean> landlineList = publicOfficeDetails.getLandline_number();

        if (landlineList != null)
            if (landlineList.size() != 0)
                rvLandline.setAdapter(new PublicOfficeLandlineAdapter(context, landlineList));
            else
                cvLandline.setVisibility(GONE);
        else
            cvLandline.setVisibility(GONE);

        List<PublicOfficeModel.ResultBean.EmailsBean> emailList = publicOfficeDetails.getEmails();

        if (emailList != null)
            if (emailList.size() != 0)
                rvEmail.setAdapter(new PublicOfficeEmailAdapter(context, emailList));
            else
                cvEmail.setVisibility(GONE);
        else
            cvEmail.setVisibility(GONE);

        List<PublicOfficeModel.ResultBean.FaxBean> faxList = publicOfficeDetails.getFax();

        if (faxList != null)
            if (faxList.size() != 0)
                rvFax.setAdapter(new PublicOfficeFaxAdapter(context, faxList));
            else
                cvFax.setVisibility(GONE);
        else
            cvFax.setVisibility(GONE);

        if (!publicOfficeDetails.getAddress().trim().equals(""))
            tvAddress.setText(publicOfficeDetails.getAddress());
        else
            cvAddress.setVisibility(GONE);

        if (callType == 1) {
            tvReportIssue.setText("View Reported Issues");
            cvPostReview.setVisibility(GONE);
            cvCreatedBy.setVisibility(GONE);
        } else if (callType == 2) {
            tvReportIssue.setText("Reporte Issue");
            imbEdit.setVisibility(View.INVISIBLE);
            imbDelete.setVisibility(View.INVISIBLE);
            cvAssignToOtherUser.setVisibility(GONE);
            cvCreatedBy.setVisibility(GONE);

            if (!publicOfficeDetails.getRating_by_user().equals("0")) {
                cvPostReview.setVisibility(GONE);
            }
        } else if (callType == 3) {
            cvReportIssue.setVisibility(GONE);
            cvAssignToOtherUser.setVisibility(GONE);
            cvPostReview.setVisibility(GONE);

            PublicOfficeModel.ResultBean.SenderDetailsBean senderDetails = publicOfficeDetails.getSender_details().get(0);

            if (!senderDetails.getImage_url().trim().isEmpty()) {
                String url = IMAGE_LINK + "" + senderDetails.getSender_id() + "/" + senderDetails.getImage_url();
                Picasso.with(context)
                        .load(url)
                        .placeholder(R.drawable.icon_user)
                        .resize(250, 250)
                        .centerCrop()
                        .into(imvUser);
            } else {
                imvUser.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_user));
            }

            tvCreaterName.setText(senderDetails.getFirst_name());
            tvCreaterMobile.setText(senderDetails.getCountry_code() + senderDetails.getMobile());
        }

        if (publicOfficeDetails.getTotal_number_review().equals("0")) {
            rlRating.setVisibility(GONE);
        } else {
            rlRating.setVisibility(View.VISIBLE);
            float averageRating = Float.parseFloat(publicOfficeDetails.getAvg_rating());
            averageRating = Float.parseFloat(new DecimalFormat("#.#").format(averageRating));

            tvTotalRating.setText(String.valueOf(averageRating));
            tvTotalReviews.setText("(" + publicOfficeDetails.getTotal_number_review() + ")");
            rbFeedbackStars.setRating(averageRating);
        }


        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("ViewPublicOffice_Activity");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setEventHandler() {
        imbBack.setOnClickListener(v -> finish());

        imbEdit.setOnClickListener(v -> startActivity(new Intent(context, EditPublicOffice_Activity.class)
                .putExtra("publicOfficeDetails", publicOfficeDetails)));

        imbDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
            builder.setMessage("Are you sure you want to delete this item?");
            builder.setTitle("Alert");
            builder.setIcon(R.drawable.icon_alertred);
            builder.setCancelable(false);
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    new DeletePublicOffice().execute();
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertD = builder.create();
            alertD.show();
        });

        llDirection.setOnClickListener(v -> {
            if (publicOfficeDetails.getLatitude().trim().isEmpty() || publicOfficeDetails.getLongitude().trim().isEmpty()) {
                Utilities.showMessage("Location not added", context, 2);
                return;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=&daddr=" + publicOfficeDetails.getLatitude() + "," + publicOfficeDetails.getLongitude()));
            startActivity(intent);
        });

        llMobile.setOnClickListener(v -> {

            List<PublicOfficeModel.ResultBean.MobileNumberBean> mobileList = publicOfficeDetails.getMobile_number();
            if (mobileList != null)
                if (mobileList.size() > 0) {
                    if (ActivityCompat.checkSelfPermission(context, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        provideCallPremission(context);
                    } else {
                        showMobileListDialog(mobileList);
                    }
                } else
                    Utilities.showMessage("Mobile number not added", context, 2);
            else
                Utilities.showMessage("Mobile number not added", context, 2);

        });

        llLandline.setOnClickListener(v -> {
            List<PublicOfficeModel.ResultBean.LandlineNumberBean> landlineList = publicOfficeDetails.getLandline_number();
            if (landlineList != null)
                if (landlineList.size() > 0) {
                    if (ActivityCompat.checkSelfPermission(context, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        provideCallPremission(context);
                    } else {
                        showLandlineListDialog(landlineList);
                    }
                } else
                    Utilities.showMessage("Landline number not added", context, 2);
            else
                Utilities.showMessage("Landline number not added", context, 2);
        });

        llEmail.setOnClickListener(v -> {
            List<PublicOfficeModel.ResultBean.EmailsBean> emailList = publicOfficeDetails.getEmails();
            if (emailList != null)
                if (emailList.size() > 0) {
                    showEmailListDialog(emailList);
                } else
                    Utilities.showMessage("Email not added", context, 2);
            else
                Utilities.showMessage("Email not added", context, 2);
        });

        tvWebsite.setOnClickListener(v -> {

            String url = publicOfficeDetails.getWebsite();

            if (!url.startsWith("https://") || !url.startsWith("http://")) {
                url = "http://" + url;
            }
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        rlRating.setOnClickListener(v -> {
            if (Utilities.isNetworkAvailable(context))
                new GetRatingsAndReviews().execute(publicOfficeDetails.getId());
            else
                Utilities.showMessage("Please check your internet connection", context, 2);
        });

        rbPostRating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (rating == 0)
                return;

            startActivity(new Intent(context, AddPublicOfficeRatingAndReview_Activity.class)
                    .putExtra("officeId", publicOfficeDetails.getId())
                    .putExtra("officeName", publicOfficeDetails.getName())
                    .putExtra("rating", (int) rbPostRating.getRating()));
        });

        cvAssignToOtherUser.setOnClickListener(v -> {
            if (Utilities.isNetworkAvailable(context)) {
                new CheckOfficeRequest().execute();
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        });

        cvReportIssue.setOnClickListener(v -> {
            if (callType == 1) {
                startActivity(new Intent(context, PublicOfficeReportedIssues_Activity.class)
                        .putExtra("officeId", publicOfficeDetails.getId()));
            } else if (callType == 2) {
                startActivity(new Intent(context, AddPublicOfficeReportIssue_Activity.class)
                        .putExtra("officeId", publicOfficeDetails.getId()));
            }
        });

        tvCreaterMobile.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(context, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                provideCallPremission(context);
                return;
            }

            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tvCreaterMobile.getText().toString().trim())));
        });

        btnApprove.setOnClickListener(v -> {
            if (Utilities.isNetworkAvailable(context))
                new PublicOfficeRequestAction().execute(userId, "1", publicOfficeDetails.getId());
            else
                Utilities.showMessage("Please check your internet connection", context, 2);
        });

        btnReject.setOnClickListener(v -> {
            if (Utilities.isNetworkAvailable(context))
                new PublicOfficeRequestAction().execute(userId, "2", publicOfficeDetails.getId());
            else
                Utilities.showMessage("Please check your internet connection", context, 2);
        });
    }

    private void showMobileListDialog(List<PublicOfficeModel.ResultBean.MobileNumberBean> mobileList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select mobile number to make a call");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < mobileList.size(); i++) {
            arrayAdapter.add(mobileList.get(i).getCountry_code() + mobileList.get(i).getMobile());
        }

        builderSingle.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> startActivity(new Intent(Intent.ACTION_CALL,
                Uri.parse("tel:" + mobileList.get(which).getCountry_code() + mobileList.get(which).getMobile()))));
        builderSingle.show();
    }

    private void showLandlineListDialog(List<PublicOfficeModel.ResultBean.LandlineNumberBean> landlineList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select landline number to make a call");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < landlineList.size(); i++) {
            arrayAdapter.add(landlineList.get(i).getCountry_code() + landlineList.get(i).getLandlinenumbers());
        }

        builderSingle.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> startActivity(new Intent(Intent.ACTION_CALL,
                Uri.parse("tel:" + landlineList.get(which).getCountry_code() + landlineList.get(which).getLandlinenumbers()))));
        builderSingle.show();
    }

    private void showEmailListDialog(List<PublicOfficeModel.ResultBean.EmailsBean> emailList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select email address");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < emailList.size(); i++) {
            arrayAdapter.add(emailList.get(i).getEmail());
        }

        builderSingle.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", emailList.get(which).getEmail(), null));
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        });

        builderSingle.show();
    }

    private void imageNotAvailable() {
        imvImage.setVisibility(GONE);
        llNopreview.setVisibility(View.VISIBLE);
        progressBar.setVisibility(GONE);
    }

    private void openAssignToOtherDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_mobile, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setTitle("Assign to Other User");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setView(promptView);

        tv_countrycode = promptView.findViewById(R.id.tv_countrycode);
        final MaterialEditText edt_mobile = promptView.findViewById(R.id.edt_mobile);

        tv_countrycode.setOnClickListener(v -> {
            showCountryCodeDialog();
        });

        alertDialogBuilder.setPositiveButton("Proceed", null);
        alertDialogBuilder.setNegativeButton("Cancel", null);

        assignToOtherDialog = alertDialogBuilder.create();
        assignToOtherDialog.show();

        Button btnPositive = assignToOtherDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button btnNegative = assignToOtherDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        btnPositive.setOnClickListener(v -> {
            if (!Utilities.isValidMobileno(edt_mobile.getText().toString().trim())) {
                edt_mobile.setError("Please enter valid mobile number");
                return;
            }

            JsonObject mainObj = new JsonObject();
            mainObj.addProperty("type", "sendPublicOfficeRequest");
            mainObj.addProperty("current_user_id", userId);
            mainObj.addProperty("mobile", edt_mobile.getText().toString().trim());
            mainObj.addProperty("country_code", tv_countrycode.getText().toString().trim().replace("+", ""));
            mainObj.addProperty("office_id", publicOfficeDetails.getId());

            if (Utilities.isNetworkAvailable(context)) {
                new AssignPublicOfficeToOther().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        });
        btnNegative.setOnClickListener(v -> assignToOtherDialog.dismiss());

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
                    rv_country.setVisibility(GONE);
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

        CountryCodeAdapter(ArrayList<ContryCodeModel> countryCodeList) {
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
                tv_countrycode.setText(countryCodeList.get(position).getDial_code());
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

    private class DeletePublicOffice extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "deleteoffice");
            obj.addProperty("office_id", publicOfficeDetails.getId());
            res = APICall.JSONAPICall(ApplicationConstants.OFFICEAPI, obj.toString());
            return res;
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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("PublicOfficeApprovalRequestList_Activity"));
                        Utilities.showMessage("Public office deleted successfully", context, 1);
                        finish();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class GetRatingsAndReviews extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "getOfficeRating");
            obj.addProperty("office_id", params[0]);
            res = APICall.JSONAPICall(ApplicationConstants.OFFICERATINGANDREVIEWAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "";
            try {
                if (!result.equals("")) {
                    PublicRatingAndReviewModel pojoDetails = new Gson().fromJson(result, PublicRatingAndReviewModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        startActivity(new Intent(context, PublicOfficeRatingAndReviewList_Activity.class)
                                .putExtra("officeId", publicOfficeDetails.getId())
                                .putExtra("officeName", publicOfficeDetails.getName())
                                .putExtra("reviewResult", result));
                    } else {
                        Utilities.showAlertDialog(context, "Ratings and reviews not available", false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showAlertDialog(context, "Ratings and reviews not available", false);
            }
        }
    }

    private class CheckOfficeRequest extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "checkOfficeRequest");
            obj.addProperty("office_id", publicOfficeDetails.getId());
            res = APICall.JSONAPICall(ApplicationConstants.OFFICEAPI, obj.toString());
            return res;
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
                        JSONArray jsonArray = mainObj.getJSONArray("result");
                        if (jsonArray.length() > 0) {
                            String name = jsonArray.getJSONObject(0).getString("first_name");
                            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                            builder.setTitle("Alert");
                            builder.setMessage("You have already sent an assign request to " + name + ", Do you want to transfer this request to other user?");
                            builder.setCancelable(false);
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    openAssignToOtherDialog();
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.create().show();

                        } else {
                            openAssignToOtherDialog();
                        }
                    } else {
                        openAssignToOtherDialog();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class AssignPublicOfficeToOther extends AsyncTask<String, Void, String> {

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
                        assignToOtherDialog.dismiss();
                        Utilities.showAlertDialog(context, message, true);
                    } else {
                        Utilities.showAlertDialog(context, message, false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class PublicOfficeRequestAction extends AsyncTask<String, Void, String> {

        private ProgressDialog pd;
        private String Type;

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
            String res;
            Type = params[1];
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "approvedRequest");
            obj.addProperty("user_id", params[0]);
            obj.addProperty("is_approved", params[1]);
            obj.addProperty("office_id", params[2]);
            res = APICall.JSONAPICall(ApplicationConstants.OFFICEAPI, obj.toString());
            return res;
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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("PublicOfficeApprovalRequestList_Activity"));

                        if (Type.equals("1")) {
                            Utilities.showMessage("Public office approved", context, 1);
                        } else if (Type.equals("2")) {
                            Utilities.showMessage("Public office rejected", context, 1);
                        }
                        finish();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setUpToolbar() {
        setSupportActionBar(animToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        animToolbar.setNavigationIcon(R.drawable.icon_backarrow);
        animToolbar.setNavigationOnClickListener(view -> finish());
        collapsingToolbar.setTitle("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        rbPostRating.setRating(0);
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
