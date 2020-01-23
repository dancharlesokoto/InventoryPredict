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
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity{
    EditText titleEt, desEt, quanEt, priceEt, prodnoet;
    public TextView barEt;
    Button saveEt;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
    ProgressDialog pd;

    FirebaseFirestore db;

    String pId, pTitle, pDes, pBar, pQuantity, pPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prodnoet = (EditText)findViewById(R.id.prodnoEt);
        titleEt = (EditText)findViewById(R.id.titleEt);
        desEt = (EditText)findViewById(R.id.desEt);
        quanEt = (EditText)findViewById(R.id.quantityEt);
        priceEt = (EditText)findViewById(R.id.priceEt);
        barEt = (TextView)findViewById(R.id.barEt);
        saveEt = (Button)findViewById(R.id.saveBtn);

        Intent intent = getIntent();
        final String temp = intent.getStringExtra("url");

        barEt.setText(temp);

        final Bundle bundle = getIntent().getExtras();
        if (temp == null || temp == ""){

            saveEt.setText("Update Product Records");

            pId = bundle.getString("pId");
            pTitle = bundle.getString("pTitle");
            pDes = bundle.getString("pDes");
            pBar = bundle.getString("pBar");
            pQuantity = bundle.getString("pQuantity");
            pPrice = bundle.getString("pPrice");

            titleEt.setText(pTitle);
            desEt.setText(pDes);
            priceEt.setText(pPrice);
            quanEt.setText(pQuantity);
            barEt.setText(pBar);


        }else{

            saveEt.setText("Save New Product");
        }

        pd = new ProgressDialog(this);

        db = FirebaseFirestore.getInstance();

        saveEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Bundle bundle1 = getIntent().getExtras();
                if (temp == null || temp == ""){


                    String id = pId;

                    String title = titleEt.getText().toString().trim();
                    String des = desEt.getText().toString().trim();
                    String quant = quanEt.getText().toString().trim();
                    String pric = priceEt.getText().toString().trim();
                    String bar = barEt.getText().toString().trim();


                    if (title.isEmpty()) {
                        titleEt.setError("Input Product Name");

                        return;

                    } else {
                        titleEt.setError(null);
                    }



                    if (des.isEmpty()) {
                        desEt.setError("Input PDescription");

                        return;

                    } else {
                        desEt.setError(null);
                    }

                    if (pric.isEmpty()) {
                        priceEt.setError("Input Product Unit Price");

                        return;

                    } else {
                        priceEt.setError(null);
                    }

                    if (quant.isEmpty()) {
                        quanEt.setError("Input Product Quantity");

                        return;

                    } else {
                        quanEt.setError(null);
                    }

                    if (bar.isEmpty()) {
                        barEt.setError("Input Product Barcode");

                        return;

                    } else {
                        barEt.setError(null);
                    }


                    updateData(id, title, des,pric,quant, bar);


                }else
                {
                    String prono = prodnoet.getText().toString().trim();
                    String title = titleEt.getText().toString().trim();
                    String des = desEt.getText().toString().trim();
                    String quant = quanEt.getText().toString().trim();
                    String bar = barEt.getText().toString().trim();

                    String pric = priceEt.getText().toString().trim();

                    Date date = new Date();

                    Timestamp times = new Timestamp(date.getTime());

                    if (title.isEmpty()) {
                        titleEt.setError("Input Product Name");

                        return;

                    } else {
                        titleEt.setError(null);
                    }

                    if (prono.isEmpty()) {
                        prodnoet.setError("Input Product No.");

                        return;

                    } else {
                        prodnoet.setError(null);
                    }

                    if (des.isEmpty()) {
                        desEt.setError("Input PDescription");

                        return;

                    } else {
                        desEt.setError(null);
                    }

                    if (pric.isEmpty()) {
                        priceEt.setError("Input Product Unit Price");

                        return;

                    } else {
                        priceEt.setError(null);
                    }

                    if (quant.isEmpty()) {
                        quanEt.setError("Input Product Quantity");

                        return;

                    } else {
                        quanEt.setError(null);
                    }

                    if (bar.isEmpty()) {
                        barEt.setError("Input Product Barcode");

                        return;

                    } else {
                        barEt.setError(null);
                    }

                    upload(title, des,pric, quant,bar,times.toString(),prono);


                }

            }
        });
    }

    private void updateData(String id, String title, String des,String price,String quant, String bar) {

        pd.setTitle("Updating Product Records");

        pd.show();


        db.collection("products").document(id)
                .update("title",title, "search",title.toLowerCase(),"description",des ,"barcode", bar,"price",price, "quantity",quant)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        pd.dismiss();
                        Toast.makeText(MainActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, ListActivity.class));
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(MainActivity.this, "Error Updating record\n"+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void upload(String title, String des, String price, String quant, String bar,String timestamp,String prono) {

        pd.setTitle("Saving Product Records");

        pd.show();

        String id = UUID.randomUUID().toString();
        Map<String, Object> doc = new HashMap<>();
//title, description, barcode, quantity, timestamp
        doc.put("id", id);
        doc.put("prono", prono);
        doc.put("title",title);
        doc.put("search",title.toLowerCase());
        doc.put("description",des);
        doc.put("barcode",bar);
        doc.put("price",price);
        doc.put("quantity",quant);
        doc.put("timestamp",timestamp);

        db.collection("products").document(id).set(doc)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        pd.dismiss();
                        Toast.makeText(MainActivity.this, "Record Uploaded Successfully", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(MainActivity.this, ListActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        pd.dismiss();
                        Toast.makeText(MainActivity.this, "Upload Unsuccessful "+ e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }


}
