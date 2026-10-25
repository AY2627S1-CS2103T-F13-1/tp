---
layout: page
title: User Guide
---

TutorReach is a **desktop app for independent private tutors who teach secondary-school students, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). It keeps your students' and their guardians' contact details in one place, grouped by the weekly lessons you teach. If you type quickly, TutorReach can help you manage them faster than traditional GUI applications.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/AY2627S1-CS2103T-F13-1/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for TutorReach.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar tutorReach.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `addstudent n/Ben Koh gn/Koh Siew Lan gp/98765432` : Adds a student named `Ben Koh` with a guardian.

   * `addlesson n/Sec 3 A-Math d/mon st/1600 et/1800` : Adds a weekly Monday lesson from 4:00 PM to 6:00 PM.

   * `liststudents` : Lists all students.

   * `deletestudent 3` : Deletes the 3rd student shown in the current list.

   * `clear` : Deletes all students and lessons.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `addstudent n/NAME`, replace `NAME` with a value such as `Aiden Tan`.

* Items in square brackets are optional.<br>
  For example, `listlessons [d/DAY]` can be used as `listlessons` or as `listlessons d/mon`.

* Items followed by `...` take one or more keywords separated by spaces.<br>
  For example, `n/NAME_KEYWORDS...` can be written as `n/aid` or as `n/aid chl`.

* Parameters can be in any order.<br>
  For example, if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for commands that take no parameters, such as `help`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Viewing help: `help`

Shows a quick reference of TutorReach's commands and a link to the full User Guide.

Format: `help`

### Adding a student: `addstudent`

Adds a student, with an optional guardian.

Format: `addstudent n/NAME [p/PHONE] [gn/GUARDIAN_NAME gp/GUARDIAN_PHONE]`

