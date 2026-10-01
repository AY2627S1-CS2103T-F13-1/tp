[![CI Status](https://github.com/AY2627S1-CS2103T-F13-1/tp/workflows/Java%20CI/badge.svg)](https://github.com/AY2627S1-CS2103T-F13-1/tp/actions)

![Ui](docs/images/Ui.png)

# TutorReach

**TutorReach** is a desktop app for **independent private tutors** who teach secondary-school students. It keeps your students' and their guardians' contact details in one place, grouped by the lessons you teach, so you can quickly find and contact the right family.

TutorReach is optimised for use via a **Command Line Interface (CLI)** while still having the benefits of a Graphical User Interface (GUI). If you can type fast, TutorReach gets your contact management done faster than traditional GUI apps.

## Features

* **Manage students**: add a student with their own phone number and/or one guardian's name and phone number, and delete students who have stopped lessons.
* **Manage lessons**: record weekly lesson slots (name, day, start and end time) and delete those that no longer run.
* **Enrol students in lessons**: link students to lessons, so that each lesson card shows who attends it and each student card shows their lessons.
* **View by lesson or day**: list all students or only those in one lesson, and list all lessons or only those on one day.
* **Find students**: search by the student's name or their guardian's name, e.g. to find out who a message from "Mrs Tan" is about.

A quick taste of the commands:

```
addlesson n/Sec 3 A-Math d/mon st/1600 et/1800
addstudent n/Aiden Tan gn/Tan Mei Ling gp/91234567
enrol s/1 l/1
liststudents l/1
findstudent gn/tan
```

## Documentation

* [User Guide](https://ay2627s1-cs2103t-f13-1.github.io/tp/UserGuide.html): how to install and use TutorReach.
* [Developer Guide](https://ay2627s1-cs2103t-f13-1.github.io/tp/DeveloperGuide.html): how TutorReach is designed and implemented.
* [About Us](https://ay2627s1-cs2103t-f13-1.github.io/tp/AboutUs.html): the team behind TutorReach.

For the full documentation, see the **[TutorReach Product Website](https://ay2627s1-cs2103t-f13-1.github.io/tp/)**.

## Acknowledgements

This project is based on the AddressBook-Level3 project created by the [SE-EDU initiative](https://se-education.org).
