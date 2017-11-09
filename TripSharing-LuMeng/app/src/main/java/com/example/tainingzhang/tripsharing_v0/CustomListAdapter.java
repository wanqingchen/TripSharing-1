package com.example.tainingzhang.tripsharing_v0;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Taining Zhang on 11/1/17.
 */

class CustomListAdapter extends ArrayAdapter<String> {
    String[] username;
    String[] usercomments;
    public CustomListAdapter(@NonNull Context context, String[] username, String[] usercomments) {
        super(context, R.layout.custom_list, username);
        this.usercomments = usercomments;
        this.username = username;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        if(position == username.length - 1) {
            View customView = layoutInflater.inflate(R.layout.custom_list_button, parent, false);
            return customView;
        }
        View customView = layoutInflater.inflate(R.layout.custom_list, parent, false);

        TextView textView1 = (TextView) customView.findViewById(R.id.textView1);
        TextView textView2 = (TextView) customView.findViewById(R.id.textView2);

        textView1.setText(username[position] + ":");
        textView2.setText(usercomments[position]);
        return customView;
    }
}
