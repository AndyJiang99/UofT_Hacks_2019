package com.garbsort;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.garbsort.garbsort.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class ScannerActivity extends AppCompatActivity implements ScannerFragment.ImageTakenListener, ImageInfoFragment.CallReq{
    private ViewPager viewPager;
    private FragmentPagerAdapter pagerAdapter;
    private ImageInfoFragment theFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        viewPager = findViewById(R.id.vp_frag);
        pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
    }

    @Override
    public void imageTaken(File file) {
        int currentItemIndex = pagerAdapter.setNewImageFragment(file);
        viewPager.setCurrentItem(currentItemIndex);
    }

    @Override
    public String initiate(byte[] compBitmap) throws Exception{
        OkHttpClient client = new OkHttpClient();
        final String[] result = new String[1];
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), compBitmap);
        Log.e("call ex2", requestBody.toString());
        Request request = new Request.Builder()
                .url("https://eastus.api.cognitive.microsoft.com/vision/v2.0/analyze?visualFeatures=Objects&language=en")
                .post(requestBody)
                .addHeader("Content-Type", "application/octet-stream")
                .addHeader("Ocp-Apim-Subscription-Key", "bf1fcf9d392e4e6c91c0f27cd3df1e68")
                .build();
        Log.e("call ex3", request.toString() + request.headers().toString());
        try {
            FileOutputStream fou = openFileOutput("data.txt", MODE_APPEND);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fou);
            outputStreamWriter.write(request.toString());
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

        //Call call = client.newCall(request);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("onResponse: ",  response.toString());
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    JSONArray jsonArray = json.getJSONArray("objects");
                    Log.e("ksp", json.toString() + jsonArray.toString());
                    Log.e("1", jsonArray.get(0).toString());
                    JSONObject h = jsonArray.getJSONObject(0);
                    final String name = h.getString("object");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            theFragment.setupText(name);
                        }
                    });
                    result[0] = name;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return result[0];
    }

    /**
     * The class for fragment pager adapter
     */
    public class FragmentPagerAdapter extends FragmentStatePagerAdapter {
        private Fragment baseFragment;
        private Fragment currentFragment;
        private List<Fragment> fragments = new ArrayList<>();
        public Fragment getCurrentFragment(){
            return currentFragment;
        }
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (getCurrentFragment() != object) {
                currentFragment = ((Fragment) object);
            }
            super.setPrimaryItem(container, position, object);
        }
        public FragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            baseFragment = ScannerFragment.newInstance();
            fragments.add(baseFragment);
        }

        public int setNewImageFragment(File file){
            if(getCount() == 1){
                theFragment = ImageInfoFragment.newInstance(file);
                fragments.add(theFragment);

                notifyDataSetChanged();
                return 1;
            } else if (getCount() == 2){
                int resetIndex = fragments.get(0) == baseFragment ? 1 : 0;
                fragments.remove(resetIndex);
                theFragment = ImageInfoFragment.newInstance(file);
                fragments.add(theFragment);
                notifyDataSetChanged();
                return resetIndex;
            }
            return 0;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

    }
}
class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera.Size previewSize;
    private Context baseContext;
    private List<Camera.Size> mSupportedPreviewSizes;
    public CameraPreview(Context context, Camera camera) {
        super(context);
        baseContext = context;
        mCamera = camera;
        mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    public void surfaceCreated(SurfaceHolder holder) {
    }
    public void surfaceDestroyed(SurfaceHolder holder) {
    }
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mHolder.getSurface() == null){
            return;
        }
        try {
            mCamera.stopPreview();
            Camera.Parameters param = mCamera.getParameters();
            param.setPreviewSize(previewSize.width, previewSize.height);
            mCamera.setParameters(param);
            //mCamera.setDisplayOrientation(90);
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
            int rotation = ((Activity) baseContext).getWindowManager().getDefaultDisplay().getRotation();
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0: degrees = 0; break;
                case Surface.ROTATION_90: degrees = 90; break;
                case Surface.ROTATION_180: degrees = 180; break;
                case Surface.ROTATION_270: degrees = 270; break;
            }

            int result;
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;  // compensate the mirror
            } else {  // back-facing
                result = (info.orientation - degrees + 360) % 360;
            }
            mCamera.setDisplayOrientation(result);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

            //Camera.Parameters parameters = mCamera.getParameters();
            //resolveSize(getSuggestedMinimumWidth(), w);
            //resolveSize(getSuggestedMinimumHeight(), h);
            //parameters.setPreviewSize(mCamera);
        } catch (Exception e){
        }
    }
    @Override
    protected void onMeasure(int w, int h){
        final int rw = resolveSize(getSuggestedMinimumWidth(), w);
        float ratio;
        if (mSupportedPreviewSizes != null) {
            previewSize = getOptimalPreviewSize(mSupportedPreviewSizes, w, h);
        }
        if(previewSize.height >= previewSize.width)
            ratio = (float) previewSize.height / (float) previewSize.width;
        else
            ratio = (float) previewSize.width / (float) previewSize.height;
        setMeasuredDimension(rw, (int) (rw * ratio));
    }
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.height / size.width;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;

            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
}