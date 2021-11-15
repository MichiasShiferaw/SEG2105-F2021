package com.example.deliverable1fixed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeScreen extends AppCompatActivity {
    public String userID;
    private DatabaseReference reference;
    private Resources res;

    private Button logout;
    private Button admin;
    private Button userInstructorAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_home_screen);
        res = getResources();

        admin = (Button) findViewById(R.id.adminButton); // viewed by admins only
        userInstructorAdmin = (Button) findViewById(R.id.userInstructorMemberBtn); // viewed by all atm
        logout = (Button) findViewById(R.id.signOut);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeScreen.this, FrontScreen.class));
            }
        });

        userID = getIntent().getStringExtra("arg"); // passed from previous page
        reference = FirebaseDatabase.getInstance().getReference("Users");

        final TextView UsernameWTextView = (TextView) findViewById(R.id.UsernameW);
        final TextView TypeWTextView = (TextView) findViewById(R.id.TypeW);
        final TextView EmailWTextView = (TextView) findViewById(R.id.emailW);
        final TextView NameWTextView = (TextView) findViewById(R.id.nameW);
        final TextView AgeWTextView = (TextView) findViewById(R.id.ageW);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if(userProfile != null){
                    String username = userProfile.getUsername();
                    String type = userProfile.getType();
                    String email = userProfile.getEmail();
                    String age = userProfile.getAge();
                    String name = userProfile.getFullName();

                    UsernameWTextView.setText("Username: " + username);
                    TypeWTextView.setText("Type: " + type);
                    EmailWTextView.setText("Email: " + email);
                    NameWTextView.setText("Name: "+ name);
                    AgeWTextView.setText("Age: "+ age);
                    if (userProfile.getType() != null) {
                        if (!(userProfile.getType().equals("Admin"))) { // if user is not Admin type
                            admin.setVisibility(View.GONE); // hide admin button from view
                            if (userProfile.getType().equals("Instructor")) {
                                userInstructorAdmin.setText(res.getString(R.string.instructor));
                            }
                        }
                        else {
                            userInstructorAdmin.setText(res.getString(R.string.classes));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeScreen.this, "Database Error", Toast.LENGTH_LONG).show();
            }
        });
        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User userProfile = snapshot.getValue(User.class);
                        String type = userProfile.getType();

                        if(type.equals("Admin")) {
                            Intent intentClasses = new Intent(HomeScreen.this, AdminMain.class);
                            intentClasses.putExtra("arg", userID);
                            startActivity(intentClasses);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(HomeScreen.this, "Database Error", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        userInstructorAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User userProfile = snapshot.getValue(User.class);
                        String type = userProfile.getType();

                        if(type.equals("Instructor")) {
                            Intent intentClasses = new Intent(HomeScreen.this, InstructorMain.class);
                            intentClasses.putExtra("arg", userID);
                            startActivity(intentClasses);
                        }
                        if(type.equals("Admin") && userInstructorAdmin.getText().toString().equals("Classes")) {
                            Intent intentClasses = new Intent(HomeScreen.this, InstructorMain.class);
                            intentClasses.putExtra("arg", userID);
                            startActivity(intentClasses);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(HomeScreen.this, "Database Error", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
