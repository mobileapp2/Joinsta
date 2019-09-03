package in.oriange.joinsta.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.MainDrawer_Activity;
import in.oriange.joinsta.activities.ViewSearchProfDetails_Activity;
import in.oriange.joinsta.models.SearchDetailsModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.CalculateDistanceTime;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static in.oriange.joinsta.activities.MainDrawer_Activity.startLocationUpdates;
import static in.oriange.joinsta.utilities.Utilities.isLocationEnabled;
import static in.oriange.joinsta.utilities.Utilities.provideLocationAccess;
import static in.oriange.joinsta.utilities.Utilities.turnOnLocation;

public class SearchProfessionalAdapter extends RecyclerView.Adapter<SearchProfessionalAdapter.MyViewHolder> {

    private Context context;
    private String type;            //  1 = from search  // 2 = from favorite  // 3 = from home
    private String userId, name, mobile;
    private List<SearchDetailsModel.ResultBean.ProfessionalsBean> resultArrayList;

    public SearchProfessionalAdapter(Context context, List<SearchDetailsModel.ResultBean.ProfessionalsBean> resultArrayList, String type) {
        this.context = context;
        this.resultArrayList = resultArrayList;
        this.type = type;

        try {
            UserSessionManager session = new UserSessionManager(context);
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            userId = json.getString("userid");
            name = json.getString("first_name");
            mobile = json.getString("mobile");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_search, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int pos) {
        final int position = holder.getAdapterPosition();
        final SearchDetailsModel.ResultBean.ProfessionalsBean searchDetails = resultArrayList.get(position);

        holder.tv_heading.setText(searchDetails.getFirm_name());


        if (searchDetails.getTag().get(0) != null) {
            if (searchDetails.getTag().get(0).size() > 0) {
                StringBuilder tag = new StringBuilder();
                try {
                    for (int i = 0; i < 4; i++) {
                        tag.append(searchDetails.getTag().get(0).get(i).getTag_name() + ", ");
                    }
                    holder.tv_subheading.setText(tag.toString().substring(0, tag.toString().length() - 2));
                } catch (IndexOutOfBoundsException e) {
                    holder.tv_subheading.setText(tag.toString().substring(0, tag.toString().length() - 2));
                }

            } else {
                if (!searchDetails.getSubtype_description().isEmpty())
                    holder.tv_subheading.setText(searchDetails.getType_description() + ", " + searchDetails.getSubtype_description());
                else
                    holder.tv_subheading.setText(searchDetails.getType_description());
            }
        } else {
            if (!searchDetails.getSubtype_description().isEmpty())
                holder.tv_subheading.setText(searchDetails.getType_description() + ", " + searchDetails.getSubtype_description());
            else
                holder.tv_subheading.setText(searchDetails.getType_description());
        }

        holder.tv_subsubheading.setText(searchDetails.getCity() + ", " + searchDetails.getPincode());

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ViewSearchProfDetails_Activity.class)
                        .putExtra("searchDetails", searchDetails)
                        .putExtra("type", type));
            }
        });

