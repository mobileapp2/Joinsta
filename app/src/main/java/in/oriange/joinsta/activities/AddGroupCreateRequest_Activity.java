package in.oriange.joinsta.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.JsonObject;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinsta.R;
import in.oriange.joinsta.models.MasterModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class AddGroupCreateRequest_Activity extends AppCompatActivity {

    @BindView(R.id.edt_name)
    MaterialEditText edtName;
    @BindView(R.id.edt_description)
    EditText edtDescription;
    @BindView(R.id.edt_admin_name)
    MaterialEditText edtAdminName;
    @BindView(R.id.cb_can_post)
    CheckBox cbCanPost;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.edt_select_group_type)
    MaterialEditText edtSelectGroupType;

    private Context context;
    private String userId, groupTypeId;
    private List<MasterModel> groupTypeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_create_request);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = AddGroupCreateRequest_Activity.this;

        groupTypeList = new ArrayList<>();
        groupTypeList.add(new MasterModel("Private Group", "0"));
        groupTypeList.add(new MasterModel("Public Group", "1"));
        groupTypeList.add(new MasterModel("Social Group", "2"));
    }

    private void getSessionDetails() {

        try {
            JSONArray user_info = new JSONArray(new UserSessionManager(context).getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            userId = json.getString("userid");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEventHandler() {
        edtSelectGroupType.setOnClickListener(v -> {
            showEventTypeListDialog();
        });

        btnSave.setOnClickListener(v -> {
            submitDate();
        });

    }

    private void submitDate() {
        if (edtName.getText().toString().trim().isEmpty()) {
            edtName.setError("Please enter group name");
            edtName.requestFocus();
            edtName.getParent().requestChildFocus(edtName, edtName);
            return;
        }

        if (edtDescription.getText().toString().trim().isEmpty()) {
            edtDescription.setError("Please enter group description");
            edtDescription.requestFocus();
            edtDescription.getParent().requestChildFocus(edtDescription, edtDescription);
            return;
        }

        if (edtSelectGroupType.getText().toString().trim().isEmpty()) {
            edtSelectGroupType.setError("Please select group type");
            edtSelectGroupType.requestFocus();
            edtSelectGroupType.getParent().requestChildFocus(edtSelectGroupType, edtSelectGroupType);
            return;
        }

        if (edtAdminName.getText().toString().trim().isEmpty()) {
            edtAdminName.setError("Please enter group admin name");
            edtAdminName.requestFocus();
            edtAdminName.getParent().requestChildFocus(edtAdminName, edtAdminName);
            return;
        }

        String canMembersPost = cbCanPost.isChecked() ? "1" : "0";

        JsonObject mainObj = new JsonObject();
        mainObj.addProperty("type", "addGroupRequest");
        mainObj.addProperty("group_name", edtName.getText().toString().trim());
        mainObj.addProperty("group_description", edtDescription.getText().toString().trim());
        mainObj.addProperty("group_admin_name", edtAdminName.getText().toString().trim());
        mainObj.addProperty("can_members_post", canMembersPost);
        mainObj.addProperty("user_id", userId);
        mainObj.addProperty("is_public_group", groupTypeId);
        mainObj.addProperty("is_active", "2");

        if (Utilities.isNetworkAvailable(context)) {
            new CreateGroupRequest().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }

    private void showEventTypeListDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Group Type");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < groupTypeList.size(); i++) {
            arrayAdapter.add(String.valueOf(groupTypeList.get(i).getName()));
        }

        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MasterModel event = groupTypeList.get(which);
                edtSelectGroupType.setText(event.getName());
                groupTypeId = event.getId();

                if (groupTypeId.equals("2")) {
                    cbCanPost.setChecked(false);
                    cbCanPost.setVisibility(View.GONE);
                }
            }
        });
        builderSingle.show();
    }

    private class CreateGroupRequest extends AsyncTask<String, Void, String> {

        private ProgressDialog pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res;
            res = APICall.JSONAPICall(ApplicationConstants.GROUPREQUESTAPI, params[0]);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type, message;
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
                        tv_title.setText("Group request submitted successfully");
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
                        Utilities.showMessage(message, context, 3);
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
        mToolbar.setNavigationOnClickListener(view -> finish());
    }
}
