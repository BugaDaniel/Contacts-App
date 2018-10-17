package com.example.dan.uselistview.core;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.dan.uselistview.R;
import java.util.List;

public class CustomAdapter extends BaseAdapter {

    private Context context;
    private List<Person> persons;

    private static class ViewHolderItem{
        TextView textViewItem;
        ImageView contactSmallImage;
    }

    public CustomAdapter(Context applicationContext, List<Person> persons){
        this.context = applicationContext;
        this.persons = persons;
    }

    @Override
    public int getCount() {
        return persons.size();
    }

    @Override
    public Object getItem(int position) {
        return persons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolderItem viewHolder;

        if(convertView == null){
            // inflate the layout
            LayoutInflater inflater = (LayoutInflater.from(context));
            convertView = inflater.inflate(R.layout.single_row, parent, false);

            //set up the ViewHolder
            viewHolder = new ViewHolderItem();
            viewHolder.textViewItem = convertView.findViewById(R.id.text_view);
            viewHolder.contactSmallImage = convertView.findViewById(R.id.contact_image_in_list);

            // store the holder with the view.
            convertView.setTag(viewHolder);
        }
        else{
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        // object item based on the position
        Person person = persons.get(position);
        // set textView and imageView
        viewHolder.textViewItem.setText(person.getFullName());
        int adapterSmallImageWidth = viewHolder.contactSmallImage.getWidth();
        int adapterSmallImageHeight = viewHolder.contactSmallImage.getHeight();
        GlideApp
                .with(context)
                .load(person.getUri())
                .override(adapterSmallImageWidth,adapterSmallImageHeight)
                .into(viewHolder.contactSmallImage);
        return convertView;
    }
}


