package classrepo.storage.jaxb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import classrepo.common.Utils;
import classrepo.data.exception.IllegalValueException;
import classrepo.data.person.Attendance;

/**
 * JAXB-friendly adapted attendance data holder class.
 */
@XmlRootElement(name = "attendance")
public class AdaptedAttendance {

    @XmlElement
    private List<String> attendanceDate = new ArrayList<>();

    @XmlElement
    private List<String> attendanceIsPresent = new ArrayList<>();

    /**
     * No-arg constructor for JAXB use.
     */
    public AdaptedAttendance() {}

    /**
     * Converts a given attendance into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created AdaptedTag
     */
    public AdaptedAttendance(Attendance source) {
        for (Map.Entry entry: source.getAttendancePersonMap().entrySet()) {
            if (entry.getValue().equals(true)) {
                attendanceIsPresent.add("Present");
            } else if (entry.getValue().equals(false)) {
                attendanceIsPresent.add("Absent");
            }
        }
        attendanceDate = new ArrayList<>(source.getAttendancePersonMap().keySet());
    }

    /**
     * Returns true if any required field is missing.
     *
     * JAXB does not enforce (required = true) without a given XML schema.
     * Since we do most of our validation using the data class constructors, the only extra logic we need
     * is to ensure that every xml element in the document is present. JAXB sets missing elements as null,
     * so we check for that.
     */
    public boolean isAnyRequiredFieldMissing() {
        return Utils.isAnyNull(attendanceDate, attendanceIsPresent);
    }

    /**
     * Converts this jaxb-friendly adapted attendance object into the Attendance object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person
     */
    public Attendance toModelType() throws IllegalValueException {
        final Attendance attendance = new Attendance();
        for (int i = 0; i < attendanceDate.size(); i++) {
            String date = attendanceDate.get(i);
            String isPresentString = attendanceIsPresent.get(i);
            final boolean isPresent;
            if ("Present".equals(isPresentString)) {
                isPresent = true;
            } else if ("Absent".equals(isPresentString)) {
                isPresent = false;
            } else {
                throw new IllegalValueException("NIL attendance, please report this to the developers");
            }
            attendance.addAttendance(date, isPresent, false);
        }
        return attendance;
    }
}


