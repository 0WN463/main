package classrepo.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import classrepo.data.AddressBook;
import classrepo.data.ExamBook;
import classrepo.data.StatisticsBook;
import classrepo.data.exception.IllegalValueException;
import classrepo.data.person.Exam;
import classrepo.data.person.Person;
import classrepo.storage.jaxb.AdaptedAddressBook;
import classrepo.storage.jaxb.AdaptedExamBook;
import classrepo.storage.jaxb.AdaptedStatisticsBook;

/**
 * Represents the file used to store all the data.
 */
public class StorageFile extends Storage {

    /** Default file path used if the user doesn't provide the file name. */
    public static final String DEFAULT_STORAGE_FILEPATH = "addressbook.txt";

    /** Default exam file path used if the user doesn't provide the file name. */
    public static final String DEFAULT_EXAMS_FILEPATH = "exams.txt";

    /** Default statistics file path used if the user doesn't provide the file name. */
    public static final String DEFAULT_STATISTICS_FILEPATH = "statistics.txt";

    public final Path path;
    public final Path pathExam;
    public final Path pathStatistics;

    private final JAXBContext jaxbContext;
    private final JAXBContext jaxbContext2;
    private final JAXBContext jaxbContext3;

    /* Note: Note the use of nested classes below.
     * More info https://docs.oracle.com/javase/tutorial/java/javaOO/nested.html
     */

    /**
     * Signals that the given file path does not fulfill the storage filepath constraints.
     */
    public static class InvalidStorageFilePathException extends IllegalValueException {
        public InvalidStorageFilePathException(String message) {
            super(message);
        }
    }

    /**
     * Signals that there is some jaxb initialisation error.
     */
    public static class InvalidInitialisationException extends IllegalValueException {
        public InvalidInitialisationException(String message) {
            super(message);
        }
    }

    /**
     * @throws InvalidStorageFilePathException if the default path is invalid
     */
    public StorageFile() throws InvalidStorageFilePathException,
            InvalidInitialisationException {
        this(DEFAULT_STORAGE_FILEPATH, DEFAULT_EXAMS_FILEPATH, DEFAULT_STATISTICS_FILEPATH);
    }

    /**
     * @throws InvalidStorageFilePathException if the given file path is invalid
     */
    public StorageFile(String filePath, String filePathExam, String filePathStatistics)
            throws InvalidStorageFilePathException,
            InvalidInitialisationException {
        try {
            jaxbContext = JAXBContext.newInstance(AdaptedAddressBook.class);
            jaxbContext2 = JAXBContext.newInstance(AdaptedExamBook.class);
            jaxbContext3 = JAXBContext.newInstance(AdaptedStatisticsBook.class);
        } catch (JAXBException jaxbe) {
            throw new InvalidInitialisationException("jaxb initialisation error");
        }

        path = Paths.get(filePath);
        if (!isValidPath(path)) {
            throw new InvalidStorageFilePathException("Storage file should end with '.txt'");
        }
        pathExam = Paths.get(filePathExam);
        if (!isValidPath(pathExam)) {
            throw new InvalidStorageFilePathException("Exam file should end with '.txt'");
        }
        pathStatistics = Paths.get(filePathStatistics);
        if (!isValidPath(pathStatistics)) {
            throw new InvalidStorageFilePathException("Statistics file should end with '.txt'");
        }
    }

    /**
     * Returns true if the given path is acceptable as a storage file.
     * The file path is considered acceptable if it ends with '.txt'
     */
    private static boolean isValidPath(Path filePath) {
        return filePath.toString().endsWith(".txt");
    }

    /**
     * Saves all data to this storage file.
     *
     * @throws StorageOperationException if there were errors converting and/or storing data to file.
     */
    public void save(AddressBook addressBook) throws StorageOperationException {

        /* Note: Note the 'try with resource' statement below.
         * More info: https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
         */
        try (final Writer fileWriter = new BufferedWriter(new FileWriter(path.toFile()))) {
            final AdaptedAddressBook toSave = new AdaptedAddressBook(addressBook);
            final Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(toSave, fileWriter);
        } catch (IOException ioe) {
            throw new StorageOperationException("Error writing to file: " + path + " error: " + ioe.getMessage());
        } catch (JAXBException jaxbe) {
            throw new StorageOperationException("Error converting address book into storage format");
        }
    }

    /**
     * Loads data from this storage file.
     *
     * @throws StorageOperationException if there were errors reading and/or converting data from file.
     */
    public AddressBook load() throws StorageOperationException {
        try (final Reader fileReader = new BufferedReader(new FileReader(path.toFile()))) {
            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            final AdaptedAddressBook loaded = (AdaptedAddressBook) unmarshaller.unmarshal(fileReader);
            // manual check for missing elements
            if (loaded.isAnyRequiredFieldMissing()) {
                throw new StorageOperationException("File data missing some elements");
            }
            return loaded.toModelType();

            /* Note: Here, we are using an exception to create the file if it is missing. However, we should minimize
             * using exceptions to facilitate normal paths of execution. If we consider the missing file as a 'normal'
             * situation (i.e. not truly exceptional) we should not use an exception to handle it.
             */

            // create empty file if not found
        } catch (FileNotFoundException fnfe) {
            final AddressBook empty = new AddressBook();
            save(empty);
            return empty;

            // other errors
        } catch (IOException ioe) {
            throw new StorageOperationException("Error writing to file: " + path);
        } catch (JAXBException jaxbe) {
            throw new StorageOperationException("Error parsing file data format");
        } catch (IllegalValueException ive) {
            throw new StorageOperationException(ive.getMessage());
        }
    }

    public String getPath() {
        return path.toString();
    }

