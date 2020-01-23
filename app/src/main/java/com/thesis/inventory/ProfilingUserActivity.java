package com.thesis.inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfilingUserActivity extends AppCompatActivity {
    private EditText email, password, phone, name;
    TextView log;
    Button reg;
    String valemail, valpass, valphone, valname;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    String aId, aName, aMobile, aEmail, aRole;

    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiling_user);

        firebaseAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        pd = new ProgressDialog(this);

        name = findViewById(R.id.auser_name);

        email = findViewById(R.id.auser_email);
        password = findViewById(R.id.auser_password);
        phone = findViewById(R.id.auser_phone);
        reg = findViewById(R.id.auser_btn_signup);
        log = findViewById(R.id.auser_link_login);


            getuserinfo();





        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                valname = name.getText().toString().trim();
                valemail = email.getText().toString().trim();
                valpass = password.getText().toString().trim();
                valphone = phone.getText().toString().trim();

                //Validation of input

                if (valname.isEmpty()) {
                    name.setError("Enter your name");
                    return;

                } else {
                    name.setError(null);
                }

                if (valphone.isEmpty() || valphone.length() < 11 ||valphone.length() > 11 ) {

                    password.setError("Invalid Phone Number");

                    return;


                } else {
                    password.setError(null);
                }

                if (valemail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(valemail).matches()) {

                    email.setError("Enter a valid email address");

                    return;

                } else {
                    email.setError(null);
                }


                if (valpass.isEmpty() || password.length() < 6 ) {
                    password.setError("password must be 6 or more alphanumeric");

                    return;


                } else {
                    password.setError(null);
                }


                update(aId,valname,valphone,valemail,valpass,aRole);



            }
        });
    }



    private void update(String id, String name, String mobile , String email, String password, String role) {

        pd.setTitle("Updating Records");

        pd.show();


        db.collection("users").document(id)
                .update("name",name, "search",name.toLowerCase(),"mobile",mobile ,"email", email,"role",role, "password",password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        pd.dismiss();
                        Toast.makeText(ProfilingUserActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(ProfilingUserActivity.this, "Error Updating record\n"+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }



    private void getuserinfo(){



        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            String user_phone = FirebaseAuth.getInstance().getCurrentUser().getEmail();


            DocumentReference docRef = db.collection("users").document(user_phone);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {

                            String yourname = document.getString("name");
                            String yourmobile = document.getString("mobile");
                            String youremail = document.getString("email");
                            aRole = document.getString("role");
                            aId = document.getId();

                            name.setText(yourname);
                            phone.setText(yourmobile);
                            email.setText(youremail);





                        } else {
                            Toast.makeText(ProfilingUserActivity.this, "Invalid User", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        //Log.d("LOGGER", "get failed with ", task.getException());
                        Toast.makeText(ProfilingUserActivity.this, "get user failed with \n" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }



}
