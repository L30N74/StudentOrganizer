# Student Organizer

## How the project came to be

In my third Semester in University I took a job there as a student-worker where I helped my professor take tasks off students in the course Web-Programming.
In there the students were given a weekly task which I, along with my professor and another student-worker to take tose tasks from the them and give them advice on what to do better and stuff like that.

The students' progression through the semester was denoted on several pieces of paper, like an excel-sheet with their names on them and whether or not they succeeded in the weekly tasks.

In my fifth semester I took it upon me to do the same job again, because it was fun, the atmosphere was relaxing and the payment was good.
Since I didn't want to go through it again with writing everything down on paper though, because it had several disadvantages like the option to take notes on individual students just wasn't feasible, I decided to modernize it a bit and hence created this App.

## How to use

### The main page

<img src="https://user-images.githubusercontent.com/51761671/63627083-3acb0d80-c606-11e9-9861-2ac659af2b45.jpg" height="600">
Here you can see four buttons each leading to a page for a specific group.

---

If you click the button on the bottom-right 

<img src="https://user-images.githubusercontent.com/51761671/63627085-3acb0d80-c606-11e9-9a28-1fe9098ccd6a.jpg" height="600"> 
A menu pops up where you can either

- **Read students from an excel-sheet**; this will lead you to page where you set the number of tasks the students have this semester and then to a page where you can select an .xlsx-file with students split up for each group onto different sheets. 
- **Export students to an excel-file**; All student will be written to a new .xlsx-file. One group per sheet. Each student will have the status of their individual tasks written as either "Bestanden" if they passed, or "Nicht bestanden" when they failed a task, or it hasn't been completed yet, when printing them at an earlier stage.
- **Remove all database-entries**; This will remove all students and their tasks from the database, resetting everything. A pop-up demands confirmation to avoid accidents.



### The group view

<img src="https://user-images.githubusercontent.com/51761671/63627080-3a327700-c606-11e9-9722-0c300b8a6df7.jpg" height="600">

On this page every student in the chosen group will be displayed. By checking the box, failed students will also be shown.
By pressing the button on the bottom-right, a new student can be added to this group if need be.
By clicking on any name, you will get redirected to a page where all the student's tasks are shown.

<img src="https://user-images.githubusercontent.com/51761671/63627081-3acb0d80-c606-11e9-9384-3bae2e559831.jpg" height="600">
Here you can assign a comment to each task and tick off whether or not students were present and whether or not they passed the specific task.
By pressing on the button on the bottom-right, another menu will appear

<img src="https://user-images.githubusercontent.com/51761671/63627082-3acb0d80-c606-11e9-9226-545319b129ed.jpg" height="600">
Where you can either

- **Move the student to a different group**; this will open a different page where you can select the group you want to put him/her in
- **Save the changes you made to any input-field**; Changes will also be changed when you leave this page
- **Fail a student**; Failed students, without checking the box in the group-view, won't appear there.

## Tools for Delevolpment

- Language: Java
- IDE: Android Studio
- Database: SQLite

## Supported Platforms

- Android 4.0 and up
