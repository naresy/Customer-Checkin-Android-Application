package com.example.st;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class service_adapter extends RecyclerView.Adapter<service_adapter.ProductViewHolder> implements SpinnerAdapter {


    private Context mCtx;
    private List<service> productList;

    public service_adapter(Context mCtx, List<service> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.service_list, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        service product = productList.get(position);

        //loading the image


        holder.textViewTitle.setText(product.getServicename());

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle;


        public ProductViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.servicename);

        }
    }
}