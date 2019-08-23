package de.hsduesseldorf.krueger.webprostudentorganizer.View;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import de.hsduesseldorf.krueger.webprostudentorganizer.R;
import de.hsduesseldorf.krueger.webprostudentorganizer.View.ExcelImportActivity;
import de.hsduesseldorf.krueger.webprostudentorganizer.View.MainActivity;

public class TaskAmountSetterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_amount_setter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void SetTaskAmount(View view){
        //Get the text inside the input-field
        String text = ((EditText)findViewById(R.id.txt_taskAmount)).getText().toString();

        try {
            int taskAmount = Integer.parseInt(text);
            MainActivity.amountOfTasks = taskAmount;

            //Redirect to page to read excel-file
            Intent intent = new Intent(this, ExcelImportActivity.class);

            if(getIntent().getStringExtra("groupName") != null)
                intent.putExtra("groupName", getIntent().getStringExtra("groupName"));

            startActivity(intent);
        }
        catch (Exception e){
            e.printStackTrace();
            MainActivity.toastMessage(this, "Bitte eine gültige Zahl eingeben.");
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();

        return true;
    }
}
