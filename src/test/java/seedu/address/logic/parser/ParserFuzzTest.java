package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.util.SampleDataUtil;

/**
 * Deterministic mutation-based smoke testing for the command parser and command execution.
 */
public class ParserFuzzTest {
    private static final String[] COMMAND_WORDS = {
        "enrol", "unenrol", "addstudent", "deletestudent", "editstudent", "addlesson",
        "deletelesson", "editlesson", "clear", "findstudent", "listlessons", "liststudents",
        "exit", "help", "add", "list", "edit", "find", "delete", "unknown", "", " ", "\t",
    };
    private static final String[] FRAGMENTS = {
        "n/", "p/", "gn/", "gp/", "d/", "st/", "et/", "s/", "l/",
        "1", "0", "-1", "2147483647", "2147483648", "99999999999999999999",
        "Aiden", "Aiden Tan", "aid", "91234567", "91a", "mon", "MONDAY", "1600", "2400", "2359", "0100",
        "-", "'", ".", ",", "&", "(", ")", "+", "emoji", "é", "日本語", "\u0000", "\u0001", "\r", "\n",
        " ", "", "/", "//", "n/a b/c", "a/b/c", "l/1 s/2", "s/1 l/1", "d/mon st/1600 et/1800",
        "x".repeat(1000), "x".repeat(10000).strip(),
    };

    private final Random random = new Random(20261025L);

    @Test
    public void randomCommands_neverEscapeParserOrCommandContract() {
        int parserFailures = 0;
        int commandFailures = 0;
        int successfulCommands = 0;

        for (int i = 0; i < 5000; i++) {
            String input = randomCommand();
            Command command;
            try {
                command = new AddressBookParser().parseCommand(input);
            } catch (ParseException exception) {
                parserFailures++;
                continue;
            }
            Model model = new ModelManager(SampleDataUtil.getSampleAddressBook(), new UserPrefs());
            try {
                command.execute(model);
                successfulCommands++;
            } catch (CommandException exception) {
                commandFailures++;
            }
        }

        assertTrue(parserFailures > 0, "The corpus should include parser rejections");
        assertTrue(commandFailures > 0, "The corpus should include rejected command execution");
        assertTrue(successfulCommands > 0, "The corpus should include accepted commands");
    }

    @Test
    public void randomCommands_allSupportModelLookupsWithoutUnexpectedExceptions() {
        for (int i = 0; i < 1500; i++) {
            String input = randomCommand();
            assertDoesNotThrow(() -> executeOnlyExpectedFailures(input));
        }
    }

    private void executeOnlyExpectedFailures(String input) {
        Command command;
        try {
            command = new AddressBookParser().parseCommand(input);
        } catch (ParseException exception) {
            return;
        }
        Model model = new ModelManager(SampleDataUtil.getSampleAddressBook(), new UserPrefs());
        try {
            command.execute(model);
        } catch (CommandException exception) {
            return;
        }
    }

    private String randomCommand() {
        String commandWord = COMMAND_WORDS[random.nextInt(COMMAND_WORDS.length)];
        int fragmentCount = random.nextInt(10);
        if (commandWord.equals("enrol") || commandWord.equals("unenrol")) {
            int studentIndex = random.nextBoolean() ? random.nextInt(10) : random.nextInt(10) + 1;
            int lessonIndex = random.nextBoolean() ? random.nextInt(10) : random.nextInt(10) + 1;
            return commandWord + " s/" + studentIndex + " l/" + lessonIndex;
        }
        List<String> parts = new ArrayList<>();
        for (int i = 0; i < fragmentCount; i++) {
            parts.add(FRAGMENTS[random.nextInt(FRAGMENTS.length)]);
        }
        return commandWord + String.join(" ", parts);
    }
}
