//For showing list of transaction
package com.official.gold.gaming.tournamentpubg.ui.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.official.gold.gaming.tournamentpubg.utils.CustomTypefaceSpan;
import com.official.gold.R;
import com.official.gold.gaming.tournamentpubg.models.TransactionDetails;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder> {
    private Context mContext;
    private List<TransactionDetails> mData;

    public TransactionAdapter(Context mContext, List<TransactionDetails> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.transaction_data, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        TransactionDetails transactionDetails = mData.get(position);
        SharedPreferences sp = mContext.getSharedPreferences("currencyinfo", Context.MODE_PRIVATE);
        String selectedcurrency = sp.getString("currency", "₹");

        if (transactionDetails.getNoteid().matches("1") || transactionDetails.getNoteid().matches("2") || transactionDetails.getNoteid().matches("8")) {
            holder.crOrDb.setText("DEBIT");
            holder.crOrDb.setTextColor(mContext.getResources().getColor(R.color.newred));
            holder.status.setVisibility(View.GONE);
            holder.amount.setText("- " + selectedcurrency + transactionDetails.getWithdraw());
            if (TextUtils.equals(selectedcurrency, "₹")) {
                Typeface font = Typeface.DEFAULT_BOLD;
                SpannableStringBuilder SS = new SpannableStringBuilder(mContext.getResources().getString(R.string.Rs));
                SS.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                holder.amount.setText(TextUtils.concat("- ", SS, transactionDetails.getWithdraw()));
            }
            holder.amount.setTextColor(mContext.getResources().getColor(R.color.newred));
        } else if (transactionDetails.getNoteid().matches("0") || transactionDetails.getNoteid().matches("3") || transactionDetails.getNoteid().matches("4") || transactionDetails.getNoteid().matches("5") || transactionDetails.getNoteid().matches("6") || transactionDetails.getNoteid().matches("7")) {
            holder.crOrDb.setText("CREDIT");
            holder.crOrDb.setTextColor(mContext.getResources().getColor(R.color.newgreen));
            holder.status.setVisibility(View.GONE);
            holder.amount.setText("+ " + selectedcurrency + transactionDetails.getDeposit());
            if (TextUtils.equals(selectedcurrency, "₹")) {
                Typeface font = Typeface.DEFAULT_BOLD;
                SpannableStringBuilder SS = new SpannableStringBuilder(mContext.getResources().getString(R.string.Rs));
                SS.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                holder.amount.setText(TextUtils.concat("+ ", SS, transactionDetails.getDeposit()));
            }
            holder.amount.setTextColor(mContext.getResources().getColor(R.color.newgreen));
        } else {
            holder.status.setVisibility(View.VISIBLE);
            holder.crOrDb.setText("DEBIT");
            holder.status.setText("Pending");
            holder.amount.setText("- " + selectedcurrency + transactionDetails.getWithdraw());
            if (TextUtils.equals(selectedcurrency, "₹")) {
                Typeface font = Typeface.DEFAULT_BOLD;
                SpannableStringBuilder SS = new SpannableStringBuilder(mContext.getResources().getString(R.string.Rs));
                SS.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                holder.amount.setText(TextUtils.concat("- ", SS, transactionDetails.getWithdraw()));
            }
        }

        if (transactionDetails.getMatchid().matches("0")) {
            holder.detail.setText(transactionDetails.getNote() + " - #" + transactionDetails.getTransactionid());
        } else {
            holder.detail.setText(transactionDetails.getNote() + " - #" + transactionDetails.getMatchid());
        }
        holder.available.setText(selectedcurrency + String.valueOf(Integer.parseInt(transactionDetails.getWinmoney()) + Integer.parseInt(transactionDetails.getJoinmoney())));
        if (TextUtils.equals(selectedcurrency, "₹")) {
            Typeface font = Typeface.DEFAULT;
            SpannableStringBuilder SS = new SpannableStringBuilder(holder.available.getText().toString());
            SS.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            holder.available.setText(SS);
        }
        holder.time.setText(transactionDetails.getDate());
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

        TextView crOrDb;
        TextView detail;
        TextView time;
        TextView status;
        TextView amount;
        TextView available;
        TextView mobile;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            crOrDb = (TextView) itemView.findViewById(R.id.crordb_t);
            detail = (TextView) itemView.findViewById(R.id.detail_t);
            time = (TextView) itemView.findViewById(R.id.time_t);
            status = (TextView) itemView.findViewById(R.id.status_t);
            amount = (TextView) itemView.findViewById(R.id.amount_t);
            available = (TextView) itemView.findViewById(R.id.available_t);
            mobile = (TextView) itemView.findViewById(R.id.mobile_t);


        }
    }
}
