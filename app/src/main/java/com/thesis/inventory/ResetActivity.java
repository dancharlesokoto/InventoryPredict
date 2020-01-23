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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ResetActivity extends AppCompatActivity {

    private EditText email;
    Button res;
    String valemail;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        firebaseAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        pd = new ProgressDialog(this);

        email = findViewById(R.id.emailreset);
        res = findViewById(R.id.btnreset);

        res.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                valemail = email.getText().toString().trim();


                if (valemail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(valemail).matches()) {
                    email.setError("Enter a valid email address");

                    return;

                } else {
                    email.setError(null);
                }

                pd.setTitle("Sending Reset Link...");
                pd.show();



//reset password you will get a mail
                firebaseAuth.sendPasswordResetEmail(valemail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    Toast.makeText(ResetActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();

                                    ResetActivity.this.startActivity(new Intent(ResetActivity.this, LoginActivity.class));
                                    ResetActivity.this.finish();
                                } else {
                                    Toast.makeText(ResetActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                }
                                pd.dismiss();
                            }
                        });
            }
        });
    }

    @Override
    public void onBackPressed() {

        ResetActivity.this.startActivity(new Intent(ResetActivity.this, LoginActivity.class));
        ResetActivity.this.finish();

        super.onBackPressed();
    }
}
