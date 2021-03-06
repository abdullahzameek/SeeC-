package com.pennhack.seec;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MyCouponsAdapter extends RecyclerView.Adapter<MyCouponsAdapter.MyViewHolder> {

    RequestOptions requestOptions = new RequestOptions();
    private ItemClickListener mClickListener;

    private List<Coupon> mTestItemList;
    private View.OnClickListener mOnItemClickListener;

    public MyCouponsAdapter(List<Coupon> testItemList) {
        this.mTestItemList = testItemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_coupon_recycleable, parent, false);
        itemView.setOnClickListener(mOnItemClickListener);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Coupon coupon = mTestItemList.get(position);
        holder.message.setText(coupon.message);
        requestOptions = requestOptions.transform(new RoundedCorners(30));
        Glide.with(holder.imageView)
                .load(coupon.image)
                .apply(requestOptions)
                .into(holder.imageView);
        holder.vendorText.setText(coupon.vendor);

    }

    @Override
    public int getItemCount() {
        return mTestItemList.size();
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        public ImageView imageView;
        public TextView costText;
        public FloatingActionButton buyButton;
        public TextView vendorText;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.discount_message_text);
            imageView = itemView.findViewById(R.id.coupon_image_view);
            costText = itemView.findViewById(R.id.cost_text);
            buyButton = itemView.findViewById(R.id.buy_button);
            vendorText = itemView.findViewById(R.id.vendor_text);

            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
