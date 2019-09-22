package com.fdd.netsee.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fdd.netsee.R;
import com.fdd.netsee.models.Service;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class ServicesAdapter extends ArrayAdapter {

    private Context mContext;
    private int resourceLayout;

    public ServicesAdapter(Context context, int resource, List<Service> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        if (v == null)
        {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }

        Service service = (Service) getItem(position);
        if (service != null)
        {
            TextView serviceName = v.findViewById(R.id.service_name);
            TextView serviceVersion = v.findViewById(R.id.service_version);
            ChipGroup chipGroup = v.findViewById(R.id.service_chips);

            String name = service.getProtocol() + "/" + service.getPort();
            if (service.getService() != null) {
                name += " - " + service.getService();
            }
            serviceName.setText(name);

            if (service.getVersion() != null) {
                serviceVersion.setText( service.getVersion() );
            }
            else {
                serviceVersion.setText( R.string.no_service_version_info );
            }

            chipGroup.removeAllViews();
            addChipToContainer(chipGroup, service.getStatus().getState());
            addChipToContainer(chipGroup, service.getStatus().getReason());
        }

        return v;
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
