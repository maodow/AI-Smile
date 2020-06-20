package com.example.ai_smile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ai_smile.R;
import com.example.ai_smile.data.bean.TestRecordBean;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2020/5/21.
 *  人脸检测列表 adapter
 */

public class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.ViewHolder> {

    //新增itemType
    public static final int ITEM_TYPE = 100;

    private Context mContext;
    private List<TestRecordBean> mTestRecordBeanList;
    private RecordListAdapter.OnItemClickListener itemClickListener;


    public RecordListAdapter(Context context, List<TestRecordBean> list) {
        mContext = context;
        mTestRecordBeanList = list;
    }

    //重写改方法，设置ItemViewType
    @Override
    public int getItemViewType(int position) {
        //返回值与使用时设置的值需保持一致
        return ITEM_TYPE;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_test_time.setText(mTestRecordBeanList.get(position).getCreatetime());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(position, R.id.lay_item_view);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTestRecordBeanList == null ? 0 : mTestRecordBeanList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_test_time)
        TextView tv_test_time;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void setOnItemClickListener(RecordListAdapter.OnItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, int id);
    }

}
