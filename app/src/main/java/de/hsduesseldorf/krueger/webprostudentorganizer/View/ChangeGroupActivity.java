package de.hsduesseldorf.krueger.webprostudentorganizer.View;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import de.hsduesseldorf.krueger.webprostudentorganizer.Helpers.Student;
import de.hsduesseldorf.krueger.webprostudentorganizer.R;

public class ChangeGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_group);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayout linearLayout = findViewById(R.id.layout_groups);

        //Retrieve all groups
        for(Button b : MainActivity.groupButtons){
            Button button = new Button(linearLayout.getContext());
            button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300));
            button.setText(b.getText());

            button.setOnClickListener(view -> {
                String studentName = getIntent().getStringExtra("studentName");
                Student student = GroupViewActivity.getStudentFromName(studentName);

                String newGroup = button.getText().toString();
                student.setGroup(newGroup);
                
                MainActivity.databaseManager.updateStudent(student);

                startActivity(new Intent(this, MainActivity.class));
            });

            linearLayout.addView(button);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();

        return true;
    }
}
