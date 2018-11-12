package classrepo;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import classrepo.logic.Logic;
import classrepo.ui.Gui;
import classrepo.ui.Stoppable;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 *  Main entry point to the application.
 */
public class Main extends Application implements Stoppable {

    public static final Logger LOGGER = Logger.getLogger("Foo");

    /** Version info of the program. */
    private static final String VERSION = "ClassRepo - Version 1.4";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            setUpLogger();
            Gui gui = new Gui(new Logic(), VERSION);
            gui.start(primaryStage, this);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, e.getMessage());
            throw e;
        }
    }

    private void setUpLogger() throws IOException {
        try {
            SimpleFormatter formatter = new SimpleFormatter();
            FileHandler fileHandler = new FileHandler("log.txt");
            fileHandler.setFormatter(formatter);
            LOGGER.addHandler(fileHandler);
        } catch (IOException ioe) {
            throw new IOException("Error accessing log.txt");
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Platform.exit();
        System.exit(0);
    }
}


