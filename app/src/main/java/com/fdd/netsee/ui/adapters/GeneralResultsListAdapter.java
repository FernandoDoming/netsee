package com.fdd.netsee.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fdd.netsee.R;
import com.fdd.netsee.helpers.ScanHelper;
import com.fdd.netsee.models.Host;
import com.fdd.netsee.models.Service;

import java.util.List;


/**
 * Coded by fernando on 03/01/16.
 */
public class GeneralResultsListAdapter<T> extends BaseAdapter {

    private Context context;
    private List<T> elements;

    public GeneralResultsListAdapter(Context context, List<T> elements) {
        this.context = context;
        this.elements = elements;
    }

    @Override
    public int getCount() {
        return elements != null ? elements.size() : 0;
    }

    @Override
    public T getItem(int position) {
        return elements.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {      // Inflate the view
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.host_property_row, parent, false);

            /*holder = new HostListAdapter.HostViewHolder();

            holder.title = (TextView) convertView.findViewById(R.id.general_row_title);
            holder.subtitle = (TextView) convertView.findViewById(R.id.general_row_subtitle);
            holder.extra = (TextView) convertView.findViewById(R.id.general_row_extra);
            holder.icon = (ImageView) convertView.findViewById(R.id.general_row_icon);
            convertView.setTag(holder);*/

        } else {        // Recycle the previously inflated view
            //holder = (HostViewHolder) convertView.getTag();
        }

        return convertView;
    }

    public void updateItem(int position, T element) {
        elements.set(position, element);
        notifyDataSetChanged();
    }
}