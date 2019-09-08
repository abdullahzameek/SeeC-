package com.pennhack.seec;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.util.List;

public class MarketAdapter extends RecyclerView.Adapter<MarketAdapter.MyViewHolder> {

    RequestOptions requestOptions = new RequestOptions();
    private ItemClickListener mClickListener;

    private List<Coupon> mTestItemList;
    private View.OnClickListener mOnItemClickListener;

    public MarketAdapter(List<Coupon> testItemList) {
        this.mTestItemList = testItemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.market_coupon_recycleable, parent, false);
        itemView.setOnClickListener(mOnItemClickListener);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Coupon coupon = mTestItemList.get(position);
        holder.message.setText(coupon.message);
        holder.costText.setText(Integer.toString(coupon.price));
        requestOptions = requestOptions.transform(new RoundedCorners(30));
        Glide.with(holder.imageView)
                .load(coupon.image)
                .apply(requestOptions)
                .into(holder.imageView);


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

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.discount_message_text);
            imageView = itemView.findViewById(R.id.coupon_image_view);
            costText = itemView.findViewById(R.id.cost_text);
            buyButton = itemView.findViewById(R.id.buy_button);

            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
