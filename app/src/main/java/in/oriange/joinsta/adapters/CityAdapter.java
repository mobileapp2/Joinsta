package in.oriange.joinsta.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.MainDrawer_Activity;
import in.oriange.joinsta.activities.SelectCity_Activity;
import in.oriange.joinsta.models.GetCityListModel;
import in.oriange.joinsta.utilities.UserSessionManager;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<GetCityListModel.ResultBean> cityList;
    private UserSessionManager session;
    private int requestCode;

    public CityAdapter(Context context, ArrayList<GetCityListModel.ResultBean> cityList, int requestCode) {
        this.context = context;
        this.cityList = cityList;
        this.requestCode = requestCode;

        session = new UserSessionManager(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_city, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();

        final GetCityListModel.ResultBean cityDetails = cityList.get(position);

        holder.tv_city.setText(cityDetails.getCity_name());
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_city;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_city = itemView.findViewById(R.id.tv_city);
        }
    }
}
