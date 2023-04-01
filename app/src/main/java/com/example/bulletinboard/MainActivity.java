package com.example.bulletinboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import adapter.DataAdapter;
import adapter.DataSender;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView nav_view;
    private DrawerLayout drawerLayout;

    private FirebaseAuth mAuth;

    private TextView userEmail;
    private AlertDialog dialog;
    private Toolbar toolbar;
    private DataAdapter.OnItemClickCustom onItemClickCustom;

    private RecyclerView rcView;

    private DataAdapter dataAdapter;
    private DataSender dataSender;

    private DbManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init()
    {

        setOnItemClickCustom();
        rcView = findViewById(R.id.rcView);
        rcView.setLayoutManager(new LinearLayoutManager(this));
        List<NewPost> arrayPost =new ArrayList<>();
        dataAdapter = new DataAdapter(arrayPost, this, onItemClickCustom);
        rcView.setAdapter(dataAdapter);

        nav_view = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.toggle_open, R.string.toggle_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        nav_view.setNavigationItemSelectedListener(this);
        userEmail = nav_view.getHeaderView(0).findViewById(R.id.tvemail);
        mAuth = FirebaseAuth.getInstance();

        // test
        getDataDb();
        dbManager = new DbManager(dataSender);
        dbManager.getDataFromDb("Машины");




    }
    private void getDataDb()
    {
        dataSender = new DataSender() {
            @Override
            public void onDataRecived(List<NewPost> listData)
            {
                Collections.reverse(listData);
                dataAdapter.updateAdapter(listData);
            }
        };
    }
    private void setOnItemClickCustom()
    {
        onItemClickCustom = new DataAdapter.OnItemClickCustom() {
            @Override
            public void onItemSelected(int position) {
                Log.d("MyLOg", "Position: "  + position);

            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
    public void onClickEdit(View view)
    {
        Intent i = new Intent(MainActivity.this,EditActivity.class);
        startActivity(i);
    }
    private void getUser()
    {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
        {
            userEmail.setText(currentUser.getEmail());
        }
        else
        {
            userEmail.setText(R.string.sign_in_or_sign_up);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.id_my_ads:
                dbManager.getMyAdsFromDb(mAuth.getUid());
                break;
            case R.id.id_car_ads:
                dbManager.getDataFromDb("Машины");
                break;
            case R.id.id_pc_ads:
                dbManager.getDataFromDb("Компьютеры");
                break;
            case R.id.id_phone_ads:
                dbManager.getDataFromDb("Смартфоны");
                break;
            case R.id.id_dm_ads:
                dbManager.getDataFromDb("Бытовая техника");
                break;
            case R.id.id_sign_up:
                signDialog(R.string.sign_up, R.string.sign_up_button, 0);
                break;
            case R.id.id_sign_in:
                signDialog(R.string.sign_in, R.string.sign_in_button, 1);
                break;
            case R.id.id_sign_out:
                signOut();
                break;
        }
        return true;
    }

    private void signDialog(int title, int buttonTitle, int index)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.sign_up_layout, null);
        dialogBuilder.setView(dialogView);

        TextView titleTextView = dialogView.findViewById(R.id.tvAlertTitle);
        titleTextView.setText(title);

        Button butt = dialogView.findViewById(R.id.buttonSignUp);
        EditText edEmail = dialogView.findViewById(R.id.edEmail);
        EditText edPassword = dialogView.findViewById(R.id.edPass);
        butt.setText(buttonTitle);
        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (index == 0){
                   signUp(edEmail.getText().toString(),edPassword.getText().toString());

               }
               else
               {
                   signIn(edEmail.getText().toString(),edPassword.getText().toString());
               }
               dialog.dismiss();
            }
        });
        dialog = dialogBuilder.create();
        dialog.show();

    }

    private void signUp(String email, String password){

        if (!email.equals("") && !password.equals("")) {

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                getUser();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("MyLogMain", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Ошибка",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(this, "Почта и пароль не указаны", Toast.LENGTH_SHORT).show();
        }

    }

    private void signIn(String email, String password){
        if (!email.equals("") && !password.equals(""))
        {
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    getUser();
                }
                else{
                    Log.d("MyLogMain", "signInWithEmail:failure", task.getException());
                    Toast.makeText(getApplicationContext(), "Ошибка",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        }
        else
        {
            Toast.makeText(this, "Почта и пароль не указаны", Toast.LENGTH_SHORT).show();
        }
    }
    private void signOut()
    {
        mAuth.signOut();
        getUser();
    }


}
