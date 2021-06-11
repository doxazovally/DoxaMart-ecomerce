package com.team.my_gorcery.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.team.my_gorcery.R;
import com.team.my_gorcery.com.team.my_gorcery.activities.OrderDetailsUserActivity;
import com.team.my_gorcery.model.ModelOrderUser;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrderUser extends RecyclerView.Adapter<AdapterOrderUser.HolderOrderUser> {

    private Context context;
    private ArrayList<ModelOrderUser> orderUserList;

    public AdapterOrderUser(Context context, ArrayList<ModelOrderUser> orderUserList) {
        this.context = context;
        this.orderUserList = orderUserList;
    }

    @NonNull
    @Override
    public HolderOrderUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_order_user, parent, false);
        return new HolderOrderUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderUser holder, int position) {
        // Get data
        ModelOrderUser modelOrderUser = orderUserList.get(position);
        final String orderId = modelOrderUser.getOrderId();
        String orderBy = modelOrderUser.getOrderBy();
        String orderCost = modelOrderUser.getOrderCost();
        String orderStatus = modelOrderUser.getOrderStatus();
        String orderTime = modelOrderUser.getOrderTime();
         final String orderTo = modelOrderUser.getOrderTo();

        // Get shop info
        loadShopInfo(modelOrderUser, holder);


        // Set data
        holder.amountTv.setText("Amount: $" + orderCost);
        holder.statusTv.setText(orderStatus);
        holder.orderIdTv.setText("OrderID: " + orderId);

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

        holder.dateTv.setText(formatedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // To open the order details, we need keys: orderId, order To
                Intent intent = new Intent(context, OrderDetailsUserActivity.class);
                intent.putExtra("orderTo", orderTo);
                intent.putExtra("orderId", orderId);
                context.startActivity(intent); // we will get these values through intent on OrderDetailsUserActivity

            }
        });
    }

    private void loadShopInfo(ModelOrderUser modelOrderUser, final HolderOrderUser holder) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(modelOrderUser.getOrderTo())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String shopName = "" +dataSnapshot.child("shop").getValue();
                        holder.shopNameTv.setText("shop: " + shopName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return orderUserList.size();
    }
    // View holder class
    class HolderOrderUser extends RecyclerView.ViewHolder {


        // View of layout
        private TextView orderIdTv, dateTv, shopNameTv, amountTv, statusTv; //nextTv

        public HolderOrderUser(@NonNull View itemView) {
            super(itemView);

            // Views of row_order
            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            shopNameTv = itemView.findViewById(R.id.shopNameTv);
            amountTv = itemView.findViewById(R.id.amountTv);
            statusTv = itemView.findViewById(R.id.statusTv);
            //nextTv = itemView.findViewById(R.id.nextTv);


        }

    }
}
