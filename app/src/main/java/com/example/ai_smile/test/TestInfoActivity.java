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
import com.example.ai_smile.activity.ImageScrollActivity;
import com.example.ai_smile.adapter.RecordInfoAdapter;
import com.example.ai_smile.base.BaseCQActivity;
import com.example.ai_smile.data.bean.HttpRespose;
import com.example.ai_smile.data.bean.TestRecordBean;
import com.example.ai_smile.data.bean.TestResultBean;
import com.example.ai_smile.http.Injection;
import com.example.ai_smile.widget.ListDividerDecoration;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2020/5/21.
 *  检测详情
 */
public class TestInfoActivity extends BaseCQActivity<TestRecordContract.View, TestRecordContract.Presenter> implements TestRecordContract.View {

    @BindView(R.id.rv_info_list)
    RecyclerView mRecordInfoView;
    @BindView(R.id.image_no_data)
    ImageView imageNoData;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isFromRecord = getIntent().getBooleanExtra("isFromRecord", false);
        if (isFromRecord) {
            String recordId = getIntent().getStringExtra("recordId");
            getPresenter().getTestRecordInfo(recordId);
        } else {
            Bundle bundle = getIntent().getBundleExtra("testResultData");
            List<TestResultBean> testResults = (List<TestResultBean>)bundle.getSerializable("testResultList");
            if(null != testResults && !testResults.isEmpty()){
                refreshListView(testResults);
            }
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_record_info;
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

    }

    @Override
    public void onGetTestRecordInfoSuccess(HttpRespose<List<TestResultBean>> httpRespose) {
        if (null != httpRespose && null != httpRespose.getResult()) {
            if (null != httpRespose.getResult()&& !httpRespose.getResult().isEmpty()) {
                mRecordInfoView.setVisibility(View.VISIBLE);
                imageNoData.setVisibility(View.GONE);
                refreshListView(httpRespose.getResult());
            } else {
                mRecordInfoView.setVisibility(View.GONE);
                imageNoData.setVisibility(View.VISIBLE);
            }
        }
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

    private void refreshListView(final List<TestResultBean> testResultBeanList) {
        RecordInfoAdapter adapter = new RecordInfoAdapter(this, testResultBeanList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecordInfoView.setLayoutManager(layoutManager);
        mRecordInfoView.addItemDecoration(new ListDividerDecoration(TestInfoActivity.this));
        mRecordInfoView.setFocusableInTouchMode(false);
        mRecordInfoView.setAdapter(adapter);
        adapter.setOnItemImageClickListener(new RecordInfoAdapter.OnItemImageClickListener() {
            @Override
            public void onItemImageClick(int position, String imagUrl) {
                Intent intent = new Intent(TestInfoActivity.this, ImageScrollActivity.class);
                ArrayList<String> imageUrls = new ArrayList<>();
                imageUrls.add(imagUrl.trim());
                intent.putStringArrayListExtra("imageUrls", imageUrls);
                intent.putExtra("position", position);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        adapter.notifyDataSetChanged();
    }

    @Override
    public Context getActivityContext() {
        return this;
    }

}
