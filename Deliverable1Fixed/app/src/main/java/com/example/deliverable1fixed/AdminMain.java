package com.example.deliverable1fixed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminMain extends AppCompatActivity implements View.OnClickListener{

    private DatabaseReference reference;
    private String userID;

    private Button classes;
    private Button accounts;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        classes = (Button) findViewById(R.id.classes);
        classes.setOnClickListener(this);

        accounts = (Button) findViewById(R.id.accounts);
        accounts.setOnClickListener(this);

        logout = (Button) findViewById(R.id.adminSignOut);
        logout.setOnClickListener(this);

        userID = getIntent().getExtras().getString("arg"); // passed from previous page
        reference = FirebaseDatabase.getInstance().getReference("Users");

        final TextView UsernameWTextView = (TextView) findViewById(R.id.adminUsername);
        final TextView TypeWTextView = (TextView) findViewById(R.id.adminUserType);
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if(userProfile != null){
                    String username = userProfile.username;
                    String type = userProfile.type;
                    UsernameWTextView.setText("Username: " + username);
                    TypeWTextView.setText("Type: " + type);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminMain.this, "Database Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.classes:
                Intent intentClasses = new Intent(AdminMain.this, AdminClass.class);
                intentClasses.putExtra("arg", userID);
                startActivity(intentClasses);
                break;

            case R.id.accounts:
                Intent intentAccounts = new Intent(AdminMain.this, AdminAccounts.class);
                intentAccounts.putExtra("arg", userID);
                startActivity(intentAccounts);
                break;

            case R.id.adminSignOut:
                startActivity(new Intent(AdminMain.this, FrontScreen.class));
                break;
        }
    }
}
