package com.thesis.inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    TextView reset, register;
    Button login;
    String valemail, valpass;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        pd = new ProgressDialog(this);

        email = findViewById(R.id.input_email);
        password = findViewById(R.id.input_password);
        reset = findViewById(R.id.link_reset);
        register = findViewById(R.id.link_signup);
        login = findViewById(R.id.btn_login);



        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, ResetActivity.class));
                LoginActivity.this.finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginActivity.this.startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                LoginActivity.this.finish();

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd.setTitle("Authenticating...");
                pd.show();

                valemail = email.getText().toString().trim();
                valpass = password.getText().toString().trim();

                //Validation

                if (valemail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(valemail).matches()) {
                    email.setError("enter a valid email address");
                    pd.dismiss();
                    return;

                } else {
                    email.setError(null);
                }

                if (valpass.isEmpty() || password.length() < 6) {
                    password.setError("must be 6 or more alphanumeric characters");
                    pd.dismiss();
                    return;


                } else {
                    password.setError(null);
                }

                //login user
                firebaseAuth.signInWithEmailAndPassword(valemail, valpass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //progressBar.setVisibility(View.GONE);

                                if (!task.isSuccessful()) {

                                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();

                                    pd.dismiss();

                                } else {
                                    pd.dismiss();


                                    //Validate User Role... SuperAdmin, Admin or User
                                    DocumentReference docRef = db.collection("users").document(valemail);

                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();

                                                if (document != null && document.exists()) {

                                                    String userrole = document.getString("role");
                                                   if (userrole.equals("super")) {
                                                        startActivity(new Intent(LoginActivity.this, ListActivity.class));

                                                        finish();
                                                    } else {
                                                        startActivity(new Intent(LoginActivity.this, UsersActivity.class));

                                                        finish();
                                                    }


                                                } else {
                                                    Toast.makeText(LoginActivity.this, "Invalid User", Toast.LENGTH_SHORT).show();

                                                }
                                            } else {
                                                //Log.d("LOGGER", "get failed with ", task.getException());
                                                Toast.makeText(LoginActivity.this, "get failed with \n" + task.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }
                            }
                        });


            }
        });


    }


    @Override
    protected void onStart() {


        //Keep Current User signed in until he clicks signout

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String user_phone = FirebaseAuth.getInstance().getCurrentUser().getEmail();

            DocumentReference docRef = db.collection("users").document(user_phone);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {

                            String userrole = document.getString("role");
                            if (userrole.equals("super")) {
                                startActivity(new Intent(LoginActivity.this, ListActivity.class));

                                finish();
                            } else {
                                startActivity(new Intent(LoginActivity.this, UsersActivity.class));

                                finish();
                            }


                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid User", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        //Log.d("LOGGER", "get failed with ", task.getException());
                        Toast.makeText(LoginActivity.this, "get failed with \n" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        super.onStart();

    }


}
