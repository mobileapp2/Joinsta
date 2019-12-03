package in.oriange.joinsta.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.GroupNotifications_Activity;
import in.oriange.joinsta.models.GroupNotificationListModel;
import in.oriange.joinsta.utilities.APICall;
import in.oriange.joinsta.utilities.ApplicationConstants;
import in.oriange.joinsta.utilities.UserSessionManager;
import in.oriange.joinsta.utilities.Utilities;
import jp.shts.android.library.TriangleLabelView;

import static in.oriange.joinsta.utilities.ApplicationConstants.IMAGE_LINK;
import static in.oriange.joinsta.utilities.Utilities.changeDateFormat;

public class GroupNotificationAdapter extends RecyclerView.Adapter<GroupNotificationAdapter.MyViewHolder> {

    private List<GroupNotificationListModel.ResultBean> resultArrayList;
    private Context context;
    private UserSessionManager session;
    private String userId;
    private PrettyTime p;

    private File downloadedImagefolder, downloadedDocumentfolder, file;
    private boolean isDownloaded = false;

    private File downloadedFile;

    public GroupNotificationAdapter(Context context, List<GroupNotificationListModel.ResultBean> resultArrayList) {
        this.context = context;
        this.resultArrayList = resultArrayList;
        p = new PrettyTime();
        session = new UserSessionManager(context);
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);

