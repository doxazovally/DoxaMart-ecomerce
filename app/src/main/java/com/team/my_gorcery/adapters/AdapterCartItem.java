package com.team.my_gorcery.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.team.my_gorcery.R;
import com.team.my_gorcery.com.team.my_gorcery.activities.shop_DetailsActivity;
import com.team.my_gorcery.model.ModelCartItem;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterCartItem extends RecyclerView.Adapter<AdapterCartItem.HolderCartItem> {
    private Context context;
    private ArrayList<ModelCartItem> cartItems;

    public AdapterCartItem(Context context, ArrayList<ModelCartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public HolderCartItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout row_cartItem.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_cart_confirm, parent, false);
        return new HolderCartItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCartItem holder, final int position) {
        // Get data
        ModelCartItem modelCartItem = cartItems.get(position);

        final String id = modelCartItem.getId();
        String Pid = modelCartItem.getpId();
        String price = modelCartItem.getPrice();
        final String cost = modelCartItem.getCost();
        String quantity = modelCartItem.getQuantity();
        final String name = modelCartItem.getName();

        // Set data
        holder.itemTitleTv.setText(""+name);
        holder.itemPriceTv.setText(""+cost);
        holder.itemQuantityTv.setText("[x"+quantity+"]");
        holder.itemPriceEachTv.setText(""+price);


        // Handle remove click listener, delete item from cart
        holder.itemRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Will create a table if it does not exist, but in that case it has to exist
                EasyDB easyDB = EasyDB.init(context, "ITEM_DB")
                        .setTableName("Item_Table")
                        .addColumn(new Column("Item_Id", new String[]{"text", "unique"}))
                        .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Price_Each", new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                        .doneTableColumn();

                easyDB.deleteRow(1, id);
                Toast.makeText(context, "Remove form your cart....", Toast.LENGTH_SHORT).show();

                // Refresh list
                cartItems.remove(position);
                notifyItemChanged(position);
                notifyDataSetChanged();

                double kalu = Double.parseDouble((((shop_DetailsActivity)context).allTotalPriceTv.getText().toString().trim().replace("$", "")));
                double totalPrice = kalu - Double.parseDouble(cost.replace("$", ""));
                double deliveryFee = Double.parseDouble((((shop_DetailsActivity)context).deliveryFee.replace("$", "")));
                double sTotalPrice = Double.parseDouble(String.format("%.2f", totalPrice)) - Double.parseDouble(String.format("%.2f", deliveryFee));
                ((shop_DetailsActivity)context).allTotalPrice = 0.00;
                ((shop_DetailsActivity)context).sTotalTv.setText("$"+String.format("%.2f", sTotalPrice));
                ((shop_DetailsActivity)context).allTotalPriceTv.setText("$"+String.format("%.2f", Double.parseDouble(String.format("%.2f", totalPrice))));

                // After removing item from  cart, update cart count
                ((shop_DetailsActivity)context).cartCount();
            }
        });
    }


    @Override
    public int getItemCount() {

        return cartItems.size(); // Return number of cart records
    }

    // View holder class
    class HolderCartItem extends RecyclerView.ViewHolder {

        // UI views of row_cartItems.xml
        private TextView itemTitleTv, itemPriceTv, itemPriceEachTv, itemQuantityTv, itemRemove;
        public HolderCartItem(@NonNull View itemView) {
            super(itemView);

            itemTitleTv = itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv = itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv = itemView.findViewById(R.id.itemPriceEachTv);
            itemQuantityTv = itemView.findViewById(R.id.itemQuantityTv);
            itemRemove = itemView.findViewById(R.id.itemRemove);



        }
    }
}
