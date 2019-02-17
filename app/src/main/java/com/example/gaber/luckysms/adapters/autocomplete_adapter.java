package com.example.gaber.luckysms.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaber.luckysms.R;
import com.example.gaber.luckysms.custom.PicassoCircleTransformation;
import com.example.gaber.luckysms.model.autocomplete_contact_model;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class autocomplete_adapter extends ArrayAdapter<autocomplete_contact_model> {
private List<autocomplete_contact_model> countryListFull;
private Context context;

public autocomplete_adapter(@NonNull Context context, @NonNull List<autocomplete_contact_model> countryList) {
        super(context, 0, countryList);
        countryListFull = new ArrayList<>(countryList);
        context=context;
        }

@NonNull
@Override
public Filter getFilter() {
        return countryFilter;
        }

@NonNull
@Override
public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
        convertView = LayoutInflater.from(getContext()).inflate(
        R.layout.contact_item, parent, false
        );
        }

        TextView name = convertView.findViewById(R.id.contact_name);
        TextView number = convertView.findViewById(R.id.contact_number);
        ImageView imageViewFlag = convertView.findViewById(R.id.contact_image);

        autocomplete_contact_model data = getItem(position);

        if (data != null) {
            name.setText(data.contact_name);
            number.setText(data.contact_phoneNumber);
            Picasso.with(context)
                    .load(data.contact_image)
                    .placeholder(R.drawable.nothumbinal)
                    .transform(new PicassoCircleTransformation())
                    .into(imageViewFlag, new Callback() {
                        @Override
                        public void onSuccess() {}
                        @Override public void onError() {
                            Toast.makeText(context,"error loading image",Toast.LENGTH_LONG).show();
                        }
                    });
        }

        return convertView;
        }

private Filter countryFilter = new Filter() {
@Override
protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        List<autocomplete_contact_model> suggestions = new ArrayList<>();

        if (constraint == null || constraint.length() == 0) {
        suggestions.addAll(countryListFull);
        } else {
        String filterPattern = constraint.toString().toLowerCase().trim();

        for (autocomplete_contact_model item : countryListFull) {
        if (item.contact_name.toLowerCase().contains(filterPattern)|| item.contact_phoneNumber.contains(filterPattern)) {
        suggestions.add(item);
        } }

        }

        results.values = suggestions;
        results.count = suggestions.size();

        return results;
        }

@Override
protected void publishResults(CharSequence constraint, FilterResults results) {
        clear();
        addAll((List) results.values);
        notifyDataSetChanged();
        }

@Override
public CharSequence convertResultToString(Object resultValue) {
        return ((autocomplete_contact_model) resultValue).contact_name;
        }
        };
        }
