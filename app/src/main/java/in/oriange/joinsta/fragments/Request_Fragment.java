package in.oriange.joinsta.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.AddRequirement_Activity;
import in.oriange.joinsta.adapters.RequirementAdapter;
import in.oriange.joinsta.models.RequirementsListModel;
import in.oriange.joinsta.pojos.RequirementsListPojo;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

public class Request_Fragment extends Fragment {

    private Context context;
    private UserSessionManager session;
    private RecyclerView rv_requirementlist;
    private Button btn_post_requirement;
    private SpinKitView progressBar;
    private String userId;
    private ArrayList<RequirementsListModel> requirementsList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_request, container, false);
        context = getActivity();
        init(rootView);
        setDefault();
        getSessionDetails();
        setEventHandlers();
        return rootView;
    }

    private void init(View rootView) {
        session = new UserSessionManager(context);
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        progressBar = rootView.findViewById(R.id.progressBar);
        btn_post_requirement = rootView.findViewById(R.id.btn_post_requirement);
        rv_requirementlist = rootView.findViewById(R.id.rv_requirementlist);
        rv_requirementlist.setLayoutManager(new LinearLayoutManager(context));

        requirementsList = new ArrayList<>();

    }

    private void setDefault() {

        if (Utilities.isNetworkAvailable(context)) {
            new GetRequirementList().execute();
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

    private void setEventHandlers() {
        btn_post_requirement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AddRequirement_Activity.class));
            }
        });
    }

    private class GetRequirementList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            rv_requirementlist.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getrequirements");
            obj.addProperty("user_id", userId);
            res = APICall.JSONAPICall(ApplicationConstants.REQUIREMENTAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            rv_requirementlist.setVisibility(View.VISIBLE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {

                    RequirementsListPojo pojoDetails = new Gson().fromJson(result, RequirementsListPojo.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        requirementsList = pojoDetails.getResult();

                        if (requirementsList.size() > 0) {
                            rv_requirementlist.setAdapter(new RequirementAdapter(context, requirementsList));
                        }
                    } else {
                        Utilities.showAlertDialog(context, "Categories not available", false);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showAlertDialog(context, "Server Not Responding", false);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menus_requirement, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_filter:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
