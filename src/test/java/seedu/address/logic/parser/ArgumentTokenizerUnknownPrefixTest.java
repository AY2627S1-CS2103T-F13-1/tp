package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.parser.exceptions.ParseException;

public class ArgumentTokenizerUnknownPrefixTest {
    private static final String COMMAND_WORD = "findstudent";
    private static final Prefix[] KNOWN_PREFIXES = {PREFIX_NAME, PREFIX_GUARDIAN_NAME, PREFIX_PHONE};

    @Test
    public void verifyNoUnknownPrefixes_onlyKnownPrefixes_doesNotThrow() {
        assertNoUnknownPrefix(" n/Aiden gn/Tan p/9123");
        assertNoUnknownPrefix("n/Aiden");
        assertNoUnknownPrefix("");
        assertNoUnknownPrefix(" Aiden");
    }

    @Test
    public void verifyNoUnknownPrefixes_slashInsideWord_doesNotThrow() {
        // a slash right after a prefix, or inside a word, is part of the value and is checked by the value's rules
        assertNoUnknownPrefix(" n/Math/Science");
        assertNoUnknownPrefix(" n/Aiden 1/2");
        assertNoUnknownPrefix(" p/9123/4567");
    }

    @Test
    public void verifyNoUnknownPrefixes_unknownPrefixInValue_throwsParseException() {
        // the tokenizer reads a prefix it was not given as part of the value before it
        assertUnknownPrefix(" n/Aiden gp/91234567", "gp/");
        assertUnknownPrefix(" n/Aiden p/9123 l/1", "l/");
        assertUnknownPrefix(" p/9123 et/1800", "et/");
    }

    @Test
    public void verifyNoUnknownPrefixes_unknownPrefixInPreamble_throwsParseException() {
        assertUnknownPrefix(" gp/91234567", "gp/");
        assertUnknownPrefix("gp/91234567", "gp/");
        assertUnknownPrefix(" Aiden gp/91234567", "gp/");

        // prefixes are case-sensitive, so an upper-case prefix is unknown
        assertUnknownPrefix(" N/Aiden", "N/");
    }

    @Test
    public void verifyNoUnknownPrefixes_severalUnknownPrefixes_namesTheFirst() {
        assertUnknownPrefix(" t/friend n/Aiden gp/9123", "t/");
        assertUnknownPrefix(" n/Aiden gp/9123 t/friend", "gp/");
    }

    @Test
    public void verifyNoUnknownPrefixes_knownPrefixAfterOtherWhitespace_doesNotThrow() {
        // the tokenizer only splits at a space before a prefix, but a known prefix is never reported as unknown
        assertNoUnknownPrefix(" n/Aiden\tgn/Tan");
    }

    @Test
    public void getErrorMessageForUnknownPrefix_listsKnownPrefixes() {
        assertEquals("findstudent takes only n/, gn/ and p/, not gp/.",
                Messages.getErrorMessageForUnknownPrefix(COMMAND_WORD, "gp/", KNOWN_PREFIXES));
        assertEquals("listlessons takes only d/, not n/.",
                Messages.getErrorMessageForUnknownPrefix("listlessons", "n/", PREFIX_DAY));
        assertEquals("findstudent takes only n/ and p/, not gp/.",
                Messages.getErrorMessageForUnknownPrefix(COMMAND_WORD, "gp/", PREFIX_NAME, PREFIX_PHONE));
    }

    private static void assertNoUnknownPrefix(String args) {
        assertDoesNotThrow(() -> ArgumentTokenizer.verifyNoUnknownPrefixes(args, COMMAND_WORD, KNOWN_PREFIXES));
    }

    private static void assertUnknownPrefix(String args, String unknownPrefix) {
        assertThrows(ParseException.class,
                Messages.getErrorMessageForUnknownPrefix(COMMAND_WORD, unknownPrefix, KNOWN_PREFIXES), () ->
                        ArgumentTokenizer.verifyNoUnknownPrefixes(args, COMMAND_WORD, KNOWN_PREFIXES));
    }
}
