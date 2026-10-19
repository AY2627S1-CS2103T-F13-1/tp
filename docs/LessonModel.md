# Lesson model and storage

This issue supplies shared data and APIs. Lesson commands, their parsers, and UI layouts remain
separate tasks. Storage uses the existing local `data/addressbook.json` path (or the configured path).
No database or new dependencies are needed.

## Types

```java
Lesson lesson = new Lesson("Sec 3 A-Math", DayOfWeek.MONDAY,
        LocalTime.of(16, 0), LocalTime.of(18, 0));
```

`Lesson` is immutable, in `seedu.address.model.lesson`, and exposes `getId()`, `getName()`,
`getDay()`, `getStartTime()`, and `getEndTime()`. Days use `java.time.DayOfWeek`; times use
`java.time.LocalTime`. The constructor validates the name and minute precision, and rejects an end time
equal to the start time. An end time earlier than the start time means the lesson runs past midnight and
ends on the next day; `getDay()` is the day it starts on.
Names are trimmed and repeated spaces collapsed. `isSameLesson` compares the name ignoring case,
day and both times. Overlapping lessons are allowed. `overlaps` reports whether two lessons' weekly
time ranges overlap: a lesson that starts exactly when another ends does not overlap it, and a lesson
past midnight also covers the start of the next day. `equals` includes the ID and exact details.

`Student.getId()` now returns a permanent UUID. Existing constructors still work and generate
new IDs. The extra constructor taking a UUID is for restoring records from storage. Student
`equals` retains its existing contact-value semantics for compatibility; use `getId()` for links.
Do not create a new Student just to enrol someone: take the existing record from the model.

`Enrolment(UUID studentId, UUID lessonId)` is an immutable record linking those IDs. Never store
screen indexes as links. Students and lessons each reject duplicate IDs as well as duplicate details.

## Commands and filters

```java
model.hasLesson(lesson);
model.addLesson(lesson); // Clears only the lesson filter.
model.deleteLesson(lesson); // Keeps students; removes this lesson's links.
model.enrolStudent(student, lesson); // Keeps both filters; rejects duplicate/missing references.
model.getFilteredLessonList(); // Resolve lesson indexes here.
model.getFilteredStudentList(); // Resolve student indexes here.
model.updateFilteredLessonList(l -> l.getDay() == DayOfWeek.MONDAY);
model.updateFilteredLessonList(Model.PREDICATE_SHOW_ALL_LESSONS);
model.filterStudentsByLesson(lesson); // Uses the stable ID and refreshes after enrolment.
model.updateFilteredStudentList(Model.PREDICATE_SHOW_ALL_STUDENTS);
```

Changing the student predicate (e.g. a search) replaces the lesson-based student filter.
Deleting the lesson used by that filter restores all students. Deleting a student removes their
links while keeping all lessons, including empty ones. `setAddressBook` validates replacement
data before mutation and preserves the observable list instances.

Invalid lesson/enrolment operations throw `IllegalArgumentException` with a descriptive message;
null required arguments throw `NullPointerException`. Commands should convert user-facing failures
into `CommandException`. Parsers still own command syntax: three-letter day names (`mon` to `sun`), strict
four-digit HHmm input, repeated prefixes, positive indexes, and appropriate usage messages.
The model accepts typed day/time values, so it cannot distinguish `1600` from `16:00` user input.

## UI and membership queries

```java
logic.getFilteredLessonList(); // Observable, unmodifiable lesson panel data.
logic.getEnrolmentList(); // Observable links; listen here to refresh membership labels.
logic.getAddressBook().getStudentsInLesson(lesson);
logic.getAddressBook().getLessonsForStudent(student);
logic.getAddressBook().isEnrolled(student, lesson);
logic.getAddressBook().getClashingLessons(lesson); // Overlapping lessons, excluding lesson's own ID.
```

The last four are also available through `model.getAddressBook()`. These queries return
unmodifiable snapshots in insertion order, independent of either panel's filters. Query again
when observable enrolments change. Also observe the relevant student/lesson list for additions,
deletions or data resets. Cards do not automatically redraw merely because their record is immutable.

## Storage and compatibility

The JSON root retains `persons` and `students` and adds `lessons` and `enrolments`:

```json
{
  "persons": [],
  "students": [{
    "id": "00000000-0000-0000-0000-000000000001",
    "name": "Aiden Tan", "phone": "91234567",
    "guardianName": null, "guardianPhone": null
  }],
  "lessons": [{
    "id": "00000000-0000-0000-0000-000000000002",
    "name": "Sec 3 A-Math", "day": "MONDAY",
    "startTime": "1600", "endTime": "1800"
  }],
  "enrolments": [{
    "studentId": "00000000-0000-0000-0000-000000000001",
    "lessonId": "00000000-0000-0000-0000-000000000002"
  }]
}
```

Missing lesson/enrolment lists default to empty. Older students without IDs receive one on load;
it becomes persistent at the next normal save. Existing person data remains intact. Lessons and
links require IDs. Duplicate lessons, duplicate links, invalid fields and missing references are
rejected on load using the existing data-loading error path. Existing successful commands already
save the whole address book through LogicManager; do not add separate file writes to new commands.
