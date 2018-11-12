//@@author meisbokai
package classrepo.data.person;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a person's attendance in the address book.
 */

public class Attendance implements Printable {

    /** Represents a map for each person, showing which every attendance for each date*/
    private Map<String, Boolean> attendancePersonMap = new HashMap<>();

    /** Method to add attendance*/
    public boolean addAttendance(String date, Boolean isPresent, Boolean overWrite) {
        String inputDate = date;
        if ("0".equals(date)) {
            inputDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        }
        // return true if duplicate date
        if (attendancePersonMap.containsKey(inputDate) && overWrite) {
            attendancePersonMap.put(inputDate, isPresent);
            return true;
        } else if (attendancePersonMap.containsKey(inputDate) && !overWrite) {
            return true;
        } else if (!attendancePersonMap.containsKey(inputDate) && overWrite) {
            return false;
        } else {
            attendancePersonMap.put(inputDate, isPresent);
            return false;
        }
    }

    /** Method to reiterate person's attendance */
    public String viewAttendance() {
        String output = "Date \t\t Attendance\n";
        String attendance = "Absent";
        for (Map.Entry entry : attendancePersonMap.entrySet()) {
            if (entry.getValue().equals(true)) {
                attendance = "Present";
            }
            output += entry.getKey() + "\t\t" + attendance + "\n";
        }
        if ("Date \t\t Attendance\n".equals(output)) {
            output += "NIL\t\t\t NIL";
        }
        return output;
    }

    /** Method to get attendance of a particular date **/
    public Boolean viewAttendanceDate(String date) {
        String lookForDate = date;
        if ("0".equals(date)) {
            lookForDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        }
        Boolean isPresent = attendancePersonMap.get(lookForDate);
        if (null == isPresent) {
            return false;
        } else {
            return isPresent;
        }
    }

    /** Accessor Method **/
    public Map<String, Boolean> getAttendancePersonMap() {
        return this.attendancePersonMap;
    }

    @Override
    public String getPrintableString(boolean showPrivate) {
        return "{}";
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Attendance // instanceof handles nulls
                && this.attendancePersonMap.equals(((Attendance) other).attendancePersonMap)); // state check
    }

    public boolean isPrivate() {
        return true;
    }

}

