package seedu.address.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Student;

/** Loads records independently, retaining valid data in file order. */
final class AddressBookRecovery {
    // short reasons for the load warning, in place of messages written for commands
    private static final Map<String, String> SHORT_REASONS = Map.of(
            Student.MESSAGE_CONTACT_REQUIRED, "needs a phone number",
            Name.MESSAGE_CONSTRAINTS, "invalid name",
            Phone.MESSAGE_CONSTRAINTS, "invalid phone number",
            "Guardian name and phone must be given together.", "guardian name and phone must be given together",
            "This lesson already exists.", "duplicate lesson",
            "Enrolment refers to a missing student or lesson.", "refers to a missing student or lesson",
            "Student is already enrolled in this lesson.", "duplicate enrolment");
    private static final Map<String, String> RECORD_TYPES = Map.of(
            "persons", "person", "students", "student", "lessons", "lesson", "enrolments", "enrolment");

    private final AddressBook addressBook = new AddressBook();
    private final List<String> skipped = new ArrayList<>();

    /** Reads a complete JSON object; malformed file structure is not recoverable. */
    static AddressBookRecovery read(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);
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
                    result.skipped.add(describe(field, index, record, e));
                }
            }
        }
        return result;
    }

    /**
     * Describes a skipped record so the user can tell which record it was, e.g.
     * {@code student 2 (Aiden Tan): guardian name and phone must be given together}.
     * Records are numbered from 1 within their list.
     */
    private static String describe(String field, int index, JsonNode record, Exception e) {
        String description = RECORD_TYPES.get(field) + " " + index;
        JsonNode name = record.get("name");
        if (name != null && name.isTextual()) {
            description += " (" + name.asText() + ")";
        }
        return description + ": " + describeReason(e);
    }

    /** Returns a reason the user can act on, rather than a Java or JSON library message. */
    private static String describeReason(Exception e) {
        if (e instanceof NullPointerException) {
            return "a value is missing";
        }
        if (e instanceof IOException) {
            return "a value has the wrong type or format";
        }
        if (e.getMessage() == null) {
            return "invalid value";
        }
        return SHORT_REASONS.getOrDefault(e.getMessage(), e.getMessage().lines().findFirst().orElse(""));
    }

    private void add(String field, String json) throws IOException, IllegalValueException {
        switch (field) {
            case "persons":
                Person person = JsonUtil.fromJsonString(json, JsonAdaptedPerson.class).toModelType();
                if (addressBook.hasPerson(person)) {
                    throw new IllegalValueException("duplicate person");
                }
                addressBook.addPerson(person);
                break;
            case "students":
                Student student = JsonUtil.fromJsonString(json, JsonAdaptedStudent.class).toModelType();
                if (addressBook.hasStudent(student)) {
                    throw new IllegalValueException("duplicate student");
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
