package com.thesis.inventory;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CartActivity extends AppCompatActivity {

    List<Model> modelList = new ArrayList<>();
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    CustomAdapterCart adapter;

    ProgressDialog pd;
    Button checkout;
    String cartprice;

    String email = "";
    String fName = "";
    String lName = "";
    String narration = "payment";
    String txRef;
    String country = "NG";
    String currency = "NGN";

    final String publicKey = "FLWPUBK-a5350848a09d3e782833f2122bbf965f-X"; //Get your public key from your account
    final String encryptionKey = "054a22414b23e0a8dd0e1d74"; //Get your encryption key from your account

    int amount = 0;

    String wallet ="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppThemeSearch);
        setContentView(R.layout.activity_cart);

        firebaseAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        pd = new ProgressDialog(this);



        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);


        showData();
        getuserinfo();

        pd = new ProgressDialog(this);


        wallet = cartprice;


        checkout = findViewById(R.id.checkout);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView price = findViewById(R.id.totally);
                String ppt = price.getText().toString();



                makePayment(Integer.parseInt(ppt));
            }
        });

    }

    private void showData() {

        pd.setTitle("Loading...");
        pd.show();

        String id = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        db.collection("cart").document(id).collection(id).whereGreaterThanOrEqualTo("status","open")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        modelList.clear();
                        pd.dismiss();
                        for(DocumentSnapshot doc: task.getResult()){
                            Model model = new Model(doc.getString("id")
                                    , doc.getString("title")
                                    ,doc.getString("description")
                                    ,doc.getString("barcode")
                                    ,doc.getString("price")
                                    ,doc.getString("quantity")
                                    ,doc.getString("timestamp")
                                    ,doc.getString("status"));

                            modelList.add(model);
                        }

                        adapter = new CustomAdapterCart(CartActivity.this, modelList);

                        mRecyclerView.setAdapter(adapter);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        pd.dismiss();
                        Toast.makeText(CartActivity.this, "Error Loading Products\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



    //Searching Database for the string provided from the search box
    private void searchDb(String query) {

        pd.setTitle("Searching Cart Records");

        pd.show();

        String id = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        //db.collection("cart").document(id).collection(id).document(timestamp).set(doc)


        db.collection("cart").document(id).collection(id).whereEqualTo("search",query.toLowerCase())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        modelList.clear();
                        pd.dismiss();
                        for(DocumentSnapshot doc: task.getResult()){
                            Model model = new Model(doc.getString("id")
                                    , doc.getString("title")
                                    ,doc.getString("description")
                                    ,doc.getString("barcode")
                                    ,doc.getString("price")
                                    ,doc.getString("quantity")
                                    ,doc.getString("timestamp")
                                    ,doc.getString("status"));
                            modelList.add(model);
                        }

                        //populating the List from the result of the search
                        adapter = new CustomAdapterCart(CartActivity.this, modelList);

                        mRecyclerView.setAdapter(adapter);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        pd.dismiss();
                        Toast.makeText(CartActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void singleSearchDb(String newText) {


        String id = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        db.collection("cart").document(id).collection(id).whereGreaterThanOrEqualTo("search",newText.toLowerCase())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        modelList.clear();

                        for(DocumentSnapshot doc: task.getResult()){
                            Model model = new Model(doc.getString("id")
                                    , doc.getString("title")
                                    ,doc.getString("description")
                                    ,doc.getString("barcode")
                                    ,doc.getString("price")
                                    ,doc.getString("quantity")
                                    ,doc.getString("timestamp")
                                    ,doc.getString("status"));

                            modelList.add(model);
                        }

                        //populating the List from the result of the search
                        adapter = new CustomAdapterCart(CartActivity.this, modelList);

                        mRecyclerView.setAdapter(adapter);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                        Toast.makeText(CartActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }


    //Search Box.... Collecting the string from user
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                //Passing the search word or string to Function SearchDb
                searchDb(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                //Passing the search word or string to Function SearchDb
                //Search as the user is typing
                singleSearchDb(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    public void getuserinfo(){



        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            final String user_phone = FirebaseAuth.getInstance().getCurrentUser().getEmail();


            db.collection("totalcart").document(user_phone)
            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {


                            cartprice = document.getString("price");
                            email = user_phone;

                            TextView price = findViewById(R.id.totally);
                            price.setText(cartprice);

                            checkout = findViewById(R.id.checkout);
                            checkout.setText("CHECK OUT $"+cartprice);





                        } else {
                            Toast.makeText(CartActivity.this, "Invalid User", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        //Log.d("LOGGER", "get failed with ", task.getException());
                        Toast.makeText(CartActivity.this, "get user failed with \n" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }

    public void makePayment(int amount){
        txRef = email +" "+  UUID.randomUUID().toString();

        /*
        Create instance of RavePayManager
         */
        new RavePayManager(this).setAmount(amount)
                .setCountry(country)
                .setCurrency(currency)
                .setEmail(email)
                .setNarration(narration)
                .setPublicKey(publicKey)
                .setEncryptionKey(encryptionKey)
                .setTxRef(txRef)
                .acceptAccountPayments(true)
                .acceptCardPayments(true)
                .acceptMpesaPayments(false)
                .acceptGHMobileMoneyPayments(false)
                .onStagingEnv(false)
                .withTheme(R.style.MyCustomTheme)
                .initialize();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            String message = data.getStringExtra("response");
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                //String user_phone = mAuth.getCurrentUser().getPhoneNumber();
                //wallet = String.valueOf(Integer.parseInt(wallet) + amount);
                //updateData(user_phone, wallet);
                Toast.makeText(this, "SUCCESS " + message, Toast.LENGTH_SHORT).show();
            } else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(this, "ERROR " + message, Toast.LENGTH_SHORT).show();
            } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(this, "CANCELLED " + message, Toast.LENGTH_SHORT).show();
            }
        }
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
                        Toast.makeText(CartActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(CartActivity.this, "Error Updating record\n"+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

}
