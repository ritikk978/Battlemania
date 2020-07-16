//For show data in referrals list
package com.official.gold.gaming.tournamentpubg.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.official.gold.R;
import com.official.gold.gaming.tournamentpubg.models.ReferralInfo;
import java.util.List;

public class ReferralAdapter extends RecyclerView.Adapter<ReferralAdapter.MyViewHolder> {
    private Context mContext;
    private List<ReferralInfo> mData;

    public ReferralAdapter(Context mContext, List<ReferralInfo> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public ReferralAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.referral_data, parent, false);
        return new ReferralAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReferralAdapter.MyViewHolder holder, int position) {
        ReferralInfo referralInfo = mData.get(position);
        holder.rDate.setText(referralInfo.getData());
        holder.rPlayerName.setText(referralInfo.getPlayername());
        holder.rStatus.setText(referralInfo.getStatus());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView rDate;
        TextView rPlayerName;
        TextView rStatus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rDate = (TextView) itemView.findViewById(R.id.rdate);
            rPlayerName = (TextView) itemView.findViewById(R.id.rplayername);
            rStatus = (TextView) itemView.findViewById(R.id.rstatus);
        }
    }
}
