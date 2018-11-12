package classrepo.data.person;

import static classrepo.common.Messages.MESSAGE_DATE_CONSTRAINTS;

import classrepo.data.exception.IllegalValueException;

/**
 * Represents a person's fees due in the address book.
 * Guarantees: immutable;
 */
public class Fees implements Printable {
    public static final String EXAMPLE = "$3560.98";
    public static final String MESSAGE_FEES_CONSTRAINTS = "Person's fees should be positive and have 2 decimal places.";
    public static final String FEES_VALIDATION_REGEX = "[0-9]+([,.][0-9]{1,2})";
    public static final String DATE_VALIDATION_REGEX = "[0-9]{1,2}-[0-9]{1,2}-[0-9]{4}";

    public final String value;
    public final String duedate;
    private boolean isEdited = false;

    /**
     * Validates given fees.
     *
     * @throws IllegalValueException if given fees string is invalid.
     */
    public Fees(String fees, String date) throws IllegalValueException {
        if (!isValidFees(fees)) {
            throw new IllegalValueException(MESSAGE_FEES_CONSTRAINTS);
        }
        if (!isValidDate(date)) {
            throw new IllegalValueException(MESSAGE_DATE_CONSTRAINTS);
        }
        this.value = fees;
        this.duedate = date;
    }

    /**
     * Initialises all fees of everyone to 0.
     *
     */
    public Fees() {
        this.value = "0.00";
        this.duedate = "00-00-0000";
    }

    /**
     * Checks if a given string is a valid person fee.
     *
     */

    public static boolean isValidFees(String test) {
        return test.matches(FEES_VALIDATION_REGEX);
    }

    /**
     * Checks if a given string is a valid date.
     *
     */
    public static boolean isValidDate(String test) {
        return test.matches(DATE_VALIDATION_REGEX);
    }

    public boolean isPrivate() {
        return true;
    }

    public boolean isEdited() {
        return isEdited; }

    /**
     * Changes the value of isEdited, whenever a fee is set accordingly.
     * @param val
     */
    public void getEdited(boolean val) {
        isEdited = val;
    }
    @Override
    public String getPrintableString(boolean showPrivate) {
        if (isPrivate()) {
            if (showPrivate) {
                return "{private Fees: " + value + " / " + duedate + "} ";
            } else {
                return "";
            }
        }
        return "Fees: " + value + " " + duedate;
    }
}
