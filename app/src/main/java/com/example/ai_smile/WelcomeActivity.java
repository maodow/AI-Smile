package com.example.ai_smile;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.ai_smile.base.BaseCQActivity;
import com.example.ai_smile.base.BlankPresenter;
import com.example.ai_smile.utils.SpUtils;
import com.example.ai_smile.utils.ToastUtils;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import org.tensorflow.lite.SDK;
import java.util.List;
import butterknife.BindView;
import rx.Subscription;

/**
 * Created by louis on 2020/5/22.
 *
 */

public class WelcomeActivity extends BaseCQActivity {

    private static final String TAG = MainActivity.class.getName();

    @BindView(R.id.view_root)
    View mRootView;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_welcome;
    }

    Subscription subscription;

    /**
     * Instantiate a presenter instance
     * @return The {@link MvpPresenter} for this view
     */
    @NonNull
    @Override
    public MvpPresenter createPresenter() {
        return BlankPresenter.INSTANCE;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                int height = mRootView.getMeasuredHeight();
                if(height == 2340){
                    mRootView.setBackground(getResources().getDrawable(R.mipmap.ic_welcome_h2340));
                } else{
                    mRootView.setBackground(getResources().getDrawable(R.mipmap.ic_welcome_h1080));
                }
                return true;
            }
        });

        requestPermission();

    }

    public void requestPermission() {
        XXPermissions.with(this)
                //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                .constantRequest()
                .permission(Permission.Group.STORAGE)
                .permission(Permission.CAMERA)
                .permission(Permission.READ_PHONE_STATE)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        if (isAll) {
                            try {
                                SDK.getInstance().startup(getApplicationContext(), null);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(!SpUtils.getInstance().getBooleanValue(SpUtils.ISLOGIN, false)) {
                                            startActivity(LoginActivity.class);
                                            overridePendingTransition(R.anim.screen_zoom_in, R.anim.screen_zoom_out);
                                        } else{
                                            startActivity(MainActivity.class);
                                        }
                                        finish();
                                    }
                                }, 500);
                            } catch (Exception e) {
                                Log.i(TAG, "SDK初始化失败 " + e.getMessage(), e);
                            }
                        } else {
//                            ToastUtils.showShort("获取权限成功，部分权限未正常授予");
                        }
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        if (quick) {
                            ToastUtils.showToast(WelcomeActivity.this, "被永久拒绝授权，请手动授予权限");
                            //如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.gotoPermissionSettings(WelcomeActivity.this);
                        } else {
                            ToastUtils.showToast(WelcomeActivity.this, "获取权限失败");
                        }
                    }
                });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(subscription!=null &&subscription.isUnsubscribed())
            subscription.unsubscribe();
    }

}