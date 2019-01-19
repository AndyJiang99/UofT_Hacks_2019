package com.garbsort;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.garbsort.garbsort.R;

public class ScannerFragment extends Fragment {
    private Button captureButton;
    private FrameLayout cameraDisplay;
    private Camera camera;
    public ScannerFragment() {
        // Required empty public constructor
    }
    public static ScannerFragment newInstance() {
        ScannerFragment fragment = new ScannerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scanner, container, false);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cameraDisplay = getActivity().findViewById(R.id.camera_preview);
        captureButton = getActivity().findViewById(R.id.bt_capture);
        if(checkCameraHardware(getContext())){
            try {
                camera = Camera.open();
                Log.e( "CAMERAFOUND ", camera + "< camera" );
            } catch (Exception e) {
                Log.e("CAMERANF", "CAMERA NOT FOUND");
            }
            if(camera != null){
                setupPreviewDisplay(camera);
            }
        }
    }
    private void setupPreviewDisplay(Camera camera){
        CameraPreview cameraPreview = new CameraPreview(getContext(), camera);
        FrameLayout preview = getActivity().findViewById(R.id.camera_preview);
        preview.addView(cameraPreview);
    }
    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
}
