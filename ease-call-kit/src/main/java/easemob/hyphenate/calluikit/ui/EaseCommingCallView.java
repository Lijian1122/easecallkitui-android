package easemob.hyphenate.calluikit.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;


import com.hyphenate.util.EMLog;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import easemob.hyphenate.calluikit.EaseCallUIKit;
import easemob.hyphenate.calluikit.R;
import easemob.hyphenate.calluikit.utils.EaseCallKitUtils;
import easemob.hyphenate.calluikit.widget.EaseImageView;


/**
 * author lijian
 * email: Allenlee@easemob.com
 * date: 01/15/2021
 */
public class EaseCommingCallView extends FrameLayout {

    private static final String TAG = EaseVideoCallActivity.class.getSimpleName();

    private ImageButton mBtnReject;
    private ImageButton mBtnPickup;
    private TextView mInviterName;
    private OnActionListener mOnActionListener;
    private EaseImageView avatar_view;
    private Bitmap headBitMap;
    private String headUrl;

    public EaseCommingCallView(@NonNull Context context) {
        this(context, null);
    }

    public EaseCommingCallView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseCommingCallView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.activity_comming_call, this);
        mBtnReject = findViewById(R.id.btn_reject);
        mBtnPickup = findViewById(R.id.btn_pickup);
        mInviterName = findViewById(R.id.tv_nick);
        avatar_view = findViewById(R.id.iv_avatar);


        mBtnReject.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnActionListener != null) {
                    mOnActionListener.onRejectClick(v);
                }
            }
        });

        mBtnPickup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnActionListener != null) {
                    mOnActionListener.onPickupClick(v);
                }
            }
        });
    }

    public void setInviteInfo(String username){
        mInviterName.setText(EaseCallKitUtils.getUserNickName(username));
        headUrl = EaseCallKitUtils.getUserHeadImage(username);

        //加载头像图片
        loadHeadImage();
    }

    /**
     * 加载用户配置头像
     * @return
     */
    private void loadHeadImage() {
        if(headUrl != null) {
            if (headUrl.startsWith("http://") || headUrl.startsWith("https://")) {
                new AsyncTask<String, Void, Bitmap>() {
                    //该方法运行在后台线程中，因此不能在该线程中更新UI，UI线程为主线程
                    @Override
                    protected Bitmap doInBackground(String... params) {
                        Bitmap bitmap = null;
                        try {
                            String url = params[0];
                            URL HttpURL = new URL(url);
                            HttpURLConnection conn = (HttpURLConnection) HttpURL.openConnection();
                            conn.setDoInput(true);
                            conn.connect();
                            InputStream is = conn.getInputStream();
                            bitmap = BitmapFactory.decodeStream(is);
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return bitmap;
                    }

                    //在doInBackground 执行完成后，onPostExecute 方法将被UI 线程调用，
                    // 后台的计算结果将通过该方法传递到UI线程，并且在界面上展示给用户.
                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        if (bitmap != null && !bitmap.isRecycled()) {
                            avatar_view.setImageBitmap(bitmap);
                        }
                    }
                }.execute(headUrl);
            } else {
                if(headBitMap == null){
                    //该方法直接传文件路径的字符串，即可将指定路径的图片读取到Bitmap对象
                    headBitMap = BitmapFactory.decodeFile(headUrl);
                }
                if(headBitMap != null && !headBitMap.isRecycled()){
                    avatar_view.setImageBitmap(headBitMap);
                }else{
                    EMLog.d(TAG,"headBitMap is isRecycled");
                }
            }
        }
    }


    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if(visibility == View.VISIBLE) {
 //           mIvCallAnim.setBackgroundResource(R.drawable.call_ring_anim);
//            mDrawableAnim = mIvCallAnim.getBackground();
//            if(mDrawableAnim instanceof AnimationDrawable) {
//                ((AnimationDrawable) mDrawableAnim).setOneShot(false);
//                ((AnimationDrawable) mDrawableAnim).start();
//            }
        }else {
//            if(mDrawableAnim instanceof AnimationDrawable) {
//                if(((AnimationDrawable) mDrawableAnim).isRunning()) {
//                    ((AnimationDrawable) mDrawableAnim).stop();
//                }
//                mDrawableAnim = null;
//            }
        }
    }



    public void setOnActionListener(OnActionListener listener) {
        this.mOnActionListener = listener;
    }

    public interface OnActionListener {
        void onRejectClick(View v);
        void onPickupClick(View v);
    }


    float[] getScreenInfo(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        float[] info = new float[5];
        if(manager != null) {
            DisplayMetrics dm = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(dm);
            info[0] = dm.widthPixels;
            info[1] = dm.heightPixels;
            info[2] = dm.densityDpi;
            info[3] = dm.density;
            info[4] = dm.scaledDensity;
        }
        return info;
    }
}
