package classrepo.data.person;

import java.util.Objects;

import classrepo.data.exception.IllegalValueException;


/**
 * Represents the grades or marks of a person
 */
public class Grades implements Printable {

    public static final String GRADE_EXAMPLE = "27";
    public static final String MESSAGE_GRADES_CONSTRAINTS = "Person's grades can only be a positive value (>= 0).";

    private double value;

    /**
     * Validates given grades.
     * */
    public Grades(double grade) throws IllegalValueException {
        if (!isValidGrades(grade)) {
            throw new IllegalValueException(MESSAGE_GRADES_CONSTRAINTS);
        }
        this.value = grade;
    }

    /**
     * Checks if a given value is a valid grade.
     */

    public static boolean isValidGrades(Double test) {
        return (test >= 0);
    }

    @Override
    public String toString() {
        return " Grade: " + value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Grades // instanceof handles nulls
                && this.value == (((Grades) other).value)); // state check
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(value);
    }

    @Override
    public String getPrintableString(boolean showPrivate) {
        return "Grade: " + value;
    }

    public double getValue() {
        return value;
    }

}
