package de.hsduesseldorf.krueger.webprostudentorganizer.View;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import de.hsduesseldorf.krueger.webprostudentorganizer.R;
import de.hsduesseldorf.krueger.webprostudentorganizer.Helpers.Student;

public class GroupViewActivity extends AppCompatActivity {

    static List<ArrayList<Student>> studentLists = new ArrayList<>();

    private String group;

    @Override
    public void onStart(){
        super.onStart();

        MainActivity.activityList.add(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Set-up a list of Students for every group
        for(int i = 0; i < 4; i++)
            studentLists.add(new ArrayList<>());

        final TextView txt_groupName = findViewById(R.id.txt_groupName);

        group = getIntent().getStringExtra("groupName");
        txt_groupName.setText(group);

//        FloatingActionButton fabBack = findViewById(R.id.fabBack);
        FloatingActionButton fabAdd = findViewById(R.id.fabAddStudent);
        fabAdd.setOnClickListener(view ->  {
                Intent intent = new Intent(this, AddStudentActivity.class);
                intent.putExtra("groupName", group);
                startActivity(intent);
        });

        CheckBox chck_showFailedStatus = findViewById(R.id.chck_showFailed);
        chck_showFailedStatus.setOnCheckedChangeListener((button, checked) ->
                setUpStudents(getListIndex(group), checked)
        );

        if(MainActivity.databaseManager.getGroupSize(group) == 0){
            //Create a button to read from excel file
            Button btn = new Button(this);
            btn.setText("Noch keine Studenten in dieser Gruppe. Aus Excel-Datei importieren?");

            btn.setOnClickListener(view -> {
                Intent intent = new Intent(this, TaskAmountSetterActivity.class);
//                intent.putExtra("groupName", group);
                startActivity(intent);
            });

            LinearLayout view = findViewById(R.id.layout_students);
            view.addView(btn, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200));
        }
        else {
            setUpStudents(getListIndex(group), false);
        }
    }

    private int getListIndex(String groupName){

        //groupName is set up like this: Gruppe [A/B/C/D]
        //Cut after the space to get the group's letter
        String[] nameParts = groupName.split(" ");

        //Stash the letter's character-representation
        int number = nameParts[1].charAt(0);

        //A = 65, B = 66, etc
        // -> number - 65 = 0, 1, 2, ...
        return (number - 65);
    }

    /**
     * Adds a clickable button for every student in the group
     * @param listIndex The index which group to access in the list
     */
    private void setUpStudents(int listIndex, boolean showFailed){

        //Get all students in this group
        String groupName = getIntent().getStringExtra("groupName");
        studentLists.get(listIndex).clear();

        studentLists.get(listIndex).addAll(MainActivity.databaseManager.getStudentGroup(groupName, showFailed));

        LinearLayout linearLayout = findViewById(R.id.layout_students);

        //Remove every child from the linearLayout
        linearLayout.removeAllViews();

        for(Student s : studentLists.get(listIndex)){
            Button btn = new Button(linearLayout.getContext());
            btn.setText(s.getNameAsOne());
            btn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200));
            btn.setAllCaps(false);
            btn.setOnClickListener(event -> {
                Intent intent = new Intent(this, TaskViewActivity.class);
                intent.putExtra("studentName", s.getNameAsOne());
                intent.putExtra("groupName", groupName);
                startActivity(intent);
            });

            linearLayout.addView(btn);
        }
    }

    public static Student getStudentFromName(String name){
        for(ArrayList<Student> list : studentLists){
            for(Student student : list){
                if(student.getNameAsOne().equals(name))
                    return student;
            }
        }
        return null;
    }
}
