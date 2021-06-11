package com.team.my_gorcery.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.team.my_gorcery.R;
import com.team.my_gorcery.model.ModelReview;

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterReview extends RecyclerView.Adapter<AdapterReview.HolderReview>{

    private Context context;
    private ArrayList<ModelReview> reviewList;

    public AdapterReview(Context context, ArrayList<ModelReview> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public HolderReview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the row_review layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_review, parent, false);
        return new HolderReview(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderReview holder, int position) {
        // Get data at position
        ModelReview modelReview = reviewList.get(position);
        String uid = modelReview.getUid();
        String ratings = modelReview.getRatings();
        String review = modelReview.getReview();
        String timestamp = modelReview.getTimestamp();

        /* We need the info of the user who wrote the review(the name, profileImage)
        this can be done using the user's uid
         */
        loadUserDetails(modelReview, holder);

        // convert timestamp to proper format
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String dateFormat = DateFormat.format("dd/MM/yyyy", calendar).toString();

        // Set data
        holder.datetv.setText(dateFormat);
        holder.ratingBar.setRating(Float.parseFloat(ratings));
        holder.reviewTv.setText(review);

    }

    private void loadUserDetails(ModelReview modelReview, final HolderReview holder) {
        String uid = modelReview.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Use the same key names as in the FB
                        String name = ""+dataSnapshot.child("name").getValue();
                        String profileImage = ""+dataSnapshot.child("profileImage").getValue();

                        // Set data
                        holder.R_UserName.setText(name);

                        try {
                            Picasso.get().load(profileImage).placeholder(R.drawable.ic_profile).into(holder.R_profileImage);
                        }

                        catch (Exception e){
                            holder.R_profileImage.setImageResource(R.drawable.ic_store);
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }


    class HolderReview extends RecyclerView.ViewHolder{

        private CircleImageView R_profileImage;
        private TextView R_UserName, datetv, reviewTv;
        private RatingBar ratingBar;

        public HolderReview(@NonNull View itemView) {
            super(itemView);

            R_profileImage = itemView.findViewById(R.id.R_profileImage);
            R_UserName = itemView.findViewById(R.id.R_UserName);
            datetv = itemView.findViewById(R.id.datetv);
            reviewTv = itemView.findViewById(R.id.reviewTv);
            ratingBar = itemView.findViewById(R.id.ratingBar);

        }
    }
}
