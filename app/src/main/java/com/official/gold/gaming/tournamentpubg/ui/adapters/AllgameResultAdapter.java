//For show data in completed tab
package com.official.gold.gaming.tournamentpubg.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.official.gold.gaming.tournamentpubg.models.AllGameResultData;
import com.official.gold.gaming.tournamentpubg.utils.CustomTypefaceSpan;
import com.official.gold.R;
import com.official.gold.gaming.tournamentpubg.ui.activities.SelectedResultActivity;
import com.squareup.picasso.Picasso;
import java.util.List;

public class AllgameResultAdapter extends RecyclerView.Adapter<AllgameResultAdapter.MyViewHolder> {

    private Context mContext;
    private List<AllGameResultData> mData;

    public AllgameResultAdapter(Context mContext, List<AllGameResultData> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public AllgameResultAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.allgameresultdata, parent, false);
        return new AllgameResultAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AllgameResultAdapter.MyViewHolder holder, int position) {

        SharedPreferences sp = mContext.getSharedPreferences("currencyinfo", Context.MODE_PRIVATE);
        String selectedcurrency = sp.getString("currency", "₹");
        final AllGameResultData data = mData.get(position);
        if (!data.getMatchbanner().equals("")) {
            Picasso.get().load(data.getMatchbanner()).placeholder(R.drawable.default_battlemania).fit().into(holder.imageView);
        } else {
            holder.imageView.setImageDrawable(mContext.getDrawable(R.drawable.default_battlemania));
        }
        holder.allGametvMatchTitle.setText(data.getMatchname() + " - Match #" + data.getMid());
        String newdate = data.getMatchtime().replace(data.getMatchtime().substring(11, 18), "<br><b>" + data.getMatchtime().substring(11, 18));
        holder.allGametvTime.setText(Html.fromHtml(newdate));
        holder.allGametvPrizeWin.setText(Html.fromHtml("PRIZE POOL<br><b>" + selectedcurrency + " " + data.getWinprize() + "</b>"));
        if (TextUtils.equals(selectedcurrency, "₹")) {
            Typeface font = Typeface.DEFAULT_BOLD;
            SpannableStringBuilder SS = new SpannableStringBuilder(mContext.getResources().getString(R.string.Rs));
            SS.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            holder.allGametvPrizeWin.setText(TextUtils.concat(Html.fromHtml("PRIZE POOL<br>"), SS, Html.fromHtml("<b>" + data.getWinprize() + "</b>")));
        }
        holder.allGametvPerKill.setText(Html.fromHtml("PER KILL<br><b>" + selectedcurrency + " " + data.getPerkill() + "</b>"));
        if (TextUtils.equals(selectedcurrency, "₹")) {
            Typeface font = Typeface.DEFAULT_BOLD;
            SpannableStringBuilder SS = new SpannableStringBuilder(mContext.getResources().getString(R.string.Rs));
            SS.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            holder.allGametvPerKill.setText(TextUtils.concat(Html.fromHtml("PER KILL<br>"), SS, Html.fromHtml("<b>" + data.getPerkill() + "</b>")));
        }
        holder.allGametvEntryFee.setText(Html.fromHtml("<b>" + selectedcurrency + " " + data.getEntryfee() + "</b>"));
        if (TextUtils.equals(selectedcurrency, "₹")) {
            Typeface font = Typeface.DEFAULT_BOLD;
            SpannableStringBuilder SS = new SpannableStringBuilder(mContext.getResources().getString(R.string.Rs));
            SS.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            holder.allGametvEntryFee.setText(TextUtils.concat(Html.fromHtml("<b>"), SS, Html.fromHtml("<b>" + data.getEntryfee() + "</b>")));
        } else {
            holder.allGametvEntryFee.setPadding(10, 5, 5, -5);
        }
        holder.allGametvType.setText(Html.fromHtml("<b>" + data.getType() + "</b>"));
        holder.allGametvVersion.setText(Html.fromHtml("<b>" + data.getVersion() + "</b>"));
        holder.allGametvMap.setText(Html.fromHtml("<b>" + data.getMap() + "</b>"));
        holder.allGameResultCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SelectedResultActivity.class);
                intent.putExtra("M_ID", data.getMid());
                intent.putExtra("BANER", data.getMatchbanner());
                mContext.startActivity(intent);
            }
        });
        holder.watchMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.getMatchurl()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.google.android.youtube");
                mContext.startActivity(intent);
            }
        });
        if (TextUtils.equals(data.getJoinstatus(), "false")) {
            holder.allGameResultStatus.setText("NOT JOINED");
        } else {
            holder.allGameResultStatus.setText("JOINED");
            holder.joinStatusll.setBackgroundColor(mContext.getResources().getColor(R.color.newgreen));
            holder.allGametvEntryFee.setBackgroundColor(mContext.getResources().getColor(R.color.newgreen));
            holder.allGameResultStatus.setBackgroundColor(mContext.getResources().getColor(R.color.newgreen));
        }
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
        ImageView imageView;
        TextView allGametvMatchTitle;
        TextView allGametvTime;
        TextView allGametvPrizeWin;
        TextView allGametvPerKill;
        TextView allGametvEntryFee;
        TextView allGametvType;
        TextView allGametvVersion;
        TextView allGametvMap;
        TextView watchMatch;
        CardView allGameResultCardview;
        TextView allGameResultStatus;
        LinearLayout joinStatusll;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageview);
            allGametvMatchTitle = (TextView) itemView.findViewById(R.id.allgametvmatchtitle);
            allGametvTime = (TextView) itemView.findViewById(R.id.allgametvtime);
            allGametvPrizeWin = (TextView) itemView.findViewById(R.id.allgametvprizewin);
            allGametvPerKill = (TextView) itemView.findViewById(R.id.allgametvperkill);
            allGametvEntryFee = (TextView) itemView.findViewById(R.id.allgametventryfee);
            allGametvType = (TextView) itemView.findViewById(R.id.allgametvtype);
            allGametvVersion = (TextView) itemView.findViewById(R.id.allgametvversion);
            allGametvMap = (TextView) itemView.findViewById(R.id.allgametvmap);
            watchMatch = (TextView) itemView.findViewById(R.id.watchmatch);
            allGameResultCardview = (CardView) itemView.findViewById(R.id.allgameresultcardview);
            allGameResultStatus = (TextView) itemView.findViewById(R.id.allgameresultstatus);
            joinStatusll = (LinearLayout) itemView.findViewById(R.id.joinstatusll);
        }
    }
}
