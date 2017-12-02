package com.narwal.parvesh.findunfollowers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Parvesh on 24-Dec-16.
 */

public class NonFollowersAdapter extends BaseAdapter {

    private UserTwitterData data;
    private Context context;
    private List<String> nfb_screenname;

    public NonFollowersAdapter(Context context, UserTwitterData data) {
        this.context = context;
        this.data = data;
        this.nfb_screenname = data.getNfb_screen_names();

    }

    @Override
    public int getCount() {
        return nfb_screenname.size();
    }

    @Override
    public Object getItem(int position) {
        return nfb_screenname.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder{
        ImageView userImg;
        TextView userName;

        ViewHolder(View v){
            userImg = (ImageView) v.findViewById(R.id.ivUserImg);
            userName = (TextView) v.findViewById(R.id.tvUserName);
            userName.setTypeface(FontCache.get("font/consola.ttf", context));
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View grid = convertView;

        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            grid = layoutInflater.inflate(R.layout.single_non_follower, parent, false);

            holder = new ViewHolder(grid);
            grid.setTag(holder);

        } else {
            holder = (ViewHolder) grid.getTag();

        }



        //holder.userImg.setImageResource();
        Picasso.with(context).load(data.getProfile_pic_ids().get(position)).into(holder.userImg);
        holder.userName.setText("@"+data.getNfb_screen_names().get(position));
        //holder.emoName.setTypeface(Typeface.createFromAsset(context.getApplicationContext().getAssets(), "fonts/TravelingTypewriter.ttf" ));


        return grid;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }


}
