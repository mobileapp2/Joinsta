package in.oriange.joinsta.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.JsonObject;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;

import de.hdodenhof.circleimageview.CircleImageView;
import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.GroupFeedsCommentsAdapter;
import in.oriange.joinsta.models.GroupFeedsModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.ApplicationConstants.IMAGE_LINK;

public class GroupFeedsComments_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private CircleImageView imv_user, imv_current_user;
    private TextView tv_name, tv_time, tv_feed_text;
    private CardView cv_feed_image;
    private ImageView imv_feed_image;
    private Button btn_comment, btn_share;
    private RecyclerView rv_feeds_comments;
    private EditText edt_comment;
    private ImageButton imb_post_comment;

    private GroupFeedsModel.ResultBean feedDetails;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_feeds_comments);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = GroupFeedsComments_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        imv_user = findViewById(R.id.imv_user);
        imv_current_user = findViewById(R.id.imv_current_user);
        tv_name = findViewById(R.id.tv_name);
        tv_time = findViewById(R.id.tv_time);
        tv_feed_text = findViewById(R.id.tv_feed_text);
        cv_feed_image = findViewById(R.id.cv_feed_image);
        imv_feed_image = findViewById(R.id.imv_feed_image);
        btn_comment = findViewById(R.id.btn_comment);
        btn_share = findViewById(R.id.btn_share);
        rv_feeds_comments = findViewById(R.id.rv_feeds_comments);
        rv_feeds_comments.setLayoutManager(new LinearLayoutManager(context));
        edt_comment = findViewById(R.id.edt_comment);
        imb_post_comment = findViewById(R.id.imb_post_comment);

    }

    private void getSessionDetails() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            userId = json.getString("userid");
            String imageUrl = json.getString("image_url");

            if (!imageUrl.equals("")) {
                Picasso.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.icon_user)
                        .into(imv_current_user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefault() {
        feedDetails = (GroupFeedsModel.ResultBean) getIntent().getSerializableExtra("feedDetails");

        if (!feedDetails.getImage_url().trim().isEmpty()) {
            Picasso.with(context)
                    .load(feedDetails.getImage_url().trim())
                    .placeholder(R.drawable.icon_user)
                    .resize(100, 100)
                    .into(imv_user);
        } else {
            imv_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_user));
        }

        tv_name.setText(feedDetails.getFirst_name());

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            tv_time.setText(new PrettyTime().format(formatter.parse(feedDetails.getCreated_at())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        tv_feed_text.setText(feedDetails.getFeed_text());

        if (!feedDetails.getFeed_doc().equals("")) {
            String url = IMAGE_LINK + "feed_doc/" + feedDetails.getFeed_doc();
            Picasso.with(context)
                    .load(url)
                    .resize(450, 300)
                    .into(imv_feed_image, new Callback() {
                        @Override
                        public void onSuccess() {
                            cv_feed_image.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            cv_feed_image.setVisibility(View.GONE);
                        }
                    });
        } else {
            cv_feed_image.setVisibility(View.GONE);
        }

        if (feedDetails.getFeed_comments().size() == 1) {
            btn_comment.setText("1 Comment");
        } else {
            btn_comment.setText(feedDetails.getFeed_comments().size() + " Comments");
        }

        rv_feeds_comments.setAdapter(new GroupFeedsCommentsAdapter(context, feedDetails.getFeed_comments()));

    }

    private void setEventHandler() {
        imb_post_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!edt_comment.getText().toString().trim().isEmpty()) {
//
//                    JsonObject mainObj = new JsonObject();
//                    mainObj.addProperty("type", "addFeedCommentDetails");
//                    mainObj.addProperty("user_id", userId);
//                    mainObj.addProperty("feed_id", feedDetails.getId());
//                    mainObj.addProperty("message", edt_comment.getText().toString().trim());
//                    mainObj.addProperty("is_private", "0");
//
//                    if (Utilities.isNetworkAvailable(context)) {
//                        new AddFeedComment().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
//                    } else {
//                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
//                    }
//                }
            }
        });
    }

    private class AddFeedComment extends AsyncTask<String, Void, String> {

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
                        finish();
                    } else {
                        Utilities.showMessage("Failed to submit the details", context, 3);
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
        Utilities.hideSoftKeyboard(GroupFeedsComments_Activity.this);
    }
}
