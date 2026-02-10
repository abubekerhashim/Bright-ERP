package com.bright.client.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bright.client.Model.AssetHistory;
import com.bright.client.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AssetHistoryAdapter extends RecyclerView.Adapter<AssetHistoryAdapter.HistoryViewHolder> {

    private Context context;
    private List<AssetHistory> historyList;

    public AssetHistoryAdapter(Context context, List<AssetHistory> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_asset_log, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {

        AssetHistory history = historyList.get(position);

        holder.txtAction.setText(history.getAction());
        holder.txtFrom.setText("From: " + history.getFrom());
        holder.txtTo.setText("To: " + history.getTo());

        // Convert timestamp to date
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.getDefault());
        holder.txtTime.setText(sdf.format(new Date(history.getTimestamp())));
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {

        TextView txtAction, txtFrom, txtTo, txtTime;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAction = itemView.findViewById(R.id.txt_action);
            txtFrom = itemView.findViewById(R.id.txt_from);
            txtTo = itemView.findViewById(R.id.txt_to);
            txtTime = itemView.findViewById(R.id.txt_time);
        }
    }
}
