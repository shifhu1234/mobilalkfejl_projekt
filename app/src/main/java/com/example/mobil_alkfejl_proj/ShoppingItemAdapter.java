package com.example.mobil_alkfejl_proj;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ShoppingItemAdapter extends RecyclerView.Adapter<ShoppingItemAdapter.ViewHolder> implements Filterable {
    private ArrayList<ShoppingItem> mShoppingItemsData;
    private final ArrayList<ShoppingItem> mShoppingItemsDataAll;
    private final Context mContext;
    private int lastPosition = -1;
    private final FirebaseUser user;
    private final FirebaseAuth mAuth;
    private CartUpdateListener cartUpdateListener;

    ShoppingItemAdapter(Context context, ArrayList<ShoppingItem> itemsData) {
        this.mShoppingItemsData = itemsData;
        this.mShoppingItemsDataAll = itemsData;
        this.mContext = context;

        mAuth = FirebaseAuth.getInstance();
//        setContentView(R.layout.activity_fruit_product);
        user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            Log.d(LOG_TAG, "Azonositott felhasznalo FRUITPRODUCT");
//        } else {
//            Log.d(LOG_TAG, "Nem sikerult bejelentkeztetni a felhasznalot");
//            finish();
//        }
        if (context instanceof CartUpdateListener) {
            cartUpdateListener = (CartUpdateListener) context;
        }

    }

    @Override
    public ShoppingItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ShoppingItemAdapter.ViewHolder holder, int position) {
        ShoppingItem currentItem = mShoppingItemsData.get(position);

        holder.bindTo(currentItem);

        if (holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mShoppingItemsData.size();
    }

    @Override
    public Filter getFilter() {
        return shoppingFilter;
    }

    private final Filter shoppingFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<ShoppingItem> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.count = mShoppingItemsDataAll.size();
                results.values = mShoppingItemsDataAll;
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (ShoppingItem item : mShoppingItemsDataAll) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mShoppingItemsData = (ArrayList) results.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTitleText;
        private final TextView mInfoText;
        private final TextView mPriceText;
        private final ImageView mItemImage;
        private final RatingBar mRatingBar;

        public ViewHolder(View itemView) {
            super(itemView);

            mTitleText = itemView.findViewById(R.id.itemTitle);
            mInfoText = itemView.findViewById(R.id.subTitle);
            mPriceText = itemView.findViewById(R.id.price);
            mItemImage = itemView.findViewById(R.id.itemImage);
            mRatingBar = itemView.findViewById(R.id.ratingBar);

            itemView.findViewById(R.id.add_to_cart).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!user.isAnonymous()) {
                        if (cartUpdateListener != null) {
                            cartUpdateListener.updateAlertIcon();
                        }
                    } else {
                        Intent intent = new Intent(mContext, MainActivity.class);
                        ContextCompat.startActivity(mContext, intent, null);
                    }
                }
            });
        }

        public void bindTo(ShoppingItem currentItem) {
            mTitleText.setText(currentItem.getName());
            mInfoText.setText(currentItem.getInfo());
            mPriceText.setText(String.valueOf(currentItem.getPrice()));
            mRatingBar.setRating(currentItem.getRatedInfo());

//            Glide.with(mContext).load(currentItem.getImageResource()).into(mItemImage);
//            Glide.with(mContext)
//                    .load(currentItem.getImageResource())
//                    .transition(com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade(750))
//                    .into(mItemImage);

            Glide.with(mContext)
                    .load(currentItem.getImageResource())
                    .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            Animation fadeIn = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
                            mItemImage.startAnimation(fadeIn);
                            return false;
                        }
                    })
                    .into(mItemImage);

        }
    }

}


