package classrepo.privilege.user;

import java.util.Arrays;
import java.util.List;

import classrepo.commands.Command;
import classrepo.commands.assessment.AddAssessmentCommand;
import classrepo.commands.assessment.AddAssignmentStatistics;
import classrepo.commands.assessment.AddGradesCommand;
import classrepo.commands.assessment.DeleteAssessmentCommand;
import classrepo.commands.assessment.DeleteGradesCommand;
import classrepo.commands.assessment.DeleteStatisticsCommand;
import classrepo.commands.assessment.ViewGradesCommand;
import classrepo.commands.attendance.ReplaceAttendanceCommand;
import classrepo.commands.attendance.UpdateAttendanceCommand;
import classrepo.commands.attendance.ViewAttendanceDateCommand;
import classrepo.commands.attendance.ViewAttendancePersonCommand;
import classrepo.commands.exams.DeregisterExamCommand;
import classrepo.commands.exams.ListExamsCommand;
import classrepo.commands.exams.RegisterExamCommand;
import classrepo.commands.fees.ListDueFeesCommand;
import classrepo.commands.fees.ListFeesCommand;
import classrepo.commands.fees.ViewFeesCommand;
import classrepo.commands.person.ListAllCommand;
import classrepo.commands.person.ViewAllCommand;

/**
 * Represents a user with Tutor access
 * */
public class TutorUser extends BasicUser {
    private static List<Command> newAllowedCommand = Arrays.asList(
            new ViewAllCommand(),
            new ListAllCommand(),
            new ListExamsCommand(),
            new RegisterExamCommand(),
            new DeregisterExamCommand(),
            new UpdateAttendanceCommand(),
            new ReplaceAttendanceCommand(),
            new ViewAttendancePersonCommand(),
            new ViewAttendanceDateCommand(),
            new ListFeesCommand(),
            new ListDueFeesCommand(),
            new AddAssessmentCommand(),
            new DeleteAssessmentCommand(),
            new AddGradesCommand(),
            new ViewGradesCommand(),
            new DeleteGradesCommand(),
            new ViewFeesCommand(),
            new AddAssignmentStatistics(),
            new DeleteStatisticsCommand()
    );

    public TutorUser() {
        super();
        addAllowedCommands(newAllowedCommand);
        setCurrentLevel(PrivilegeLevel.Tutor);
        sortCommands();
    }
}
