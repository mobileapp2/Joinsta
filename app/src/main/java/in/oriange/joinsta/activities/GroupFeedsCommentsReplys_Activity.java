package in.oriange.joinsta.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.JsonObject;
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
import in.oriange.joinsta.adapters.GroupFeedsCommentsRepliesAdapter;
import in.oriange.joinsta.models.GroupFeedsModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class GroupFeedsCommentsReplys_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private CircleImageView imv_user, imv_current_user;
    private TextView tv_name, tv_time, tv_comment;
    private RecyclerView rv_feeds_comments_replies;
    private EditText edt_reply;
    private ImageButton imb_post_reply;

    private GroupFeedsModel.ResultBean.FeedCommentsBean commentsDetails;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_feeds_comments_replys);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {

        context = GroupFeedsCommentsReplys_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        imv_user = findViewById(R.id.imv_user);
        imv_current_user = findViewById(R.id.imv_current_user);
        tv_name = findViewById(R.id.tv_name);
        tv_time = findViewById(R.id.tv_time);
        tv_comment = findViewById(R.id.tv_comment);
        rv_feeds_comments_replies = findViewById(R.id.rv_feeds_comments_replies);
        rv_feeds_comments_replies.setLayoutManager(new LinearLayoutManager(context));
        edt_reply = findViewById(R.id.edt_reply);
        imb_post_reply = findViewById(R.id.imb_post_reply);

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
        commentsDetails = (GroupFeedsModel.ResultBean.FeedCommentsBean) getIntent().getSerializableExtra("commentsDetails");

        if (!commentsDetails.getImage_url().trim().isEmpty()) {
            Picasso.with(context)
                    .load(commentsDetails.getImage_url().trim())
                    .placeholder(R.drawable.icon_user)
                    .resize(100, 100)
                    .into(imv_user);
        } else {
            imv_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_user));
        }

        tv_name.setText(commentsDetails.getFirst_name());

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            tv_time.setText(new PrettyTime().format(formatter.parse(commentsDetails.getCreated_at())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        tv_comment.setText(commentsDetails.getMessage());

        rv_feeds_comments_replies.setAdapter(new GroupFeedsCommentsRepliesAdapter(context, commentsDetails.getComment_reply()));

    }

    private void setEventHandler() {
        imb_post_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edt_reply.getText().toString().trim().isEmpty()) {

                    JsonObject mainObj = new JsonObject();
                    mainObj.addProperty("type", "addFeedCommentReplyDetails");
                    mainObj.addProperty("user_id", userId);
                    mainObj.addProperty("comment_id", commentsDetails.getId());
                    mainObj.addProperty("message", edt_reply.getText().toString().trim());
                    mainObj.addProperty("is_private", "0");

                    if (Utilities.isNetworkAvailable(context)) {
                        new AddFeedCommentReply().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
                    } else {
                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                    }
                }
            }
        });
    }


    private class AddFeedCommentReply extends AsyncTask<String, Void, String> {

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
        Utilities.hideSoftKeyboard(GroupFeedsCommentsReplys_Activity.this);
    }
}
