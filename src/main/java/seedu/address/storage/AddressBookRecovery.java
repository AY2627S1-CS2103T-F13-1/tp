package seedu.address.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.student.Student;

/** Loads records independently, retaining valid data in file order. */
final class AddressBookRecovery {
    private final AddressBook addressBook = new AddressBook();
    private final List<String> skipped = new ArrayList<>();

    /** Reads a complete JSON object; malformed file structure is not recoverable. */
    static AddressBookRecovery read(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;
        try (JsonParser parser = mapper.getFactory().createParser(json)) {
            root = mapper.readTree(parser);
            if (root == null || !root.isObject() || parser.nextToken() != null) {
                throw new IOException("Expected one address book JSON object.");
            }
        }
        AddressBookRecovery result = new AddressBookRecovery();
        for (String field : List.of("persons", "students", "lessons", "enrolments")) {
            JsonNode records = root.get(field);
            if (records == null || records.isNull()) {
                continue; // Older files may omit lists.
            }
            if (!records.isArray()) {
                throw new IOException(field + " must be a JSON array.");
            }
            int index = 0;
            for (JsonNode record : records) {
                index++;
                try {
                    if (!record.isObject()) {
                        throw new IllegalValueException("Expected an object.");
                    }
                    result.add(field, record.toString());
                } catch (IOException | IllegalValueException | IllegalArgumentException | NullPointerException e) {
                    // Null nested values (e.g. tags) can also violate adapter constraints.
                    result.skipped.add(field + "[" + index + "]");
                }
            }
        }
        return result;
    }

    private void add(String field, String json) throws IOException, IllegalValueException {
        switch (field) {
            case "persons":
                Person person = JsonUtil.fromJsonString(json, JsonAdaptedPerson.class).toModelType();
                if (addressBook.hasPerson(person)) {
                    throw new IllegalValueException("Duplicate person.");
                }
                addressBook.addPerson(person);
                break;
            case "students":
                Student student = JsonUtil.fromJsonString(json, JsonAdaptedStudent.class).toModelType();
                if (addressBook.hasStudent(student)) {
                    throw new IllegalValueException("Duplicate student.");
                }
                addressBook.addStudent(student);
                break;
            case "lessons":
                addressBook.addLesson(JsonUtil.fromJsonString(json, JsonAdaptedLesson.class).toModelType());
                break;
            case "enrolments":
                addressBook.addEnrolment(JsonUtil.fromJsonString(json, JsonAdaptedEnrolment.class).toModelType());
                break;
            default:
                throw new IllegalArgumentException("Unknown record type: " + field);
        }
    }

    AddressBook getAddressBook() {
        return addressBook;
    }

    List<String> getSkippedRecords() {
        return List.copyOf(skipped);
    }
}
