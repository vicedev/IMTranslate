package com.vice.imtranslate.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.vice.imtranslate.R;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageShowActivity extends BaseActivity {

    private View layout;

    public static String IMAGE_PATH="image_path";
    private String imagePath;
    private PhotoView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideTitleBar();
        imagePath = getIntent().getStringExtra(IMAGE_PATH);

        Glide.with(this)
                .load(imagePath)
                .placeholder(R.drawable.default_image)
                .fitCenter()
                .into(ivImage);

    }

    @Override
    View addContentLayout() {
        layout = getLayoutInflater().inflate(R.layout.activity_image_show,contentLayout,false);
        ivImage = (PhotoView) layout.findViewById(R.id.iv_image);
        ivImage.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                finish();
            }
        });

        return layout;
    }
}
