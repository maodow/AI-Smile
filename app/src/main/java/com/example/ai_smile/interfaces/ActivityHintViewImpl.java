package com.example.ai_smile.interfaces;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import com.example.ai_smile.utils.ToastUtils;
import com.example.ai_smile.widget.WaitingDialog;

/**
 * Created by Robert yao on 2016/10/18.
 *
 */

public class ActivityHintViewImpl implements ActivityHintView {

    private ProgressDialog progressDialog;
    private Context context;

    public ActivityHintViewImpl(Context context) {
        this.context = context;
    }

    @Override
    public void showToast(String msg) {
        ToastUtils.showToast(context, msg);
        //Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToast(int resId) {
        String msg = context.getResources().getString(resId);
        showToast(msg);
    }

    @Override
    public void showProgressDialog(int msgRes) {
        showProgressDialog(context.getResources().getString(msgRes));
    }

    @Override
    public void showProgressDialog(String msg) {
        showProgressDialog(msg,null,null);
    }

    @Override
    public void showProgressDialog(String title, String msg) {
        showProgressDialog(msg,title,null);
    }

    @Override
    public void showProgressDialog(String msg, String title, View view) {
        if (null == progressDialog){
            progressDialog = new ProgressDialog(context);
        }else if(null != view){
            progressDialog.dismiss();
        }
        setupProgressDialog(msg,title,view);
        if(!progressDialog.isShowing())
            progressDialog.show();
    }

    private void setupProgressDialog(String msg, String title, View view) {
        if (null == msg){
            throw new IllegalArgumentException("Progress dialog must contain msg");
        }
        progressDialog.setMessage(msg);
        if (!TextUtils.isEmpty(title)){
            progressDialog.setTitle(title);
        }
        if (null != view ){
            progressDialog.setContentView(view);
        }
    }

    @Override
    public void hideProgressDialogIfShowing() {
        if (null != progressDialog){
            progressDialog.dismiss();
        }
    }

    public WaitingDialog dialog;

    /**
     * 自定义APP加载loading框
     */
    @Override
    public void showWaitingDialog() {
        if (dialog == null) {
            dialog = new WaitingDialog(context);
            dialog.setTextVisibility(View.GONE);
        }
        dialog.show();
    }

    @Override
    public boolean hideWaitingDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            return true;
        }
        return false;
    }
}
