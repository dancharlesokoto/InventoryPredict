package com.thesis.inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {

    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    List<Model> modelList = new ArrayList<>();
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    CustomAdapterUsers adapter;

    ProgressDialog pd;
    AlertDialog.Builder builder;
    String userinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppThemeSearch);
        setContentView(R.layout.activity_users);

        firebaseAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        getuserinfo();



        //TextView textView = findViewById(R.id.userinfonav);
        //textView.setText("Welcome "+userinfo);

        pd = new ProgressDialog(this);

        builder = new AlertDialog.Builder(this);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);



        showData();

        dl = (DrawerLayout)findViewById(R.id.activity_mainly);
        t = new ActionBarDrawerToggle(this, dl,R.string.Open, R.string.Close);

        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = (NavigationView)findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.nav_home:

                        startActivity(new Intent(UsersActivity.this, UsersActivity.class));
                        Toast.makeText(UsersActivity.this, "Welcome "+userinfo,Toast.LENGTH_SHORT).show();break;


                    case R.id.nav_ordered:
                        startActivity(new Intent(UsersActivity.this, OrderedActivity.class));

                        Toast.makeText(UsersActivity.this, "Ordered Items",Toast.LENGTH_SHORT).show();break;

                    case R.id.nav_cart:
                        startActivity(new Intent(UsersActivity.this, CartActivity.class));
                        Toast.makeText(UsersActivity.this, "Cart",Toast.LENGTH_SHORT).show();break;

                    case R.id.profile:
                        startActivity(new Intent(UsersActivity.this, ProfilingUserActivity.class));
                        Toast.makeText(UsersActivity.this, "Profile",Toast.LENGTH_SHORT).show();break;
                    case R.id.signout:

                        builder.setMessage("Do you want to Sign Out ?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        firebaseAuth.signOut();
                                        startActivity(new Intent(UsersActivity.this, LoginActivity.class));

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


                default:
                        return true;
                }


                return true;

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        MenuItem cart = menu.findItem(R.id.action_cart);
        cart.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(UsersActivity.this, "Cart", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UsersActivity.this, CartActivity.class));

                return false;
            }
        });

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
    public boolean onOptionsItemSelected(MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
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

                        adapter = new CustomAdapterUsers(UsersActivity.this, modelList);

                        mRecyclerView.setAdapter(adapter);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        pd.dismiss();
                        Toast.makeText(UsersActivity.this, "Error Loading Products\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        adapter = new CustomAdapterUsers(UsersActivity.this, modelList);

                        mRecyclerView.setAdapter(adapter);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        pd.dismiss();
                        Toast.makeText(UsersActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

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
                        adapter = new CustomAdapterUsers(UsersActivity.this, modelList);

                        mRecyclerView.setAdapter(adapter);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                        Toast.makeText(UsersActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
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

                            TextView textView = findViewById(R.id.userinfonav);
                            textView.setText("Welcome "+userrole);
                            Toast.makeText(UsersActivity.this, "Welcome "+userrole, Toast.LENGTH_SHORT).show();


                        } else {
                            Toast.makeText(UsersActivity.this, "Invalid User", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        //Log.d("LOGGER", "get failed with ", task.getException());
                        Toast.makeText(UsersActivity.this, "get user failed with \n" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }



}