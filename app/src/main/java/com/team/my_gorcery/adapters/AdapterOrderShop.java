package com.team.my_gorcery.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.team.my_gorcery.FilterOrderShop;
import com.team.my_gorcery.R;
import com.team.my_gorcery.com.team.my_gorcery.activities.OrderDetailsSeller;
import com.team.my_gorcery.model.ModelOrderShop;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrderShop extends RecyclerView.Adapter<AdapterOrderShop.HolderOrderShop>implements Filterable {

     private Context context;
     public ArrayList<ModelOrderShop> orderShopList, filterList;
     private FilterOrderShop filter;

    public AdapterOrderShop(Context context, ArrayList<ModelOrderShop> orderShopList) {
        this.context = context;
        this.orderShopList = orderShopList;
        this.filterList = orderShopList;
    }

    @NonNull
    @Override
    public HolderOrderShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_order_seller, parent, false);
        return new HolderOrderShop(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderShop holder, int position) {

        ModelOrderShop modelOrderShop = orderShopList.get(position);
        final String orderId = modelOrderShop.getOrderId();
        final String orderBy = modelOrderShop.getOrderBy();
        String orderCost = modelOrderShop.getOrderCost();
        String orderStatus = modelOrderShop.getOrderStatus();
        String orderTime = modelOrderShop.getOrderTime();
        final String orderTo = modelOrderShop.getOrderTo();


        //Set user/buyer info
        loadUserInfo(modelOrderShop, holder);

        // Set data
        holder.amountTv.setText("Amount: $" + orderCost);
        holder.statusTv.setText(orderStatus);
        holder.orderIdTv.setText("Order ID: " + orderId);

        // Change order status text color
        if (orderStatus.equals("In Progress")) {
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        } else if (orderStatus.equals("Completed")) {
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.colorGreen));
        } else if (orderStatus.equals("Cancelled")) {
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.colorRed));
        }

        // Convert timestamp to proper format
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(orderTime));
        String formatedDate = DateFormat.format("dd/MM/yyyy", calendar).toString();

        holder.orderDateTv.setText(formatedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 //To open the order details, we need keys: orderId, order To
                Intent intent = new Intent(context, OrderDetailsSeller.class);
                intent.putExtra("orderBy", orderBy); // this loads order info
                intent.putExtra("orderId", orderId); // This loads the info of the user that placed order
                context.startActivity(intent); // we will get these values through intent on OrderDetailsUserActivity

            }
        });

    }

    private void loadUserInfo(ModelOrderShop modelOrderShop, final HolderOrderShop holder) {
        // orderBy contain the uid of the user/buyer
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(modelOrderShop.getOrderBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String email = "" +dataSnapshot.child("email").getValue();
                        holder.emailTv.setText("Email: " + email);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return orderShopList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            // Initiate filter
            filter = new FilterOrderShop(this, filterList);
        }
        return filter;
    }

    // View holder class for row_order_seller
    class HolderOrderShop extends RecyclerView.ViewHolder {


        // View of layout
        private TextView orderIdTv, orderDateTv, emailTv, amountTv, statusTv; //nextTv

        public HolderOrderShop(@NonNull View itemView) {
            super(itemView);

            // Views of row_order_seller
            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            orderDateTv = itemView.findViewById(R.id.orderDateTv);
            emailTv = itemView.findViewById(R.id.emailTv);
            amountTv = itemView.findViewById(R.id.amountTv);
            statusTv = itemView.findViewById(R.id.statusTv);
            //nextTv = itemView.findViewById(R.id.nextTv);


        }

    }
}
