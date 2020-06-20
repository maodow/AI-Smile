package com.example.ai_smile.activity;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import com.example.ai_smile.R;
import com.example.ai_smile.base.BaseCQActivity;
import com.example.ai_smile.base.BlankPresenter;
import com.example.ai_smile.widget.HackyViewPager;
import com.example.ai_smile.widget.PageAdapter;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import butterknife.BindView;

public class ImageScrollActivity extends BaseCQActivity {

    @BindView(R.id.custom_viewpager)
    HackyViewPager customViewpager;
    @BindView(R.id.txt_index)
    TextView txtIndex;

    private PageAdapter pageAdapter;

    private GestureDetector mGesDetector;


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        this.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGesDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_image_scroll;
    }

    @NonNull
    @Override
    public MvpPresenter createPresenter() {
        return BlankPresenter.INSTANCE;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        pageAdapter = new PageAdapter(getSupportFragmentManager(), getIntent().getStringArrayListExtra("imageUrls"));
        customViewpager.setAdapter(pageAdapter);
        customViewpager.setCurrentItem(getIntent().getIntExtra("position", 0));
        customViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                txtIndex.setText(new StringBuilder()
                        .append(position + 1)
                        .append("/")
                        .append(pageAdapter.getCount()));
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mGesDetector = new GestureDetector(this, new GestureListener());
    }


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            if (event.getRepeatCount() == 0) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
