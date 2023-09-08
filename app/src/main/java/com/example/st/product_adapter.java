package com.example.st;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;

public class product_adapter extends BaseAdapter {
    Context context;
    List<product_data>product_data;
    public product_adapter(List<product_data> listValue, Context context)
    {
        this.context = context;
        this.product_data = listValue;
    }

    public int getCount()

    {
        try {


            return this.product_data.size();
        }catch (NullPointerException e)
        {
            e.printStackTrace();



        }
        return 0;

    }

    @Override
    public Object getItem(int position)
    {
        return this.product_data.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        fetch_service viewItem = null;
        product_data  subcategory_data=product_data.get(position);

        if(convertView == null)
        {
            viewItem = new fetch_service();

            LayoutInflater layoutInfiater = (LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.fetch_checkin, null);

            viewItem.get_service = (TextView)convertView.findViewById(R.id.service);
            viewItem.click_here=convertView.findViewById(R.id.click_me);
            viewItem.get_time = (TextView)convertView.findViewById(R.id.time);
            viewItem.get_staff=convertView.findViewById(R.id.artist);
            viewItem.clinet_name=convertView.findViewById(R.id.name);

            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (fetch_service) convertView.getTag();
        }

        viewItem.get_time.setText(product_data.get(position).time);
        viewItem.get_service.setText(product_data.get(position).service);
        viewItem.get_staff.setText(product_data.get(position).artist);
        viewItem.clinet_name.setText(product_data.get(position).client_name);



        viewItem.click_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context.getApplicationContext(), controll_checkin.class);
                intent.putExtra("id",product_data.get(position).id);
                intent.putExtra("service",product_data.get(position).service);
                intent.putExtra("staff",product_data.get(position).artist);
                intent.putExtra("name",product_data.get(position).client_name);


                context.startActivity(intent);

            }
        });




        return convertView;
    }
}

class fetch_service
{
    TextView get_time,get_service,get_staff,clinet_name;

    LinearLayout click_here;



}
