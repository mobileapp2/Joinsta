package in.oriange.joinsta.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.fragments.GetDistrictListModel;
import in.oriange.joinsta.utilities.Utilities;

import static in.oriange.joinsta.utilities.Utilities.hideSoftKeyboard;

public class SelectDistrict_Activity extends AppCompatActivity {

    private Context context;
    private MaterialButton btn_select;
    private EditText edt_search;
    private RecyclerView rv_district;

    private JSONArray selectedDistIds;
    private List<GetDistrictListModel.ResultBean> districtListFromApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_district);

        init();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = SelectDistrict_Activity.this;
        btn_select = findViewById(R.id.btn_select);
        edt_search = findViewById(R.id.edt_search);
        rv_district = findViewById(R.id.rv_district);
        rv_district.setLayoutManager(new LinearLayoutManager(context));

        districtListFromApi = new ArrayList<>();
        selectedDistIds = new JSONArray();
    }

    private void setDefault() {
        districtListFromApi = (List<GetDistrictListModel.ResultBean>) getIntent().getSerializableExtra("districtList");
        try {
            selectedDistIds = new JSONArray(getIntent().getStringExtra("selectedDistIds"));

            for (int j = 0; j < selectedDistIds.length(); j++) {
                for (int i = 0; i < districtListFromApi.size(); i++) {
                    if (selectedDistIds.get(j).equals(districtListFromApi.get(i).getDistrictId())) {
                        districtListFromApi.get(i).setChecked(true);
                        break;
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        rv_district.setAdapter(new DistrictAdapter(districtListFromApi));
    }

    private void setEventHandler() {
        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                if (!query.toString().equals("")) {
                    ArrayList<GetDistrictListModel.ResultBean> groupsSearchedList = new ArrayList<>();
                    for (GetDistrictListModel.ResultBean groupsDetails : districtListFromApi) {
                        if (groupsDetails.getDistrict().toLowerCase().contains(query.toString().toLowerCase())) {
                            groupsSearchedList.add(groupsDetails);
                        }
                    }
                    rv_district.setAdapter(new DistrictAdapter(groupsSearchedList));
                } else {
                    rv_district.setAdapter(new DistrictAdapter(districtListFromApi));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAtleastOneSelected = false;

                for (GetDistrictListModel.ResultBean bean : districtListFromApi) {
                    if (bean.isChecked()) {
                        isAtleastOneSelected = true;
                        break;
                    }
                }

                if (isAtleastOneSelected) {
                    Intent intent = new Intent();
                    intent.putExtra("districtList", (Serializable) districtListFromApi);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Utilities.showMessage("Please select atleast one district", context, 2);
                }

            }
        });
    }

    private class DistrictAdapter extends RecyclerView.Adapter<DistrictAdapter.MyViewHolder> {

        private List<GetDistrictListModel.ResultBean> districtList;

        public DistrictAdapter(List<GetDistrictListModel.ResultBean> districtList) {
            this.districtList = districtList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_state_district, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
            final int position = holder.getAdapterPosition();
            final GetDistrictListModel.ResultBean districtObj = districtList.get(position);

            holder.tv_district.setText(districtObj.getDistrict());
            holder.tv_state.setText(districtObj.getState());

            if (districtObj.isChecked()) {
                holder.cb_select.setChecked(true);
            } else {
                holder.cb_select.setChecked(false);
            }

            holder.cb_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    for (int i = 0; i < districtListFromApi.size(); i++) {
                        if (districtListFromApi.get(i).getDistrictId().equals(districtObj.getDistrictId())) {
                            districtListFromApi.get(i).setChecked(isChecked);
                            break;
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return districtList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private CheckBox cb_select;
            private TextView tv_district, tv_state;

            public MyViewHolder(@NonNull View view) {
                super(view);
                cb_select = view.findViewById(R.id.cb_select);
                tv_district = view.findViewById(R.id.tv_district);
                tv_state = view.findViewById(R.id.tv_state);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
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
        hideSoftKeyboard(SelectDistrict_Activity.this);
    }
}
