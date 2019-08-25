package in.oriange.joinsta.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.rengwuxian.materialedittext.MaterialEditText;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.AllGroupsListModel;
import in.oriange.joinsta.utilities.UserSessionManager;

public class GroupDetails_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;

    private MaterialEditText edt_grp_admin, edt_grp_name, edt_grp_code, edt_grp_description, edt_noofmembers;
    private MaterialButton btn_members;
    private Button btn_connect;

    private AllGroupsListModel.ResultBean groupDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = GroupDetails_Activity.this;
        session = new UserSessionManager(context);

        edt_grp_admin = findViewById(R.id.edt_grp_admin);
        edt_grp_name = findViewById(R.id.edt_grp_name);
        edt_grp_code = findViewById(R.id.edt_grp_code);
        edt_grp_description = findViewById(R.id.edt_grp_description);
        edt_noofmembers = findViewById(R.id.edt_noofmembers);
        btn_members = findViewById(R.id.btn_members);
        btn_connect = findViewById(R.id.btn_connect);
    }

    private void getSessionDetails() {
    }

    private void setDefault() {
        groupDetails = (AllGroupsListModel.ResultBean) getIntent().getSerializableExtra("groupDetails");
        edt_grp_admin.setText("");
        edt_grp_name.setText(groupDetails.getGroup_name());
        edt_grp_code.setText(groupDetails.getGroup_code());
        edt_grp_description.setText(groupDetails.getGroup_description());
        edt_noofmembers.setText(groupDetails.getGroup_description());
    }

    private void setEventHandler() {
        btn_members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, GroupMembersList_Activity.class)
                        .putExtra("groupId", groupDetails.getId()));
            }
        });
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
}
