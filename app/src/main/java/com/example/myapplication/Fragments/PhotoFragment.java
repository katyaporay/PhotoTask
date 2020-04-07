package com.example.myapplication.Fragments;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.myapplication.CameraUtils;
import com.example.myapplication.MainActivity;
import com.example.myapplication.MyAPI;
import com.example.myapplication.MyResponse;
import com.example.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

public class PhotoFragment extends Fragment {
    private Button btn_capture;
    private Button btn_send;
    private TextView txt_help;
    private String imageSavePath;
    private ImageView imageView;
    private Uri fileuri;
    private ProgressBar pb;
    private FrameLayout lr;
    private Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo,container,false);

        toolbar = view.findViewById(R.id.toolbar);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txt_help = view.findViewById(R.id.textView);
        imageView = view.findViewById(R.id.imageView);
        btn_capture = view.findViewById(R.id.capture);
        lr = view.findViewById(R.id.root);
        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CameraUtils.checkPermissions(getActivity().getApplicationContext())) {
                    // фотографируем
                    captureImage();
                } else {
                    // даем права
                    requestCameraPermission();
                }
            }
        });
        btn_send = view.findViewById(R.id.send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });
        return view;
    }
    private void requestCameraPermission() {
        Log.e("EEEEEEEEE","123");
        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(
                new MultiplePermissionsListener() {

                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){
                            Toast.makeText(getActivity().getApplicationContext(),"All granted",Toast.LENGTH_LONG).show();
                        } else {
                            // todo Toast - нет прав
                            Toast.makeText(getActivity().getApplicationContext(),"Access denied",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }
        ).check();
    }

    private void captureImage(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = CameraUtils.getOutputMediaFile();
        Log.e("RRRRR","file:"+file);
        if (file != null){
            imageSavePath = file.getAbsolutePath();
        }
        fileuri = CameraUtils.getOutputMediaUri(getActivity().getApplicationContext(), file);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileuri);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        Log.e("RRRRR","rc:"+requestCode);
        if (requestCode == 1){
            if (resultCode == RESULT_OK){
                CameraUtils.refreshGallery(getActivity().getApplicationContext(), imageSavePath);
                previewImage();
            }
        }
    }


    private void previewImage(){
        txt_help.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
        Bitmap bitmap = BitmapFactory.decodeFile(imageSavePath);
        imageView.setImageBitmap(bitmap);
    }


    private void uploadFile(){
        File file = new File(imageSavePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse(getActivity().getContentResolver().getType(fileuri)), file);
        RequestBody descBody = RequestBody.create(MediaType.parse("text/plain"), "desc file");

        Gson gson = new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MyAPI api = retrofit.create(MyAPI.class);
        Call<MyResponse> call = api.uploadImage(requestFile, descBody);

        pb = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleLarge);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(100, 100);
        params.gravity = Gravity.CENTER;
        lr.addView(pb, params);
        pb.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                pb.setVisibility(View.GONE);
                if (!response.body().error){
                    Toast.makeText(getActivity().getApplicationContext(), "File upload success!", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), response.body().message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
                pb.setVisibility(View.GONE);
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        super.onDestroy();
    }
}
