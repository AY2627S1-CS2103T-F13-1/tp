package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_DAY;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_TIME;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

public class ParserUtilTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "(65) 1234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_PHONE = "12345678";
    private static final String VALID_ADDRESS = "123 Main Street #0505";
    private static final String VALID_EMAIL = "rachel@example.com";
    private static final String VALID_TAG_1 = "friend";
    private static final String VALID_TAG_2 = "neighbour";

    private static final String WHITESPACE = " \t\r\n";

    @Test
    public void parseIndex_invalidInput_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseIndex("10 a"));
    }

    @Test
    public void parseIndex_outOfRangeInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, ()
            -> ParserUtil.parseIndex(Long.toString(Integer.MAX_VALUE + 1)));
    }

    @Test
    public void parseIndex_validInput_success() throws Exception {
        // No whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("1"));

        // Leading and trailing whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("  1  "));
    }

    @Test
    public void parseName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseName((String) null));
    }

    @Test
    public void parseName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseName(INVALID_NAME));
    }

    @Test
    public void parseName_validValueWithoutWhitespace_returnsName() throws Exception {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(VALID_NAME));
    }

    @Test
    public void parseName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = WHITESPACE + VALID_NAME + WHITESPACE;
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(nameWithWhitespace));
    }

    @Test
    public void parseName_validValueWithRepeatedSpaces_returnsCollapsedName() throws Exception {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName("Rachel   Walker"));
    }

    @Test
    public void parsePhone_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parsePhone((String) null));
    }

    @Test
    public void parsePhone_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parsePhone(INVALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithoutWhitespace_returnsPhone() throws Exception {
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(VALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithWhitespace_returnsTrimmedPhone() throws Exception {
        String phoneWithWhitespace = WHITESPACE + VALID_PHONE + WHITESPACE;
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(phoneWithWhitespace));
    }

    @Test
    public void parsePhone_formattedValue_returnsNormalizedPhone() throws Exception {
        assertEquals(new Phone("91234567"), ParserUtil.parsePhone("+65 9123-4567"));
        assertEquals(new Phone("+447700900123"), ParserUtil.parsePhone("+44 7700-900123"));
    }

    @Test
    public void parseStudentPhone_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseStudentPhone(null));
    }

    @Test
    public void parseStudentPhone_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, Phone.MESSAGE_STUDENT_PHONE_CONSTRAINTS, ()
            -> ParserUtil.parseStudentPhone("6591234567"));
    }

    @Test
    public void parseStudentPhone_manyFormattedGroups_throwsParseExceptionWithoutOverflow() {
        String longPhone = "1 ".repeat(2_999) + "1";

        assertThrows(ParseException.class, Phone.MESSAGE_STUDENT_PHONE_CONSTRAINTS, ()
            -> ParserUtil.parseStudentPhone(longPhone));
    }

    @Test
    public void parseStudentPhone_validValue_returnsNormalizedPhone() throws Exception {
        assertEquals(new Phone("91234567"), ParserUtil.parseStudentPhone(" +65 9123-4567 "));
        assertEquals(new Phone("+447700900123"), ParserUtil.parseStudentPhone("+44 7700-900123"));
    }

    @Test
    public void parseAddress_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseAddress((String) null));
    }

    @Test
    public void parseAddress_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseAddress(INVALID_ADDRESS));
    }

    @Test
    public void parseAddress_validValueWithoutWhitespace_returnsAddress() throws Exception {
        Address expectedAddress = new Address(VALID_ADDRESS);
        assertEquals(expectedAddress, ParserUtil.parseAddress(VALID_ADDRESS));
    }

    @Test
    public void parseAddress_validValueWithWhitespace_returnsTrimmedAddress() throws Exception {
        String addressWithWhitespace = WHITESPACE + VALID_ADDRESS + WHITESPACE;
        Address expectedAddress = new Address(VALID_ADDRESS);
        assertEquals(expectedAddress, ParserUtil.parseAddress(addressWithWhitespace));
    }

    @Test
    public void parseEmail_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseEmail((String) null));
    }

    @Test
    public void parseEmail_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseEmail(INVALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithoutWhitespace_returnsEmail() throws Exception {
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(VALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithWhitespace_returnsTrimmedEmail() throws Exception {
        String emailWithWhitespace = WHITESPACE + VALID_EMAIL + WHITESPACE;
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(emailWithWhitespace));
    }

    @Test
    public void parseTag_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTag(null));
    }

    @Test
    public void parseTag_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTag(INVALID_TAG));
    }

    @Test
    public void parseTag_validValueWithoutWhitespace_returnsTag() throws Exception {
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(VALID_TAG_1));
    }

    @Test
    public void parseTag_validValueWithWhitespace_returnsTrimmedTag() throws Exception {
        String tagWithWhitespace = WHITESPACE + VALID_TAG_1 + WHITESPACE;
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(tagWithWhitespace));
    }

    @Test
    public void parseTags_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTags(null));
    }

    @Test
    public void parseTags_collectionWithInvalidTags_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTags(List.of(VALID_TAG_1, INVALID_TAG)));
    }

    @Test
    public void parseTags_emptyCollection_returnsEmptySet() throws Exception {
        assertTrue(ParserUtil.parseTags(List.of()).isEmpty());
    }

    @Test
    public void parseTags_collectionWithValidTags_returnsTagSet() throws Exception {
        Set<Tag> actualTagSet = ParserUtil.parseTags(List.of(VALID_TAG_1, VALID_TAG_2));
        Set<Tag> expectedTagSet = Set.of(new Tag(VALID_TAG_1), new Tag(VALID_TAG_2));

        assertEquals(expectedTagSet, actualTagSet);
    }

    @Test
    public void parseDay_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseDay(null));
    }

    @Test
    public void parseDay_threeLetterAbbreviation_returnsDay() throws Exception {
        assertEquals(DayOfWeek.MONDAY, ParserUtil.parseDay("mon"));
        assertEquals(DayOfWeek.TUESDAY, ParserUtil.parseDay("tue"));
        assertEquals(DayOfWeek.WEDNESDAY, ParserUtil.parseDay("wed"));
        assertEquals(DayOfWeek.THURSDAY, ParserUtil.parseDay("thu"));
        assertEquals(DayOfWeek.FRIDAY, ParserUtil.parseDay("fri"));
        assertEquals(DayOfWeek.SATURDAY, ParserUtil.parseDay("sat"));
        assertEquals(DayOfWeek.SUNDAY, ParserUtil.parseDay("sun"));
    }

    @Test
    public void parseDay_anyLetterCase_returnsDay() throws Exception {
        for (String day : List.of("WED", "Wed", "wEd", "weD")) {
            assertEquals(DayOfWeek.WEDNESDAY, ParserUtil.parseDay(day));
        }
    }

    @Test
    public void parseDay_validValueWithWhitespace_returnsDay() throws Exception {
        assertEquals(DayOfWeek.SUNDAY, ParserUtil.parseDay(WHITESPACE + "sun" + WHITESPACE));
    }

    @Test
    public void parseDay_turkishDefaultLocale_returnsDay() throws Exception {
        // in Turkish, the upper case of "i" is not "I" and the lower case of "I" is not "i"
        Locale originalLocale = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag("tr-TR"));
        try {
            for (String day : List.of("fri", "FRI", "Fri")) {
                assertEquals(DayOfWeek.FRIDAY, ParserUtil.parseDay(day));
            }
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    public void parseDay_fullName_returnsDay() throws Exception {
        // users often type the full name, and blocking it adds nothing
        assertEquals(DayOfWeek.MONDAY, ParserUtil.parseDay("monday"));
        assertEquals(DayOfWeek.TUESDAY, ParserUtil.parseDay("tuesday"));
        assertEquals(DayOfWeek.WEDNESDAY, ParserUtil.parseDay("wednesday"));
        assertEquals(DayOfWeek.THURSDAY, ParserUtil.parseDay("thursday"));
        assertEquals(DayOfWeek.FRIDAY, ParserUtil.parseDay("friday"));
        assertEquals(DayOfWeek.SATURDAY, ParserUtil.parseDay("saturday"));
        assertEquals(DayOfWeek.SUNDAY, ParserUtil.parseDay("sunday"));
        for (String day : List.of("Monday", "MONDAY", "mOnDaY")) {
            assertEquals(DayOfWeek.MONDAY, ParserUtil.parseDay(day));
        }
    }

    @Test
    public void parseDay_weds_returnsWednesday() throws Exception {
        // "weds" is as common as "tues" and "thurs", but is not the start of "wednesday"
        assertEquals(DayOfWeek.WEDNESDAY, ParserUtil.parseDay("weds"));
        assertEquals(DayOfWeek.WEDNESDAY, ParserUtil.parseDay("Weds"));
        assertEquals(DayOfWeek.WEDNESDAY, ParserUtil.parseDay(" WEDS "));
    }

    @Test
    public void parseDay_longerStartOfName_returnsDay() throws Exception {
        // any start of the name with at least three letters, such as the common "tues" and "thurs"
        assertEquals(DayOfWeek.MONDAY, ParserUtil.parseDay("mond"));
        assertEquals(DayOfWeek.TUESDAY, ParserUtil.parseDay("tues"));
        assertEquals(DayOfWeek.WEDNESDAY, ParserUtil.parseDay("wedn"));
        assertEquals(DayOfWeek.THURSDAY, ParserUtil.parseDay("thur"));
        assertEquals(DayOfWeek.THURSDAY, ParserUtil.parseDay("Thurs"));
        assertEquals(DayOfWeek.FRIDAY, ParserUtil.parseDay("frida"));
        assertEquals(DayOfWeek.SATURDAY, ParserUtil.parseDay("satur"));
        assertEquals(DayOfWeek.SUNDAY, ParserUtil.parseDay("SUNDA"));
    }

    @Test
    public void parseDay_invalidValue_throwsParseException() {
        List<String> invalidDays = List.of(
                // empty, too short, or not the start of a day's name
                "", WHITESPACE, "m", "mo", "sa", "mons", "mondays", "monday1", "wends", "tuesdays",
                // day numbers
                "0", "1", "7",
                // more than one day
                "mon tue", "mon,tue", "mon-fri", "mon/tue",
                // punctuation, or spaces inside a day
                "mon.", "m o n",
                // three characters that are not a day; the last is "Monday" in Chinese, written as escapes
                "day", "all", "\u661F\u671F\u4E00",
                // look-alike letters that Java treats as "i" and "s" when ignoring case: dotless i and long s
                "fr\u0131", "\u017Fun");

        for (String day : invalidDays) {
            assertThrows(ParseException.class, MESSAGE_INVALID_DAY, () -> ParserUtil.parseDay(day));
        }
    }

    @Test
    public void parseTime_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTime(null));
    }

    @Test
    public void parseTime_validValue_returnsTime() throws Exception {
        // boundaries of a day
        assertEquals(LocalTime.of(0, 0), ParserUtil.parseTime("0000"));
        assertEquals(LocalTime.of(0, 1), ParserUtil.parseTime("0001"));
        assertEquals(LocalTime.of(23, 59), ParserUtil.parseTime("2359"));

        // boundaries of an hour
        assertEquals(LocalTime.of(15, 59), ParserUtil.parseTime("1559"));
        assertEquals(LocalTime.of(16, 0), ParserUtil.parseTime("1600"));

        // leading zeros
        assertEquals(LocalTime.of(9, 5), ParserUtil.parseTime("0905"));
        assertEquals(LocalTime.of(12, 0), ParserUtil.parseTime("1200"));
    }

    @Test
    public void parseTime_withColon_returnsTime() throws Exception {
        // the lesson cards show times as HH:mm, so users may type them that way too
        assertEquals(LocalTime.of(16, 0), ParserUtil.parseTime("16:00"));
        assertEquals(LocalTime.of(0, 0), ParserUtil.parseTime("00:00"));
        assertEquals(LocalTime.of(23, 59), ParserUtil.parseTime("23:59"));
        assertEquals(LocalTime.of(9, 30), ParserUtil.parseTime("09:30"));

        // with a colon, the hour can be a single digit
        assertEquals(LocalTime.of(9, 30), ParserUtil.parseTime("9:30"));
        assertEquals(LocalTime.of(0, 5), ParserUtil.parseTime("0:05"));
        assertEquals(LocalTime.of(16, 0), ParserUtil.parseTime(WHITESPACE + "16:00" + WHITESPACE));
    }

    @Test
    public void parseTime_invalidWithColon_throwsParseException() {
        List<String> invalidTimes = List.of("24:00", "16:60", "29:00", "16:0", "16:000", "016:00", ":1600",
                "1600:", "16:", ":00", "16::00", "16:00:00", "9:5", "-9:00", "+9:00", "16 :00", "16: 00",
                "１６:００");

        for (String time : invalidTimes) {
            assertThrows(ParseException.class, MESSAGE_INVALID_TIME, () -> ParserUtil.parseTime(time));
        }
    }

    @Test
    public void parseTime_validValueWithWhitespace_returnsTime() throws Exception {
        assertEquals(LocalTime.of(16, 0), ParserUtil.parseTime(WHITESPACE + "1600" + WHITESPACE));
    }

    @Test
    public void parseTime_wrongLength_throwsParseException() {
        for (String time : List.of("", WHITESPACE, "9", "16", "900", "160", "16000", "001600")) {
            assertThrows(ParseException.class, MESSAGE_INVALID_TIME, () -> ParserUtil.parseTime(time));
        }
    }

    @Test
    public void parseTime_hourOrMinuteOutOfRange_throwsParseException() {
        // "2400" is the Developer Guide's example of an invalid time
        for (String time : List.of("2400", "2401", "2500", "9999", "0060", "1660", "1699")) {
            assertThrows(ParseException.class, MESSAGE_INVALID_TIME, () -> ParserUtil.parseTime(time));
        }
    }

    @Test
    public void parseTime_otherTimeFormats_throwsParseException() {
        // "4pm" is the Developer Guide's example of an invalid time
        List<String> invalidTimes = List.of("4pm", "4:00pm", "0400pm", "16.00", "16-00", "16 00",
                "16h00", "1600h", "1600hrs", "noon", "abcd");

        for (String time : invalidTimes) {
            assertThrows(ParseException.class, MESSAGE_INVALID_TIME, () -> ParserUtil.parseTime(time));
        }
    }

    @Test
    public void parseTime_fourCharactersThatAreNotAllDigits_throwsParseException() {
        // each of these has four characters, and some are numbers to Integer#parseInt
        for (String time : List.of("-100", "+100", "1e03", "0x10", "16.0", "1 00", "100a")) {
            assertThrows(ParseException.class, MESSAGE_INVALID_TIME, () -> ParserUtil.parseTime(time));
        }
    }

    @Test
    public void parseTime_nonAsciiDigits_throwsParseException() {
        // "1600" in full-width digits and in Arabic-Indic digits, written as escapes
        for (String time : List.of("\uFF11\uFF16\uFF10\uFF10", "\u0661\u0666\u0660\u0660")) {
            assertThrows(ParseException.class, MESSAGE_INVALID_TIME, () -> ParserUtil.parseTime(time));
        }
    }
}
