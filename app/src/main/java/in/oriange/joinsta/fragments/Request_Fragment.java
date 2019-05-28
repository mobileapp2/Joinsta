package in.oriange.joinsta.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    private static Context context;
    private UserSessionManager session;
    private static RecyclerView rv_requirementlist;
    private Button btn_post_requirement;
    private ImageButton ib_filter;
    private static SpinKitView progressBar;
    private static String userId;
    private static ArrayList<RequirementsListModel> requirementsList;

    private boolean business, employment, others, posted, starred;

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

        ib_filter = rootView.findViewById(R.id.ib_filter);
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
        ib_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View promptView = layoutInflater.inflate(R.layout.dialog_layout_filter, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                alertDialogBuilder.setTitle("Filter");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setView(promptView);

                final CheckBox cb_business = promptView.findViewById(R.id.cb_business);
                final CheckBox cb_employment = promptView.findViewById(R.id.cb_employment);
                final CheckBox cb_others = promptView.findViewById(R.id.cb_others);
                final CheckBox cb_postedbyme = promptView.findViewById(R.id.cb_postedbyme);
                final CheckBox cb_staredbyme = promptView.findViewById(R.id.cb_staredbyme);

                if (business)
                    cb_business.setChecked(true);

                if (employment)
                    cb_employment.setChecked(true);

                if (others)
                    cb_others.setChecked(true);

                if (posted)
                    cb_postedbyme.setChecked(true);

                if (starred)
                    cb_staredbyme.setChecked(true);


                alertDialogBuilder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ArrayList<RequirementsListModel> filteredRequirementsList = new ArrayList<>();


                        if (cb_business.isChecked()) {
                            business = true;
                            if (filteredRequirementsList.size() > 0) {
                                for (int i = 0; i < filteredRequirementsList.size(); i++)
                                    if (!filteredRequirementsList.get(i).getCategory_type_id().equals("1"))
                                        filteredRequirementsList.remove(filteredRequirementsList.get(i));
                            } else {
                                for (int j = 0; j < requirementsList.size(); j++)
                                    if (requirementsList.get(j).getCategory_type_id().equals("1"))
                                        filteredRequirementsList.add(requirementsList.get(j));
                            }
                        } else {
                            business = false;
                        }

                        if (cb_employment.isChecked()) {
                            employment = true;
                            if (filteredRequirementsList.size() > 0) {
                                for (int i = 0; i < filteredRequirementsList.size(); i++)
                                    if (!(filteredRequirementsList.get(i).getCategory_type_id().equals("2") || filteredRequirementsList.get(i).getCategory_type_id().equals("3")))
                                        filteredRequirementsList.remove(filteredRequirementsList.get(i));
                            } else {
                                for (int j = 0; j < requirementsList.size(); j++) {
                                    if (requirementsList.get(j).getCategory_type_id().equals("2") || requirementsList.get(j).getCategory_type_id().equals("3")) {
                                        filteredRequirementsList.add(requirementsList.get(j));
                                    }
                                }
                            }

                        } else {
                            employment = false;
                        }

                        if (cb_others.isChecked()) {
                            others = true;
                            if (filteredRequirementsList.size() > 0) {
                                for (int i = 0; i < filteredRequirementsList.size(); i++) {
                                    if (!filteredRequirementsList.get(i).getCategory_type_id().equals("4")) {
                                        filteredRequirementsList.remove(filteredRequirementsList.get(i));
                                    }
                                }
                            } else {
                                for (int j = 0; j < requirementsList.size(); j++) {
                                    if (requirementsList.get(j).getCategory_type_id().equals("4")) {
                                        filteredRequirementsList.add(requirementsList.get(j));
                                    }
                                }
                            }

                        } else {
                            others = false;
                        }

                        if (cb_postedbyme.isChecked()) {
                            posted = true;
                            if (filteredRequirementsList.size() > 0) {
                                for (int i = 0; i < filteredRequirementsList.size(); i++)
                                    if (!filteredRequirementsList.get(i).getCreated_by().equals(userId)) {
                                        filteredRequirementsList.remove(filteredRequirementsList.get(i));
                                    }
                            } else {
                                for (int i = 0; i < requirementsList.size(); i++) {
                                    if (requirementsList.get(i).getCreated_by().equals(userId)) {
                                        filteredRequirementsList.add(requirementsList.get(i));
                                    }
                                }
                            }
                        } else {
                            posted = false;
                        }

                        if (cb_staredbyme.isChecked()) {
                            starred = true;
                            if (filteredRequirementsList.size() > 0) {
                                for (int i = 0; i < filteredRequirementsList.size(); i++)
                                    if (!(filteredRequirementsList.get(i).getCreated_by().equals(userId) && filteredRequirementsList.get(i).getIsStarred().equals("1")))
                                        filteredRequirementsList.remove(filteredRequirementsList.get(i));
                            } else {
                                for (int i = 0; i < requirementsList.size(); i++)
                                    if ((requirementsList.get(i).getCreated_by().equals(userId) && requirementsList.get(i).getIsStarred().equals("1")))
                                        filteredRequirementsList.add(requirementsList.get(i));
                            }
                        } else {
                            starred = false;
                        }


                        rv_requirementlist.setAdapter(new RequirementAdapter(context, filteredRequirementsList));
                    }
                });

                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


                final AlertDialog alertD = alertDialogBuilder.create();
                alertD.show();
            }
        });

        btn_post_requirement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AddRequirement_Activity.class));
            }
        });
    }

    public static class GetRequirementList extends AsyncTask<String, Void, String> {

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

}