* At least one phone number is required: the student's own `p/`, or a guardian's `gp/`.
* `gn/` and `gp/` must be given together.
* Names may contain only English letters, digits and spaces, so accented letters and symbols such as `-`, `'` and `/` (as in `s/o`) are not accepted. Phone numbers may contain only digits and must be at least 3 digits long.
* A student is a duplicate if another student has the same name, ignoring letter case and extra spaces, and shares at least one phone number with them (own or guardian's). Siblings with different names can share a guardian's number.
* Adding a student clears the student filter, so all students are shown.

Examples:
* `addstudent n/Marcus Lim gn/Lim Hui Min gp/98761234` adds a student whose guardian is the only contact.
* `addstudent n/Chloe Lim p/98765432` adds a student with their own phone number and no guardian.

### Deleting a student: `deletestudent`

Deletes a student using their displayed index. The student is also removed from every lesson they are enrolled in. The lessons are kept, even if they become empty.

Format: `deletestudent INDEX`

* `INDEX` refers to the current displayed student list and must be a positive integer.
* The student filter stays active after the deletion.

Example:
* `findstudent n/aiden` followed by `deletestudent 1` deletes the first student in the search results.

### Listing students: `liststudents`

Shows all students, or only the students enrolled in a displayed lesson.

Format: `liststudents [l/LESSON_INDEX]`

* `LESSON_INDEX` refers to the index shown in the current lesson list and must be a positive integer.
* Running `liststudents` without `l/` clears the student filter.
* If the lesson used by the student filter is deleted, all remaining students are shown again.

Examples:

* `liststudents` shows all students.
* `liststudents l/2` shows students enrolled in the second displayed lesson.

### Editing a student: `editstudent`

Edits the student at the specified index in the displayed student list.

Format: `editstudent INDEX [n/NAME] [p/PHONE] [gn/GUARDIAN_NAME gp/GUARDIAN_PHONE]`

* `INDEX` must be a positive integer and refers to the index shown in the current student list.
* At least one optional field must be provided. Fields that are not provided remain unchanged.
* `gn/GUARDIAN_NAME` and `gp/GUARDIAN_PHONE` must be provided together. They replace the student's existing guardian details.
* Names must contain only English letters, digits and spaces, as for `addstudent`, and must not be blank. Phone numbers must contain only digits and be at least three digits long.
* The edited student must not duplicate another student.
* The student's identity and lesson enrolments are preserved.
* The active student filter remains in effect and is re-evaluated after the edit. For example, a renamed student may disappear from a name search that they no longer match.

Examples:

* `editstudent 1 p/92345678` changes only the phone number of the first displayed student.
* `editstudent 2 gn/Lim Mei gp/91234567` replaces only the guardian details of the second displayed student.
### Finding students: `findstudent`

Finds students by their name, their guardian's name, or part of a phone number.

Format: `findstudent [n/NAME_KEYWORDS...] [gn/GUARDIAN_NAME_KEYWORDS...] [p/PHONE_DIGITS...]`

* At least one of `n/`, `gn/` and `p/` must be given, and each can be given at most once.
* `n/` searches students' own names, and `gn/` searches their guardians' names.
* `p/` searches both the student's own phone number and their guardian's phone number. Its keywords must contain only digits.
* The search is case-insensitive, and a keyword can match any part of a name or number; for example, `aid` matches `Aiden` and `4567` matches `91234567`.
* Within one prefix, a student needs to match only one of the keywords; for example, `n/aid chl` finds both `Aiden Tan` and `Chloe Lim`.
* If more than one prefix is given, a student must match all of them.
* Each search replaces the current student filter.

Examples:
* `findstudent n/aid chl` shows students whose names contain `aid` or `chl`.
* `findstudent gn/tan` shows students whose guardian's name contains `tan`.
* `findstudent p/4567` shows students whose own or guardian's phone number contains `4567`, such as a guardian who just called from an unsaved number.
* `findstudent n/aiden p/9123` shows students whose names contain `aiden` and whose own or guardian's phone number contains `9123`.

### Listing lessons: `listlessons`

Shows all lessons, or only lessons that start on a specified day.

Format: `listlessons [d/DAY]`

* `DAY` must be `mon`, `tue`, `wed`, `thu`, `fri`, `sat` or `sun`, in any letter case.
* A lesson that runs past midnight belongs to the day on which it starts.
* Running `listlessons` without `d/` clears the lesson filter.

Examples:

* `listlessons` shows all lessons.
* `listlessons d/mon` shows lessons that start on Monday.

### Editing a lesson: `editlesson`

Edits the lesson at the specified index in the displayed lesson list.

Format: `editlesson INDEX [n/NAME] [d/DAY] [st/START_TIME] [et/END_TIME]`

* `INDEX` must be a positive integer and refers to the index shown in the current lesson list.
* At least one optional field must be provided. Fields that are not provided remain unchanged.
* `NAME` must be 1 to 50 characters long, start with a letter or digit, and contain only English letters, digits, spaces and `- ' . , & ( ) +`.
* `DAY` must be `mon`, `tue`, `wed`, `thu`, `fri`, `sat` or `sun`, in any letter case.
* Times must use four-digit 24-hour format from `0000` to `2359`. The start and end times cannot be equal. An end time earlier than the start time means that the lesson ends on the next day.
* The edited lesson must not duplicate another lesson.
* The lesson's identity and student enrolments are preserved. A student list filtered by this lesson remains valid.
* The active lesson filter remains in effect and is re-evaluated after the edit. For example, changing the day may remove the lesson from the displayed day filter.

Examples:

* `editlesson 1 d/tue st/1700 et/1900` changes only the day and times of the first displayed lesson.
* `editlesson 2 n/Sec 3 Mathematics` changes only the name of the second displayed lesson.


### Adding a lesson: `addlesson`

Adds a weekly lesson.

Format: `addlesson n/NAME d/DAY st/START_TIME et/END_TIME`

* `DAY` must be `mon`, `tue`, `wed`, `thu`, `fri`, `sat` or `sun`, in any letter case.
* `START_TIME` and `END_TIME` must use four digits in 24-hour time, such as `1600`.
* If the end time is earlier than the start time, the lesson is treated as ending on the next day.
* The start and end times must be different.
* `NAME` must be 1 to 50 characters long, start with a letter or digit, and contain only English letters, digits, spaces and `- ' . , & ( ) +`.
* A lesson is not added if another lesson has the same name, ignoring letter case and extra spaces, on the same day with the same start and end times.
* If the new lesson's time overlaps other lessons, it is still added, since you may teach overlapping groups on purpose, and the result warns which lessons it clashes with. Lessons hidden by `listlessons d/DAY` count too. A lesson that starts exactly when another ends does not clash with it.
* Adding a lesson clears the lesson filter, so all lessons are shown.
* Results mark a lesson that ends on the next day with `(next day)`, for example `Late Physics (Fri 2300-0100) (next day)`.

Examples:
* `addlesson n/Sec 3 A-Math d/mon st/1600 et/1800` adds a Monday lesson from 4:00 PM to 6:00 PM.
* `addlesson n/Late Physics d/fri st/2300 et/0100` adds a Friday lesson that ends on Saturday.
* `addlesson n/Sec 3 E-Math d/mon st/1700 et/1900`, after the first example, adds the lesson and warns that it clashes with `Sec 3 A-Math (Mon 1600-1800)`.

![The result of addlesson with a clash warning, and the two lessons in the lessons panel](images/AddLessonClashWarning.png)

### Enrolling a student: `enrol`

Enrols a student in a lesson using the indices shown in the student and lesson panels.

Format: `enrol s/STUDENT_INDEX l/LESSON_INDEX`

* Both indices must be positive integers.
* `STUDENT_INDEX` refers to the current displayed student list.
* `LESSON_INDEX` refers to the current displayed lesson list.
* A student cannot be enrolled in the same lesson more than once.

Example:
* `enrol s/1 l/2` enrols the first displayed student in the second displayed lesson.

### Unenrolling a student: `unenrol`

Removes a student from a lesson using the indices shown in the student and lesson panels. The student and lesson are both kept.

Format: `unenrol s/STUDENT_INDEX l/LESSON_INDEX`

* Both indices must be positive integers.
* `STUDENT_INDEX` refers to the current displayed student list.
* `LESSON_INDEX` refers to the current displayed lesson list.
* The student must be enrolled in the lesson.
* Both panel filters stay active. If the student list shows only that lesson's students, the unenrolled student disappears from it.

Example:
* `unenrol s/1 l/2` removes the first displayed student from the second displayed lesson.

### Deleting a lesson: `deletelesson`

Deletes a lesson using its displayed index. Students are kept, but their enrolment in the deleted lesson is removed.

Format: `deletelesson INDEX`

* `INDEX` refers to the current displayed lesson list and must be a positive integer.
* The result names the deleted lesson and lists the students who were unenrolled from it, if any.
* If the students panel is showing only the students of that lesson (`liststudents l/INDEX`), all students are shown again.

Examples:
* `deletelesson 2` deletes the second displayed lesson.
* `listlessons d/mon` followed by `deletelesson 1` deletes the first lesson on Monday.

### Clearing all entries: `clear`

Deletes all students and lessons.

Format: `clear`

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
`clear` deletes everything at once, with no confirmation, and cannot be undone. Any text after `clear` is ignored, so `clear 1` also deletes everything. To keep a copy, back up the data file first.
</div>

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

TutorReach automatically saves after commands that change data. Viewing, searching, help and exit commands do not rewrite the data file. You do not need to save manually.

If saving fails, the data change is undone and the previous display filters are restored. Fix the reported storage problem before retrying the command. Saves use atomic file replacement; a filesystem that does not support this will report a save error instead of overwriting the existing file unsafely.

### Editing the data file

TutorReach stores data in `data/addressbook.json` by default, relative to the folder from which you launch the app.
Only edit this file while the app is closed.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If individual records are invalid or duplicated, the app loads the valid records and skips the invalid ones.
Enrolments referencing missing students or lessons are also skipped. The first valid duplicate is kept.
A warning in the message box reports how many records were skipped and where the original file was backed up.
Backups are saved beside the data file as `addressbook.json.recovery-<unique number>.bak`; older backups are preserved.
These files remain on disk after closing the app and are not automatically deleted. Open a backup in a text editor
to review the original data, including skipped records. A recovery backup contains the original problems too;
repair those entries before using it as your data file.
The source file is unchanged until a successful command that changes records saves the recovered data.<br>
If the JSON cannot be read, its lists have an invalid structure, or a backup cannot be created, the app starts empty
with a warning and **disables saving** to protect the source file. Fix the file or restore a backup while the app
is closed, then restart. While saving is disabled, commands that change records are rejected before changing the data.
Listing, searching, help, and exit remain available.<br>
Only edit the file while the app is closed, and keep a backup before editing it.<br>
Keep student and lesson IDs unchanged when editing details, as enrolments use those IDs to link records.
</div>

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous TutorReach home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add student** | `addstudent n/NAME [p/PHONE] [gn/GUARDIAN_NAME gp/GUARDIAN_PHONE]`<br> e.g., `addstudent n/Marcus Lim gn/Lim Hui Min gp/98761234`
**Delete student** | `deletestudent INDEX`<br> e.g., `deletestudent 3`
**Add lesson** | `addlesson n/NAME d/DAY st/START_TIME et/END_TIME`<br> e.g., `addlesson n/Sec 3 A-Math d/mon st/1600 et/1800`
**Clear** | `clear`
**Delete lesson** | `deletelesson INDEX`<br> e.g., `deletelesson 2`
**Edit student** | `editstudent INDEX [n/NAME] [p/PHONE] [gn/GUARDIAN_NAME gp/GUARDIAN_PHONE]`<br> e.g., `editstudent 1 p/92345678`
**Edit lesson** | `editlesson INDEX [n/NAME] [d/DAY] [st/START_TIME] [et/END_TIME]`<br> e.g., `editlesson 1 d/tue st/1700 et/1900`
**Find** | `find KEYWORD [MORE_KEYWORDS]`<br> e.g., `find James Jake`
**Find students** | `findstudent [n/NAME_KEYWORDS...] [gn/GUARDIAN_NAME_KEYWORDS...] [p/PHONE_DIGITS...]`<br> e.g., `findstudent p/4567`
**List** | `list`
**List students** | `liststudents [l/LESSON_INDEX]`<br> e.g., `liststudents l/1`
**List lessons** | `listlessons [d/DAY]`<br> e.g., `listlessons d/mon`
**Enrol student** | `enrol s/STUDENT_INDEX l/LESSON_INDEX`<br> e.g., `enrol s/1 l/2`
**Unenrol student** | `unenrol s/STUDENT_INDEX l/LESSON_INDEX`<br> e.g., `unenrol s/1 l/2`
**Help** | `help`
