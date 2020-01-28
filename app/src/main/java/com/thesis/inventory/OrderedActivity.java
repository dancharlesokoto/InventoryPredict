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

public class OrderedActivity extends AppCompatActivity {

    List<Model> modelList = new ArrayList<>();
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    CustomAdapterOrdered adapter;

    ProgressDialog pd;
    Button checkout;
    String cartprice;
    String email;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppThemeSearch);
        setContentView(R.layout.activity_ordered);

        firebaseAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        pd = new ProgressDialog(this);



        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);


        showData();

        pd = new ProgressDialog(this);



    }

    private void showData() {

        pd.setTitle("Loading...");
        pd.show();

        String id = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        db.collection("cart").document(id).collection(id).whereEqualTo("status","paid")
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

                        adapter = new CustomAdapterOrdered(OrderedActivity.this, modelList);

                        mRecyclerView.setAdapter(adapter);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        pd.dismiss();
                        Toast.makeText(OrderedActivity.this, "Error Loading Products\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        adapter = new CustomAdapterOrdered(OrderedActivity.this, modelList);

                        mRecyclerView.setAdapter(adapter);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        pd.dismiss();
                        Toast.makeText(OrderedActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

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
                        adapter = new CustomAdapterOrdered(OrderedActivity.this, modelList);

                        mRecyclerView.setAdapter(adapter);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                        Toast.makeText(OrderedActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

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





    public void deleteData(int index){

        pd.setTitle("Deleting Record...");
        pd.show();
        String id = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        //db.collection("cart").document(id).collection(id).document(timestamp).set(doc)


        db.collection("cart").document(id).collection(id).document(modelList.get(index).getId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        pd.dismiss();

                        Toast.makeText(OrderedActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(OrderedActivity.this, CartActivity.class));
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(OrderedActivity.this, "Error Deleting \n"+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

}
