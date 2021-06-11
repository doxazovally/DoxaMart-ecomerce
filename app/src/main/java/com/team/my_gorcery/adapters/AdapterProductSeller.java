package com.team.my_gorcery.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.team.my_gorcery.FilterProducts;
import com.team.my_gorcery.R;
import com.team.my_gorcery.com.team.my_gorcery.activities.edit_ProductActivity;
import com.team.my_gorcery.model.ModelProduct;

import java.util.ArrayList;

public class AdapterProductSeller extends RecyclerView.Adapter<AdapterProductSeller.HolderProductSeller> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productList, filterList;
    private FilterProducts filter;

    public AdapterProductSeller(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList = productList;
    }

    @NonNull
    @Override
    public HolderProductSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_seller, parent, false);
        return new HolderProductSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductSeller holder, int position) {
        // Get data
        final ModelProduct modelProduct = productList.get(position);
        String productId = modelProduct.getProductId();
        String uid = modelProduct.getUid();
        String  discountAvailability= modelProduct.getDiscountAvailability();
        String discountNote = modelProduct.getDiscountNote();
        String priceDiscount = modelProduct.getPriceDiscount();
        String name = modelProduct.getProductName();
        String quantity = modelProduct.getProductQuantity();
        String productCategory = modelProduct.getProductCategory();
        String timestamp = modelProduct.getTimestamp();
        String productPrice = modelProduct.getProductPrice();
        String productDescription = modelProduct.getProductDescription();
        String image = modelProduct.getProductImage();
        String returnDate = modelProduct.getReturnDate();
        String deliveryDate = modelProduct.getDeliveryDate();

        // Set data
        holder.nameTv.setText(name);
        holder.quantityTv.setText(quantity);
        holder.discountNoteTv.setText(discountNote);
        holder.discountPriceTv.setText("$"+priceDiscount);
        holder.originalPriceTv.setText("$"+productPrice);

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
            holder.originalPriceTv.setPaintFlags(0);
        }


        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_add_shopping_cart_accent).into(holder.productIcon);
        }
        catch (Exception e){
            holder.productIcon.setImageResource(R.drawable.ic_add_shopping_cart_accent);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle item clicks, show item details (in bottom sheet)
                detailsBottomSheet(modelProduct); // Here model product contains details of clicked product

            }
        });
    }

    private void detailsBottomSheet(ModelProduct modelProduct) {
        // Bottom Sheet
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        // Inflate view for bottomSheet
        View view = LayoutInflater.from(context).inflate(R.layout.bs_product_details_seller, null);
        // Set view to bottomSheet
        bottomSheetDialog.setContentView(view);


        // Initiate views of bottomSheet
        ImageButton bs_backbtn = view.findViewById(R.id.bs_backbtn);
        ImageButton editBtn = view.findViewById(R.id.bs_edit_Btn);
        ImageButton deletBtn = view.findViewById(R.id.delete_Btn);
        ImageView productIcon = view.findViewById(R.id.BproductIcon);
        TextView bs_name = view.findViewById(R.id.bs_nameTv);
        TextView bs_discountNote = view.findViewById(R.id.bs_discountNoteBtnTv);
        TextView bs_description = view.findViewById(R.id.bs_descriptionTv);
        TextView bs_category = view.findViewById(R.id.bs_CategoryTv);
        TextView bs_quantity = view.findViewById(R.id.bs_QuantityTv);
        TextView bs_originalPrice = view.findViewById(R.id.bs_OriginalPriceTv);
        TextView bs_discountPrice = view.findViewById(R.id.bs_discountPriceTv);
        TextView deliveryDate = view.findViewById(R.id.Delivery_Days);
        TextView returnDate = view.findViewById(R.id.return_Days);



        // Get data
        final String productId = modelProduct.getProductId();
        String uid = modelProduct.getUid();
        String  discountAvailability= modelProduct.getDiscountAvailability();
        String discountNote = modelProduct.getDiscountNote();
        String priceDiscount = modelProduct.getPriceDiscount();
        final String name = modelProduct.getProductName();
        String quantity = modelProduct.getProductQuantity();
        String productCategory = modelProduct.getProductCategory();
        String timestamp = modelProduct.getTimestamp();
        String productPrice = modelProduct.getProductPrice();
        String productDescription = modelProduct.getProductDescription();
        String image = modelProduct.getProductImage();
        String deliverydate = modelProduct.getDeliveryDate();
        String returndate = modelProduct.getReturnDate();



        // Set data
        bs_name.setText("NAME: "+name);
        bs_description.setText("DESCRIPTION: "+productDescription);
        bs_category.setText("CATEGORY: "+productCategory);
        bs_quantity.setText("WEIGHT: "+quantity);
        bs_discountPrice.setText("$"+priceDiscount);
        bs_originalPrice.setText("$"+productPrice);
        bs_discountNote.setText(discountNote);
        deliveryDate.setText("DELIVERY DATE: "+deliverydate);
        returnDate.setText("RETURN DATE: Wethin "+returndate+ " of purchase");


        if(discountAvailability.equals("true")){
            //Product is on discount
            bs_discountPrice.setVisibility(View.VISIBLE);
            bs_discountNote.setVisibility(View.VISIBLE);
            bs_originalPrice.setPaintFlags(bs_originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            bs_originalPrice.setTextColor(Color.RED); // Add strike through on original price
        }
        else {
            //Product is not on discount
            bs_discountPrice.setVisibility(View.GONE);
            bs_discountNote.setVisibility(View.GONE);
            //bs_originalPrice.setPaintFlags(bs_originalPrice.getPaintFlags());
        }

        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_add_shopping).into(productIcon);
        }
        catch (Exception e){
            productIcon.setImageResource(R.drawable.ic_add_shopping);
        }

        // Show dialog
        bottomSheetDialog.show();


        // Edit click
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open edit product activity and pass product id
                Intent intent = new Intent(context, edit_ProductActivity.class);
                intent.putExtra("productId", productId);
                context.startActivity(intent);


            }
        });

        // Delete click
        deletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();

                // Show delete confirm dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete product "+name+" ?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Delete
                                deleteProduct(productId); // targeting the (product id) in the db
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Cancel and dismiss dialog
                            }
                        })
                        .show();
               // tabOrders.setTextColor(getResources().getColor(R.color.brownLightcolor));

            }
        });

        // Back btn

        bs_backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show dialog
                bottomSheetDialog.dismiss();

            }
        });


    }

    private void deleteProduct(String productId) {
        // Delete product from its db id

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products").child(productId).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Product deleted
                        Toast.makeText(context, "Product deleted successfully...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to delete of product
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new FilterProducts(this, filterList);
        }
        return filter;
    }

    class HolderProductSeller extends RecyclerView.ViewHolder{
        /*Holds views of the recycleview*/

        private ImageView productIcon;
        private TextView discountNoteTv, discountPriceTv, nameTv, quantityTv, originalPriceTv;

        public HolderProductSeller(@NonNull View itemView) {
            super(itemView);

            productIcon = itemView.findViewById(R.id.productIcon);
            discountNoteTv = itemView.findViewById(R.id.discountNoteTv);
            discountPriceTv = itemView.findViewById(R.id.discountPriceTv);
            nameTv = itemView.findViewById(R.id.nameTv);
            quantityTv = itemView.findViewById(R.id.quantityTv);
            originalPriceTv = itemView.findViewById(R.id.originalPriceTv);


        }
    }
}