            userId = json.getString("userid");

        } catch (Exception e) {
            e.printStackTrace();
        }


        downloadedImagefolder = new File(Environment.getExternalStorageDirectory() + "/Joinsta/" + "Notification Images");
        if (!downloadedImagefolder.exists())
            downloadedImagefolder.mkdirs();

        downloadedDocumentfolder = new File(Environment.getExternalStorageDirectory() + "/Joinsta/" + "Notification Documents");
        if (!downloadedDocumentfolder.exists())
            downloadedDocumentfolder.mkdirs();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_groupnotification, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int pos) {
        final int position = holder.getAdapterPosition();
        final GroupNotificationListModel.ResultBean notificationDetails = resultArrayList.get(position);

        holder.tv_title.setText(notificationDetails.getSubject().trim());
        holder.tv_message.setText(notificationDetails.getMessage().trim());

        if (notificationDetails.getCreated_at().equalsIgnoreCase("0000-00-00 00:00:00")) {
            holder.tv_time.setText("");
        } else {
            holder.tv_time.setText(changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd-MM-yyyy HH:mm", notificationDetails.getCreated_at()));
        }

        if (notificationDetails.getIs_read().equals("0")) {
            holder.ll_outimage.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));
            holder.tv_title.setTextColor(context.getResources().getColor(R.color.black));
            holder.tv_message.setTextColor(context.getResources().getColor(R.color.black));
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.black));
            holder.tv_new.setVisibility(View.VISIBLE);
        } else {
            holder.tv_title.setTextColor(context.getResources().getColor(R.color.mediumGray));
            holder.tv_message.setTextColor(context.getResources().getColor(R.color.mediumGray));
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.mediumGray));
        }

        if (!notificationDetails.getAttachment().equals("")) {
            String url = IMAGE_LINK + "groupnotifications/" + notificationDetails.getAttachment();
            Picasso.with(context)
                    .load(url)
                    .into(holder.imv_notificationimg, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.imv_notificationimg.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            holder.imv_notificationimg.setVisibility(View.GONE);
                        }
                    });
        } else {
            holder.imv_notificationimg.setVisibility(View.GONE);
            holder.ll_outimage.setVisibility(View.VISIBLE);
        }

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isNetworkAvailable(context)) {
                    holder.ll_outimage.setBackgroundColor(context.getResources().getColor(R.color.white));
                    holder.tv_title.setTextColor(context.getResources().getColor(R.color.mediumGray));
                    holder.tv_message.setTextColor(context.getResources().getColor(R.color.mediumGray));
                    holder.tv_time.setTextColor(context.getResources().getColor(R.color.mediumGray));

                    holder.tv_new.setVisibility(View.GONE);

                    showNotification(notificationDetails);
                } else {
                    Utilities.showMessage("Please check your internet connection", context, 2);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private ImageView imv_notificationimg;
        private TextView tv_title, tv_message, tv_time;
        private TriangleLabelView tv_new;
        private LinearLayout ll_outimage;

        public MyViewHolder(View view) {
            super(view);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            imv_notificationimg = view.findViewById(R.id.imv_notificationimg);
            tv_title = view.findViewById(R.id.tv_title);
            tv_message = view.findViewById(R.id.tv_message);
            tv_time = view.findViewById(R.id.tv_time);
            tv_new = view.findViewById(R.id.tv_new);
            ll_outimage = view.findViewById(R.id.ll_outimage);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void showNotification(final GroupNotificationListModel.ResultBean notificationDetails) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_layout_notification, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setView(promptView);

        final ImageView imv_notificationimg = promptView.findViewById(R.id.imv_notificationimg);
        final TextView tv_title = promptView.findViewById(R.id.tv_title);
        final TextView tv_message = promptView.findViewById(R.id.tv_message);
        final TextView tv_time = promptView.findViewById(R.id.tv_time);
        final Button btn_download = promptView.findViewById(R.id.btn_download);
        final Button btn_delete = promptView.findViewById(R.id.btn_delete);
        final TextView tv_viewdocs = promptView.findViewById(R.id.tv_viewdocs);

        if (!notificationDetails.getAttachment().equals("")) {
            String url = IMAGE_LINK + "groupnotifications/" + notificationDetails.getAttachment();
            String fileName = url.substring(url.lastIndexOf('/') + 1, url.length());

            downloadedFile = new File(downloadedImagefolder.toString() + "/" + fileName);
            if (downloadedFile.isFile()) {
                isDownloaded = true;
                btn_download.setText("VIEW");
            } else {
                isDownloaded = false;
            }

            Picasso.with(context)
                    .load(url)
                    .into(imv_notificationimg, new Callback() {
                        @Override
                        public void onSuccess() {
                            imv_notificationimg.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            imv_notificationimg.setVisibility(View.GONE);
                        }
                    });


        } else {
            imv_notificationimg.setVisibility(View.GONE);
            btn_download.setVisibility(View.GONE);
        }

        tv_title.setText(notificationDetails.getSubject().trim());

        tv_message.setText(notificationDetails.getMessage().trim());
        Linkify.addLinks(tv_message, Linkify.ALL);

        if (notificationDetails.getCreated_at().equalsIgnoreCase("0000-00-00 00:00:00")) {
            tv_time.setText("");
        } else {
            tv_time.setText(changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd-MM-yyyy HH:mm", notificationDetails.getCreated_at()));
        }

        if (notificationDetails.getIs_read().equals("0")) {
            if (Utilities.isNetworkAvailable(context))
                new ReadNotification().execute(notificationDetails.getMsg_details_id());
            else
                Utilities.showMessage("Please check your internet connection", context, 2);
        }

        if (notificationDetails.getDocuments().size() != 0) {
            tv_viewdocs.setVisibility(View.VISIBLE);
            if (notificationDetails.getDocuments().size() == 1) {
                tv_viewdocs.setText(notificationDetails.getDocuments().size() + " Document Attached");
            } else {
                tv_viewdocs.setText(notificationDetails.getDocuments().size() + " Documents Attached");
            }
            tv_viewdocs.setPaintFlags(tv_viewdocs.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }

        final AlertDialog alertD = alertDialogBuilder.create();

        tv_viewdocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDocumentsList(notificationDetails.getDocuments());
            }
        });

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDownloaded) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = Uri.parse("file://" + downloadedFile);
                    intent.setDataAndType(uri, "image/jpeg");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    new DownloadImage().execute(IMAGE_LINK + "groupnotifications/" + notificationDetails.getAttachment());
                }
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setMessage("Are you sure you want to delete this notification?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.icon_alertred);
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alertD.dismiss();
                        if (Utilities.isNetworkAvailable(context))
                            new DeleteNotification().execute(notificationDetails.getMsg_details_id());
                        else
                            Utilities.showMessage("Please check your internet connection", context, 2);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertD = builder.create();
                alertD.show();
            }
        });

        alertD.show();
    }

    private void showDocumentsList(final List<GroupNotificationListModel.ResultBean.DocumentsBean> documentList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Document List");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < documentList.size(); i++) {
            arrayAdapter.add("View document " + (i + 1));
        }

        builderSingle.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Utilities.isNetworkAvailable(context))
                    new DownloadDocument().execute(IMAGE_LINK + "groupnotifications/" + documentList.get(which).getDocuments());
                else
                    Utilities.showMessage("Please check your internet connection", context, 2);
            }
        });
        builderSingle.show();
    }

    public class DeleteNotification extends AsyncTask<String, Void, String> {

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
            String res;
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "deleteGroupNotification");
            obj.addProperty("msg_details_id", params[0]);
            res = APICall.JSONAPICall(ApplicationConstants.NOTIFICATIONAPI, obj.toString());
            return res;
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
                        new GroupNotifications_Activity.GetGroupNotification().execute();
                        Utilities.showMessage("Notification deleted successfully", context, 1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class ReadNotification extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String res;
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "markReadNotification");
            obj.addProperty("msg_details_id", params[0]);
            res = APICall.JSONAPICall(ApplicationConstants.NOTIFICATIONAPI, obj.toString());
            return res;
        }
    }

    private class DownloadImage extends AsyncTask<String, Integer, Boolean> {
        int lenghtOfFile = -1;
        int count = 0;
        int content = -1;
        int counter = 0;
        int progress = 0;
        URL downloadurl = null;
        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setCancelable(true);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setMessage("Downloading Image");
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            pd.show();

        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean success = false;
            HttpURLConnection httpURLConnection = null;
            InputStream inputStream = null;
            int read = -1;
            byte[] buffer = new byte[1024];
            FileOutputStream fileOutputStream = null;
            long total = 0;

            try {
                downloadurl = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) downloadurl.openConnection();
                lenghtOfFile = httpURLConnection.getContentLength();
                inputStream = httpURLConnection.getInputStream();

                file = new File(downloadedImagefolder, Uri.parse(params[0]).getLastPathSegment());
                fileOutputStream = new FileOutputStream(file);
                while ((read = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, read);
                    counter = counter + read;
                    publishProgress(counter);
                }
                success = true;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return success;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progress = (int) (((double) values[0] / lenghtOfFile) * 100);
            pd.setProgress(progress);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            pd.dismiss();
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                Utilities.showMessage("Image successfully downloaded", context, 1);
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            }
        }
    }

    private class DownloadDocument extends AsyncTask<String, Integer, Boolean> {
        int lenghtOfFile = -1;
        int count = 0;
        int content = -1;
        int counter = 0;
        int progress = 0;
        URL downloadurl = null;
        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setCancelable(true);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setMessage("Downloading Document");
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            pd.show();

        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean success = false;
            HttpURLConnection httpURLConnection = null;
            InputStream inputStream = null;
            int read = -1;
            byte[] buffer = new byte[1024];
            FileOutputStream fileOutputStream = null;
            long total = 0;

            try {
                downloadurl = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) downloadurl.openConnection();
                lenghtOfFile = httpURLConnection.getContentLength();
                inputStream = httpURLConnection.getInputStream();

                file = new File(downloadedDocumentfolder, Uri.parse(params[0]).getLastPathSegment());
                fileOutputStream = new FileOutputStream(file);
                while ((read = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, read);
                    counter = counter + read;
                    publishProgress(counter);
                }
                success = true;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return success;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progress = (int) (((double) values[0] / lenghtOfFile) * 100);
            pd.setProgress(progress);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            pd.dismiss();
            super.onPostExecute(aBoolean);
            if (aBoolean == true) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse("file://" + file);
                if (downloadurl.toString().contains(".doc") || downloadurl.toString().contains(".docx")) {
                    // Word document
                    intent.setDataAndType(uri, "application/msword");
                } else if (downloadurl.toString().contains(".pdf")) {
                    // PDF file
                    intent.setDataAndType(uri, "application/pdf");
                } else if (downloadurl.toString().contains(".ppt") || downloadurl.toString().contains(".pptx")) {
                    // Powerpoint file
                    intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
                } else if (downloadurl.toString().contains(".xls") || downloadurl.toString().contains(".xlsx")) {
                    // Excel file
                    intent.setDataAndType(uri, "application/vnd.ms-excel");
                } else if (downloadurl.toString().contains(".zip") || downloadurl.toString().contains(".rar")) {
                    // WAV audio file
                    intent.setDataAndType(uri, "application/x-wav");
                } else if (downloadurl.toString().contains(".rtf")) {
                    // RTF file
                    intent.setDataAndType(uri, "application/rtf");
                } else if (downloadurl.toString().contains(".wav") || downloadurl.toString().contains(".mp3")) {
                    // WAV audio file
                    intent.setDataAndType(uri, "audio/x-wav");
                } else if (downloadurl.toString().contains(".gif")) {
                    // GIF file
                    intent.setDataAndType(uri, "image/gif");
                } else if (downloadurl.toString().contains(".jpg") || downloadurl.toString().contains(".jpeg") || downloadurl.toString().contains(".png")) {
                    // JPG file
                    intent.setDataAndType(uri, "image/jpeg");
                } else if (downloadurl.toString().contains(".txt")) {
                    // Text file
                    intent.setDataAndType(uri, "text/plain");
                } else if (downloadurl.toString().contains(".3gp") || downloadurl.toString().contains(".mpg") || downloadurl.toString().contains(".mpeg") || downloadurl.toString().contains(".mpe") || downloadurl.toString().contains(".mp4") || downloadurl.toString().contains(".avi")) {
                    // Video files
                    intent.setDataAndType(uri, "video/*");
                } else {
                    intent.setDataAndType(uri, "*/*");
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        }
    }
}
