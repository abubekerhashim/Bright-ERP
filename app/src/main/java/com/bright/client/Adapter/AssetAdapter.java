package com.bright.client.Adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bright.client.Model.Asset;
import com.bright.client.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class AssetAdapter extends RecyclerView.Adapter<AssetAdapter.ProductViewHolder> {
    private Context context;
    private List<Asset> orderList;
    private OnProductClickListener onItemClickListener;

    public AssetAdapter(Context context, List<Asset> orderList, OnProductClickListener onItemClickListener) {
        this.context = context;
        this.orderList = orderList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_asset, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Asset assets = orderList.get(position);

        holder.assetName.setText(assets.getName());
        holder.assetDetail.setText(assets.getDetail());
        holder.assetId.setText(assets.getId());

        Glide.with(context)
                .load(assets.getImageUrl())
                .placeholder(R.drawable.ic_loading)   // shown while loading
                .error(R.drawable.ic_error)               // shown if load fails
                .centerCrop()
                .into(holder.assetImage);

        holder.assignedBy.setText("Assigned by: " + (assets.getAssignedBy() != null ? assets.getAssignedBy() : "N/A"));
        holder.assignedById.setText("Assigned by ID: " + (assets.getAssignedById() != null ? assets.getAssignedById() : "N/A"));
        holder.assignedTo.setText(assets.getAssigneeName() != null ? assets.getAssigneeName() : "Unassigned");
        holder.assignStatus.setText("Assign Status: " + (assets.getAssignmentStatus() != null ? assets.getAssignmentStatus() : "N/A"));


        if (assets.getAssignedAt() != null && assets.getAssignedAt() != 0) {
            holder.assignedAt.setText(
                    "Assigned at: " + android.text.format.DateFormat
                            .format("dd MMM yyyy, hh:mm a", assets.getAssignedAt()));
        } else {
            holder.assignedAt.setText("Assigned at: N/A");
        }


        if ("pending".equalsIgnoreCase(assets.getAssignmentStatus())) {
            holder.assigningLayout.setVisibility(View.VISIBLE);
        } else {
            holder.assigningLayout.setVisibility(View.GONE);
        }


        holder.btnAccept.setOnClickListener(v ->
                onItemClickListener.onAcceptClick(assets, position));

        holder.btnReject.setOnClickListener(v ->
                onItemClickListener.onRejectClick(assets, position));


        // Click listener for the entire layout
        holder.itemView.setOnClickListener(v -> onItemClickListener.onLayoutClick(assets, position));

        holder.btnMore.setOnClickListener(v -> {

            PopupMenu popupMenu = new PopupMenu(context, holder.btnMore, Gravity.END);
            popupMenu.getMenuInflater().inflate(R.menu.asset_more_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {

                int id = item.getItemId();

                if (id == R.id.action_edit) {
                    onItemClickListener.onEditClick(assets, position);
                    return true;
                }
                else if (id == R.id.action_delete) {
                    onItemClickListener.onDeleteClick(assets, position);
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView assetName, assetDetail, assignedTo, assetId;
        ImageView assetImage;
        ImageView btnMore;

        LinearLayout btnAccept, btnReject, assigningLayout;
        TextView assignStatus, assignedAt, assignedBy, assignedById;


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            assetName = itemView.findViewById(R.id.asset_name);
            assetDetail = itemView.findViewById(R.id.asset_detail);
            assignedTo = itemView.findViewById(R.id.asset_assigned_to);
            assetId = itemView.findViewById(R.id.asset_id);

            assetImage = itemView.findViewById(R.id.asset_image);

            btnMore = itemView.findViewById(R.id.btn_more);

            btnAccept = itemView.findViewById(R.id.btn_asset_accept);
            btnReject = itemView.findViewById(R.id.btn_asset_reject);
            assignStatus = itemView.findViewById(R.id.assign_status);
            assignedAt = itemView.findViewById(R.id.assigned_at);
            assignedBy = itemView.findViewById(R.id.assigned_by);
            assignedById = itemView.findViewById(R.id.assigned_by_id);

            assigningLayout = itemView.findViewById(R.id.assigning_layout);


        }
    }

    public interface OnProductClickListener {
        void onLayoutClick(Asset assets, int position);
        void onDeleteClick(Asset assets, int position);
        void onEditClick(Asset assets, int position);


        void onAcceptClick(Asset assets, int position);
        void onRejectClick(Asset assets, int position);
    }


}
