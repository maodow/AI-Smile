package com.example.ai_smile.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.ai_smile.R;

public class WaitingDialog extends Dialog {

	private TextView tvContent;
	private String content;
	private ProgressBar progressBar1;

	public WaitingDialog(Context context) {
		super(context, R.style.loadingDialogStyle);
		init(context);
	}

	public WaitingDialog(Context context, String content) {
		super(context, R.style.loadingDialogStyle);
		this.content = content;
		init(context);
	}

	private WaitingDialog(Context context, int theme) {
		super(context, theme);
		init(context);
	}

	private void init(Context context) {
		View view = getLayoutInflater().inflate(R.layout.waiting_dialog, null);
		tvContent = (TextView) view.findViewById(R.id.tv_content);
		LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.LinearLayout);
		linearLayout.getBackground().setAlpha(255);
		if(TextUtils.isEmpty(content)){
			tvContent.setVisibility(View.GONE);
		}else{
			tvContent.setVisibility(View.VISIBLE);
			tvContent.setText(content);
		}
		progressBar1 = (ProgressBar) view.findViewById(R.id.progressBar1);
		setContentView(view);
	}

	public void setText(String text){
		if(TextUtils.isEmpty(text)){
			tvContent.setVisibility(View.GONE);
		}else{
			tvContent.setVisibility(View.VISIBLE);
		}
		tvContent.setText(text);
	}

	public void setTextVisibility(int visibility){
		tvContent.setVisibility(visibility);
	}

	public void setProgressBarVisibility(int visibility){
		if(visibility != progressBar1.getVisibility())
			progressBar1.setVisibility(visibility);
	}

}
