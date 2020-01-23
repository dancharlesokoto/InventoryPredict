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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AdminProfilingActivity extends AppCompatActivity {
    private EditText email, password, phone, name, role;
    TextView log;
    Button reg;
    String valemail, valpass, valphone, valname, valrole;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;


    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profiling);

        firebaseAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        pd = new ProgressDialog(this);

        name = findViewById(R.id.aad_name);

        email = findViewById(R.id.aad_email);
        password = findViewById(R.id.aad_password);
        phone = findViewById(R.id.aad_phone);
        role = findViewById(R.id.aad_role);
        reg = findViewById(R.id.aad_btn_signup);
        log = findViewById(R.id.aad_link_login);




        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                valname = name.getText().toString().trim();
                valemail = email.getText().toString().trim();
                valpass = password.getText().toString().trim();
                valphone = phone.getText().toString().trim();
                valrole = role.getText().toString().trim();

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

                if (valrole.isEmpty()) {
                    role.setError("Role must be set");

                    return;


                } else {
                    role.setError(null);
                }

                if (valpass.isEmpty() || password.length() < 6 ) {
                    password.setError("password must be 6 or more alphanumeric");

                    return;


                } else {
                    password.setError(null);
                }


                    firebaseAuth.createUserWithEmailAndPassword(valemail,valpass)
                            .addOnCompleteListener(AdminProfilingActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    //Log.d(TAG, "New user registration: " + task.isSuccessful());

                                    if (!task.isSuccessful()) {
                                        Toast.makeText(AdminProfilingActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                                        //RegisterActivity.this.showToast("Authentication failed. " + task.getException());
                                    } else {

                                        //call a function that saves user typed info to database
                                        upload(valname,valphone,valemail,valpass,valrole);

                                    }
                                }
                            });




            }
        });
    }

    //Save User Details in Database
    private void upload(String name, String mobile , String email, String pass, String role) {

        pd.setTitle("Saving Admin Records");

        pd.show();

        Map<String, Object> doc = new HashMap<>();
        doc.put("name",name);
        doc.put("mobile",mobile);
        doc.put("email",email);
        doc.put("password",pass);
        doc.put("role",role.toLowerCase());


        db.collection("users").document(email).set(doc)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        pd.dismiss();
                        Toast.makeText(AdminProfilingActivity.this, "Record Saved Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminProfilingActivity.this, RoleActivity.class));

                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        pd.dismiss();
                        Toast.makeText(AdminProfilingActivity.this, "Upload Unsuccessful "+ e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }



}
