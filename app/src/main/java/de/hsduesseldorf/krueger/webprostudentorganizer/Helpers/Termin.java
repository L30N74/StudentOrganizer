package de.hsduesseldorf.krueger.webprostudentorganizer.Helpers;

public class Termin {
    private int _id;
    private boolean passed = false;
    private boolean present = false;
    private String comment;

    public void setId(int value){ this._id = value; }
    public int getId(){ return this._id; }

    public void setPassed(boolean value) { this.passed = value; }
    public boolean getPassed() { return this.passed; }

    public void setPresent(boolean value) { this.present = value; }
    public boolean getPresent() { return this.present; }

    public void setComment(String value) { this.comment = value; }
    public String getComment() { return this.comment; }
}
