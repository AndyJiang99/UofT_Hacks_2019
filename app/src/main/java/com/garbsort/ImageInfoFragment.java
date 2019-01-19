package com.garbsort;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.garbsort.garbsort.R;

import java.io.File;


public class ImageInfoFragment extends Fragment {
    private String pathname;
    private ImageView thumbnail;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
        //        Environment.DIRECTORY_PICTURES), "MyCameraApp");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        thumbnail = getActivity().findViewById(R.id.iv_thumbnail);
        if (getArguments() != null) {
            pathname = getArguments().getString("path");
            Log.e("PATHNAME", pathname + thumbnail.toString());
            Drawable bgDrawable = Drawable.createFromPath(pathname);
            if(bgDrawable != null)thumbnail.setBackground(bgDrawable);
            else{
                bgDrawable = Drawable.createFromPath(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES + pathname).toString());
                if(bgDrawable != null)thumbnail.setBackground(bgDrawable);
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
}
