package com.garbsort;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.garbsort.garbsort.R;

import java.io.ByteArrayOutputStream;
import java.io.File;


public class ImageInfoFragment extends Fragment {
    private String pathname;
    private ImageView thumbnail;
    private Bitmap compBitmap;
    CallReq c;
    public static final String uriBase = "https://eastus.api.cognitive.microsoft.com/vision/v2.0/analyze";
    public static final String uriKey = "312f7b6b79fd4562ae3ad1572adbc071";
    public ImageInfoFragment() {
        // Required empty public constructor
    }

    public static ImageInfoFragment newInstance(File file) {
        ImageInfoFragment fragment = new ImageInfoFragment();
        Bundle args = new Bundle();
        args.putString("path", file.getPath());
        fragment.setArguments(args);
        return fragment;
    }

    public void setupText(String str){
        TextView current = getActivity().findViewById(R.id.txt_pred);
        current.setText(str);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
        //        Environment.DIRECTORY_PICTURES), "MyCameraApp");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        c = (CallReq) getContext();
        thumbnail = getActivity().findViewById(R.id.iv_thumbnail);
        if (getArguments() != null) {
            pathname = getArguments().getString("path");
            Log.e("PATHNAME", pathname + thumbnail.toString());
                compBitmap = BitmapFactory.decodeFile(pathname);
                thumbnail.setImageBitmap(compBitmap);
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                compBitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                byte[] byteArray = stream.toByteArray();
                String cx = c.initiate(byteArray);
                setupText(cx);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_info, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    public interface CallReq{
        String initiate(byte[] comp)throws Exception;
    }
}
