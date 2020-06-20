package com.example.ai_smile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ai_smile.R;
import com.example.ai_smile.data.bean.TestResultBean;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2020/5/21.
 *  人脸检测列表 adapter
 */

public class RecordInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int ITEM_TYPE_IMAGE = 99;
    public static final int ITEM_TYPE = 100;

    private Context mContext;
    private List<TestResultBean> mTestResultBeanList;
    private RecordInfoAdapter.OnItemImageClickListener itemImageClickListener;


    public RecordInfoAdapter(Context context, List<TestResultBean> list) {
        mContext = context;
        mTestResultBeanList = list;
    }

    //重写改方法，设置ItemViewType
    @Override
    public int getItemViewType(int position) {
        //返回值与使用时设置的值需保持一致
        if(position == 0){
            return ITEM_TYPE_IMAGE;
        }
        return ITEM_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item;
        RecyclerView.ViewHolder holder = null;
        if (viewType == ITEM_TYPE_IMAGE) {
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record_info_type_image, parent, false);
            holder = new ViewHolder(item);
        } else if(viewType == ITEM_TYPE) {
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record_info, parent, false);
            holder = new ViewHolder1(item);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            ViewHolder holder0 = (ViewHolder) holder;
            holder0.tv_key.setText(mTestResultBeanList.get(position).getKey());
            RequestOptions options = new RequestOptions();
            options.centerInside();
            options.placeholder(R.drawable.anim_loading).centerInside();
            String imagUrl = mTestResultBeanList.get(position).getValue();
            if (null != imagUrl) {
                Glide.with(mContext).load(imagUrl.trim())
                        .apply(options)
                        .into(holder0.iv_value);

                holder0.iv_value.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemImageClickListener.onItemImageClick(position, imagUrl);
                    }
                });
            }
        } else{
            ViewHolder1 holder1 = (ViewHolder1) holder;
            holder1.tv_key.setText(mTestResultBeanList.get(position).getKey());
            RequestOptions options = new RequestOptions();
            options.centerInside();
            options.placeholder(R.drawable.anim_loading).centerInside();
            String imagUrl = mTestResultBeanList.get(position).getImage();
            if (null != imagUrl && !imagUrl.isEmpty()) {
                holder1.iv_value.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(imagUrl.trim())
                        .apply(options)
                        .into(holder1.iv_value);

                holder1.iv_value.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemImageClickListener.onItemImageClick(position, imagUrl);
                    }
                });
            } else{
                holder1.iv_value.setVisibility(View.GONE);
            }

            holder1.tv_value.setText(mTestResultBeanList.get(position).getValue());
        }
    }

    @Override
    public int getItemCount() {
        return mTestResultBeanList == null ? 0 : mTestResultBeanList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_key)
        TextView tv_key;
        @BindView(R.id.iv_value)
        ImageView iv_value;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class ViewHolder1 extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_key)
        TextView tv_key;
        @BindView(R.id.iv_value)
        ImageView iv_value;
        @BindView(R.id.tv_value)
        TextView tv_value;

        ViewHolder1(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void setOnItemImageClickListener(RecordInfoAdapter.OnItemImageClickListener itemImageClickListener){
        this.itemImageClickListener = itemImageClickListener;
    }

    public interface OnItemImageClickListener {
        void onItemImageClick(int position, String imagUrl);
    }

}
