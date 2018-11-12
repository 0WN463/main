package classrepo.storage;

import classrepo.data.AddressBook;
import classrepo.data.ExamBook;
import classrepo.data.StatisticsBook;


/**
 *  This is the abstract class in charge of Storage.
 *  Used to apply DI
 * */
public abstract class Storage {
    /**
     * Signals that some error has occurred while trying to convert and read/write data between the application
     * and the storage file.
     */
    public static class StorageOperationException extends Exception {
        public StorageOperationException(String message) {
            super(message);
        }
    }

    public abstract void save(AddressBook addressBook) throws StorageOperationException;
    public abstract void saveStatistics(StatisticsBook statisticsBook) throws StorageOperationException;

    public abstract AddressBook load() throws StorageFile.StorageOperationException;
    public abstract StatisticsBook loadStatistics() throws StorageFile.StorageOperationException;

    public abstract String getPath();
    public abstract String getPathStatistics();

    public abstract void saveExam(ExamBook examBook) throws StorageOperationException;

    public abstract ExamBook loadExam() throws StorageFile.StorageOperationException;
    public abstract String getPathExam();

    public abstract void syncAddressBookExamBook(AddressBook addressBook, ExamBook examBook)
            throws StorageOperationException;
}
