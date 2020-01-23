package com.thesis.inventory;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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

public class PredictActivity extends AppCompatActivity {

    List<ProModel> modelList = new ArrayList<>();
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    CustomAdapterPred adapter;

    ProgressDialog pd;
    AlertDialog.Builder builder;
    String userinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppThemeSearch);
        setContentView(R.layout.activity_predict);

        firebaseAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        pd = new ProgressDialog(this);

        getuserinfo();


        builder = new AlertDialog.Builder(this);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        showData();
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.addbtn:
                        startActivity(new Intent(PredictActivity.this, ScanActivity.class));
                        finish();
                        break;
                    case R.id.action_sales:
                        Toast.makeText(PredictActivity.this, "Sales", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_admin:
                        Toast.makeText(PredictActivity.this, "All Admins", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(PredictActivity.this, RoleActivity.class));
                        break;
                    case R.id.action_predict:
                        Toast.makeText(PredictActivity.this, "Sales Forecast", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_nearby:

                        builder.setMessage("Do you want to Sign Out ?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        firebaseAuth.signOut();
                                        startActivity(new Intent(PredictActivity.this, LoginActivity.class));

                                        finish();

                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //  Action for 'NO' Button
                                        dialog.cancel();
                                    }
                                });
                        //Creating dialog box
                        AlertDialog alert = builder.create();
                        //Setting the title manually
                        alert.setTitle("Sign Out "+userinfo);
                        alert.show();
                }
                return true;
            }
        });


    }

    private void showData() {

        pd.setTitle("Loading...");
        pd.show();

        db.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        modelList.clear();
                        pd.dismiss();
                        for(DocumentSnapshot doc: task.getResult()){
                            ProModel model = new ProModel(doc.getString("id")
                                    , doc.getString("title")
                                    ,doc.getString("description")
                                    ,doc.getString("barcode")
                                    ,doc.getString("price")
                                    ,doc.getString("quantity")
                                    ,doc.getString("timestamp")
                                    ,doc.getString("status")
                                    ,doc.getString("prono"));

                            modelList.add(model);
                        }

                        adapter = new CustomAdapterPred(PredictActivity.this, modelList);

                        mRecyclerView.setAdapter(adapter);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        pd.dismiss();
                        Toast.makeText(PredictActivity.this, "Error Loading Products\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void deleteData(int index){

        pd.setTitle("Deleting Record...");
        pd.show();


        db.collection("products").document(modelList.get(index).getId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toast.makeText(PredictActivity.this, "Record Deleted successfully", Toast.LENGTH_SHORT).show();

                        showData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(PredictActivity.this, "Error Deleting Record\n"+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
    //Searching Database for the string provided from the search box
    private void searchDb(String query) {

        pd.setTitle("Searching Product Records");

        pd.show();

        db.collection("products").whereEqualTo("search",query.toLowerCase())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        modelList.clear();
                        pd.dismiss();
                        for(DocumentSnapshot doc: task.getResult()){
                            ProModel model = new ProModel(doc.getString("id")
                                    , doc.getString("title")
                                    ,doc.getString("description")
                                    ,doc.getString("barcode")
                                    ,doc.getString("price")
                                    ,doc.getString("quantity")
                                    ,doc.getString("timestamp")
                                    ,doc.getString("status")
                                    ,doc.getString("prono"));

                            modelList.add(model);
                        }

                        //populating the List from the result of the search
                        adapter = new CustomAdapterPred(PredictActivity.this, modelList);

                        mRecyclerView.setAdapter(adapter);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        pd.dismiss();
                        Toast.makeText(PredictActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void singleSearchDb(String newText) {


        db.collection("products").whereGreaterThanOrEqualTo("search",newText.toLowerCase())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        modelList.clear();

                        for(DocumentSnapshot doc: task.getResult()){
                            ProModel model = new ProModel(doc.getString("id")
                                    , doc.getString("title")
                                    ,doc.getString("description")
                                    ,doc.getString("barcode")
                                    ,doc.getString("price")
                                    ,doc.getString("quantity")
                                    ,doc.getString("timestamp")
                                    ,doc.getString("status")
                                    ,doc.getString("prono"));

                            modelList.add(model);
                        }

                        //populating the List from the result of the search
                        adapter = new CustomAdapterPred(PredictActivity.this, modelList);

                        mRecyclerView.setAdapter(adapter);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                        Toast.makeText(PredictActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

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

                            String userrole = document.getString("name");
                            userinfo = userrole;

                            Toast.makeText(PredictActivity.this, "Welcome "+userrole, Toast.LENGTH_SHORT).show();


                        } else {
                            Toast.makeText(PredictActivity.this, "Invalid User", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        //Log.d("LOGGER", "get failed with ", task.getException());
                        Toast.makeText(PredictActivity.this, "get user failed with \n" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
}
