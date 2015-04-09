package com.zoltarianie.ftpclient.src;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.zoltarianie.ftpclient.R;

public class ActionMenu {

    private Spinner spinner_action;
    private Context context;
    private Event event;
    private String side;
    private String[] Languages = {};

    public ActionMenu(String side, Spinner spinner_action, Context context, Event event) {
        this.spinner_action = spinner_action;
        this.context = context;
        this.event = event;
        this.side = side;

        spinner_action.setAdapter(new MyAdapter(context, R.layout.action_menu_poz, Languages));
        spinner_action.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> parent) { }
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedAction(pos, id);
            }
        });
    }

    public void setNewMenu(String[] Languages) {
        this.Languages = new String[Languages.length+1];
        this.Languages[0] = "Action";
        for (int j = 0; j < Languages.length; j++) {
            this.Languages[j+1] = Languages[j];
        }
        spinner_action.setAdapter(new MyAdapter(context, R.layout.action_menu_poz, this.Languages));
    }

    private void selectedAction(int pos, long id) {
        spinner_action.setSelection(0);

        if(pos != 0){
            event.dispath("act_"+side, Languages[pos]);
        }
    }

    public class MyAdapter extends ArrayAdapter {

        public MyAdapter(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            //LayoutInflater inflater = context.getLayoutInflater();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            View layout = inflater.inflate(R.layout.action_menu_poz, parent, false);
            TextView tvLanguage = (TextView) layout.findViewById(R.id.tvActionMenu);
            tvLanguage.setText(Languages[position]);
            if (position != 0) {
                ImageView img = (ImageView) layout.findViewById(R.id.ivActionMenu);
                img.setVisibility(View.GONE);
            }

            return layout;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }
    }
}