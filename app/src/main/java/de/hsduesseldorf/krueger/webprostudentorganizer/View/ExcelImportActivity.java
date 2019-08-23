package de.hsduesseldorf.krueger.webprostudentorganizer.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.hsduesseldorf.krueger.webprostudentorganizer.Helpers.Student;
import de.hsduesseldorf.krueger.webprostudentorganizer.Helpers.Termin;
import de.hsduesseldorf.krueger.webprostudentorganizer.R;

public class ExcelImportActivity extends AppCompatActivity {

    private String[] filePathStrings;
    private String[] fileNameStrings;
    private File[] listFile;
    File file;

    Button btn_upDirectory, btn_SDCard;

    ArrayList<String> pathHistory;
    String lastDirectory;
    int count = 0;

    ArrayList<Student> data;

    ListView viewInternalStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excel_import);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewInternalStorage = findViewById(R.id.lv_internalStorage);
        btn_upDirectory = findViewById(R.id.btn_upDirectory);
        data = new ArrayList<>();

        checkFilePermissions();

        viewInternalStorage.setOnItemClickListener(((adapterView, view, i, l) -> {
            lastDirectory = pathHistory.get(count);
            if(lastDirectory.equals(adapterView.getItemAtPosition(i))){
                String groupName = getIntent().getStringExtra("groupName");
                if(groupName != null)
                    readExcelData(lastDirectory, groupName);
                else
                    readExcelData(lastDirectory);
            }
            else {
                count++;
                pathHistory.add(count, (String)adapterView.getItemAtPosition(i));
                checkInternalStorage();
            }
        }));

        btn_upDirectory.setOnClickListener(view -> {
            if(count != 0){
                pathHistory.remove(count);
                count--;
                checkInternalStorage();
            }
        });

        count = 0;
        pathHistory = new ArrayList<>();
        pathHistory.add(count, System.getenv("EXTERNAL_STORAGE"));
        checkInternalStorage();
    }

    private void checkInternalStorage() {
        try{
            if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                MainActivity.toastMessage(this,"Keine SD Karte gefunden");
            }
            else {
                file = new File(pathHistory.get(count));
            }

            listFile = file.listFiles();

            filePathStrings = new String[listFile.length];
            fileNameStrings = new String[listFile.length];

            for(int i = 0; i < listFile.length; i++){
                filePathStrings[i] = listFile[i].getAbsolutePath();
                fileNameStrings[i] = listFile[i].getName();
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filePathStrings);
            viewInternalStorage.setAdapter(adapter);
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void readExcelData(String filePath, String groupName) {
        File inputFile = new File(filePath);

        try{
            InputStream stream = new FileInputStream(inputFile);

            XSSFWorkbook workbook = new XSSFWorkbook(stream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            int rowsCount = sheet.getPhysicalNumberOfRows();

            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

            for(int r = 1; r < rowsCount; r++){

                Row row = sheet.getRow(r);
                int cellCount = row.getPhysicalNumberOfCells();

                ArrayList<String> nameParts = new ArrayList<>();

                for(int c = 0; c < cellCount; c++){
                    //Name is only in the first and second cell
                    if(c > 2) break;
                    nameParts.add(getCellAsString(row, c, formulaEvaluator));
                }

                Student student = new Student(nameParts, groupName);
                MainActivity.databaseManager.addStudentToDatabase(student);
            }

            startActivity(new Intent(this, MainActivity.class));
            MainActivity.toastMessage(this, "Fertig");
        }
        catch (FileNotFoundException fnf){
            fnf.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void readExcelData(String filePath) {
        File inputFile = new File(filePath);

        try {
            InputStream stream = new FileInputStream(inputFile);

            XSSFWorkbook workbook = new XSSFWorkbook(stream);

            String importGroup = getIntent().getStringExtra("groupName");
            System.out.println("----------------------------| Group: " + importGroup);

            if(importGroup != null){
                int sheetIndex = importGroup.charAt(importGroup.toCharArray().length-1) - 65;
                System.out.println("----------------------------| Index: " + sheetIndex);

                XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
                int rowsCount = sheet.getPhysicalNumberOfRows();

                FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

                for (int r = 0; r < rowsCount; r++) {

                    Row row = sheet.getRow(r);
                    int cellCount = row.getPhysicalNumberOfCells();

                    ArrayList<String> nameParts = new ArrayList<>();

                    for (int c = 0; c < cellCount; c++) {
                        //Name is only in the first and second cell
                        if (c > 1) break;
                        nameParts.add(getCellAsString(row, c, formulaEvaluator));
                    }

                    //0 + 65 = A as char
                    String groupName = "Gruppe " + (char) (sheetIndex + 65);
                    Student student = new Student(nameParts, groupName);
                    MainActivity.databaseManager.addStudentToDatabase(student);
                }
            }
            else {
                for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                    XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
                    int rowsCount = sheet.getPhysicalNumberOfRows();

                    FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

                    for (int r = 0; r < rowsCount; r++) {

                        Row row = sheet.getRow(r);
                        int cellCount = row.getPhysicalNumberOfCells();

                        ArrayList<String> nameParts = new ArrayList<>();

                        for (int c = 0; c < cellCount; c++) {
                            //Name is only in the first and second cell
                            if (c > 2) break;
                            nameParts.add(getCellAsString(row, c, formulaEvaluator));
                        }

                        //0 + 65 = A as char
                        String groupName = "Gruppe " + (char) (sheetIndex + 65);
                        Student student = new Student(nameParts, groupName);
                        MainActivity.databaseManager.addStudentToDatabase(student);
                    }
                }
            }

            startActivity(new Intent(this, MainActivity.class));
            MainActivity.toastMessage(this, "Fertig");
        }
        catch (FileNotFoundException fnf){
            fnf.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void saveStudentsToExcelFile(List<String> groupNames){

        //Request permission if needed again
        if (ActivityCompat.checkSelfPermission(MainActivity.activityList.get(0), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.activityList.get(0),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        Workbook workbook = new XSSFWorkbook();

        //Loop through every group
        for(String groupName : groupNames){
            List<Student> studentList = MainActivity.databaseManager.getStudentGroup(groupName, true);

            try {
                //Create a new Sheet for every group
                Sheet sheet = workbook.createSheet(groupName);

                //Set up the first row with descriptions
                Row row = sheet.createRow(0);
                row.createCell(0).setCellValue("Name");
                row.createCell(1).setCellValue("Nachname");

                for(int i = 1; i < MainActivity.amountOfTasks + 1; i++){
                    row.createCell(i+1).setCellValue("Aufgabe " + i);
                }

                //Loop through all students in the group
                for(int studentIndex = 1;  studentIndex <= studentList.size(); studentIndex++) {
                    //Create a new row for that student
                    row = sheet.createRow(studentIndex);

                    Student student = studentList.get(studentIndex-1);

                    //Add the student's name into the first two columns
                    row.createCell(0).setCellValue(student.getFirstName());
                    row.createCell(1).setCellValue(student.getLastName());

                    List<Termin> studentTasks = MainActivity.databaseManager.getStudentTaskIDs(student);
                    //Go over every task and add a cell-entry. Write "Bestanden" or "Nicht bestanden" respectively
                    for (int i = 0; i < studentTasks.size(); i++) {
                        Termin task = MainActivity.databaseManager.getStudentTask(studentTasks.get(i).getId());

                        String cellText = (task.getPassed()) ? "Bestanden" : "Nicht bestanden";
                        row.createCell(i + 2).setCellValue(cellText);
                    }
                }

                for (int i = 0; i < MainActivity.amountOfTasks + 2; i++) {
                    sheet.setColumnWidth(i, 4200);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        String fileName = "studentList.xlsx";
        String extStorageDirectory = Environment.getExternalStorageState();
        File folder = new File(extStorageDirectory, "StudentOrganizer");
        if(!folder.exists())
            IGNORE_RESULT(folder.mkdir());

        try {
            File file = new File(Environment.getExternalStorageDirectory(), fileName);
            IGNORE_RESULT(file.createNewFile());

            FileOutputStream fileOut = new FileOutputStream(file);

            //Write data to the file
            workbook.write(fileOut);

            fileOut.flush();
            //Give the user feedback that everything was successful
            MainActivity.toastMessage(MainActivity.activityList.get(0), String.format("Gepeichert unter %s/%s", "interner Speicher", fileName));
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator) {
        String value = "";

        try {
            Cell cell = row.getCell(c);
            CellValue cellValue = formulaEvaluator.evaluate(cell);

            switch (cellValue.getCellType()){
                case Cell.CELL_TYPE_STRING:
                    value = cellValue.getStringValue();
                    break;
            }
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

        return value;
    }

    public void checkFilePermissions(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Mainifest.permission.READ_EXTERNAL_STORAGE");
            permissionCheck += this.checkSelfPermission("Mainifest.permission.WRITE_EXTERNAL_STORAGE");

            if(permissionCheck != 0){
                this.requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
            }
        }
    }

    @SuppressWarnings("unused")
    private static void IGNORE_RESULT(boolean b){}
}
