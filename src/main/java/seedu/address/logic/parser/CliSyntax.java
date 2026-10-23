package seedu.address.logic.parser;

import java.util.List;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {

    /* Prefix definitions */
    public static final Prefix PREFIX_STUDENT = new Prefix("s/");
    public static final Prefix PREFIX_LESSON = new Prefix("l/");
    public static final Prefix PREFIX_NAME = new Prefix("n/");
    public static final Prefix PREFIX_PHONE = new Prefix("p/");
    public static final Prefix PREFIX_EMAIL = new Prefix("e/");
    public static final Prefix PREFIX_ADDRESS = new Prefix("a/");
    public static final Prefix PREFIX_TAG = new Prefix("t/");
    public static final Prefix PREFIX_GUARDIAN_NAME = new Prefix("gn/");
    public static final Prefix PREFIX_GUARDIAN_PHONE = new Prefix("gp/");
    public static final Prefix PREFIX_DAY = new Prefix("d/");
    public static final Prefix PREFIX_START_TIME = new Prefix("st/");
    public static final Prefix PREFIX_END_TIME = new Prefix("et/");

    /** Prefixes taken by TutorReach's commands. */
    public static final List<Prefix> TUTORREACH_PREFIXES = List.of(PREFIX_STUDENT, PREFIX_LESSON, PREFIX_NAME,
            PREFIX_PHONE, PREFIX_GUARDIAN_NAME, PREFIX_GUARDIAN_PHONE, PREFIX_DAY, PREFIX_START_TIME, PREFIX_END_TIME);

}
