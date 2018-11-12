package classrepo.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import classrepo.data.person.Person;

/**
 * Utility methods
 */
public class Utils {
    private static final String DATE_PATTERN = "dd-MM-yyyy";

    /**
     * Checks whether any of the given items are null.
     */
    public static boolean isAnyNull(Object... items) {
        for (Object item : items) {
            if (item == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if every element in a collection are unique by {@link Object#equals(Object)}.
     */
    public static boolean elementsAreUnique(Collection<?> items) {
        final List<Object> testSet = new ArrayList<>();
        for (Object item: items) {
            if (objectIsInList(testSet, item)) {
                return false;
            }

            testSet.add(item);
        }
        return true;
    }
    /** Checks if an object is exists in the list*/
    private static boolean objectIsInList (List<Object> list, Object object) {
        return (!list.stream()
                .filter(p -> (p == null && object == null)
                        || (p != null && p.equals(object)))
                .collect(Collectors.toList())
                .isEmpty());
    }

    /**
     * Checks if a given string is a valid date.
     * Solution below adapted from https://stackoverflow.com/a/30578421
     */
    public static boolean isValidDate(String value) {
        boolean isValid;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        try {
            String parsedDate = LocalDate.parse(value, formatter).format(formatter);
            isValid = value.equals(parsedDate);
        } catch (DateTimeParseException ex) {
            isValid = false;
        }
        return isValid;
    }

    /**
     * Custom comparator for Date string in Fees in the form of DD-MM-YYYY
     * Allows for sorting of Person's list according to YYYYMMDD of Fees section.
     */
    public static class FeesComparator implements Comparator<Person> {
        @Override
        public int compare(Person p1, Person p2) {
            String person1 = convertToValidDateStringUser(p1.getFees().duedate);
            String person2 = convertToValidDateStringUser(p2.getFees().duedate);
            return person1.compareTo
                    (person2);
        }
    }

    /**
     * Date Converter to use for comparisons during Fees command
     * Such as sorting and if dates have passed relative to User's current date.
     */
    public static String convertToValidDateStringUser (String value) {
        StringBuilder result = new StringBuilder();
        result.append(value, 6, 10);
        result.append(value, 3, 5);
        result.append(value, 0, 2);
        return result.toString();
    }

    /**
     * Date Converter for system date to String for comparison
     */
    public static String convertToValidDateStringSystem (String value) {
        StringBuilder result = new StringBuilder();
        result.append(value, 0, 4);
        result.append(value, 5, 7);
        result.append(value, 8, 10);
        return result.toString();
    }
}