    public String getPathExam() {
        return pathExam.toString();
    }

    public String getPathStatistics() {
        return pathStatistics.toString();
    }

    /**
     * Saves all data to this storage file.
     *
     * @throws StorageOperationException if there were errors converting and/or storing data to file.
     */
    public void saveExam(ExamBook examBook) throws StorageOperationException {

        /* Note: Note the 'try with resource' statement below.
         * More info: https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
         */
        try (final Writer fileWriter = new BufferedWriter(new FileWriter(pathExam.toFile()))) {
            final AdaptedExamBook toSave = new AdaptedExamBook(examBook);
            final Marshaller marshaller = jaxbContext2.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(toSave, fileWriter);
        } catch (IOException ioe) {
            throw new StorageOperationException("Error writing to exam file: "
                    + pathExam + " error: " + ioe.getMessage());
        } catch (JAXBException jaxbe) {
            throw new StorageOperationException("Error converting exam book into storage format");
        }
    }

    /**
     * Loads data from this storage file.
     *
     * @throws StorageOperationException if there were errors reading and/or converting data from file.
     */
    public ExamBook loadExam() throws StorageOperationException {
        try (final Reader fileReader = new BufferedReader(new FileReader(pathExam.toFile()))) {
            final Unmarshaller unmarshaller = jaxbContext2.createUnmarshaller();
            final AdaptedExamBook loaded = (AdaptedExamBook) unmarshaller.unmarshal(fileReader);
            // manual check for missing elements
            if (loaded.isAnyRequiredFieldMissing()) {
                throw new StorageOperationException("Exam file data missing some elements");
            }
            return loaded.toModelType();

            /* Note: Here, we are using an exception to create the file if it is missing. However, we should minimize
             * using exceptions to facilitate normal paths of execution. If we consider the missing file as a 'normal'
             * situation (i.e. not truly exceptional) we should not use an exception to handle it.
             */

            // create empty file if not found
        } catch (FileNotFoundException fnfe) {
            final ExamBook empty = new ExamBook();
            saveExam(empty);
            return empty;

            // other errors
        } catch (IOException ioe) {
            throw new StorageOperationException("Error writing to exam file: " + pathExam);
        } catch (JAXBException jaxbe) {
            throw new StorageOperationException("Error parsing exam file data format");
        } catch (IllegalValueException ive) {
            throw new StorageOperationException(ive.getMessage());
        }
    }

    /**
     * Saves all data to this storage file.
     *
     * @throws StorageOperationException if there were errors converting and/or storing data to file.
     */
    public void saveStatistics(StatisticsBook statisticsBook) throws StorageOperationException {

        /* Note: Note the 'try with resource' statement below.
         * More info: https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
         */
        try (final Writer fileWriter = new BufferedWriter(new FileWriter(pathStatistics.toFile()))) {
            final AdaptedStatisticsBook toSave = new AdaptedStatisticsBook(statisticsBook);
            final Marshaller marshaller = jaxbContext3.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(toSave, fileWriter);
        } catch (IOException ioe) {
            throw new StorageOperationException("Error writing to statistics file: "
                    + pathStatistics + " error: " + ioe.getMessage());
        } catch (JAXBException jaxbe) {
            throw new StorageOperationException("Error converting statistics book into storage format");
        }
    }

    /**
     * Loads data from this storage file.
     *
     * @throws StorageOperationException if there were errors reading and/or converting data from file.
     */
    public StatisticsBook loadStatistics() throws StorageOperationException {
        try (final Reader fileReader = new BufferedReader(new FileReader(pathStatistics.toFile()))) {
            final Unmarshaller unmarshaller = jaxbContext3.createUnmarshaller();
            final AdaptedStatisticsBook loaded = (AdaptedStatisticsBook) unmarshaller.unmarshal(fileReader);
            // manual check for missing elements
            if (loaded.isAnyRequiredFieldMissing()) {
                throw new StorageOperationException("Statistics file data missing some elements");
            }
            return loaded.toModelType();

            /* Note: Here, we are using an exception to create the file if it is missing. However, we should minimize
             * using exceptions to facilitate normal paths of execution. If we consider the missing file as a 'normal'
             * situation (i.e. not truly exceptional) we should not use an exception to handle it.
             */

            // create empty file if not found
        } catch (FileNotFoundException fnfe) {
            final StatisticsBook empty = new StatisticsBook();
            saveStatistics(empty);
            return empty;

            // other errors
        } catch (IOException ioe) {
            throw new StorageOperationException("Error writing to statistics file: " + pathStatistics);
        } catch (JAXBException jaxbe) {
            throw new StorageOperationException("Error parsing statistics file data format");
        } catch (IllegalValueException ive) {
            throw new StorageOperationException(ive.getMessage());
        }
    }

    /**
     * Checks the exams of each person to see if they exist in the ExamBook and
     * checks if the takers for all exams in ExamBook are all accounted for in the AddressBook
     * @throws StorageOperationException if exam data of a person was modified
     */
    public void syncAddressBookExamBook(AddressBook addressBook, ExamBook examBook) throws StorageOperationException {
        for (Person p : addressBook.getAllPersons()) {
            for (Exam e : p.getExams()) {
                if (!examBook.contains(e)) {
                    throw new StorageOperationException("A person has an exam not in ExamBook!");
                }
            }
        }

        for (Exam e: examBook.getAllExam()) {
            int totalCount = e.getTakers();
            int currentCount = 0;
            for (Person p: addressBook.getAllPersons()) {
                if (p.isExamPresent(e)) {
                    currentCount++;
                }
            }
            if (totalCount != currentCount) {
                throw new StorageOperationException("An exam has inaccurate number of takers compared to AddressBook!");
            }
        }
    }
}
