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

public class AdminProfileActivity extends AppCompatActivity {
    private EditText email, password, phone, name, role;
    TextView log;
    Button reg;
    String valemail, valpass, valphone, valname, valrole;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    String aId, aName, aMobile, aEmail, aRole, aPassword;

    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        pd = new ProgressDialog(this);

        name = findViewById(R.id.ad_name);

        email = findViewById(R.id.ad_email);
        password = findViewById(R.id.ad_password);
        phone = findViewById(R.id.ad_phone);
        role = findViewById(R.id.ad_role);
        reg = findViewById(R.id.ad_btn_signup);
        log = findViewById(R.id.ad_link_login);



        final Bundle bundle = getIntent().getExtras();

            reg.setText("Update Admin Profile");
            log.setText("Update Admin Profile");

            aId = bundle.getString("aId");
            aName = bundle.getString("aName");
            aMobile = bundle.getString("aMobile");
            aEmail = bundle.getString("aEmail");
            aRole = bundle.getString("aRole");
            aPassword = bundle.getString("aPassword");

            name.setText(aName);
            phone.setText(aMobile);
            email.setText(aEmail);
            role.setText(aRole);





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


                String user_phone = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                    update(user_phone,valname,valphone,valemail,valpass,valrole);



            }
        });
    }



    private void update(String id, String name, String mobile , String email, String password, String role) {

        pd.setTitle("Updating Admin Records");

        pd.show();


        db.collection("users").document(id)
                .update("name",name, "search",name.toLowerCase(),"mobile",mobile ,"email", email,"role",role, "password",password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        pd.dismiss();
                        Toast.makeText(AdminProfileActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(AdminProfileActivity.this, "Error Updating record\n"+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }


}
