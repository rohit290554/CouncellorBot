package com.example.councellorbot;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;


public class CollegeAdapter extends ArrayAdapter<College> {

    //storing all the names in the list
    private List<College> colleges;

    //context object
    private Context context;

    //constructor
    public CollegeAdapter(Context context, int resource, List<College> colleges) {
        super(context, resource, colleges);

        this.context = context;
        this.colleges = colleges;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //getting the layoutinflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //getting listview itmes
        final View listViewItem = inflater.inflate(R.layout.master, null, true);
        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
        ImageView imageView = (ImageView) listViewItem.findViewById(R.id.circleView);

        //getting the current name
        final College name = colleges.get(position);

        //setting the name to textview
        textViewName.setText(name.getName());
        String url = name.getImage();

        Glide.with(context)
                .asBitmap()
                .load(url)
                .into(imageView);


        listViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String college_id = name.getId();
                Intent intent = new Intent(context, CollegeDetailActivity.class);
                intent.putExtra("college_id", college_id);
                context.startActivity(intent);
            }
        });


        return listViewItem;
    }
}
