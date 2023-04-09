package org.wood.guidingall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.wood.guidingall.R;
import org.wood.guidingall.tools.JSON;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Danny on 2014/11/3.
 */
public class FriendListAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<HashMap<String, Object>> friendList;
    private class ViewHolder {
        TextView textView;
        ImageView imageView;
    }

    public FriendListAdapter(Context context, List<HashMap<String, Object>> list) {
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        friendList = list;
    }

    @Override
    public int getCount() {
        return friendList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.listview_item, null);

            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView)convertView.findViewById(R.id.tv_friendName);
            viewHolder.imageView = (ImageView)convertView.findViewById(R.id.iv_isFriend);

            //利用setTag()方法將convertView與viewHolder建立View元件之間的階層關聯，換句話說，convertView為父元件，viewHolder屬性所參照的物件為子元件*/
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(!(boolean)friendList.get(position).get(JSON.KEY_ISFRIEND))
            viewHolder.imageView.setVisibility(View.INVISIBLE);
        else
            viewHolder.imageView.setVisibility(View.VISIBLE);

        viewHolder.textView.setText((String)friendList.get(position).get(JSON.KEY_USERNAME));

        return convertView;
    }
}
