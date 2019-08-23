package de.hsduesseldorf.krueger.webprostudentorganizer.View;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.widget.EditText;

import java.util.ArrayList;

import de.hsduesseldorf.krueger.webprostudentorganizer.R;
import de.hsduesseldorf.krueger.webprostudentorganizer.Helpers.Student;

public class AddStudentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {

            EditText txt_firstName = findViewById(R.id.txt_firstName);
            EditText txt_lastName = findViewById(R.id.txt_lastName);

            ArrayList<String> name = new ArrayList<>();
            name.add(txt_firstName.getText().toString());
            name.add(txt_lastName.getText().toString());

            String groupName = getIntent().getStringExtra("groupName");

            Student student = new Student(name, groupName);

            //Add the student to the database
            MainActivity.databaseManager.addStudentToDatabase(student);

            //Open view
            Intent i = new Intent(AddStudentActivity.this, GroupViewActivity.class);
            i.putExtra("groupName", groupName);

            startActivity(i);
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();

        return true;
    }
}
