package com.team.my_gorcery.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.team.my_gorcery.FilterProductsUser;
import com.team.my_gorcery.R;
import com.team.my_gorcery.com.team.my_gorcery.activities.shop_DetailsActivity;
import com.team.my_gorcery.model.ModelProduct;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterProductUser extends RecyclerView.Adapter<AdapterProductUser.HolderProductUser> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productList, filterList;
    private FilterProductsUser filter;

    public AdapterProductUser(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList = productList;
    }

    @NonNull
    @Override
    public HolderProductUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_user, parent, false);
        return new HolderProductUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductUser holder, int position) {
        // Get data
        final ModelProduct modelProduct = productList.get(position);
        String discountAvailability = modelProduct.getDiscountAvailability();
        String discountNote = modelProduct.getDiscountNote();
        String productCategory = modelProduct.getProductCategory();
        String priceDiscount = modelProduct.getPriceDiscount();
        String productPrice = modelProduct.getProductPrice();
        String productDescription = modelProduct.getProductDescription();
        String productName = modelProduct.getProductName();
        String productQuantity = modelProduct.getProductQuantity();
        String productId = modelProduct.getProductId();
        String timestamp = modelProduct.getTimestamp();
        String productImage = modelProduct.getProductImage();
        String returnDate = modelProduct.getReturnDate();
        String deliveryDate = modelProduct.getDeliveryDate();

        // Set data
        holder.nameTv.setText(productName);
        holder.descriptionTv.setText(productDescription);
        holder.quantityTv.setText(productQuantity);
        holder.originalPriceTv.setText("$"+productPrice);
        holder.discountPriceTv.setText("$"+priceDiscount);
        holder.discountNoteTv.setText(discountNote);

        if(discountAvailability.equals("true")){
            //Product is on discount
            holder.discountPriceTv.setVisibility(View.VISIBLE);
            holder.discountNoteTv.setVisibility(View.VISIBLE);
            holder.originalPriceTv.setPaintFlags(holder.originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); // Add strike through on original price
            holder.originalPriceTv.setTextColor(Color.RED); // Add strike through on original price
        }
        else {
            //Product is not on discount
            holder.discountPriceTv.setVisibility(View.GONE);
            holder.discountNoteTv.setVisibility(View.GONE);
            holder.originalPriceTv.setPaintFlags(0);        }


        try {
            Picasso.get().load(productImage).placeholder(R.drawable.ic_add_shopping_cart_accent).into(holder.productIcon);
        }
        catch (Exception e){
            holder.productIcon.setImageResource(R.drawable.ic_add_shopping_cart_accent);
        }

        holder.productIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showQuantityDialog(modelProduct);
            }
        });

        holder.addToCartTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Add product to cart
                showQuantityDialog(modelProduct);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });

    }

    private double cost = 0;
    private double finalCost = 0;
    private int quantity = 0;

    private void showQuantityDialog(ModelProduct modelProduct){
        //  Inflate layout for dialog
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_quantity, null);
        // initialize layout view
        // Show product details
        ImageView productIv = view.findViewById(R.id.productIv);
        final TextView P_name = view.findViewById(R.id.P_name);
        TextView P_Q = view.findViewById(R.id.P_Q);
        TextView P_D = view.findViewById(R.id.P_D);
        TextView P_DN = view.findViewById(R.id.P_DN);
        final TextView P_Op = view.findViewById(R.id.P_Op);
        TextView P_Dp = view.findViewById(R.id.P_Dp);
        final TextView P_quantityNum = view.findViewById(R.id.P_quantityNum);
        final TextView finalTv = view.findViewById(R.id.finalTv);
        ImageButton decrementBtn = view.findViewById(R.id.decrementBtn);
        ImageButton incrementBtn = view.findViewById(R.id.incrementBtn);
        TextView deliverydate = view.findViewById(R.id.P_deliveryDate);
        TextView returndate = view.findViewById(R.id.P_returnDate);
        final Button addToCartBtn = view.findViewById(R.id.addToCartBtn);

        // Get data from model
        final String productId = modelProduct.getProductId();
        String productName = modelProduct.getProductName();
        String productDescription = modelProduct.getProductDescription();
        String productQuantity = modelProduct.getProductQuantity();
        String discountNote = modelProduct.getDiscountNote();
        String productImage = modelProduct.getProductImage();
        final String deliveryDate = modelProduct.getDeliveryDate();
        final String returnDate = modelProduct.getReturnDate();




        final String price;
        if (modelProduct.getDiscountAvailability().equals("true")){
            // Product have discount
            price = modelProduct.getPriceDiscount();
            P_DN.setVisibility(View.VISIBLE);
            P_Op.setPaintFlags(P_Op.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); // Add strike through on original price
            P_Op.setTextColor(Color.RED);
        }
        else{
            // Product does not have discount
            P_Dp.setVisibility(View.GONE);
            P_DN.setVisibility(View.GONE);
            price = modelProduct.getProductPrice();
        }

        cost = Double.parseDouble(price.replaceAll("$", ""));
        finalCost = Double.parseDouble(price.replaceAll("$", ""));
        quantity = 1;

        // Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        try {
            Picasso.get().load(productImage).placeholder(R.drawable.ic_add_shopping_cart_accent).into(productIv);
        }
        catch (Exception e){
            productIv.setImageResource(R.drawable.ic_add_shopping_cart_accent);
        }

        P_name.setText(""+productName);
        P_D.setText(""+productDescription);
        P_Q.setText(""+productQuantity);
        P_DN.setText(""+discountNote);
        P_Op.setText("Price: "+modelProduct.getProductPrice());
        P_Dp.setText("Price Discount: "+modelProduct.getPriceDiscount());
        P_quantityNum.setText(""+quantity);
        finalTv.setText(" "+finalCost);
        deliverydate.setText("Delivery Date: "+deliveryDate+" of delivery");
        returndate.setText("Return Date: "+returnDate+" of purchase");




        final AlertDialog dialog = builder.create();
        dialog.show();

        //Increasing quantity of the product
        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalCost = finalCost + cost;
                quantity++;

                finalTv.setText(""+finalCost);
                P_quantityNum.setText(""+quantity);
            }
        });

        // Decreasing quantity of the product, only if quantity is greater than 1
        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(quantity > 1){
                    finalCost = finalCost - cost;
                    quantity--;

                    finalTv.setText(""+finalCost);
                    P_quantityNum.setText(""+quantity);
                }
            }
        });

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = P_name.getText().toString().trim();
                String priceEach = price;
                String totalPrice = finalTv.getText().toString().trim().replace("$", "");
                String quantity = P_quantityNum.getText().toString().trim();

                // Add to db(SQLite
                addToCart(productId, name, priceEach, totalPrice, quantity);

                dialog.dismiss();

            }
        });
    }

    private int itemId = 1;
    private  void addToCart(String productId, String name, String priceEach, String price, String quantity){
        itemId++;

        EasyDB easyDB = EasyDB.init(context, "ITEM_DB")
                .setTableName("Item_Table")
                .addColumn(new Column("Item_Id", new String[]{"text", "unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price_Each", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                .doneTableColumn();

        Boolean b = easyDB.addData("Item_Id", itemId)
                .addData("Item_PID", productId)
                .addData("Item_Name", name)
                .addData("Item_Price_Each", priceEach)
                .addData("Item_Price", price)
                .addData("Item_Quantity", quantity)

                .doneDataAdding();

        Toast.makeText(context, "Added into your cart...", Toast.LENGTH_SHORT).show();

        // Update cart count
        ((shop_DetailsActivity)context).cartCount();

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterProductsUser(this, filterList);
        }
        return filter;
    }

    class  HolderProductUser extends RecyclerView.ViewHolder{

        // Ui views
        private ImageView productIcon, nextTv;
        private TextView discountNoteTv, nameTv, descriptionTv, quantityTv, addToCartTv, discountPriceTv, originalPriceTv, deliverydate;

        public HolderProductUser(@NonNull View itemView) {
            super(itemView);

            productIcon = itemView.findViewById(R.id.productIcon);
            discountNoteTv = itemView.findViewById(R.id.discountNoteTv);
            nameTv = itemView.findViewById(R.id.nameTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
            quantityTv = itemView.findViewById(R.id.quantityTv);
            addToCartTv = itemView.findViewById(R.id.addToCartTv);
            discountPriceTv = itemView.findViewById(R.id.discountPriceTv);
            originalPriceTv = itemView.findViewById(R.id.originalPriceTv);
            deliverydate = itemView.findViewById(R.id.Delivery_Days);
           // nextTv = itemView.findViewById(R.id.nextTv);





        }
    }
}
