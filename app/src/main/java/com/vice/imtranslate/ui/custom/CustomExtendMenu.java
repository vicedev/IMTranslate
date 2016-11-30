package com.vice.imtranslate.ui.custom;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.vice.imtranslate.R;

/**
 * Created by vice on 2016/10/2 0002.
 */
public class CustomExtendMenu extends FrameLayout {
    public CustomExtendMenu(Context context) {
        this(context, null);
    }

    public CustomExtendMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomExtendMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.custom_more_menu, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ImageButton ibPicture = (ImageButton) findViewById(R.id.ib_picture);
        ImageButton ibTakePhoto = (ImageButton) findViewById(R.id.ib_take_photo);
        ibPicture.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onMenuItemlick != null) {
                    onMenuItemlick.onMenuItemlick(PICTURE);
                }
            }
        });

        ibTakePhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onMenuItemlick != null) {
                    onMenuItemlick.onMenuItemlick(TAKE_PHOTO);
                }
            }
        });

    }

    public static final int PICTURE = 1;
    public static final int TAKE_PHOTO = 2;

    private MenuItemClickListener onMenuItemlick ;

    public interface MenuItemClickListener {
        void onMenuItemlick(int type);
    }

    public void setOnMenuItemClickListener(MenuItemClickListener listener) {
        if (onMenuItemlick == null) {
            onMenuItemlick = listener;
        }
    }
}
