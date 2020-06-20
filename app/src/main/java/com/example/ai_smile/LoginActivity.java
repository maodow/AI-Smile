package com.example.ai_smile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ai_smile.base.BaseCQActivity;
import com.example.ai_smile.data.bean.HttpRespose;
import com.example.ai_smile.data.bean.UserInfo;
import com.example.ai_smile.http.Injection;
import com.example.ai_smile.login.LoginContract;
import com.example.ai_smile.login.LoginPresenter;
import com.example.ai_smile.utils.SpUtils;
import com.example.ai_smile.utils.StringUtil;
import com.example.ai_smile.utils.ToastUtils;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2020/4/20.
 */

public class LoginActivity extends BaseCQActivity<LoginContract.View, LoginContract.Presenter> implements LoginContract.View {

    @BindView(R.id.edit_account)
    EditText editAccount;
    @BindView(R.id.edit_pwd)
    EditText editPwd;
    @BindView(R.id.iv_eyes)
    ImageView imageEyes;
    @BindView(R.id.btn_login)
    Button mBtnLogin;
    @BindView(R.id.cb_user_protocol)
    CheckBox mCbProtocol;

    private boolean mIsPwdVisiable;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.statusBarView(R.id.top_view)
                .keyboardEnable(true)  //解决软键盘与底部输入框冲突问题
                .fullScreen(true)
                .init();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCbProtocol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mCbProtocol.isChecked()) {
                    mBtnLogin.setBackground(getResources().getDrawable(R.drawable.btn_selector));
                } else {
                    mBtnLogin.setBackground(getResources().getDrawable(R.drawable.btn_shape_not_enable));
                }
            }
        });
    }

    @NonNull
    @Override
    public LoginContract.Presenter createPresenter() {
        return new LoginPresenter(Injection.provideLoginUseCase());
    }

    @OnClick({R.id.iv_eyes, R.id.btn_login, R.id.ll_user_protocol})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_eyes:
                if (!mIsPwdVisiable) {
                    // 显示为普通文本
                    imageEyes.setImageResource(R.mipmap.icon_password_close);
                    editPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    // 使光标始终在最后位置
                    Editable etable = editPwd.getText();
                    Selection.setSelection(etable, etable.length());
                    mIsPwdVisiable = true;
                } else {
                    mIsPwdVisiable = false;
                    // 显示为密码
                    imageEyes.setImageResource(R.mipmap.icon_password_close);
                    editPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    // 使光标始终在最后位置
                    Editable etable = editPwd.getText();
                    Selection.setSelection(etable, etable.length());
                }
                break;
            case R.id.ll_user_protocol:
                mCbProtocol.setChecked(!mCbProtocol.isChecked());
                break;
            case R.id.btn_login:
                if (mCbProtocol.isChecked()) {
                    getPresenter().login(StringUtil.clearSpaceStr(editAccount.getText().toString()), StringUtil.clearSpaceStr(editPwd.getText().toString()));
                } else {
                    ToastUtils.showToast(LoginActivity.this, "请仔细阅读并同意《用户注册协议》");
                }
                break;
        }
    }

    @Override
    public void onLoginSuccess(HttpRespose httpRespose) {
        UserInfo userInfo = (UserInfo) httpRespose.getResult();
        if (null != userInfo) {
            SpUtils.getInstance().saveBoolenTosp(SpUtils.ISLOGIN, true);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            if (!isFinishing()) {
                finish();
            }
            //保存用户登录手机号
            userInfo.setMobile(editAccount.getText().toString().trim());
            SpUtils.getInstance().saveStringToSp(SpUtils.USERINFO, new Gson().toJson(userInfo));
        }
    }

    @Override
    public void onLoginFail() {
        SpUtils.getInstance().saveBoolenTosp(SpUtils.ISLOGIN, true);
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        if (!isFinishing()) {
            finish();
        }
    }

    @Override
    public void usernameRequestFocus() {
        requestFocus(editAccount);
    }

    @Override
    public void passwordRequestFocus() {
        requestFocus(editPwd);
    }

    @Override
    public void verifyRequestFocus() {

    }

    public void requestFocus(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    @Override
    public Context getActivityContext() {
        return this;
    }

    private long firstTime = 0;

    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime < 2000) {
            super.onBackPressed();
            System.exit(0);
        } else {
            showToast(getString(R.string.str_hint_quit_app));
            firstTime = System.currentTimeMillis();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
