package com.example.administrator.bluetoothdevice.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.bluetoothdevice.R;

/**
 * Created by 2018/4/12 11:45
 * 创建：Administrator on
 * 描述:等待对话框
 */

public class WaitingDialog extends AlertDialog {

    private ImageView imageViewAnimation = null;
    private TextView textViewInfo = null;
    private String TextTips = null;
    private WaitingKeyListener listener;

    public WaitingDialog(Context context) {
        this(context, R.style.AlertDialogStyle);
    }

    public WaitingDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    private boolean isReturn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_wait_dialog);
        setCanceledOnTouchOutside(true);
        initView();
        initWaitAnimation();
    }

    private void initView() {
        imageViewAnimation = (ImageView) findViewById(R.id.img_waiting);
        textViewInfo = (TextView) findViewById(R.id.text_view_wait_info);
        setInfoText();
    }

    private void setInfoText() {
        if (null != textViewInfo) {
            if (TextUtils.isEmpty(TextTips)) {
                textViewInfo.setText("");
                textViewInfo.setVisibility(View.GONE);
            } else {
                textViewInfo.setText(TextTips);
                textViewInfo.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initWaitAnimation() {
        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                imageViewAnimation.setImageResource(R.drawable.lading_live);
                AnimationDrawable animationDrawable = (AnimationDrawable) imageViewAnimation
                        .getDrawable();
                animationDrawable.start();
            }
        });
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                imageViewAnimation.clearAnimation();
            }
        });

        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (isReturn) {
                        dismiss();
                        if (listener != null) {
                            listener.OnWaitingKeyListener(isReturn);
                        }
                        return false;
                    } else {
                        if (listener != null) {
                            listener.OnWaitingKeyListener(isReturn);
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }

    //设置是否可以点击取消
    public WaitingDialog setOnTouchOutside(boolean isOnTouchOutside) {
        isReturn = isOnTouchOutside;
        setCanceledOnTouchOutside(isOnTouchOutside);
        setCancelable(isOnTouchOutside);
        return this;
    }

    //设置显示信息
    public WaitingDialog setWaitText(String text) {
        TextTips = text;
        setInfoText();
        return this;
    }

    public void showDialog(boolean show) {
        if (show) {
            show();
        } else {
            dismiss();
        }
    }

    public void setIsClick(boolean isCLick) {
        if (isCLick) {
            setOnKeyListener(new DialogInterface.OnKeyListener()

            {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (KeyEvent.KEYCODE_SEARCH == keyCode) {
                        dismiss();
                    }
                    return false;
                }
            });
        }
    }


    /**
     * 对外开放返回键接口
     *
     * @param listener
     */
    public void setOnWaitingKeyListener(WaitingKeyListener listener) {
        this.listener = listener;
    }

    public interface WaitingKeyListener {
        void OnWaitingKeyListener(boolean isReturn);
    }
}