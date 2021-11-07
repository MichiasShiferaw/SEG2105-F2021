package com.example.deliverable1fixed;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Hashtable;

public class InstructorTeachClass extends AppCompatActivity implements View.OnClickListener{

    private static class NumericKeyBoardTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return source;
        }
    }

    private String userID;
    private User user;

    private DatabaseReference referenceClassTypes;
    private DatabaseReference referenceClasses;

    private EditText editTextSetCapacity;
    private EditText editTextSetName;

    private Spinner daysSpinner;
    private String selectedDay;
    private String[] daysList;

    private Spinner timeSlotsSpinner;
    private String selectedTimeSlot;
    private String[] timeSlotsList;

    private Spinner difficultyLevelsSpinner;
    private String selectedDifficultyLevel;
    private String[] difficultyLevelsList;

    private Spinner classTypesSpinner;
    private String selectedClassType;
    private ArrayList<String> classTypesList;
    private Hashtable<String, ClassType> classTypesMap;

    private ArrayList<Class> classesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_instructor_teach_class);

        userID = getIntent().getExtras().getString("arg"); // passed from previous page
        referenceClassTypes = FirebaseDatabase.getInstance().getReference("ClassTypes");
        referenceClasses = FirebaseDatabase.getInstance().getReference("Classes");

        DatabaseReference referenceUsers = FirebaseDatabase.getInstance().getReference("Users");
        referenceUsers.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InstructorTeachClass.this, "Database Error", Toast.LENGTH_LONG).show();
            }
        });

        Resources res = getResources();

        editTextSetName = (EditText) findViewById(R.id.text_teach_class_name_field);

        editTextSetCapacity= (EditText) findViewById(R.id.text_teach_class_capacity_field);
        editTextSetCapacity.setTransformationMethod(new NumericKeyBoardTransformationMethod());

        Button home = (Button) findViewById(R.id.homeBtn);
        home.setOnClickListener(this);

        Button createClass = (Button) findViewById(R.id.createClassToTeachBtn);
        createClass.setOnClickListener(this);

        daysSpinner = (Spinner) findViewById(R.id.teachClassDaySpinner);
        daysList = res.getStringArray(R.array.days);

        timeSlotsSpinner = (Spinner) findViewById(R.id.teachClassTimeSlotSpinner);
        timeSlotsList = res.getStringArray(R.array.timeSlots);

        difficultyLevelsSpinner = (Spinner) findViewById(R.id.teachClassDifficultySpinner);
        difficultyLevelsList = res.getStringArray(R.array.difficultyLevels);


        classTypesSpinner = (Spinner) findViewById(R.id.teachClassTypeSpinner);
        classTypesList = new ArrayList<>();
        classTypesList.add(0, "Select class type");

        classesList = new ArrayList<>();

        classTypesMap = new Hashtable<String, ClassType>();

        pullClassTypeData();
        pullClassesData();
        initializeAllSpinnerDropdowns();
        checkDayAndClassType(new ClassType(), ""); // initialize pull with null values
        checkExistingName(""); // initialize pull with null values
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createClassToTeachBtn:
                createClass();
                break;
            case R.id.homeBtn:
                Intent intentView = new Intent(InstructorTeachClass.this, InstructorMain.class);
                intentView.putExtra("arg", userID);
                startActivity(intentView);
                break;
        }
    }

    private void pullClassTypeData() {
        referenceClassTypes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ClassType classTypeObject = snapshot.getValue(ClassType.class);
                    String name = snapshot.child("name").getValue(String.class);
                    if(name != null && classTypeObject != null) {
                        if(!(classTypesList.contains(name))) {
                            classTypesList.add(name);
                            classTypesMap.put(name, classTypeObject);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InstructorTeachClass.this, "Database Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void pullClassesData() {
        referenceClasses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Class classObject = snapshot.getValue(Class.class);
                    if(classObject != null) {
                        classesList.add(classObject);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InstructorTeachClass.this, "Database Error", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void initializeAllSpinnerDropdowns() {

        // classTypesSpinner
        ArrayAdapter<String> classTypesAdapter =
                new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, classTypesList);
        classTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classTypesSpinner.setAdapter(classTypesAdapter);
        classTypesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!(parent.getItemAtPosition(position).equals("Select class type"))) {
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
                    selectedClassType = item;
                } else {
                    selectedClassType = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(InstructorTeachClass.this, "Select class type", Toast.LENGTH_LONG).show();
                selectedClassType = "";
            }
        });

        // daysSpinner
        ArrayAdapter<String> daysAdapter =
                new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, daysList);
        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daysSpinner.setAdapter(daysAdapter);
        daysSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!(parent.getItemAtPosition(position).equals("Select a day"))) {
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
                    selectedDay = item;
                } else {
                    selectedDay = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(InstructorTeachClass.this, "Select a day", Toast.LENGTH_LONG).show();
                selectedDay = "";
            }
        });

        // timeSlotSpinner
        ArrayAdapter<String> timeSlotAdapter =
                new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, timeSlotsList);
        timeSlotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSlotsSpinner.setAdapter(timeSlotAdapter);
        timeSlotsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!(parent.getItemAtPosition(position).equals("Select a time slot"))) {
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
                    selectedTimeSlot = item;
                } else {
                    selectedTimeSlot = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(InstructorTeachClass.this, "Select a time slot", Toast.LENGTH_LONG).show();
                selectedTimeSlot = "";
            }
        });

        // difficultyLevelsSpinner
        ArrayAdapter<String> difficultyLevelsAdapter =
                new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, difficultyLevelsList);
        difficultyLevelsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultyLevelsSpinner.setAdapter(difficultyLevelsAdapter);
        difficultyLevelsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!(parent.getItemAtPosition(position).equals("Select a difficulty"))) {
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
                    selectedDifficultyLevel = item;
                } else {
                    selectedDifficultyLevel = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(InstructorTeachClass.this, "Select a difficulty", Toast.LENGTH_LONG).show();
                selectedDifficultyLevel = "";
            }
        });
    }

    private String checkDayAndClassType(ClassType classType, String day) {
        if(classesList != null) {
            for (int i = 0; i < classesList.size(); i++) {
                if (classesList.get(i).classType.name.equals(classType.name)) {
                    if (classesList.get(i).day.equals(day)) {
                        return classesList.get(i).instructor.fullName;
                    }
                }
            }
            return "";
        }
        return "";
    }

    private String checkExistingName(String name) {
        if(classesList != null) {
            for (int i = 0; i < classesList.size(); i++) {
                    if (classesList.get(i).name.equals(name)) {
                        return classesList.get(i).name;
                    }
                }
            return "";
        }
        return "";
    }

    private void createClass() {

        ClassType classType = classTypesMap.get(selectedClassType);
        if(classType != null) {

            // form field validation
            String name = editTextSetName.getText().toString().trim();
            if (name.isEmpty()) {
                String estring = "Enter a valid name";
                ForegroundColorSpan fgcspan = new ForegroundColorSpan(getResources().getColor(R.color.white));
                SpannableStringBuilder ssbuilder = new SpannableStringBuilder(estring);
                ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
                editTextSetName.setError(ssbuilder);
                editTextSetName.requestFocus();
                return;
            }
            if(!(checkExistingName(name).equals(""))) {
                Toast.makeText(InstructorTeachClass.this, "Already existing " + classType.name +
                                " class named: " + checkExistingName(name), Toast.LENGTH_SHORT).show();
                return;
            }


            if (selectedDifficultyLevel.equals("")) {
                Toast.makeText(InstructorTeachClass.this, "Select a difficulty level", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedDay.equals("")) {
                Toast.makeText(InstructorTeachClass.this, "Select a day", Toast.LENGTH_SHORT).show();
                return;
            }

            // verify if there exists a class of the same type on the selectedDay
            if(!(checkDayAndClassType(classType, selectedDay).equals(""))) {
                Toast.makeText(InstructorTeachClass.this, "Already existing " + classType.name +
                        " class on " + selectedDay + " scheduled by: " + checkDayAndClassType(classType, selectedDay)
                        , Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedTimeSlot.equals("")) {
                Toast.makeText(InstructorTeachClass.this, "Select a time slot", Toast.LENGTH_SHORT).show();
                return;
            }
            String capacity = editTextSetCapacity.getText().toString().trim();
            if (capacity.isEmpty()) {
                String estring = "Enter a valid capacity";
                ForegroundColorSpan fgcspan = new ForegroundColorSpan(getResources().getColor(R.color.white));
                SpannableStringBuilder ssbuilder = new SpannableStringBuilder(estring);
                ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
                editTextSetCapacity.setError(ssbuilder);
                editTextSetCapacity.requestFocus();
                return;
            }

            // push to realtime database
            Class newClass = new Class(name, user, classType, selectedDifficultyLevel, selectedDay, selectedTimeSlot, capacity);
            referenceClasses.push().setValue(newClass).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(InstructorTeachClass.this, "Class created", Toast.LENGTH_SHORT).show();
                }
            });
            // reset page (without refresh)
            editTextSetName.setText("");
            editTextSetCapacity.setText("");
            classTypesList.clear();
            classTypesList.add(0, "Select class type");
            classTypesMap.clear();
            classesList.clear();
            pullClassTypeData();
            pullClassesData();
            initializeAllSpinnerDropdowns();
        } else {
            Toast.makeText(InstructorTeachClass.this, "Select a class type", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
