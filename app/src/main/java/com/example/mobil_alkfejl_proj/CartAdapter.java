package com.example.mobil_alkfejl_proj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<ShoppingItem> cartItems;
    private Context context;

    public CartAdapter(Context context, List<ShoppingItem> items) {
        this.context = context;
        this.cartItems = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ShoppingItem item = cartItems.get(position);
        holder.title.setText(item.getName());
        holder.price.setText(String.valueOf(item.getPrice() + " Ft"));
        Glide.with(context).load(item.getImageResource()).into(holder.image);

        holder.deleteButton.setOnClickListener(v -> {
            CartManager.getInstance().removeItem(item);
            cartItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());
//            CartActivity.getInstance().setItemCount(getItemCount()-1);

            if (context instanceof CartActivity) {
                ((CartActivity) context).recalculateTotal();
            }
            CartActivity.getInstance().removeItem();

        });

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView price;
        ImageView image;
        Button deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.itemTitle);
            price = itemView.findViewById(R.id.price);
            image = itemView.findViewById(R.id.itemImage);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
