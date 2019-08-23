package de.hsduesseldorf.krueger.webprostudentorganizer.Helpers;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import de.hsduesseldorf.krueger.webprostudentorganizer.View.MainActivity;

public class DBManager extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "StudentOrganizer.db";

    // Table Names
    private static final String TABLE_STUDENTS = "Students";
    private static final String TABLE_TASKS = "Tasks";
    private static final String TABLE_STUDENT_TASK = "student_task";

    // Common column names
    private static final String KEY_ID = "id";

    // Table Student - column names
    private static final String KEY_NAME = "name";
    private static final String KEY_GROUPNAME = "groupName";
    private static final String KEY_FAILED = "failed";

    // Tasks Table - column names
    private static final String KEY_NUMBER = "number";
    private static final String KEY_PASSED = "passed";
    private static final String KEY_PRESENT = "present";
    private static final String KEY_COMMENT = "comment";

    // Student_tasks Table - column names
    private static final String KEY_STUDENTID = "studentId";
    private static final String KEY_TASKID = "taskId";

    //Create statement - Student
    private static final String CREATE_TABLE_STUDENT = "create table " + TABLE_STUDENTS +"(" +
            KEY_ID + " integer primary key," +
            KEY_NAME + " TEXT," +
            KEY_GROUPNAME + " TEXT," +
            KEY_FAILED + " integer)";

    //Create statement - Task
    private static final String CREATE_TABLE_TASK = "create table " + TABLE_TASKS + "(" +
            KEY_ID + " integer primary key," +
            KEY_NUMBER + " integer," +
            KEY_PRESENT + " integer," +
            KEY_PASSED + " integer," +
            KEY_COMMENT + " TEXT)";

    private static final String CREATE_TABLE_STUDENT_TASK = "create table " + TABLE_STUDENT_TASK + "(" +
            KEY_ID + " integer primary key," +
            KEY_STUDENTID + " integer, " +
            KEY_TASKID + " integer)";


    public DBManager(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_STUDENT);
        db.execSQL(CREATE_TABLE_TASK);
        db.execSQL(CREATE_TABLE_STUDENT_TASK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists " + TABLE_STUDENTS);
        db.execSQL("drop table if exists " + TABLE_TASKS);
        db.execSQL("drop table if exists " + TABLE_STUDENT_TASK);

        //Create tables anew
        onCreate(db);
    }

    /**
     * Run when first adding a Student
     * @param student The student's information
     * @return
     */
    public void addStudentToDatabase(Student student){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues studentValues = new ContentValues();
        studentValues.put(KEY_NAME, student.getNameAsOne());
        studentValues.put(KEY_GROUPNAME, student.getGroupName());

        //insert student into database
        long student_id = db.insert(TABLE_STUDENTS, null, studentValues);

        //create entry into task-table for every task the student has
        for(int i = 1; i <= student.getTasks().size(); i++){
            ContentValues taskValues = new ContentValues();
            taskValues.put(KEY_NUMBER, i);
            taskValues.put(KEY_COMMENT, "");
            taskValues.put(KEY_PASSED, "0");
            taskValues.put(KEY_PRESENT, "0");

            long task_id = db.insert(TABLE_TASKS,null, taskValues);

            //insert entry into student_tasks-table for every task
            ContentValues combinedContent = new ContentValues();
            combinedContent.put(KEY_STUDENTID, student_id);
            combinedContent.put(KEY_TASKID, task_id);

            db.insert(TABLE_STUDENT_TASK, null, combinedContent);
        }

        db.close();
    }

    /**
     * Get a student from the database
     * @param studentId The student's id
     * @return Student
     */
    public Student getStudent(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "select * from Students where " + KEY_ID + " = " + studentId;

        Student student = new Student();
        try (Cursor c = db.rawQuery(query, null)) {

            if (c != null) c.moveToFirst();

            String nameFromBase = c.getString(c.getColumnIndex(KEY_NAME));
            List<String> nameParts = Arrays.asList(nameFromBase.split(" "));
            student.setName(nameParts);
            student.setGroup(c.getString(c.getColumnIndex(KEY_GROUPNAME)));

            student.setTasks(getStudentTaskIDs(student));
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        db.close();

        return student;
    }

    public List<Student> getAllStudents(){
        List<Student> students = new ArrayList<>();
        String selectQuery = "select * from " + TABLE_STUDENTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Student student = new Student();
                student.setId(c.getInt((c.getColumnIndex(KEY_ID))));

                String nameFromBase = c.getString(c.getColumnIndex(KEY_NAME));
                List<String> nameParts = Arrays.asList(nameFromBase.split(" "));
                student.setName(nameParts);

                student.setGroup(c.getString(c.getColumnIndex(KEY_GROUPNAME)));

//                student.setTasks(getStudentTaskIDs(student));

                // adding to student list
                students.add(student);
            } while (c.moveToNext());
        }

        c.close();

        db.close();

        return students;
    }

    public List<Student> getStudentGroup(String groupName, boolean showFailed){
        SQLiteDatabase db = this.getReadableDatabase();

        String query = String.format("select * from %s where %s = \"%s\"", TABLE_STUDENTS, KEY_GROUPNAME, groupName);

        ArrayList<Student> list = new ArrayList<>();
        try (Cursor c = db.rawQuery(query, null)) {
            if (c.moveToFirst()) {
                do {
                    boolean studentHasFailed = (c.getInt(c.getColumnIndex(KEY_FAILED)) == 1);

                    //If that student has failed and failed students shouldn't be shown, continue with the next one
                    if(studentHasFailed && !showFailed)
                        continue;

                    //Student either has not failed, or failed student should be shown

                    Student student = new Student();

                    //Set the student's name
                    String nameFromBase = c.getString(c.getColumnIndex(KEY_NAME));
                    List<String> nameParts = Arrays.asList(nameFromBase.split(" "));
                    student.setName(nameParts);

                    //Set the student's group
                    student.setGroup(c.getString(c.getColumnIndex(KEY_GROUPNAME)));

                    //Set the student's id
                    student.setId(c.getInt(c.getColumnIndex(KEY_ID)));

                    //Set the student's tasks
                    student.setTasks(getStudentTaskIDs(student));

                    list.add(student);
                }
                while (c.moveToNext());
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }

        db.close();

        return list;
    }

    public int getGroupSize(String groupName){
        SQLiteDatabase db = this.getReadableDatabase();

        String query = String.format("select %s from %s where %s = '%s'", KEY_ID, TABLE_STUDENTS, KEY_GROUPNAME, groupName);

        int amountOfStudents = 0;

        try (Cursor c = db.rawQuery(query, null)) {
            if(c.moveToFirst()){
                amountOfStudents = c.getCount();
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }

        return amountOfStudents;
    }

    /**
     * Retrieve all tasks of this student
     * @param student The student from which to get the tasks
     * @return List of all tasks of the student
     */
    public List<Termin> getStudentTaskIDs(Student student){
        SQLiteDatabase db = this.getReadableDatabase();

        //Get all taskIDs in the mixed list assigned to this student
        String query = String.format("select %s from %s where %s = %s", KEY_TASKID, TABLE_STUDENT_TASK, KEY_STUDENTID, student.getId());
        try (Cursor cStudentTask = db.rawQuery(query, null)) {

            List<Termin> taskList = new ArrayList<>();

            if (cStudentTask.moveToFirst()) {
                do {
                    //Retrieve the next task's id from the cursor
                    int currentTaskId = cStudentTask.getInt(0);
                    query = String.format(Locale.GERMAN, "select * from %s where %s = %d", TABLE_TASKS, KEY_ID, currentTaskId);
                    try (Cursor cTask = db.rawQuery(query, null)){
                        if (cTask != null) {
                            cTask.moveToFirst();

                            Termin termin = new Termin();
                            termin.setId(currentTaskId);
                            taskList.add(termin);
                        }
                    }
                    catch (SQLException e){
                        System.err.println(e.getMessage());
                    }
                }
                while (cStudentTask.moveToNext());
            }
            db.close();

            return taskList;
        }
    }

    public Termin getStudentTask(int taskId){
        SQLiteDatabase db = this.getReadableDatabase();

        Termin task = new Termin();
        task.setId(taskId);

        //Get all taskIDs in the mixed list assigned to this student
        String query = String.format("select * from %s where %s = %s", TABLE_TASKS, KEY_ID, taskId);
        try (Cursor c = db.rawQuery(query, null)) {
            if(c != null) c.moveToFirst();

            task.setPresent(c.getInt(c.getColumnIndex(KEY_PRESENT)) == 1);
            task.setPassed(c.getInt(c.getColumnIndex(KEY_PASSED)) == 1);
            task.setComment(c.getString(c.getColumnIndex(KEY_COMMENT)));

            return task;
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public void updateTask(Termin task){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PRESENT, task.getPresent() ? 1 : 0);
        values.put(KEY_PASSED, task.getPassed() ? 1 : 0);
        values.put(KEY_COMMENT, task.getComment());

        db.update(TABLE_TASKS, values, String.format("%s = ?", KEY_ID),
                new String[]{String.valueOf(task.getId())});

        db.close();
    }

    /**
     * Update a student's information
     * @param student Transfer object with the student's new information
     * @return id
     */
    public void updateStudent(Student student){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, student.getNameAsOne());
        values.put(KEY_GROUPNAME, student.getGroupName());
        values.put(KEY_FAILED, student.getFailed());

        db.update(TABLE_STUDENTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(student.getId()) });

        db.close();
    }

    /**
     * Reinitialize all tables
     */
    public void resetAll(){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("drop table if exists " + TABLE_STUDENTS);
        db.execSQL("drop table if exists " + TABLE_TASKS);
        db.execSQL("drop table if exists " + TABLE_STUDENT_TASK);

        onCreate(db);
    }
}
