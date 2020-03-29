package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.RatingAndReviewAdapter;
import in.oriange.joinsta.models.RatingAndReviewModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class RatingAndReviewList_Activity extends AppCompatActivity {

    @BindView(R.id.tv_no_review)
    AppCompatTextView tvNoReview;
    @BindView(R.id.ll_nopreview)
    LinearLayout llNopreview;
    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    @BindView(R.id.cg_category)
    ChipGroup cgCategory;
    @BindView(R.id.edt_profile_name)
    AppCompatEditText edtProfileName;
    @BindView(R.id.pb_five_star)
    RoundCornerProgressBar pbFiveStar;
    @BindView(R.id.pb_four_star)
    RoundCornerProgressBar pbFourStar;
    @BindView(R.id.pb_three_star)
    RoundCornerProgressBar pbThreeStar;
    @BindView(R.id.pb_two_star)
    RoundCornerProgressBar pbTwoStar;
    @BindView(R.id.pb_one_star)
    RoundCornerProgressBar pbOneStar;
    @BindView(R.id.tv_total_rating)
    TextView tvTotalRating;
    @BindView(R.id.rb_feedback_stars)
    RatingBar rbFeedbackStars;
    @BindView(R.id.tv_total_reviews)
    TextView tvTotalReviews;
    @BindView(R.id.chip_all)
    Chip chipAll;
    @BindView(R.id.chip_five_star)
    Chip chipFiveStar;
    @BindView(R.id.chip_four_star)
    Chip chipFourStar;
    @BindView(R.id.chip_three_star)
    Chip chipThreeStar;
    @BindView(R.id.chip_two_star)
    Chip chipTwoStar;
    @BindView(R.id.chip_one_star)
    Chip chipOneStar;
    @BindView(R.id.rv_reviews)
    RecyclerView rvReviews;

    private String userId, recordId, reviewResult, profileName, categoryTypeId;
    private List<RatingAndReviewModel.ResultBean> reviewsList;

    private int reviewType = 0;

    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_and_review_list);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = RatingAndReviewList_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);
        reviewsList = new ArrayList<>();
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
        recordId = getIntent().getStringExtra("recordId");
        profileName = getIntent().getStringExtra("profileName");
        reviewResult = getIntent().getStringExtra("reviewResult");
        categoryTypeId = getIntent().getStringExtra("categoryTypeId");
        rvReviews.setLayoutManager(new LinearLayoutManager(context));

        try {
            if (!reviewResult.equals("")) {
                RatingAndReviewModel pojoDetails = new Gson().fromJson(reviewResult, RatingAndReviewModel.class);
                String type = pojoDetails.getType();

                if (type.equalsIgnoreCase("success")) {
                    reviewsList = pojoDetails.getResult();
                    setUpRatingAndReviewRv();
                    setUpRatingAndReviewBars();
                } else {
                    Utilities.showAlertDialog(context, "Ratings and reviews not available", false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utilities.showAlertDialog(context, "Server Not Responding", false);
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("RatingAndReviewList_Activity");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setUpRatingAndReviewBars() {
        int totalNoOfReviews = reviewsList.size();

        int totalStars = 0, fiveStars = 0, fourStars = 0, threeStars = 0, twoStars = 0, oneStars = 0;

        for (RatingAndReviewModel.ResultBean resultBean : reviewsList) {
            totalStars = totalStars + Integer.parseInt(resultBean.getRating());

            if (Integer.parseInt(resultBean.getRating()) == 5) {
                fiveStars = fiveStars + 1;
            } else if (Integer.parseInt(resultBean.getRating()) == 4) {
                fourStars = fourStars + 1;
            } else if (Integer.parseInt(resultBean.getRating()) == 3) {
                threeStars = threeStars + 1;
            } else if (Integer.parseInt(resultBean.getRating()) == 2) {
                twoStars = twoStars + 1;
            } else if (Integer.parseInt(resultBean.getRating()) == 1) {
                oneStars = oneStars + 1;
            }
        }

        float averageRating = (float) totalStars / totalNoOfReviews;
        averageRating = Float.parseFloat(new DecimalFormat("#.#").format(averageRating));

        pbFiveStar.setMax(totalNoOfReviews);
        pbFourStar.setMax(totalNoOfReviews);
        pbThreeStar.setMax(totalNoOfReviews);
        pbTwoStar.setMax(totalNoOfReviews);
        pbOneStar.setMax(totalNoOfReviews);

        pbFiveStar.setProgress(fiveStars);
        pbFourStar.setProgress(fourStars);
        pbThreeStar.setProgress(threeStars);
        pbTwoStar.setProgress(twoStars);
        pbOneStar.setProgress(oneStars);

        tvTotalRating.setText(String.valueOf(averageRating));
        tvTotalReviews.setText(String.valueOf(totalNoOfReviews));
        rbFeedbackStars.setRating(averageRating);
    }

    private void setUpRatingAndReviewRv() {
        if (reviewType == 0) {

            if (reviewsList.size() != 0) {
                rvReviews.setVisibility(View.VISIBLE);
                llNopreview.setVisibility(View.GONE);
                rvReviews.setAdapter(new RatingAndReviewAdapter(context, reviewsList, categoryTypeId));
            } else {
                rvReviews.setVisibility(View.GONE);
                llNopreview.setVisibility(View.VISIBLE);
                tvNoReview.setText("There are no reviews for this profile");
            }


            rvReviews.setAdapter(new RatingAndReviewAdapter(context, reviewsList, categoryTypeId));
        } else {
            List<RatingAndReviewModel.ResultBean> filterResultBeans = new ArrayList<>();

            for (RatingAndReviewModel.ResultBean resultBean : reviewsList)
                if (Integer.parseInt(resultBean.getRating()) == reviewType)
                    filterResultBeans.add(resultBean);

            if (filterResultBeans.size() != 0) {
                rvReviews.setVisibility(View.VISIBLE);
                llNopreview.setVisibility(View.GONE);
                rvReviews.setAdapter(new RatingAndReviewAdapter(context, filterResultBeans, categoryTypeId));
            } else {
                rvReviews.setVisibility(View.GONE);
                llNopreview.setVisibility(View.VISIBLE);
                tvNoReview.setText("There are no " + reviewType + " \u2605 reviews");
            }
        }
    }

    private void setEventHandler() {
        cgCategory.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.chip_all:
                        reviewType = 0;
                        setUpRatingAndReviewRv();
                        break;
                    case R.id.chip_five_star:
                        reviewType = 5;
                        setUpRatingAndReviewRv();
                        break;
                    case R.id.chip_four_star:
                        reviewType = 4;
                        setUpRatingAndReviewRv();
                        break;
                    case R.id.chip_three_star:
                        reviewType = 3;
                        setUpRatingAndReviewRv();
                        break;
                    case R.id.chip_two_star:
                        reviewType = 2;
                        setUpRatingAndReviewRv();
                        break;
                    case R.id.chip_one_star:
                        reviewType = 1;
                        setUpRatingAndReviewRv();
                        break;
                }
            }
        });
    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        edtProfileName.setText(profileName);
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

    public class GetRatingsAndReviews extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "GetProfileRating");
            obj.addProperty("category_type_id", params[0]);
            obj.addProperty("record_id", params[1]);
            res = APICall.JSONAPICall(ApplicationConstants.RATINGANDREVIEWAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "";
            try {
                if (!result.equals("")) {
                    RatingAndReviewModel pojoDetails = new Gson().fromJson(result, RatingAndReviewModel.class);
                    type = pojoDetails.getType();
                    if (type.equalsIgnoreCase("success")) {
                        reviewsList = pojoDetails.getResult();
                        setUpRatingAndReviewRv();
                        setUpRatingAndReviewBars();
                    } else {
                        Utilities.showAlertDialog(context, "Ratings and reviews not available", false);
                        reviewsList = new ArrayList<>();
                        setUpRatingAndReviewRv();
                        setUpRatingAndReviewBars();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                reviewsList = new ArrayList<>();
                setUpRatingAndReviewRv();
                setUpRatingAndReviewBars();
                Utilities.showAlertDialog(context, "Ratings and reviews not available", false);
            }
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Utilities.isNetworkAvailable(context))
                new GetRatingsAndReviews().execute(categoryTypeId, recordId);
            else
                Utilities.showMessage("Please check your internet connection", context, 2);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }
}
