package com.example.user.trofdo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static java.util.UUID.randomUUID;

public class Home extends AppCompatActivity{
    private static final int CHOOSE_IMAGE = 101;
    RelativeLayout relativeLayoutReport;
    EditText editTextDescription;
    ImageView imageView;
    RecyclerView listViewOffences;

    private Button btnSubmit, btnSubmit_Report, btnPrevious;
    private String downloadUrl;

    Uri uriattachment;
    private DatabaseReference dbRef, mData ;
    private ArrayList<String> offences;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private StorageReference storageReference;
    Integer REQUEST_CAMERA=1, SELECT_FILE=0;
    UUID uuid, uuidChild;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        relativeLayoutReport = findViewById(R.id.Report_description);
        editTextDescription = findViewById(R.id.Description);
        imageView = findViewById(R.id.Attarchment);
        btnSubmit = findViewById(R.id.Report);
        btnSubmit_Report = findViewById(R.id.Submit_Report);
        btnPrevious = findViewById(R.id.Previous);
        listViewOffences = findViewById(R.id.Offences);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mFirebaseUser.getUid());

        //uuidChild = UUID.randomUUID();
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile Images");
        mData =  FirebaseDatabase.getInstance().getReference().child("Reported Offences").child(mFirebaseUser.getUid());


        findViewById(R.id.Exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayoutReport.setVisibility(View.GONE);
            }
        });
        findViewById(R.id.LogOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogOut();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayoutReport.setVisibility(View.VISIBLE);
            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listViewOffences.setVisibility(View.VISIBLE);
                relativeLayoutReport.setVisibility(View.GONE);
                DisplayReported();
            }
        });
        btnSubmit_Report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportSubmision();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });


        listViewOffences.setLayoutManager(new LinearLayoutManager(this));
    }

    //////////////////////////////////////////////////


    @Override
    protected void onStart() {
        super.onStart();

//        FirebaseRecyclerAdapter<Home, CaseHolder>  firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Home, CaseHolder>() {
//            @Override
//            protected void populateViewHolder(CaseHolder viewHolder, Home model, int position) {
//                Home.class,
//                        R.layout.one_case,
//                        CaseHolder.class,
//                        CaseHolder
//            } {
//              @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//                      protected void popilateViewHolder(CaseHolder viewHolder, Home model, int position) {
//
//                }
//            }
//
//
//        }
        FirebaseRecyclerAdapter<Case_model, CaseHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Case_model, CaseHolder>(

                Case_model.class,
                R.layout.one_case,
                CaseHolder.class,
                mData

        ) {

             @Override
            protected void populateViewHolder(CaseHolder viewHolder, Case_model model, int position) {

                String postKey = getRef(position).getKey();

                viewHolder.setName_(model.getName());
                viewHolder.setImageView(getApplicationContext(), model.getImage());
            }


        };

        listViewOffences.setAdapter(firebaseRecyclerAdapter);
    }


    private void DisplayReported() {
    }

    private void LogOut() {
        FirebaseAuth.getInstance().signOut();
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }


    private void ReportSubmision() {
        String ReportedOffence = editTextDescription.getText().toString().trim();
        if (TextUtils.isEmpty(ReportedOffence)) {
            Toast.makeText(this, "Please give a brief description", Toast.LENGTH_LONG).show();
        }else{
            HashMap postData = new HashMap();
            postData.put("Offence",ReportedOffence);
            postData.put("capturedImage", downloadUrl);

            dbRef.updateChildren(postData).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Toast.makeText(Home.this, "Data entered successfull to firebase database", Toast.LENGTH_SHORT).show();
                    }else{
                        String message = task.getException().getMessage();
                        Toast.makeText(Home.this, "Error occurred" + message, Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK && data != null) {

            Bundle bundle = data.getExtras();
            final Bitmap bmp = (Bitmap) bundle.get("data");
            imageView.setImageBitmap(bmp);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            byte [] byteArray = byteArrayOutputStream.toByteArray();

            uuid = randomUUID();
            final StorageReference filePath = storageReference.child(uuid.toString()+ ".jpeg");

            filePath.putBytes(byteArray).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(Home.this, "Image saved successfully to firebase storage", Toast.LENGTH_SHORT).show();
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadUrl = uri.toString();
                            }
                        });
                    }else{
                        String message =task.getException().getMessage();
                        Toast.makeText(Home.this, "An error occurred" +message, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else if(requestCode == SELECT_FILE && resultCode== RESULT_OK && data !=null) {
            Uri imageUrl = data.getData();
            imageView.setImageURI(imageUrl);

            uuid = randomUUID();
            final StorageReference filePath = storageReference.child(uuid.toString()+ ".jpeg");

            filePath.putFile(imageUrl).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(Home.this, "Image saved successfully to firebase storage", Toast.LENGTH_SHORT).show();
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadUrl = uri.toString();
                            }
                        });
                    }else{
                        String message =task.getException().getMessage();
                        Toast.makeText(Home.this, "An error occurred" +message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
    private void SelectImage(){
        final CharSequence[] items = {"Camera", "Gallery","Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        builder.setTitle("Upload Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which].equals("Camera")){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getPackageManager()) != null){
                        startActivityForResult(intent, REQUEST_CAMERA);
                    }
                } else if (items[which].equals("Gallery")){
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent, "Select File"), SELECT_FILE);
                } else if (items[which].equals("Cancel")) {
                    dialog.dismiss();
                }

            }
        });
        builder.show();

//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Attarch picture or clip"), CHOOSE_IMAGE);
    }


    private class CaseHolder extends RecyclerView.ViewHolder{

        View mView;

        ImageView mImageView;
        TextView name_;

        public CaseHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        void setName_(String name) {
            name_ = findViewById(R.id.one_name);
           name_.setText(name);
        }

        public void setImageView(final Context context, final String imageView) {
            mImageView = findViewById(R.id.one_image);
           // mImageView = imageView;

            Picasso.with(context).load(imageView).networkPolicy(NetworkPolicy.OFFLINE).into(mImageView, new Callback() {

                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(imageView).into(mImageView);
                }
                });


            }
    }
}

