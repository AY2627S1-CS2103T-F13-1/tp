package seedu.address.model.student.exceptions;

/** Signals an attempt to add a duplicate student. */
public class DuplicateStudentException extends RuntimeException {
    public DuplicateStudentException() {
        super("This student already exists.");
    }
}
