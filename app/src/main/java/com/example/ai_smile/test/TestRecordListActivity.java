package com.example.ai_smile.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ai_smile.R;
import com.example.ai_smile.adapter.RecordListAdapter;
import com.example.ai_smile.base.BaseCQActivity;
import com.example.ai_smile.data.bean.HttpRespose;
import com.example.ai_smile.data.bean.TestRecordBean;
import com.example.ai_smile.data.bean.TestResultBean;
import com.example.ai_smile.http.Injection;
import com.example.ai_smile.utils.AddressUtil;
import com.example.ai_smile.widget.ListDividerDecoration;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2020/5/21.
 *  人脸检测记录
 */
public class TestRecordListActivity extends BaseCQActivity<TestRecordContract.View, TestRecordContract.Presenter> implements TestRecordContract.View {

    @BindView(R.id.rv_record_list)
    RecyclerView mRecordListView;
    @BindView(R.id.image_no_data)
    ImageView imageNoData;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPresenter().getTestRecordData(AddressUtil.getMacAddr(TestRecordListActivity.this));
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_record_list;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.statusBarView(R.id.top_view)
                .statusBarDarkFont(true)//android 6.0以上设置状态栏字体为暗色
                .keyboardEnable(true)  //解决软键盘与底部输入框冲突问题
                .fullScreen(true)
                .init();
    }

    @NonNull
    @Override
    public TestRecordContract.Presenter createPresenter() {
        return new TestRecordPresenter(Injection.provideTestRecordUseCase(), Injection.provideTestInfoUseCase());
    }

    @Override
    public void onGetTestRecordListSuccess(HttpRespose<List<TestRecordBean>> httpRespose) {
        if (null != httpRespose && null != httpRespose.getResult()) {
            if (null != httpRespose.getResult()&& !httpRespose.getResult().isEmpty()) {
                mRecordListView.setVisibility(View.VISIBLE);
                imageNoData.setVisibility(View.GONE);
                refreshListView(httpRespose.getResult());
            } else {
                mRecordListView.setVisibility(View.GONE);
                imageNoData.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onGetTestRecordInfoSuccess(HttpRespose<List<TestResultBean>> httpRespose) {

    }

    @OnClick({R.id.image_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            default:
                break;
        }
    }

    private void refreshListView(final List<TestRecordBean> testRecordBeanList) {
        RecordListAdapter adapter = new RecordListAdapter(this, testRecordBeanList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecordListView.setLayoutManager(layoutManager);
        mRecordListView.addItemDecoration(new ListDividerDecoration(TestRecordListActivity.this));
        mRecordListView.setFocusableInTouchMode(false);
        mRecordListView.setAdapter(adapter);
        adapter.setOnItemClickListener(new RecordListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, int id) {
                if(id == R.id.lay_item_view){
                    if(null != testRecordBeanList && !testRecordBeanList.isEmpty()){
                        Intent intent = new Intent(TestRecordListActivity.this, TestInfoActivity.class);
                        intent.putExtra("recordId", testRecordBeanList.get(position).getId());
                        intent.putExtra("isFromRecord", true);
                        startActivity(intent);
                    }
                }
            }
        });
        adapter.notifyDataSetChanged();
    }
    
    @Override
    public Context getActivityContext() {
        return this;
    }

}
