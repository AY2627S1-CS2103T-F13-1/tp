package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LESSON;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index must be a positive integer.";
    public static final String MESSAGE_INVALID_DAY =
            "Day must be a day's name or at least its first 3 letters, e.g. mon, tues or friday.";
    public static final String MESSAGE_INVALID_TIME =
            "Times must be 24-hour HHmm or HH:mm, from 0000 to 2359, e.g. 1600 or 16:00.";

    private static final int MIN_DAY_PREFIX_LENGTH = 3;
    // a common abbreviation that is not the start of the day's name
    private static final String WEDNESDAY_ABBREVIATION = "weds";
    private static final Pattern TIME_WITHOUT_COLON = Pattern.compile("(?<hour>[01][0-9]|2[0-3])(?<minute>[0-5][0-9])");
    // with a colon, the hour may drop its leading zero, as in 9:30
    private static final Pattern TIME_WITH_COLON = Pattern.compile("(?<hour>[01]?[0-9]|2[0-3]):(?<minute>[0-5][0-9])");

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not a non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses {@code args} of the form {@code s/STUDENT_INDEX l/LESSON_INDEX} and passes the two indices to
     * {@code commandFactory} to create the command.
     *
     * @param messageUsage the usage message of the command, shown if {@code args} is invalid.
     * @param commandFactory creates the command using the student index first and the lesson index second.
     * @throws ParseException if a prefix is missing or repeated, there is text before the first prefix,
     *     or either index is not a positive integer.
     */
    public static <T> T parseStudentAndLessonIndices(String args, String messageUsage,
            BiFunction<Index, Index, T> commandFactory) throws ParseException {
        ArgumentMultimap values = ArgumentTokenizer.tokenize(args, PREFIX_STUDENT, PREFIX_LESSON);
        values.verifyNoDuplicatePrefixesFor(PREFIX_STUDENT, PREFIX_LESSON);
        if (!values.getPreamble().isEmpty() || values.getValue(PREFIX_STUDENT).isEmpty()
                || values.getValue(PREFIX_LESSON).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, messageUsage));
        }
        try {
            return commandFactory.apply(parseIndex(values.getValue(PREFIX_STUDENT).get()),
                    parseIndex(values.getValue(PREFIX_LESSON).get()));
        } catch (ParseException e) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, messageUsage), e);
        }
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed. Spaces and hyphens within the phone number will be removed,
     * and supported country codes will be normalized.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a student or guardian {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed. Accepted formatting will be removed, and a Singapore
     * country code will be normalized to the corresponding local number.
     *
     * @throws ParseException if the given {@code phone} is invalid for a student or guardian.
     */
    public static Phone parseStudentPhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidStudentPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_STUDENT_PHONE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String address} into an {@code Address}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code address} is invalid.
     */
    public static Address parseAddress(String address) throws ParseException {
        requireNonNull(address);
        String trimmedAddress = address.trim();
        if (!Address.isValidAddress(trimmedAddress)) {
            throw new ParseException(Address.MESSAGE_CONSTRAINTS);
        }
        return new Address(trimmedAddress);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }

    /**
     * Parses a {@code String day} into a {@code DayOfWeek}. The day may be its full name or any start of it with
     * at least three letters, such as {@code mon}, {@code tues} or {@code friday}, or {@code weds}, in any letter
     * case. Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code day} is invalid.
     */
    public static DayOfWeek parseDay(String day) throws ParseException {
        requireNonNull(day);
        String normalizedDay = day.trim().toLowerCase(Locale.ROOT);
        if (normalizedDay.equals(WEDNESDAY_ABBREVIATION)) {
            return DayOfWeek.WEDNESDAY;
        }
        if (normalizedDay.length() >= MIN_DAY_PREFIX_LENGTH) {
            for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
                // the first three letters of each day differ, so at most one day can match
                if (dayOfWeek.name().toLowerCase(Locale.ROOT).startsWith(normalizedDay)) {
                    return dayOfWeek;
                }
            }
        }
        throw new ParseException(MESSAGE_INVALID_DAY);
    }

    /**
     * Parses a {@code String time} in the 24-hour format {@code HHmm} or {@code HH:mm} into a {@code LocalTime}.
     * With a colon, the hour may be a single digit, as in {@code 9:30}. Leading and trailing whitespaces will be
     * trimmed.
     *
     * @throws ParseException if the given {@code time} is invalid.
     */
    public static LocalTime parseTime(String time) throws ParseException {
        requireNonNull(time);
        String trimmedTime = time.trim();
        for (Pattern format : List.of(TIME_WITHOUT_COLON, TIME_WITH_COLON)) {
            Matcher matcher = format.matcher(trimmedTime);
            if (matcher.matches()) {
                return LocalTime.of(Integer.parseInt(matcher.group("hour")), Integer.parseInt(matcher.group("minute")));
            }
        }
        throw new ParseException(MESSAGE_INVALID_TIME);
    }
}
