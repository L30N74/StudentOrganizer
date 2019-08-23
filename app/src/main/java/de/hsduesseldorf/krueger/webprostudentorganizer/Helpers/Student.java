package de.hsduesseldorf.krueger.webprostudentorganizer.Helpers;

import java.util.ArrayList;
import java.util.List;

import de.hsduesseldorf.krueger.webprostudentorganizer.View.MainActivity;

public class Student {
    private int _id;
    private ArrayList<Termin> tasks = new ArrayList<>();
    private ArrayList<String> name = new ArrayList<>();
    private String group;

    private boolean hasFailed = false;

    public Student(){
        //Set up tasks
        for(int i = 1; i <= MainActivity.amountOfTasks; i++)
            this.tasks.add(new Termin());
    }

    public Student(String name){
        this.name.add(name);

        //Set up tasks
        for(int i = 1; i <= MainActivity.amountOfTasks; i++)
            this.tasks.add(new Termin());
    }

    public Student(ArrayList<String> name, String groupName){

        //Set up tasks
        for(int i = 1; i <= MainActivity.amountOfTasks; i++)
            this.tasks.add(new Termin());

        this.name.addAll(name);
        this.group = groupName;
    }

    public void setId(int value){ this._id = value; }
    public int getId(){ return this._id; }

    public void setGroup(String value){ this.group = value; }

    public ArrayList<Termin> getTasks() { return this.tasks; }

    public void setTasks(List<Termin> tasks){
        this.tasks.clear();
        this.tasks.addAll(tasks);
    }

    public void setName(List<String> nameParts){
        this.name.addAll(nameParts);
    }

    public String getFirstName() { return this.name.get(0); }
    public String getLastName() {
        StringBuilder builder = new StringBuilder();
        for(int i = 1; i < this.name.size(); i++){
            builder.append(this.name.get(i) + " ");
        }

        return builder.toString();
    }
    public String getNameAsOne() {
        StringBuilder sb = new StringBuilder();

        for(String s : this.name){
            sb.append(s);
            sb.append(" ");
        }

        return sb.toString();
    }
    public String getGroupName() { return this.group; }

    public boolean getFailed() { return this.hasFailed; }
    public void setFailed(boolean value) { this.hasFailed = value; }
}
