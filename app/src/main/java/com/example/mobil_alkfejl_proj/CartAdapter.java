package com.example.mobil_alkfejl_proj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ShoppingItem item = cartItems.get(position);
        holder.title.setText(item.getName());
        holder.price.setText(String.valueOf(item.getPrice()));
        Glide.with(context).load(item.getImageResource()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView price;

        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.itemTitle);
            price = itemView.findViewById(R.id.price);
            image = itemView.findViewById(R.id.itemImage);
        }
    }
}
