package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_TIME;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddLessonCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.lesson.Lesson;

public class AddLessonCommandParserTest {
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddLessonCommand.MESSAGE_USAGE);

    private static final String NAME_DESC = " n/Sec 3 A-Math";
    private static final String DAY_DESC = " d/mon";
    private static final String START_DESC = " st/1600";
    private static final String END_DESC = " et/1800";

    private static final AddLessonCommand ADD_A_MATH_COMMAND =
            addCommand("Sec 3 A-Math", DayOfWeek.MONDAY, "16:00", "18:00");

    private final AddLessonCommandParser parser = new AddLessonCommandParser();

    @Test
    public void parse_allFieldsPresent_returnsAddLessonCommand() {
        assertParseSuccess(parser, NAME_DESC + DAY_DESC + START_DESC + END_DESC, ADD_A_MATH_COMMAND);
    }

    @Test
    public void parse_usageExample_returnsAddLessonCommand() {
        // the example shown to the tutor in error messages must itself be a valid command
        String usage = AddLessonCommand.MESSAGE_USAGE;
        String exampleArguments = usage.substring(
                usage.lastIndexOf(AddLessonCommand.COMMAND_WORD) + AddLessonCommand.COMMAND_WORD.length());

        assertParseSuccess(parser, exampleArguments, ADD_A_MATH_COMMAND);
    }

    @Test
    public void parse_fieldsInAnyOrder_returnsAddLessonCommand() {
        assertParseSuccess(parser, END_DESC + START_DESC + DAY_DESC + NAME_DESC, ADD_A_MATH_COMMAND);
        assertParseSuccess(parser, DAY_DESC + NAME_DESC + END_DESC + START_DESC, ADD_A_MATH_COMMAND);
    }

    @Test
    public void parse_extraWhitespace_returnsAddLessonCommand() {
        // spaces around every value are trimmed, and repeated spaces inside the name are collapsed
        assertParseSuccess(parser, "   n/  Sec 3   A-Math   d/  mon   st/  1600   et/  1800   ", ADD_A_MATH_COMMAND);

        // the 50-character limit applies to the name after its repeated spaces are collapsed
        String spacedOutName = "Sec 3" + " ".repeat(60) + "A-Math";
        assertParseSuccess(parser, " n/" + spacedOutName + DAY_DESC + START_DESC + END_DESC, ADD_A_MATH_COMMAND);
    }

    @Test
    public void parse_dayInAnyLetterCase_returnsAddLessonCommand() {
        for (String day : List.of("mon", "MON", "Mon", "mOn")) {
            assertParseSuccess(parser, NAME_DESC + " d/" + day + START_DESC + END_DESC, ADD_A_MATH_COMMAND);
        }
        assertParseSuccess(parser, " n/Math d/sun st/1600 et/1800",
                addCommand("Math", DayOfWeek.SUNDAY, "16:00", "18:00"));
        assertParseSuccess(parser, " n/Math d/SAT st/1600 et/1800",
                addCommand("Math", DayOfWeek.SATURDAY, "16:00", "18:00"));
    }

    @Test
    public void parse_fullDayNameAndTimesWithColons_returnsAddLessonCommand() {
        assertParseSuccess(parser, NAME_DESC + " d/Monday st/16:00 et/18:00", ADD_A_MATH_COMMAND);
        assertParseSuccess(parser, " n/Math d/thurs st/9:30 et/1100",
                addCommand("Math", DayOfWeek.THURSDAY, "09:30", "11:00"));
    }

    @Test
    public void parse_boundaryTimes_returnsAddLessonCommand() {
        // earliest start and latest end in a day
        assertParseSuccess(parser, " n/Math d/mon st/0000 et/2359",
                addCommand("Math", DayOfWeek.MONDAY, "00:00", "23:59"));

        // shortest lesson: the end is one minute after the start
        assertParseSuccess(parser, " n/Math d/mon st/0000 et/0001",
                addCommand("Math", DayOfWeek.MONDAY, "00:00", "00:01"));
        assertParseSuccess(parser, " n/Math d/mon st/2358 et/2359",
                addCommand("Math", DayOfWeek.MONDAY, "23:58", "23:59"));

        // leading zeros are part of a morning time
        assertParseSuccess(parser, " n/Math d/mon st/0805 et/0930",
                addCommand("Math", DayOfWeek.MONDAY, "08:05", "09:30"));
    }

    @Test
    public void parse_validNames_returnsAddLessonCommand() {
        String longestName = "A".repeat(50);
        List<String> names = List.of("M", "3E Math", longestName, "Sec 3 A-Math (Group 2)",
                "P5 English & Science", "O'Level Prep", "Math, Sec 4", "E.Math+",
                "P5 Math: Fractions", "Group #1", "Sec 3 A-Math @ Bishan", "Sec 3 A/E Math");

        for (String name : names) {
            assertParseSuccess(parser, " n/" + name + DAY_DESC + START_DESC + END_DESC,
                    addCommand(name, DayOfWeek.MONDAY, "16:00", "18:00"));
        }
    }

    @Test
    public void parse_nameContainingDayOrTime_returnsAddLessonCommand() {
        // only prefixes separate fields, so a name may mention a day or a time
        assertParseSuccess(parser, " n/Monday 1600 Class d/tue st/0900 et/1000",
                addCommand("Monday 1600 Class", DayOfWeek.TUESDAY, "09:00", "10:00"));
    }

    @Test
    public void parse_missingField_throwsParseException() {
        // no arguments at all
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "   ", MESSAGE_INVALID_FORMAT);

        // values without any prefixes
        assertParseFailure(parser, " Sec 3 A-Math mon 1600 1800", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_someFieldsMissing_throwsParseExceptionNamingThem() {
        // each field missing in turn
        assertParseFailure(parser, DAY_DESC + START_DESC + END_DESC, missing("n/NAME"));
        assertParseFailure(parser, NAME_DESC + START_DESC + END_DESC, missing("d/DAY"));
        assertParseFailure(parser, NAME_DESC + DAY_DESC + END_DESC, missing("st/START_TIME"));
        assertParseFailure(parser, NAME_DESC + DAY_DESC + START_DESC, missing("et/END_TIME"));

        // several fields missing, named in the order of the command format
        assertParseFailure(parser, END_DESC + NAME_DESC, missing("d/DAY and st/START_TIME"));
        assertParseFailure(parser, NAME_DESC, missing("d/DAY, st/START_TIME and et/END_TIME"));

        // a name typed without its prefix
        assertParseFailure(parser, " Sec 3 A-Math" + DAY_DESC + START_DESC + END_DESC, missing("n/NAME"));
    }

    @Test
    public void parse_wrongPrefix_throwsParseException() {
        // prefixes are case-sensitive, so the error names the prefix that was not recognised
        assertParseFailure(parser, " N/Sec 3 A-Math d/mon st/1600 et/1800", unknownPrefix("N/"));
        assertParseFailure(parser, " n/Sec 3 A-Math D/mon st/1600 et/1800", unknownPrefix("D/"));
        assertParseFailure(parser, " n/Sec 3 A-Math d/mon ST/1600 et/1800", unknownPrefix("ST/"));
        assertParseFailure(parser, " n/Sec 3 A-Math d/mon st/1600 ET/1800", unknownPrefix("ET/"));

        // similar prefixes that are not the lesson ones; the first is named
        assertParseFailure(parser, " n/Sec 3 A-Math d/mon s/1600 e/1800", unknownPrefix("s/"));

        // longer words are not taken for prefixes, so the missing st/ and et/ are reported
        assertParseFailure(parser, " n/Sec 3 A-Math d/mon start/1600 end/1800",
                missing("st/START_TIME and et/END_TIME"));

        // a prefix must follow a space to be read as one, so a missed space reports that prefix as missing
        assertParseFailure(parser, " n/Sec 3 A-Mathd/mon st/1600 et/1800", missing("d/DAY"));
        assertParseFailure(parser, " n/Sec 3 A-Math d/monst/1600 et/1800", missing("st/START_TIME"));
        assertParseFailure(parser, " n/Sec 3 A-Math d/mon st/1600et/1800", missing("et/END_TIME"));
    }

    @Test
    public void parse_nonEmptyPreamble_throwsParseException() {
        assertParseFailure(parser, " 1" + NAME_DESC + DAY_DESC + START_DESC + END_DESC, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " lesson" + NAME_DESC + DAY_DESC + START_DESC + END_DESC, MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_repeatedField_throwsParseException() {
        assertParseFailure(parser, " n/Math" + NAME_DESC + DAY_DESC + START_DESC + END_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
        assertParseFailure(parser, NAME_DESC + " d/tue" + DAY_DESC + START_DESC + END_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DAY));
        assertParseFailure(parser, NAME_DESC + DAY_DESC + " st/1500" + START_DESC + END_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_START_TIME));
        assertParseFailure(parser, NAME_DESC + DAY_DESC + START_DESC + " et/1900" + END_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_END_TIME));

        // repeating the same value is still a repeat
        assertParseFailure(parser, NAME_DESC + DAY_DESC + DAY_DESC + START_DESC + END_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DAY));

        // several repeated fields are reported together
        assertParseFailure(parser, NAME_DESC + DAY_DESC + DAY_DESC + START_DESC + START_DESC + END_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DAY, PREFIX_START_TIME));
    }

    @Test
    public void parse_invalidName_throwsParseException() {
        String tooLongName = "A".repeat(51);
        // the last two are Chinese characters and an accented letter, written as escapes
        List<String> names = List.of("", "-Math", "(Math)", "Math_1", tooLongName,
                "\u6570\u5B66", "Caf\u00E9 Math");

        for (String name : names) {
            assertParseFailure(parser, " n/" + name + DAY_DESC + START_DESC + END_DESC,
                    Lesson.MESSAGE_NAME_CONSTRAINTS);
        }
    }

    @Test
    public void parse_invalidDay_throwsParseException() {
        // "funday" is the Developer Guide's example of an invalid day
        for (String day : List.of("", "funday", "mo", "m", "1")) {
            assertParseFailure(parser, NAME_DESC + " d/" + day + START_DESC + END_DESC,
                    ParserUtil.MESSAGE_INVALID_DAY);
        }
    }

    @Test
    public void parse_validDayFollowedByText_throwsParseExceptionNamingText() {
        assertParseFailure(parser, NAME_DESC + " d/mon tue" + START_DESC + END_DESC,
                String.format(ParserUtil.MESSAGE_UNEXPECTED_TEXT_AFTER_DAY, "mon", "tue"));
        assertParseFailure(parser, NAME_DESC + DAY_DESC + " st/1600 xyz" + END_DESC,
                String.format(ParserUtil.MESSAGE_UNEXPECTED_TEXT_AFTER_TIME, "1600", "xyz"));
    }

    @Test
    public void parse_invalidStartOrEndTime_throwsParseException() {
        // "4pm" and "2400" are the Developer Guide's examples of invalid times
        for (String time : List.of("", "4pm", "2400", "1660", "16:60", "900")) {
            assertParseFailure(parser, NAME_DESC + DAY_DESC + " st/" + time + END_DESC,
                    ParserUtil.MESSAGE_INVALID_TIME);
            assertParseFailure(parser, NAME_DESC + DAY_DESC + START_DESC + " et/" + time,
                    ParserUtil.MESSAGE_INVALID_TIME);
        }
    }

    @Test
    public void parse_endBeforeStart_returnsLessonEndingNextDay() {
        // a late lesson that runs past midnight belongs to the day it starts on
        assertParseSuccess(parser, " n/Night Class d/fri st/2200 et/0100",
                addCommand("Night Class", DayOfWeek.FRIDAY, "22:00", "01:00"));

        // shortest and longest lessons across midnight
        assertParseSuccess(parser, " n/Math d/mon st/2359 et/0000",
                addCommand("Math", DayOfWeek.MONDAY, "23:59", "00:00"));
        assertParseSuccess(parser, " n/Math d/mon st/1601 et/1600",
                addCommand("Math", DayOfWeek.MONDAY, "16:01", "16:00"));

        // any earlier end time is read this way, so swapped times are not reported as a mistake
        assertParseSuccess(parser, NAME_DESC + DAY_DESC + " st/1800 et/1600",
                addCommand("Sec 3 A-Math", DayOfWeek.MONDAY, "18:00", "16:00"));
    }

    @Test
    public void parse_endSameAsStart_throwsParseException() {
        // neither an empty lesson nor one lasting exactly a day
        for (String time : List.of("0000", "1600", "2359")) {
            assertParseFailure(parser, NAME_DESC + DAY_DESC + " st/" + time + " et/" + time,
                    Lesson.MESSAGE_TIME_CONSTRAINTS);
        }
    }

    @Test
    public void parse_severalProblemsAtOnce_throwsParseException() {
        // the Developer Guide does not say which problem is reported first, so only the rejection is checked
        List<String> inputs = List.of(
                " n/-Math d/funday st/4pm et/5pm", // every value invalid
                " n/-Math d/mon st/1600 et/1600", // invalid name, and end same as start
                " n/Math d/funday st/1600 et/1600", // invalid day, and end same as start
                " n/Math d/mon st/1800 et/4pm", // invalid end time, so the times cannot be compared
                " n/-Math d/funday st/4pm", // missing field and invalid values
                " n/Math n/Science d/mon st/1600", // missing field and repeated field
                " n/-Math n/Math d/mon st/1600 et/1800", // repeated field and invalid value
                " n/Math d/mon st/4pm st/5pm et/1800"); // repeated field with invalid values

        for (String input : inputs) {
            assertThrows(ParseException.class, () -> parser.parse(input));
        }
    }

    @Test
    public void parse_unsupportedPrefixOrTrailingText_throwsParseException() {
        // text that is not a lesson field is never silently dropped; it makes the field before it invalid
        assertThrows(ParseException.class, () -> parser.parse(" n/Math d/mon st/1600 et/1800 extra"));
    }

    @Test
    public void parse_unsupportedPrefix_namesThatPrefix() {
        // the error names the prefix, rather than the field whose value it ended up in
        assertParseFailure(parser, " n/Math d/mon st/1600 et/1800 p/91234567", unknownPrefix("p/"));
        assertParseFailure(parser, " n/Math gn/Tan d/mon st/1600 et/1800", unknownPrefix("gn/"));
        assertParseFailure(parser, " n/Math d/mon l/1 st/1600 et/1800", unknownPrefix("l/"));

        // a mistyped prefix is reported instead of the missing field it was meant to be
        assertParseFailure(parser, " n/Math d/mon st/1600 e/1800", unknownPrefix("e/"));
    }

    @Test
    public void parse_slashInLessonName_doesNotBecomePrefix() {
        // a slash inside a name is not a prefix, so the whole value stays in the lesson name
        for (String name : List.of("Sec 3 Physics/Chem", "Sec 3 A/E Math", "Math and/or Science", "Physics/Chem")) {
            assertParseSuccess(parser, " n/" + name + DAY_DESC + START_DESC + END_DESC,
                    addCommand(name, DayOfWeek.MONDAY, "16:00", "18:00"));
        }
    }

    private static String missing(String fields) {
        return String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                String.format(AddLessonCommandParser.MESSAGE_MISSING_FIELDS, fields) + "\n"
                        + AddLessonCommand.MESSAGE_USAGE);
    }

    private static String unknownPrefix(String prefix) {
        return Messages.getErrorMessageForUnknownPrefix(AddLessonCommand.COMMAND_WORD, prefix,
                PREFIX_NAME, PREFIX_DAY, PREFIX_START_TIME, PREFIX_END_TIME);
    }

    /** Creates the command expected for a lesson with these details; times are given as {@code HH:mm}. */
    private static AddLessonCommand addCommand(String name, DayOfWeek day, String startTime, String endTime) {
        return new AddLessonCommand(new Lesson(name, day, LocalTime.parse(startTime), LocalTime.parse(endTime)));
    }
}
