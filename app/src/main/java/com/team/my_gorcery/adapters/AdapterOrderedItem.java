package com.team.my_gorcery.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.team.my_gorcery.R;
import com.team.my_gorcery.model.ModelOrderedItem;

import java.util.ArrayList;

public class AdapterOrderedItem extends RecyclerView.Adapter<AdapterOrderedItem.HolderOrderedItem> {

    private Context context;
    private ArrayList<ModelOrderedItem> orderedItemList;


    public AdapterOrderedItem(Context context, ArrayList<ModelOrderedItem> orderedItemArrayList) {
        this.context = context;
        this.orderedItemList = orderedItemArrayList;
    }

    @NonNull
    @Override
    public HolderOrderedItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_ordereditem, parent, false);
        return new HolderOrderedItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderedItem holder, int position) {

        // Get data at position
        ModelOrderedItem modelOrderedItem = orderedItemList.get(position);
        String getpId = modelOrderedItem.getpId();
        String name = modelOrderedItem.getName();
        String price = modelOrderedItem.getPrice();
        String cost = modelOrderedItem.getCost();
        String quantity = modelOrderedItem.getQuantity();

        // Set data
        holder.O_itemTitleTv.setText(name);
        holder.O_itemPriceTv.setText("$" +cost);
        holder.O_itemPriceEachTv.setText("$" +price);
        holder.O_itemQuantityTv.setText("[" + quantity + "]");

    }

    @Override
    public int getItemCount() {
        return orderedItemList.size(); // return the list size
    }

    // View holder class
    class HolderOrderedItem extends RecyclerView.ViewHolder{

        // row_OrderedItem views
        private TextView O_itemTitleTv, O_itemPriceTv, O_itemPriceEachTv, O_itemQuantityTv;

        public HolderOrderedItem(@NonNull View itemView) {
            super(itemView);

            O_itemTitleTv = itemView.findViewById(R.id.O_itemTitleTv);
            O_itemPriceTv = itemView.findViewById(R.id.O_itemPriceTv);
            O_itemPriceEachTv = itemView.findViewById(R.id.O_itemPriceEachTv);
            O_itemQuantityTv = itemView.findViewById(R.id.O_itemQuantityTv);

        }
    }
}
