package classrepo.commands.exams;

import java.util.List;
import java.util.Map;

import classrepo.commands.commandformat.indexformat.IndexFormatCommand;
import classrepo.commands.commandformat.indexformat.ObjectTargeted;
import classrepo.commands.commandresult.CommandResult;
import classrepo.commands.commandresult.ListType;
import classrepo.common.Messages;
import classrepo.data.exception.IllegalValueException;
import classrepo.data.person.Exam;
import classrepo.data.person.ReadOnlyExam;
import classrepo.data.person.UniqueExamList;
import classrepo.parser.ExamField;

/**
 * Edits an exam identified using its last displayed index in the exam book.
 */
public class EditExamCommand extends IndexFormatCommand {

    public static final String COMMAND_WORD = "editexam";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Edits the exam identified by the index number in the last exam listing. "
            + "Each exam field is optional but order must be followed.\n\t"
            + "Parameters: INDEX [p/PRIVATE_STATUS] [e/EXAM_NAME] [s/SUBJECT_NAME] "
            + "[d/EXAM_DATE] [st/EXAM_START_TIME] "
            + "[et/EXAM_END_TIME] [dt/EXAM_DETAILS]\n\t"
            + "PRIVATE_STATUS is denoted by \"y\" or \"n\".\n\t"
            + "Example: " + COMMAND_WORD + " 1 e/Math Mid Terms 2018 d/07-06-2018";

    public static final String MESSAGE_EDIT_EXAM_SUCCESS = "Edited %1$s\n to %2$s";
    public static final String MESSAGE_NO_ARGS_FOUND = "No arguments found!\n";
    public static final String MESSAGE_DUPLICATE_EXAM = "This exam already exists in the exam book!";
    public static final String MESSAGE_PRIVATE_CONSTRAINTS =
            "PRIVATE_STATUS is denoted by \"y\" or \"n\" only.";
    private static final String PRIVATE_WORD = "y";
    private static final String PUBLIC_WORD = "n";

    private Map<ExamField, String> changedDetails;

    /**
     * Constructor used for Privileges
     * Command constructed has no functionality
     * */
    public EditExamCommand() {
    }

    /**
     * Constructor for edit exam Command
     * @param targetExamIndex is the index number of the exam to have its details changed
     * @param changedDetails contains the details to be changed
     * @throws IllegalValueException if there are no details to be changed
     */
    public EditExamCommand(int targetExamIndex, Map<ExamField, String> changedDetails) throws IllegalValueException {
        if (changedDetails.isEmpty()) {
            throw new IllegalValueException(MESSAGE_NO_ARGS_FOUND + MESSAGE_USAGE);
        }
        setTargetIndex(targetExamIndex, ObjectTargeted.EXAM);
        this.changedDetails = changedDetails;
    }

    @Override
    public CommandResult execute() {
        try {
            final ReadOnlyExam target = getTargetReadOnlyExam();
            Exam initial = new Exam(target);
            Exam editedExam = createEditedExam(initial, changedDetails);
            examBook.editExam(target, editedExam);
            addressBook.updateExam(initial, editedExam);
            final List<ReadOnlyExam> updatedList = examBook.getAllExam().immutableListView();
            return new CommandResult(String.format(MESSAGE_EDIT_EXAM_SUCCESS, target,
                        editedExam), updatedList, ListType.EXAMS);
        } catch (ExamIndexOutOfBoundsException eie) {
            return new CommandResult(Messages.MESSAGE_INVALID_EXAM_DISPLAYED_INDEX);
        } catch (UniqueExamList.ExamNotFoundException enfe) {
            return new CommandResult(Messages.MESSAGE_EXAM_NOT_IN_EXAMBOOK);
        } catch (UniqueExamList.DuplicateExamException dee) {
            return new CommandResult(MESSAGE_DUPLICATE_EXAM);
        } catch (IllegalValueException ive) {
            return new CommandResult(ive.getMessage());
        }
    }

    @Override
    public boolean isMutating() {
        return true;
    }

    @Override
    public boolean isExamMutating() {
        return true;
    }

    @Override
    public Category getCategory() {
        return Category.EXAM;
    }

    @Override
    public String getCommandUsageMessage() {
        return MESSAGE_USAGE;
    }

    /**
     * Creates a new exam using the details provided to the initial exam.
     * @param exam is the exam to have its details changed
     * @param changedDetails contains the details to be changed, not empty
     * @return the edited exam
     * @throws IllegalValueException if there are any errors in the fields
     */
    private Exam createEditedExam(Exam exam, Map<ExamField, String>changedDetails) throws IllegalValueException {
        String examName = exam.getExamName();
        String subjectName = exam.getSubjectName();
        String examDate = exam.getExamDate();
        String examStartTime = exam.getExamStartTime();
        String examEndTime = exam.getExamEndTime();
        String examDetails = exam.getExamDetails();
        int takers = exam.getTakers();
        boolean isPrivate = exam.isPrivate();

        for (Map.Entry<ExamField, String> entry : changedDetails.entrySet()) {
            ExamField attribute = entry.getKey();
            String newValue = entry.getValue();
            if (ExamField.examName.equals(attribute)) {
                examName = newValue.trim();
            } else if (ExamField.subjectName.equals(attribute)) {
                subjectName = newValue.trim();
            } else if (ExamField.examDate.equals(attribute)) {
                examDate = newValue.trim();
            } else if (ExamField.examStartTime.equals(attribute)) {
                examStartTime = newValue.trim();
            } else if (ExamField.examEndTime.equals(attribute)) {
                examEndTime = newValue.trim();
            } else if (ExamField.examDetails.equals(attribute)) {
                examDetails = newValue.trim();
            } else if (ExamField.isPrivate.equals(attribute)) {
                if (isPrivateValid(newValue.trim())) {
                    isPrivate = PRIVATE_WORD.equals(newValue.trim());
                } else {
                    throw new IllegalValueException(MESSAGE_PRIVATE_CONSTRAINTS);
                }
            }
        }

        return new Exam(examName, subjectName, examDate, examStartTime, examEndTime, examDetails, takers, isPrivate);
    }

    /**
     * Checks if the string to edit private status is valid.
     */
    private static boolean isPrivateValid(String value) {
        boolean isValid = true;
        if (value != null && !PRIVATE_WORD.equals(value) && !PUBLIC_WORD.equals(value)) {
            isValid = false;
        }
        return isValid;
    }
}
