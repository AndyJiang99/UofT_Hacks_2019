package com.garbsort;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.garbsort.garbsort.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class ImageInfoFragment extends Fragment {
    private String pathname;
    private ImageView thumbnail;
    private Bitmap compBitmap;
    public static final String uriBase = "https://eastus.api.cognitive.microsoft.com/vision/v1.0/analyze";
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
            if(bgDrawable != null) {
                compBitmap = BitmapFactory.decodeFile(pathname);
                thumbnail.setImageBitmap(compBitmap);
                try {
                    Log.e("Initializing", "YEE" );
                    initializeAPI();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int initializeAPI() throws Exception{
        URL obj = new URL(uriBase);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/octet-stream");
        con.setRequestProperty("Ocp-Apim-Subscription-Key", uriKey);


        Map<String, String> parameters = new HashMap<>();
        parameters.put("visualFeatures", "Categories,Description,Color,Adult");
        parameters.put("language", "en");
        Log.e("Outputsteam", con.getOutputStream().toString());
        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        Log.e("YOOO", "works" );
        out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
        int responseCode = con.getResponseCode();
        Log.e("Response Code: ", responseCode + "");
        out.flush();
        out.close();
//
//        BufferedReader in = new BufferedReader(
//                new InputStreamReader(con.getInputStream()));
//        String inputLine;
//        StringBuffer response = new StringBuffer();
//
//        while ((inputLine = in.readLine()) != null) {
//            response.append(inputLine);
//        }
//        in.close();
//        Log.e("Response: ", response.toString());
        return 0;
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
    @Override
    public void onResume(){
        super.onResume();
        try {
            initializeAPI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ParameterStringBuilder {
    public static String getParamsString(Map<String, String> params) throws UnsupportedEncodingException {
    StringBuilder result = new StringBuilder();
    for (Map.Entry<String, String> entry : params.entrySet()) {
        result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
        result.append("=");
        result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        result.append("&");
    }
    String resultString = result.toString();
    return resultString.length() > 0 ? resultString.substring(0, resultString.length() - 1) : resultString;
    }
}
