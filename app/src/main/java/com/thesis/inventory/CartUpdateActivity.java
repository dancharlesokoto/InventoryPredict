package com.thesis.inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CartUpdateActivity extends AppCompatActivity {

    TextView ubardt, utitledt, udesdt,upricedt,uquandt;
    Button ucartdt, uminusdt, uadddt, udelete;


    ProgressDialog pd;

    FirebaseFirestore db;

    String dId, dTitle, dDes, dBar, dQuantity, dPrice;
    String cartid, cartprice, prodprice;
    float price;

    float minusPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTheme(R.style.AppThemeSearch);
        setContentView(R.layout.activity_cart_update);

        pd = new ProgressDialog(this);

        db = FirebaseFirestore.getInstance();

        getuserinfo();

        ubardt = (TextView)findViewById(R.id.baruDt);
        utitledt = (TextView)findViewById(R.id.titleuDt);
        udesdt = (TextView)findViewById(R.id.desuDt);
        upricedt = (TextView)findViewById(R.id.priceuDt);
        uquandt = (TextView)findViewById(R.id.quantityuDt);
        ucartdt = (Button)findViewById(R.id.saveBtnuDt);
        uminusdt = (Button)findViewById(R.id.minusuDt);
        uadddt = (Button)findViewById(R.id.adduDt);
        udelete = (Button)findViewById(R.id.deleteuDt);



        Bundle bundle = getIntent().getExtras();

        dId = bundle.getString("cId");
        dTitle = bundle.getString("cTitle");
        dDes = bundle.getString("cDes");
        dBar = bundle.getString("cBar");
        dQuantity = bundle.getString("cQuantity");
        dPrice = bundle.getString("cPrice");



        getproductquantity(dBar);

        utitledt.setText(dTitle);
        udesdt.setText(dDes);
        minusPrice = Float.parseFloat(cartprice) - Float.parseFloat(dPrice);
        upricedt.setText("$"+dPrice);
        ubardt.setText(dBar);

        uquandt.setText(dQuantity);

        final float tota = Float.parseFloat(dPrice)/Float.parseFloat(dQuantity);

        uadddt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Integer.parseInt(uquandt.getText().toString().trim()) < Integer.parseInt(prodprice)){
                    int quan = Integer.parseInt(uquandt.getText().toString().trim()) + 1;
                    uquandt.setText(String.valueOf(quan));
                    price = quan * tota;
                    upricedt.setText("$"+ price);
                }
            }
        });

        uminusdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Integer.parseInt(uquandt.getText().toString().trim()) != 1){
                    int quan = Integer.parseInt(uquandt.getText().toString().trim()) - 1;
                    uquandt.setText(String.valueOf(quan));
                    price = quan * tota;
                    upricedt.setText("$"+ price);
                }
            }
        });

        udelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date date = new Date();
                final String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                final Timestamp times = new Timestamp(date.getTime());

                final String pricer = String.valueOf(minusPrice);


                AlertDialog.Builder builder = new AlertDialog.Builder(CartUpdateActivity.this);

                String[] options = {"Do you want to delete Product?", "Delete Product Record"};

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(which == 1){

                            updateTotalCart(user,pricer,times.toString());

                            deleteData(dId);

                        }

                    }
                }).create().show();



            }
        });




        ucartdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                Date date = new Date();

                Timestamp times = new Timestamp(date.getTime());

                String pricer = String.valueOf(price + Float.parseFloat(cartprice));

                updateTotalCart(user,pricer,times.toString());

                update(dId,user, String.valueOf(utitledt.getText()), String.valueOf(udesdt.getText()),pricer, uquandt.getText().toString().trim(),ubardt.getText().toString(),times.toString());




            }
        });



    }





    private void update(String id, String email, String title, String des,String price,String quant, String bar, String timestamp) {

        pd.setTitle("Updating Product Records");

        pd.show();


        db.collection("cart").document(email).collection(email).document(id)
                .update("id",email,"title",title, "search",title.toLowerCase(),"description",des ,"barcode", bar,"price",price, "quantity",quant,"status","open","timestamp",timestamp)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        pd.dismiss();
                        Toast.makeText(CartUpdateActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CartUpdateActivity.this, CartActivity.class));
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(CartUpdateActivity.this, "Error Updating record\n"+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void updateTotalCart(String id, String pricer, String timestamp) {



        db.collection("totalcart").document(id)
                .update("id",id, "price",pricer,"timestamp",timestamp)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        pd.dismiss();
                        Toast.makeText(CartUpdateActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(CartUpdateActivity.this, "Error \n"+e.getMessage(), Toast.LENGTH_SHORT).show();

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
                            //Toast.makeText(CartUpdateActivity.this, cartid +" & "+ cartprice, Toast.LENGTH_SHORT).show();


                        } else {
                            Toast.makeText(CartUpdateActivity.this, "Invalid User", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        //Log.d("LOGGER", "get failed with ", task.getException());
                        Toast.makeText(CartUpdateActivity.this, "get user failed with \n" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    public void deleteData(String index){

        pd.setTitle("Deleting Record...");
        pd.show();
        String id = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        //db.collection("cart").document(id).collection(id).document(timestamp).set(doc)


        db.collection("cart").document(id).collection(id).document(index)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toast.makeText(CartUpdateActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(CartUpdateActivity.this, CartActivity.class));
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(CartUpdateActivity.this, "Error Deleting \n"+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
    private void getproductquantity(String bar){



        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            String id = FirebaseAuth.getInstance().getCurrentUser().getEmail();

            db.collection("products").whereEqualTo("barcode",bar)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {


                            for(DocumentSnapshot doc: task.getResult()){

                                prodprice = doc.getString("quantity");
                                //cartprice = doc.getString("price");

                            }



                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {


                            Toast.makeText(CartUpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        }
    }




}
