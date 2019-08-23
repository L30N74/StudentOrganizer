package de.hsduesseldorf.krueger.webprostudentorganizer.View;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import de.hsduesseldorf.krueger.webprostudentorganizer.Helpers.Student;
import de.hsduesseldorf.krueger.webprostudentorganizer.Helpers.Termin;
import de.hsduesseldorf.krueger.webprostudentorganizer.R;

import com.github.clans.fab.FloatingActionButton;

public class TaskViewActivity extends AppCompatActivity {

    @Override
    public void onStart(){
        super.onStart();

        MainActivity.activityList.add(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Set Text on top to the student's name
        String studentName = getIntent().getStringExtra("studentName");
        ((TextView)findViewById(R.id.txt_studentName)).setText(studentName);

        FloatingActionButton btn = findViewById(R.id.fabOptions_SwitchGroup);
        btn.setOnClickListener(view -> {
            //Lead to different view where all groups are listed
            Intent intent = new Intent(this, ChangeGroupActivity.class);
            intent.putExtra("studentName", studentName);
            startActivity(intent);
        });

        btn = findViewById(R.id.fabOptions_Save);
        btn.setOnClickListener(view ->  saveTasks(studentName) );

        btn = findViewById(R.id.fabOptions_FailStudent);
        btn.setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setTitle("Student durchfallen lassen")
                    .setMessage(String.format("Willst du %s wirklich durchfallen lassen?", studentName))

                    .setPositiveButton("Ja", (DialogInterface, i) -> {
                        Student student = GroupViewActivity.getStudentFromName(studentName);
                        student.setFailed(true);
                        saveTasks(studentName);
                        MainActivity.databaseManager.updateStudent(student);
                        finish();
                    })

                    .setNegativeButton("Nein", null)
                    .show();
        });

        setupStudentTasks(studentName);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        //In case the user didn't save, save for him
        saveTasks(getIntent().getStringExtra("studentName"));

//        Intent i = new Intent(this, GroupViewActivity.class);
//        i.putExtra("groupName", getIntent().getStringExtra("groupName"));
//        startActivity(i);
        finish();

        return true;
    }

    private void setupStudentTasks(String studentName) {
        Student student = GroupViewActivity.getStudentFromName(studentName);

        TableLayout tableLayout = findViewById(R.id.layout_tasks);

        setTableHeading(tableLayout);
        setTableContent(student, tableLayout);
    }

    private void setTableHeading(TableLayout tableLayout){
        TableRow row = new TableRow(tableLayout.getContext());
        TextView headTxt_taskNumber = new TextView(row.getContext());
        headTxt_taskNumber.setText("#");
        row.addView(headTxt_taskNumber);

        TextView headTxt_comment = new TextView(row.getContext());
        headTxt_comment.setText(R.string.task_comment);
        row.addView(headTxt_comment);

        TextView headTxt_isPresent = new TextView(row.getContext());
        headTxt_isPresent.setText(R.string.task_present);
        row.addView(headTxt_isPresent);

        TextView headTxt_hasPassed = new TextView(row.getContext());
        headTxt_hasPassed.setText(R.string.task_passed);
        row.addView(headTxt_hasPassed);

        tableLayout.addView(row);
    }

    private void setTableContent(Student student, TableLayout tableLayout){
        List<Termin> studentTasks = MainActivity.databaseManager.getStudentTaskIDs(student);

        for(int i = 0; i < studentTasks.size(); i++){
            Termin task = MainActivity.databaseManager.getStudentTask(studentTasks.get(i).getId());

            try {
                TableRow row = new TableRow(tableLayout.getContext());

                TextView numberText = new TextView(row.getContext());
                numberText.setText((i + 1) + ".");
                row.addView(numberText);

                EditText txtComment = new EditText(row.getContext());
                txtComment.setText(task.getComment());
                txtComment.setWidth(800);
                txtComment.setMaxWidth(800);
                row.addView(txtComment);

                CheckBox checkBoxIsPresent = new CheckBox(row.getContext());
                checkBoxIsPresent.setChecked(task.getPresent());
                row.addView(checkBoxIsPresent);

                CheckBox checkBoxPassed = new CheckBox(row.getContext());
                checkBoxPassed.setChecked(task.getPassed());
                row.addView(checkBoxPassed);

                tableLayout.addView(row);
            }
            catch (Exception e){
                System.out.println("--------------| Error::");
                e.printStackTrace();
            }
        }
    }

    /**
     * OnClick-Method for saving updated tasks to the database
     * @param studentName Name of the student whom's tasks should get updated
     */
    private void saveTasks(String studentName){
        Student student = GroupViewActivity.getStudentFromName(studentName);

        TableLayout layout = findViewById(R.id.layout_tasks);

        List<Termin> taskList = MainActivity.databaseManager.getStudentTaskIDs(student);

        //First row is the heading, so start at index 1
        for(int i = 1; i < layout.getChildCount(); i++){
            //Get the row
            TableRow row = (TableRow)layout.getChildAt(i);

            //Get the inputs
            EditText txtComment = (EditText)row.getChildAt(1);
            CheckBox chckPresent = (CheckBox)row.getChildAt(2);
            CheckBox chckPassed = (CheckBox)row.getChildAt(3);

            Termin termin = taskList.get(i-1);

            termin.setComment(txtComment.getText().toString());
            termin.setPassed(chckPassed.isChecked());
            termin.setPresent(chckPresent.isChecked());

            MainActivity.databaseManager.updateTask(termin);
        }

        //Inform the user that everything is saved
        MainActivity.toastMessage(this, "Gespeichert");
    }
}
