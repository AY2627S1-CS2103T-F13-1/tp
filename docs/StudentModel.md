# Student model and storage: team handoff

This is the foundation for the first iteration. Student commands and UI changes are separate issues.

## Shared types

Use `seedu.address.model.student.Student` and `Guardian`. Both are immutable.
Reuse `seedu.address.model.person.Name` and `Phone` for validated values.

```java
Guardian guardian = new Guardian(new Name("Tan Mei Ling"), new Phone("91234567"));
Student student = new Student(new Name("Aiden Tan"), null, guardian);
Student studentWithPhone = new Student(new Name("Chloe Tan"), new Phone("88888888"), null);
```

- `Student(Name, Phone, Guardian)`: name is required; null phone/guardian means absent.
- At least one of the student's phone or guardian must be present.
- `Guardian(Name, Phone)`: both values are required, so a half-filled guardian cannot exist.
- `student.getName()` returns `Name`.
- `student.getPhone()` returns `Optional<Phone>`.
- `student.getGuardian()` returns `Optional<Guardian>`.
- `guardian.getName()` and `guardian.getPhone()` return required values.
- Empty strings are invalid values; use absence instead.
- `equals` compares all stored details. `isSameStudent` checks for duplicates: same name
  ignoring case and extra spaces, plus any shared student/guardian phone number.

## Command work

Use these methods on `Model`:

```java
model.hasStudent(student);
model.addStudent(student); // Rejects duplicates and shows all students.
model.deleteStudent(student); // Preserves the active filter.
model.getFilteredStudentList(); // Use this for displayed indexes.
model.updateFilteredStudentList(Model.PREDICATE_SHOW_ALL_STUDENTS);
model.updateFilteredStudentList(s -> s.getName().fullName.contains("Aiden"));
```

The last line only illustrates the filter API; implement the specified search matching in the find issue.
`DuplicateStudentException` and `StudentNotFoundException` signal invalid model operations.
Command parsers should report missing guardian prefixes and invalid input as `ParseException`;
commands should translate model failures into appropriate `CommandException` messages.
Do not write storage calls inside commands: the existing `LogicManager.execute` saves the whole
address book after a successful command.

## UI work

Bind a `ListView<Student>` to `logic.getFilteredStudentList()`. The list is observable and
unmodifiable: adds, deletes, filtering, and data resets update the same list automatically.
A student card can read:

```java
name.setText(student.getName().fullName);
phone.setText(student.getPhone().map(p -> p.value).orElse("-"));
guardianName.setText(student.getGuardian().map(g -> g.getName().fullName).orElse("-"));
guardianPhone.setText(student.getGuardian().map(g -> g.getPhone().value).orElse("-"));
```

For UI development without an add command, create a `ModelManager` in a test/demo and call
`addStudent` with either example above before constructing the UI.

## Local storage

Students use the existing configured JSON file (default `data/addressbook.json`). No database
or new dependency is required. The file now contains both legacy `persons` and new `students`:

```json
{
  "persons": [],
  "students": [
    {
      "name": "Aiden Tan",
      "phone": null,
      "guardianName": "Tan Mei Ling",
      "guardianPhone": "91234567"
    }
  ]
}
```

Old files without `students` still load with an empty student list. Existing AB3 contacts are
preserved as persons; they are not automatically converted to students. Student records are
validated on load, including duplicate detection. The existing startup error handling applies
to invalid files. Existing AB3 commands and screens still operate on persons until the team
implements the student commands and UI; the existing `clear` clears the entire address book.
