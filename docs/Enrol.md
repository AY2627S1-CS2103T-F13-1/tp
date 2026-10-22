# Enrolling a student in a lesson

Use `enrol s/STUDENT_INDEX l/LESSON_INDEX`, for example `enrol s/1 l/2`.
The prefixes can appear in either order. Both indices refer to the currently
shown panels and must be positive integers. Leading zeros are accepted.

The success message names the student and lesson. Both cards update immediately,
and both panel filters stay active. Repeating the same student–lesson pair
reports that the student is already enrolled. Students can join multiple lessons,
including lessons whose times overlap; lessons have no class size limit.

Use `liststudents l/2` to show students enrolled in displayed lesson 2, including
their guardian contact details. Use `liststudents` to show all students again.
Membership labels include linked records even when those records are hidden by
a panel filter.

Missing or invalid values show command usage; repeated prefixes report the
repeated field. Out-of-range indices report the student error first, then the
lesson error. Check the names in the success message to catch swapped indices.
Use `unenrol s/STUDENT_INDEX l/LESSON_INDEX` to remove a student from a lesson.
It takes the same indices and reports the same index errors as `enrol`, and
keeps both records. Unenrolling a student who is not in the lesson reports that
they are not enrolled.
