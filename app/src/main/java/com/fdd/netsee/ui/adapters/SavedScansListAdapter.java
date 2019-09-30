package com.fdd.netsee.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.RecyclerView;

import com.fdd.netsee.R;
import com.fdd.netsee.ScanResultActivity;
import com.fdd.netsee.constants.Extras;
import com.fdd.netsee.models.Host;
import com.fdd.netsee.models.ScanResult;
import com.fdd.netsee.models.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SavedScansListAdapter extends RecyclerView.Adapter<SavedScansListAdapter.SavedScanViewHolder> {

    private List<ScanResult> dataset;
    private RecyclerView recyclerView;

    private boolean multiSelect = false;
    private List<ScanResult> selectedItems = new ArrayList<>();

    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            multiSelect = true;
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_actions_bar_context, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.discard_button:
                    for (ScanResult r : selectedItems) {
                        File outputFile = new File(r.filepath);
                        outputFile.delete();
                        dataset.remove(r);
                    }
                    break;
            }

            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            multiSelect = false;
            selectedItems.clear();
            notifyDataSetChanged();
        }
    };

    static class SavedScanViewHolder extends RecyclerView.ViewHolder {
        View root;
        SavedScanViewHolder(View root) {
            super(root);
            this.root = root;
        }
    }

    public SavedScansListAdapter(List<ScanResult> dataset) {
        this.dataset = dataset;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public SavedScanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.saved_scan_list_item, parent, false);

        return new SavedScanViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedScanViewHolder holder, int position) {
        ScanResult result = dataset.get(position);
        Context ctx = holder.root.getContext();

        TextView title = holder.root.findViewById(R.id.scan_result_target);
        TextView subtitle = holder.root.findViewById(R.id.scan_result_date);
        ImageView typeIcon = holder.root.findViewById(R.id.summary_scan_icon);
        TextView onlineView = holder.root.findViewById(R.id.summary_scan_online);
        TextView offlineView = holder.root.findViewById(R.id.summary_scan_offline);

        title.setText( result.getTitle() );

        Date scandate = new Date(result.getEndTime() * 1000);
        String formatted = new SimpleDateFormat("dd-mm-yyyy hh:mm").format(scandate);
        subtitle.setText( ctx.getString(R.string.scanned_at, formatted) );

        if (result.getHosts().size() == 1) {
            Host host = result.getHosts().get(0);
            typeIcon.setImageDrawable(
                    ctx.getDrawable(R.drawable.ic_ethernet)
            );

            List<Service> services = host.getServices();
            int openServices = 0, closedServices = 0;

            for (Service s : services) {
                if (s.getStatus().isOpen()) {
                    openServices++;
                }
                else {
                    closedServices++;
                }
            }

            onlineView.setText( Integer.toString(openServices) );
            offlineView.setText( Integer.toString(closedServices) );
        }
        else {
            int upHosts = 0, downHosts = 0;

            for (Host h : result.getHosts()) {
                if (h.getStatus().isUp()) {
                    upHosts++;
                }
                else {
                    downHosts++;
                }
            }

            onlineView.setText( Integer.toString(upHosts) );
            offlineView.setText( Integer.toString(downHosts) );
        }

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = recyclerView.getChildLayoutPosition(view);
                ScanResult result = dataset.get(index);

                if (multiSelect) {
                    selectItem(result, view);
                }
                else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ScanResultActivity.class);
                    intent.putExtra(Extras.SCAN_RESULT_EXTRA, result);
                    context.startActivity(intent);
                }
            }
        });

        holder.root.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int index = recyclerView.getChildLayoutPosition(view);
                ScanResult result = dataset.get(index);

                Context context = view.getContext();
                AppCompatActivity activity = (AppCompatActivity) context;
                activity.startSupportActionMode(actionModeCallbacks);
                selectItem(result, view);

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void updateDataset(List<ScanResult> dataset) {
        this.dataset = dataset;
        notifyDataSetChanged();
    }

    private void selectItem(ScanResult item, View view) {
        if (multiSelect) {
            if (selectedItems.contains(item)) {
                selectedItems.remove(item);
                highlightView(view, false);
            } else {
                selectedItems.add(item);
                highlightView(view, true);
            }
        }
    }

    private void highlightView(View view, boolean highlighted) {
        if (highlighted) {
            view.setBackgroundColor(
                    view.getContext().getResources().getColor(R.color.highlight_color)
            );
        }
        else {
            view.setBackgroundColor(
                    view.getContext().getResources().getColor(R.color.white)
            );
        }
    }
}
