package seedu.address.pe;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ReadOnlyAddressBook;

/**
 * Data files edited by hand, as PE testers do to check that the app survives them. Invalid records must be skipped
 * with a warning and a backup of the original file; a file that cannot be read must leave the app empty with saving
 * disabled, so the original is never overwritten. The base file holds the sample students, the lessons Maths and
 * Science, and two enrolments: Aiden Tan in Maths and Chloe Lim in Science.
 */
public class HandEditedDataFileTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String ADD_COMMAND = "addstudent n/Zed Test p/90000000";

    private static final List<Edit> EDITS = List.of(
            new Edit("student name with @", json(root -> student(root, 1).put("name", "Chloe@Lim")),
                    Outcome.loads(2, 2, 1)),
            new Edit("student phone abc", json(root -> student(root, 1).put("phone", "abc")), Outcome.loads(2, 2, 1)),
            new Edit("student with no phone at all", json(root -> student(root, 1).putNull("phone")),
                    Outcome.loads(2, 2, 1)),
            new Edit("guardian name without phone", json(root -> student(root, 0).putNull("guardianPhone")),
                    Outcome.loads(2, 2, 1)),
            new Edit("student without a name", json(root -> student(root, 2).remove("name")), Outcome.loads(2, 2, 2)),
            new Edit("student id not a UUID", json(root -> student(root, 2).put("id", "abc")), Outcome.loads(2, 2, 2)),
            new Edit("two students sharing an id", json(root -> student(root, 2).set("id", student(root, 1).get("id"))),
                    Outcome.loads(2, 2, 2)),
            new Edit("exact copy of a student", json(root -> list(root, "students").add(student(root, 1).deepCopy())),
                    Outcome.loads(3, 2, 2)),
            new Edit("same student under a new id", json(root -> list(root, "students")
                    .add(student(root, 1).deepCopy().put("id", "11111111-1111-1111-1111-111111111111"))),
                    Outcome.loads(3, 2, 2)),
            new Edit("null inside the students list", json(root -> list(root, "students").insertNull(1)),
                    Outcome.loads(3, 2, 2)),
            new Edit("students list set to null", json(root -> root.putNull("students")), Outcome.loads(0, 2, 0)),
            new Edit("lesson day FUNDAY", json(root -> lesson(root, 0).put("day", "FUNDAY")), Outcome.loads(3, 1, 1)),
            new Edit("lesson start 2500", json(root -> lesson(root, 0).put("startTime", "2500")),
                    Outcome.loads(3, 1, 1)),
            new Edit("lesson start equals end", json(root -> lesson(root, 0).put("endTime", "1000")),
                    Outcome.loads(3, 1, 1)),
            new Edit("lesson name of 60 characters", json(root -> lesson(root, 0).put("name", "M".repeat(60))),
                    Outcome.loads(3, 1, 1)),
            new Edit("same lesson under a new id", json(root -> list(root, "lessons")
                    .add(lesson(root, 0).deepCopy().put("id", "22222222-2222-2222-2222-222222222222"))),
                    Outcome.loads(3, 2, 2)),
            new Edit("no lessons list", json(root -> root.remove("lessons")), Outcome.loads(3, 0, 0)),
            new Edit("enrolment of an unknown student", json(root -> list(root, "enrolments").addObject()
                    .put("studentId", "33333333-3333-3333-3333-333333333333")
                    .set("lessonId", lesson(root, 0).get("id"))), Outcome.loads(3, 2, 2)),
            new Edit("enrolment listed twice", json(root -> list(root, "enrolments")
                    .add(list(root, "enrolments").get(0).deepCopy())), Outcome.loads(3, 2, 2)),
            new Edit("unknown extra field", json(root -> student(root, 0).put("nickname", "Ai")),
                    Outcome.loadsWithoutWarning(3, 2, 2)),
            new Edit("empty object", text -> "{}", Outcome.loadsWithoutWarning(0, 0, 0)),
            new Edit("file cut short", text -> text.substring(0, text.length() - 10), Outcome.UNREADABLE),
            new Edit("first brace deleted", text -> text.replaceFirst("\\{", ""), Outcome.UNREADABLE),
            new Edit("empty file", text -> "", Outcome.UNREADABLE),
            new Edit("top-level array", text -> "[" + text + "]", Outcome.UNREADABLE));

    // Formats the commands accept, as a PE tester might hand-write them. Loading them keeps the data safe.
    private static final List<Edit> FORMAT_EDITS = List.of(
            new Edit("day as a command abbreviation", json(root -> lesson(root, 0).put("day", "mon")),
                    Outcome.loadsWithoutWarning(3, 2, 2)),
            new Edit("time with a colon", json(root -> lesson(root, 0).put("startTime", "10:00")),
                    Outcome.loadsWithoutWarning(3, 2, 2)),
            new Edit("byte order mark", text -> "﻿" + text, Outcome.SAFE));

    @TempDir
    public Path temporaryFolder;

    @TestFactory
    public Stream<DynamicTest> handEditedFile_loadedAsDocumented() {
        return Stream.concat(EDITS.stream(), FORMAT_EDITS.stream())
                .map(edit -> DynamicTest.dynamicTest(edit.name(), () -> assertLoadedAsExpected(edit)));
    }

    private void assertLoadedAsExpected(Edit edit) throws Exception {
        Path folder = Files.createTempDirectory(temporaryFolder, "file");
        Path dataFile = writeBaseFile(folder);
        Files.writeString(dataFile, edit.change().apply(Files.readString(dataFile)));
        byte[] original = Files.readAllBytes(dataFile);

        PeTestHarness harness = PeTestHarness.launch(dataFile);
        String warning = harness.getAddressBookStorage().getLoadWarning();
        Optional<Path> backup = findBackup(dataFile);
        boolean isSavingBlocked = isSavingBlocked(harness);
        Outcome outcome = edit.outcome();

        if (outcome == Outcome.SAFE) {
            assertTrue(isSavingBlocked ? Files.exists(dataFile) && sameContent(dataFile, original)
                    : warning.isEmpty() || backup.isPresent() && sameContent(backup.get(), original));
            return;
        }
        assertEquals(outcome == Outcome.UNREADABLE, isSavingBlocked);
        if (isSavingBlocked) {
            assertArrayEquals(original, Files.readAllBytes(dataFile));
            assertFalse(warning.isEmpty());
            return;
        }
        assertEquals(outcome.hasWarning(), !warning.isEmpty());
        assertEquals(outcome.hasWarning(), backup.isPresent());
        if (backup.isPresent()) {
            assertArrayEquals(original, Files.readAllBytes(backup.get()));
        }
        // the added student is saved, so one more student than loaded
        ReadOnlyAddressBook loaded = harness.getModel().getAddressBook();
        assertEquals(List.of(outcome.students() + 1, outcome.lessons(), outcome.enrolments()),
                List.of(loaded.getStudentList().size(), loaded.getLessonList().size(),
                        loaded.getEnrolmentList().size()));
    }

    /**
     * Returns true if saving is blocked, which this finds by adding a student.
     */
    private static boolean isSavingBlocked(PeTestHarness harness) throws Exception {
        try {
            harness.execute(ADD_COMMAND);
            return false;
        } catch (CommandException e) {
            return true;
        }
    }

    private static Path writeBaseFile(Path folder) throws Exception {
        PeTestHarness harness = PeTestHarness.withSampleDataAndLessons(folder);
        harness.execute("enrol s/2 l/2");
        return harness.getDataFile();
    }

    private static Optional<Path> findBackup(Path dataFile) throws IOException {
        String prefix = dataFile.getFileName() + ".recovery-";
        try (Stream<Path> files = Files.list(dataFile.getParent())) {
            return files.filter(file -> file.getFileName().toString().startsWith(prefix)).findFirst();
        }
    }

    private static boolean sameContent(Path file, byte[] expected) {
        try {
            return Arrays.equals(Files.readAllBytes(file), expected);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static UnaryOperator<String> json(Consumer<ObjectNode> change) {
        return text -> {
            try {
                ObjectNode root = (ObjectNode) MAPPER.readTree(text);
                change.accept(root);
                return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(root);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    private static ArrayNode list(ObjectNode root, String name) {
        return (ArrayNode) root.get(name);
    }

    private static ObjectNode student(ObjectNode root, int index) {
        return (ObjectNode) list(root, "students").get(index);
    }

    private static ObjectNode lesson(ObjectNode root, int index) {
        return (ObjectNode) list(root, "lessons").get(index);
    }

    /**
     * A hand edit of the base data file and what loading it must do.
     */
    private record Edit(String name, UnaryOperator<String> change, Outcome outcome) {
    }

    /**
     * The students, lessons and enrolments that must load, and whether a warning and backup are expected.
     */
    private record Outcome(int students, int lessons, int enrolments, boolean hasWarning) {
        // Nothing loads, saving is blocked and the original file is kept.
        static final Outcome UNREADABLE = new Outcome(0, 0, 0, true);
        // Either loading or refusing the file is fine, as long as the original is kept.
        static final Outcome SAFE = new Outcome(-1, -1, -1, false);

        static Outcome loads(int students, int lessons, int enrolments) {
            return new Outcome(students, lessons, enrolments, true);
        }

        static Outcome loadsWithoutWarning(int students, int lessons, int enrolments) {
            return new Outcome(students, lessons, enrolments, false);
        }
    }
}