//        float scale = context.getResources().getDisplayMetrics().density;
//        final int dpAsPixels = (int) (5 * scale + 0.5f);
//        if (!searchDetails.getImage_url().trim().isEmpty()) {
//            Picasso.with(context)
//                    .load(searchDetails.getImage_url().trim())
//                    .into(holder.imv_preview, new Callback() {
//                        @Override
//                        public void onSuccess() {
//                            holder.progressBar.setVisibility(View.GONE);
//                            holder.imv_preview.setVisibility(View.VISIBLE);
//                        }
//
//                        @Override
//                        public void onError() {
//                            holder.progressBar.setVisibility(View.GONE);
//                            holder.imv_preview.setVisibility(View.VISIBLE);
//                            holder.imv_preview.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
//                            holder.imv_preview.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_preview));
//                        }
//                    });
//        } else {
//            holder.progressBar.setVisibility(View.GONE);
//            holder.imv_preview.setVisibility(View.VISIBLE);
//            holder.imv_preview.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
//            holder.imv_preview.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_preview));
//        }

        holder.btn_caldist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (searchDetails.getLatitude().equals("") || searchDetails.getLongitude().equals("")) {
                    return;
                }

                if (ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED /*&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED*/) {
                    provideLocationAccess(context);
                    return;
                }

                if (!isLocationEnabled(context)) {
                    turnOnLocation(context);
                    return;
                }

                if (MainDrawer_Activity.latLng == null) {
                    holder.btn_caldist.setText(Html.fromHtml("<font color=\"#C62828\"> <b>Try again</b></font>"));
                    return;
                }

                startLocationUpdates();

                LatLng currentLocation = new LatLng(MainDrawer_Activity.latLng.latitude, MainDrawer_Activity.latLng.longitude);
                LatLng destinationLocation = new LatLng(Double.parseDouble(searchDetails.getLatitude()), Double.parseDouble(searchDetails.getLongitude()));

                CalculateDistanceTime distance_task = new CalculateDistanceTime(context);

                distance_task.getDirectionsUrl(currentLocation, destinationLocation);

                distance_task.setLoadListener(new CalculateDistanceTime.taskCompleteListener() {
                    @Override
                    public void taskCompleted(String[] time_distance) {
                        holder.btn_caldist.setText(time_distance[0]);
//                        holder.tv_distance.setText(Html.fromHtml("<font color=\"#FFA000\"> <b>" + time_distance[0] + "</b></font> <font color=\"#616161\">from current location</font>"));

                    }

                });
            }
        });

        holder.btn_enquire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View promptView = layoutInflater.inflate(R.layout.dialog_layout_enquiry, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                alertDialogBuilder.setTitle("Enquiry");
                alertDialogBuilder.setView(promptView);

                final MaterialEditText edt_name = promptView.findViewById(R.id.edt_name);
                final MaterialEditText edt_mobile = promptView.findViewById(R.id.edt_mobile);
                final MaterialEditText edt_subject = promptView.findViewById(R.id.edt_subject);
                final EditText edt_details = promptView.findViewById(R.id.edt_details);
                final Button btn_save = promptView.findViewById(R.id.btn_save);

                edt_name.setText(name);
                edt_mobile.setText(mobile);

                final AlertDialog alertD = alertDialogBuilder.create();

                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (edt_name.getText().toString().trim().isEmpty()) {
                            edt_name.setError("Please enter name");
                            edt_name.requestFocus();
                            return;
                        }

                        if (!Utilities.isValidMobileno(edt_mobile.getText().toString().trim())) {
                            edt_mobile.setError("Please enter valid mobile");
                            edt_mobile.requestFocus();
                            return;
                        }

                        if (edt_subject.getText().toString().trim().isEmpty()) {
                            edt_subject.setError("Please enter subject");
                            edt_subject.requestFocus();
                            return;
                        }

                        if (edt_details.getText().toString().trim().isEmpty()) {
                            edt_details.setError("Please enter details");
                            edt_details.requestFocus();
                            return;
                        }


                        if (Utilities.isNetworkAvailable(context)) {
                            alertD.dismiss();

                            if (Utilities.isNetworkAvailable(context)) {
                                new SendEnquiryDetails().execute(
                                        userId,
                                        searchDetails.getCreated_by(),
                                        edt_name.getText().toString().trim(),
                                        edt_mobile.getText().toString().trim(),
                                        "",
                                        edt_subject.getText().toString().trim(),
                                        edt_details.getText().toString().trim(),
                                        "3",
                                        searchDetails.getId()
                                );
                            } else {
                                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                            }

                        } else {
                            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                        }
                    }
                });

                alertD.show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imv_preview;
        private CardView cv_mainlayout;
        private ProgressBar progressBar;
        private Button btn_enquire, btn_caldist;
        private TextView tv_heading, tv_subheading, tv_subsubheading;

        public MyViewHolder(View view) {
            super(view);
            imv_preview = view.findViewById(R.id.imv_preview);
            tv_heading = view.findViewById(R.id.tv_heading);
            tv_subheading = view.findViewById(R.id.tv_subheading);
            tv_subsubheading = view.findViewById(R.id.tv_subsubheading);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            progressBar = view.findViewById(R.id.progressBar);
            btn_enquire = view.findViewById(R.id.btn_enquire);
            btn_caldist = view.findViewById(R.id.btn_caldist);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private class SendEnquiryDetails extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "createenquiry");
            obj.addProperty("userid", params[0]);
            obj.addProperty("name", params[1]);
            obj.addProperty("mobile", params[2]);
            obj.addProperty("email", params[3]);
            obj.addProperty("subject", params[4]);
            obj.addProperty("message", params[5]);
            obj.addProperty("category_type_id", params[6]);
            obj.addProperty("record_id", params[7]);
            res = APICall.JSONAPICall(ApplicationConstants.ENQUIRYAPI, obj.toString());
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        Utilities.showMessage("Enquiry sent successfully", context, 1);
                    } else {

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
