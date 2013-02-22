package com.lds.snscontacts;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lds.snscontacts.widget.FloatingWindow;
import com.nostra13.universalimageloader.core.ImageLoader;

public class EmptyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String name = getIntent().getStringExtra("name");
        String photo = getIntent().getStringExtra("photo");
        if (name != null && photo != null) {
            showWindow(photo, name);
        }
        finish();
//        setContentView(R.layout.activity_empty);
    }

    private void showWindow(String photoUrl, String name) {
        LayoutInflater inflater = (LayoutInflater)      
                this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.window_floating, null);
        Button closeBtn = (Button) view.findViewById(R.id.close_btn);
        ImageView photoView = (ImageView) view.findViewById(R.id.photo);
        TextView nameView = (TextView) view.findViewById(R.id.name);

        nameView.setText(name);
        if (!TextUtils.isEmpty(photoUrl)) {
            ImageLoader imageLoader = ImageLoaderManager.getImageLoader(getApplicationContext());
            imageLoader.displayImage(photoUrl, photoView);
        }

        closeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatingWindow.close(v.getContext());
            }
        });

        FloatingWindow.show(getApplicationContext(), getWindow(), view);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                FloatingWindow.onTouchEvent(event, view);
                return true;
            }
        });
    }

}
