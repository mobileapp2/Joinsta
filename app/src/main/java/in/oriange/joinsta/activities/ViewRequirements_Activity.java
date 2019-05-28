package in.oriange.joinsta.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import in.oriange.joinsta.R;
import in.oriange.joinsta.models.RequirementsListModel;
import in.oriange.joinsta.utilities.UserSessionManager;

public class ViewRequirements_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private CircleImageView imv_user;
    private ImageView imv_mobile, imv_email;
    private TextView tv_reqby_name;
    private MaterialEditText edt_categoryname, edt_reqmtitle, edt_reqmdesc, edt_city;

    private RequirementsListModel reqDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requirements);

        init();
        setDefault();
        getSessionData();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = ViewRequirements_Activity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context);
        imv_user = findViewById(R.id.imv_user);
        imv_mobile = findViewById(R.id.imv_mobile);
        imv_email = findViewById(R.id.imv_email);
        tv_reqby_name = findViewById(R.id.tv_reqby_name);
        edt_categoryname = findViewById(R.id.edt_categoryname);
        edt_reqmtitle = findViewById(R.id.edt_reqmtitle);
        edt_reqmdesc = findViewById(R.id.edt_reqmdesc);
        edt_city = findViewById(R.id.edt_city);
    }

    private void setDefault() {
        reqDetails = (RequirementsListModel) getIntent().getSerializableExtra("reqDetails");


        if (!reqDetails.getImageURL().trim().isEmpty()) {
            Picasso.with(context)
                    .load(reqDetails.getImageURL().trim())
                    .placeholder(R.drawable.icon_userphoto)
                    .into(imv_user, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                        }
                    });
        }

        tv_reqby_name.setText(reqDetails.getFname() + " " + reqDetails.getMname() + " " + reqDetails.getLname());
        edt_reqmtitle.setText(reqDetails.getTitle());
        edt_reqmdesc.setText(reqDetails.getDescription());
        edt_city.setText(reqDetails.getCity());
    }

    private void getSessionData() {

    }

    private void setEventHandler() {
        imv_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        imv_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
