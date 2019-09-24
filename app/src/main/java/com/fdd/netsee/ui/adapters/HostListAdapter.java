package com.fdd.netsee.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fdd.netsee.R;
import com.fdd.netsee.constants.Extras;
import com.fdd.netsee.helpers.ScanHelper;
import com.fdd.netsee.models.Host;
import com.fdd.netsee.HostDetailActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class HostListAdapter extends RecyclerView.Adapter<HostListAdapter.HostViewHolder> {
    private List<Host> dataset;
    private RecyclerView recyclerView;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class HostViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        View root;
        HostViewHolder(View root) {
            super(root);
            this.root = root;
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    public HostListAdapter(List<Host> dataset) {
        this.dataset = dataset;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public HostViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType)
    {
        // create a new view
        View root = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.host_list_item, parent, false);

        return new HostViewHolder(root);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(HostViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Host host = dataset.get(position);
        TextView hostname = holder.root.findViewById(R.id.host_title);
        hostname.setText( host.getTitle() );

        ImageView icon = holder.root.findViewById(R.id.host_icon);
        ImageView onlineIndicator = holder.root.findViewById(R.id.online_indicator);
        ChipGroup chipGroup = holder.root.findViewById(R.id.chip_container);

        final Context context = holder.itemView.getContext();
        Resources res = context.getResources();

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = recyclerView.getChildLayoutPosition(view);
                Host host = dataset.get(index);

                Intent intent = new Intent(context, HostDetailActivity.class);
                intent.putExtra(Extras.HOST_EXTRA, host);
                context.startActivity(intent);
            }
        });

        if (host.getStatus().isUp()) {
            TextView services = holder.root.findViewById(R.id.host_services);
            onlineIndicator.setImageDrawable(res.getDrawable(R.drawable.online_indicator));

            String servicesText;

            if (host.getServices().size() > 0) {
                servicesText = String.format(
                        res.getString(R.string.host_services), host.getServices().size()
                );
            }
            else {
                servicesText = res.getString(R.string.no_services);
            }
            services.setText(servicesText);
        }
        else {
            onlineIndicator.setImageDrawable(res.getDrawable(R.drawable.offline_indicator));
        }

        if (host.isMe()) {
            icon.setImageDrawable(
                    res.getDrawable(R.drawable.ic_android)
            );
        }
        else if (host.getOs() != null && !host.getOs().isEmpty()) {
            icon.setImageDrawable(
                    res.getDrawable( ScanHelper.getDrawableIcon(host.getOs()) )
            );
        }

        if (host.getMacVendor() != null) {
            addChipToContainer(chipGroup, host.getMacVendor());
        }
        if (host.getOs() != null) {
            addChipToContainer(chipGroup, host.getOs());
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void updateData(List<Host> hosts) {
        dataset.clear();
        dataset.addAll(hosts);
        notifyDataSetChanged();
    }

    private void addChipToContainer(ChipGroup container, String text)
    {
        Chip chip = new Chip(container.getContext());
        chip.setCheckable(false);
        chip.setClickable(false);
        chip.setText(text);
        container.addView(chip);
    }
}