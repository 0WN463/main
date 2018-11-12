package classrepo.ui;

import static classrepo.common.Messages.MESSAGE_PROGRAM_LAUNCH_ARGS_USAGE;
import static classrepo.common.Messages.MESSAGE_USING_EXAMS_FILE;
import static classrepo.common.Messages.MESSAGE_USING_STATISTICS_FILE;
import static classrepo.common.Messages.MESSAGE_USING_STORAGE_FILE;
import static classrepo.common.Messages.MESSAGE_WELCOME_ASCII_ART;

import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import classrepo.Main;
import classrepo.commands.commandresult.CommandResult;
import classrepo.commands.general.ExitCommand;
import classrepo.data.person.Assessment;
import classrepo.data.person.AssignmentStatistics;
import classrepo.data.person.ReadOnlyExam;
import classrepo.data.person.ReadOnlyPerson;
import classrepo.formatter.Formatter;
import classrepo.formatter.PersonListFormat;
import classrepo.logic.Logic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Main Window of the GUI.
 */
public class MainWindow {

    private Logic logic;
    private Stoppable mainApp;

    @FXML
    private TextField commandInput;

    @FXML
    private TextArea outputConsole;

    @FXML
    private TextArea asciiArt;

    @FXML
    private TextArea statusConsole;

    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    public void setMainApp(Stoppable mainApp) {
        this.mainApp = mainApp;
    }

    /** Returns true of the result given is the result of an exit command */
    private boolean isExitCommand(CommandResult result) {
        return result.getStatusConsoleMessage().equals(ExitCommand.MESSAGE_EXIT_ACKNOWLEDGEMENT);
    }

    /** Clears the command input box */
    private void clearCommandInput() {
        commandInput.setText("");
    }

    /** Clears the output display area */
    private void clearOutputConsole() {
        outputConsole.clear();
    }

    /** Clears the status display area */
    private void clearStatusConsole() {
        statusConsole.clear();
    }

    /** Displays the result of a command execution to the user. */
    private void displayResult(CommandResult result) {
        clearOutputConsole();
        closeAsciiArt();
        final Optional<List<? extends ReadOnlyPerson>> optResultPersons = result.getRelevantPersons();
        optResultPersons.ifPresent((p) -> display(p, result.getPersonListFormat()));

        final Optional<List<? extends ReadOnlyExam>> optResultExams = result.getRelevantExams();
        optResultExams.ifPresent(this::displayExams);

        final Optional<List<? extends Assessment>> optResultAssessment = result.getRelevantAssessments();
        optResultAssessment.ifPresent(this::displayAssessments);

        final Optional<List<? extends AssignmentStatistics>> optStatisticsList = result.getRelevantStatistics();
        optStatisticsList.ifPresent(this::displayStatistics);

        display(result.getOutputConsoleMessage());
    }

    /** Displays the welcome message**/
    public void displayWelcomeMessage(String version, String storageFilePath, String examsFilePath,
                                      String statisticsFilePath) {
        String storageFileInfo = String.format(MESSAGE_USING_STORAGE_FILE, storageFilePath);
        String examsFileInfo = String.format(MESSAGE_USING_EXAMS_FILE, examsFilePath);
        String statisticsFileInfo = String.format(MESSAGE_USING_STATISTICS_FILE, statisticsFilePath);
        displayAscii(MESSAGE_WELCOME_ASCII_ART);
        display(version, MESSAGE_PROGRAM_LAUNCH_ARGS_USAGE, storageFileInfo, examsFileInfo,
                statisticsFileInfo);
    }

    /**
     * Displays the list of persons in the output display area, formatted as an indexed list.
     */
    private void display(List<? extends ReadOnlyPerson> persons, PersonListFormat personListFormat) {
        display(Formatter.format(persons, personListFormat));
    }

    /**
     * Displays the given messages on the output display area, after formatting appropriately.
     */
    private void display(String... messages) {
        outputConsole.setText(outputConsole.getText() + Formatter.format(messages));
    }

    /**
     * Displays the given messages on the ASCII art area.
     */
    private void displayAscii(String messages) {
        asciiArt.setText(messages);
    }

    /**
     * Displays the list of exams in the output display area, formatted as an indexed list.
     */
    private void displayExams(List<? extends ReadOnlyExam> exams) {
        display(Formatter.formatExam(exams));
    }

    /**
     * Displays the given messages on the status display area, after formatting appropriately.
     */
    private void displayStatus(String message) {
        clearStatusConsole();
        statusConsole.setText(message);
    }

    /**
     * Displays the given messages from the result of a command on the respective display areas,
     * after formatting appropriately.
     */
    private void handleDisplay(CommandResult result) {
        clearStatusConsole();
        clearCommandInput();
        if (result.hasStatusMessage()) {
            displayStatus(result.getStatusConsoleMessage());
        }
        if (result.hasOutputMessage()) {
            displayResult(result);
        }
    }

    /**
     * Displays the list of assessments in the output display area, formatted as an indexed list.
     */
    private void displayAssessments(List<? extends Assessment> assessments) {
        display(Formatter.formatAssessments(assessments));
    }

    /**
     * Displays the list of assessments in the output display area, formatted as an indexed list.
     */
    private void displayStatistics(List<? extends AssignmentStatistics> statistics) {
        display(Formatter.formatStatistics(statistics));
    }

    /**
     * Removes the AsciiArt.
     */
    private void closeAsciiArt() {
        asciiArt.setText("");
        asciiArt.setManaged(false);
    }

    /** Reads the user command on the CLI **/
    @FXML
    void onCommand(ActionEvent event) {
        try {
            String userCommandText = commandInput.getText();
            CommandResult result = logic.execute(userCommandText);
            handleDisplay(result);
            if (isExitCommand(result)) {
                clearOutputConsole();
                exitApp();
            }
        } catch (Exception e) {
            displayStatus(e.getMessage() + "\nExiting App...");
            Main.LOGGER.log(Level.WARNING, e.getMessage());
            // If this error occurs, data between the app and storage is likely to be desynced,
            // so we force close the app to ensure synchronisation.
            final int delayTime = 3000;
            exitApp(delayTime);
        }
    }

    /** Exits the app after a set delay*/
    private void exitApp() {
        final int defaultDelayTime = 500;
        exitApp(defaultDelayTime);
    }

    /** Exits the app after a given delay*/
    private void exitApp(int delay) {
        TimerTask task = new TimerTask() {
            public void run() {
                try {
                    mainApp.stop();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        Timer timer = new Timer("Timer");
        timer.schedule(task, delay);
    }
}
