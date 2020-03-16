package in.oriange.joinsta.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import in.oriange.joinsta.R;
import in.oriange.joinsta.models.ContryCodeModel;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class CountryCodeAdapter extends RecyclerView.Adapter<CountryCodeAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<ContryCodeModel> countryCodeList;

    public CountryCodeAdapter(Context context, ArrayList<ContryCodeModel> countryCodeList) {
        this.context = context;
        this.countryCodeList = countryCodeList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_1, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int pos) {
        final int position = holder.getAdapterPosition();

        holder.tv_name.setText(countryCodeList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return countryCodeList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;

        public MyViewHolder(@NonNull View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}