package seedu.address.pe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddLessonCommand;
import seedu.address.logic.commands.AddStudentCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteLessonCommand;
import seedu.address.logic.commands.DeleteStudentCommand;
import seedu.address.logic.commands.EditLessonCommand;
import seedu.address.logic.commands.EditStudentCommand;
import seedu.address.logic.commands.EnrolCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindStudentCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListLessonsCommand;
import seedu.address.logic.commands.ListStudentsCommand;
import seedu.address.logic.commands.UnenrolCommand;

/**
 * Runs every example in the User Guide on the sample data. PE testers copy the examples into a fresh app, and an
 * example that fails there, such as one adding a student who is already in the sample data, is a common bug report.
 */
public class UserGuideExamplesTest {
    private static final Path USER_GUIDE = Path.of("docs", "UserGuide.md");
    private static final Set<String> COMMAND_WORDS = Set.of(AddStudentCommand.COMMAND_WORD,
            DeleteStudentCommand.COMMAND_WORD, EditStudentCommand.COMMAND_WORD, FindStudentCommand.COMMAND_WORD,
            ListStudentsCommand.COMMAND_WORD, AddLessonCommand.COMMAND_WORD, DeleteLessonCommand.COMMAND_WORD,
            EditLessonCommand.COMMAND_WORD, ListLessonsCommand.COMMAND_WORD, EnrolCommand.COMMAND_WORD,
            UnenrolCommand.COMMAND_WORD, ClearCommand.COMMAND_WORD, HelpCommand.COMMAND_WORD,
            ExitCommand.COMMAND_WORD);
    private static final Pattern CODE = Pattern.compile("`([^`]+)`");
    private static final Pattern SUMMARY_EXAMPLE = Pattern.compile("e\\.g\\., `([^`]+)`");
    // A parameter to fill in, such as NAME or LESSON_INDEX, marks a command format rather than an example.
    private static final Pattern PLACEHOLDER = Pattern.compile("\\b[A-Z][A-Z_]+\\b");

    @TempDir
    public Path temporaryFolder;

    @TestFactory
    public Stream<DynamicTest> userGuideExample_onSampleData_succeeds() throws IOException {
        List<List<String>> examples = findExamples(Files.readAllLines(USER_GUIDE));
        assertFalse(examples.isEmpty());
        return examples.stream().map(commands -> DynamicTest.dynamicTest(String.join(", then ", commands), () ->
                runOnSampleData(commands)));
    }

    @Test
    public void findExamples_bulletsAndSummaryRows_returnsCommandsInOrder() {
        List<String> lines = List.of(
                "* `listlessons d/mon` followed by `deletelesson 1` deletes the first lesson on Monday.",
                "* `INDEX` must be a positive integer, as in `deletestudent 1`.",
                "* Names follow the same rules as for `addstudent`.",
                "**Add lesson** | `addlesson n/NAME d/DAY`<br> e.g., `addlesson n/Math d/mon st/1600 et/1800`",
                "Run `liststudents` to see every student.",
                "   * `help` : Opens the help window.");

        assertEquals(List.of(List.of("listlessons d/mon", "deletelesson 1"),
                List.of("addlesson n/Math d/mon st/1600 et/1800"), List.of("help")), findExamples(lines));
    }

    /**
     * Runs {@code commands} in order on a fresh copy of the sample data with two lessons. An {@code unenrol}
     * example is preceded by the {@code enrol} it undoes, since its section says the student must be enrolled.
     */
    private void runOnSampleData(List<String> commands) throws Exception {
        PeTestHarness harness = PeTestHarness.withSampleDataAndLessons(
                Files.createTempDirectory(temporaryFolder, "example"));
        for (String command : commands) {
            if (command.startsWith(UnenrolCommand.COMMAND_WORD + " ")) {
                harness.execute(EnrolCommand.COMMAND_WORD + command.substring(UnenrolCommand.COMMAND_WORD.length()));
            }
            harness.execute(command);
        }
    }

    /**
     * Returns the example commands in {@code lines}: the commands of each bullet point that starts with one, run
     * in order as one example, and the example of each command summary row. Command formats are left out.
     */
    private static List<List<String>> findExamples(List<String> lines) {
        List<List<String>> examples = new ArrayList<>();
        for (String line : lines) {
            List<String> codes = CODE.matcher(exampleText(line)).results().map(code -> code.group(1)).toList();
            if (!codes.isEmpty() && isExampleCommand(codes.getFirst())) {
                examples.add(codes.stream().filter(UserGuideExamplesTest::isExampleCommand).toList());
            }
        }
        return examples;
    }

    /**
     * Returns the part of {@code line} that can hold an example: a bullet point that starts with code, or the
     * example of a command summary row. Returns an empty string for other lines, such as bullet points that only
     * mention a command in passing.
     */
    private static String exampleText(String line) {
        if (line.stripLeading().startsWith("* `")) {
            return line;
        }
        Matcher summaryExample = SUMMARY_EXAMPLE.matcher(line);
        return summaryExample.find() ? summaryExample.group() : "";
    }

    private static boolean isExampleCommand(String text) {
        return COMMAND_WORDS.contains(text.split(" ", 2)[0]) && !PLACEHOLDER.matcher(text).find();
    }
}
