package classrepo.logic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import classrepo.data.person.Exam;

/**
 * For further testing of methods in Exam class
 */
public class ExamTest {
    @Test
    public void isValidTime() {
        assertFalse(Exam.isValidTime("0132/2018"));
        assertFalse(Exam.isValidTime("25:00"));
        assertFalse(Exam.isValidTime("2500"));
        assertFalse(Exam.isValidTime("notATime"));
    }

    @Test
    public void isValidTimeInterval() {
        assertFalse(Exam.isValidTimeInterval("2300", "1000"));
        assertFalse(Exam.isValidTimeInterval("23:00", "10:00"));
        assertFalse(Exam.isValidTimeInterval("10:00", "10:00"));
        assertTrue(Exam.isValidTimeInterval("10:00", "10:01"));
    }

}
