package in.oriange.joinsta.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.ProfileListModel;

public class ProfileViewAdapter extends RecyclerView.Adapter<ProfileViewAdapter.MyViewHolder> {

    private List<ProfileListModel> resultArrayList;
    private Context context;

    public ProfileViewAdapter(Context context, List<ProfileListModel> resultArrayList) {
        this.context = context;
        this.resultArrayList = resultArrayList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_profile, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int pos) {
        final int position = holder.getAdapterPosition();
        final ProfileListModel profileDetails = resultArrayList.get(position);

        holder.tv_type.setText(profileDetails.getType());
        holder.tv_subtype.setText(profileDetails.getSubType());

    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_type, tv_subtype;

        public MyViewHolder(View view) {
            super(view);
            tv_type = view.findViewById(R.id.tv_type);
            tv_subtype = view.findViewById(R.id.tv_subtype);
        }
    }
}
