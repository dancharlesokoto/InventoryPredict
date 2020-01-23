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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DetailsActivity extends AppCompatActivity {

    TextView bardt, titledt, desdt,pricedt,quandt;
    Button cartdt, minusdt, adddt;


   // private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
    ProgressDialog pd;

    FirebaseFirestore db;

    String dId, dTitle, dDes, dBar, dQuantity, dPrice;
    String cartid, cartprice;
    float price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTheme(R.style.AppThemeSearch);
        setContentView(R.layout.activity_details);

        pd = new ProgressDialog(this);

        db = FirebaseFirestore.getInstance();

        bardt = (TextView)findViewById(R.id.barDt);
        titledt = (TextView)findViewById(R.id.titleDt);
        desdt = (TextView)findViewById(R.id.desDt);
        pricedt = (TextView)findViewById(R.id.priceDt);
        quandt = (TextView)findViewById(R.id.quantityDt);
        cartdt = (Button)findViewById(R.id.saveBtnDt);
        minusdt = (Button)findViewById(R.id.minusDt);
        adddt = (Button)findViewById(R.id.addDt);

        getuserinfo();


           Bundle bundle = getIntent().getExtras();
        //Intent intent = getIntent();
        //final String temp = intent.getStringExtra("url");

            dId = bundle.getString("dId");
            dTitle = bundle.getString("dTitle");
            dDes = bundle.getString("dDes");
            dBar = bundle.getString("dBar");
            dQuantity = bundle.getString("dQuantity");
            dPrice = bundle.getString("dPrice");

            titledt.setText(dTitle);
            desdt.setText(dDes);
            pricedt.setText("$"+dPrice);
            bardt.setText(dBar);

            quandt.setText("1");

            adddt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(Integer.parseInt(quandt.getText().toString().trim()) < Integer.parseInt(dQuantity)){
                        int quan = Integer.parseInt(quandt.getText().toString().trim()) + 1;
                        quandt.setText(String.valueOf(quan));
                        price = quan * Float.parseFloat(dPrice);
                        pricedt.setText("$"+ price);
                    }
                }
            });

            minusdt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                   if(Integer.parseInt(quandt.getText().toString().trim()) != 1){
                        int quan = Integer.parseInt(quandt.getText().toString().trim()) - 1;
                        quandt.setText(String.valueOf(quan));
                        price = quan * Float.parseFloat(dPrice);
                        pricedt.setText("$"+ price);
                    }
                }
            });




        cartdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(DetailsActivity.this, " User Func"+cartid + " & "+cartprice, Toast.LENGTH_LONG).show();

                Date date = new Date();

                Timestamp times = new Timestamp(date.getTime());

                String pricer = String.valueOf(price + Float.parseFloat(cartprice));

                //Toast.makeText(DetailsActivity.this, "Price "+ pricer, Toast.LENGTH_SHORT).show();


                final String id = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                updateData(id,pricer,times.toString());

                upload(id,titledt.getText().toString(), desdt.getText().toString(),pricer, quandt.getText().toString().trim(),bardt.getText().toString(),times.toString());




            }
        });



    }

   private void upload(String uid, String title, String des, final String price, String quant, String bar, final String timestamp) {


        pd.setTitle("Adding Product to Cart");

        pd.show();

       Date date = new Date();

       Timestamp times = new Timestamp(date.getTime());

        String uuid = UUID.randomUUID().toString();
        //final String id = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        Map<String, Object> doc = new HashMap<>();
        //title, description, barcode, quantity, timestamp
        doc.put("id", uid);
        doc.put("title",title);
       doc.put("search",title.toLowerCase());
        doc.put("status","open");
        doc.put("description",des);
        doc.put("barcode",bar);
        doc.put("price",price);
        doc.put("quantity",quant);
        doc.put("timestamp",timestamp);

        //.document(email).collection(email).document(id)
        db.collection("cart").document(uid).collection(uid).document(uuid).set(doc)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        pd.dismiss();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        pd.dismiss();
                        Toast.makeText(DetailsActivity.this, "Unsuccessful "+ e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


    }


    private void updateData(String id, String pricer, String timestamp) {



        db.collection("totalcart").document(id)
                .update("id",id, "price",pricer,"timestamp",timestamp)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        pd.dismiss();
                        Toast.makeText(DetailsActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(DetailsActivity.this, "Error \n"+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void getuserinfo(){



        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            String user_phone = FirebaseAuth.getInstance().getCurrentUser().getEmail();


            DocumentReference docRef = db.collection("totalcart").document(user_phone);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {

                            cartprice  = document.getString("price");
                            cartid = document.getString("id");



                        } else {
                            Toast.makeText(DetailsActivity.this, "Invalid User", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        //Log.d("LOGGER", "get failed with ", task.getException());
                        Toast.makeText(DetailsActivity.this, "get user failed with \n" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }




}
