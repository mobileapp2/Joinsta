package in.oriange.joinsta.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.adapters.MutualGroupsAdapter;
import in.oriange.joinsta.models.MutualGroupsModel;

public class MutualGroupsList_Activity extends AppCompatActivity {

    private Context context;
    private RecyclerView rv_mutual_groups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutual_groups_list);

        init();
        setDefault();
        setUpToolbar();
    }

    private void init() {
        context = MutualGroupsList_Activity.this;
        rv_mutual_groups = findViewById(R.id.rv_mutual_groups);
        rv_mutual_groups.setLayoutManager(new LinearLayoutManager(context));
    }

    private void setDefault() {
        List<MutualGroupsModel> mutualGroupsList = new ArrayList<>();
        mutualGroupsList = (List<MutualGroupsModel>) getIntent().getSerializableExtra("mutualGroupsList");

        rv_mutual_groups.setAdapter(new MutualGroupsAdapter(context, mutualGroupsList));
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
