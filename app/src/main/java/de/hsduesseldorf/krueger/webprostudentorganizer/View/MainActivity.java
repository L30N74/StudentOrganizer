package de.hsduesseldorf.krueger.webprostudentorganizer.View;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.hsduesseldorf.krueger.webprostudentorganizer.Helpers.DBManager;
import de.hsduesseldorf.krueger.webprostudentorganizer.R;


public class MainActivity extends AppCompatActivity {

    public static List<Activity> activityList = new ArrayList<>();

    public static DBManager databaseManager;

    public static ArrayList<Button> groupButtons;

    public static int amountOfTasks = 12;

    @Override
    public void onStart(){
        super.onStart();

        activityList.add(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(databaseManager == null)
            databaseManager = new DBManager(this);

        this.groupButtons = new ArrayList<>();

        //TODO: implement way for manually added groups to be persistent
        groupButtons.add(findViewById(R.id.btn_groupA));
        groupButtons.add(findViewById(R.id.btn_groupB));
        groupButtons.add(findViewById(R.id.btn_groupC));
        groupButtons.add(findViewById(R.id.btn_groupD));

        for(Button button : groupButtons){
            final String buttonText = (String)button.getText();

            button.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, GroupViewActivity.class);
                intent.putExtra("groupName", buttonText);
                startActivity(intent);
            });
        }

//        ImageButton btn = findViewById(R.id.btn_addGroup);
//        btn.setOnClickListener(view -> {
//            LinearLayout linearLayout = findViewById(R.id.layout_groupNames);
//
//            Button button = new Button(linearLayout.getContext());
//            String[] split = ((String)groupButtons.get(groupButtons.size()-1).getText()).split(" ");
//            button.setText("Gruppe " + (char)(split[1].charAt(0) + 1));
//
//            button.setHeight(groupButtons.get(0).getHeight());
//
//            button.setOnClickListener(view2 -> {
//                Intent intent = new Intent(MainActivity.this, GroupViewActivity.class);
//                intent.putExtra("message", button.getText());
//                startActivity(intent);
//            });
//
//            linearLayout.addView(button);
//            groupButtons.add(button);
//
//            toastMessage(this, "Gruppe hinzugefügt");
//        });

        ImageButton btn = findViewById(R.id.btn_resetDatabase);
        btn.setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setTitle("Datenbank zurücksetzen")
                    .setMessage("Wollen Sie wirklich alle Einträge der Gruppen löschen?")
                    .setPositiveButton("Ja", (DialogInterface, i) ->{
                        databaseManager.resetAll();
                        toastMessage(this,"Gruppen zurückgesetzt");
                    })
                    .setNegativeButton("Nein", null)
                    .show();
        });

        btn = findViewById(R.id.btn_readExcelFile);
        btn.setOnClickListener(view -> {
            Intent intent = new Intent(this, TaskAmountSetterActivity.class);
            startActivity(intent);
        });

        btn = findViewById(R.id.btn_saveToExcelFile);
        btn.setOnClickListener(view -> {
            List<String> groupNames = new ArrayList<>();

            for(Button b : this.groupButtons){
                groupNames.add(b.getText().toString());
            }

            ExcelImportActivity.saveStudentsToExcelFile(groupNames);
        });
    }

    public static int getActivityIndex(String className){
        for(int i = 0; i < activityList.size(); i++){

            if(activityList.get(i).getLocalClassName().equals("View." + className)) {
                return i;
            }
        }

        return -1;
    }

    public static void toastMessage(Context ctx, String message){
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }
}
