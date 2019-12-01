package in.oriange.joinsta.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.oriange.joinsta.models.OfferDetailsModel;

public class OffersProfessionalAdapter extends RecyclerView.Adapter<OffersProfessionalAdapter.MyViewHolder> {

    public OffersProfessionalAdapter(Context context, List<OfferDetailsModel.ResultBean.ProfessionalsBean> professionalOffersList) {

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
