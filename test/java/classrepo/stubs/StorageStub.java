package classrepo.stubs;

import classrepo.data.AddressBook;
import classrepo.data.ExamBook;
import classrepo.data.StatisticsBook;
import classrepo.storage.Storage;

/**
 * Class used to apply DI
 * */
public class StorageStub extends Storage {
    private String path;
    private String pathExam;
    private String pathStatistics;
    private boolean hasSaved = false;
    private boolean hasSavedExam = false;

    public StorageStub(String filePath, String filePathExam, String filePathStatistics) {
        path = filePath;
        pathExam = filePathExam;
        pathStatistics = filePathStatistics;
    }

    /**Stub function*/
    public void save(AddressBook addressBook) {
        hasSaved = true;
    }

    /**Stub function*/
    public void saveExam(ExamBook examBook) {
        hasSavedExam = true;
    }

    /**Stub function*/
    public void saveStatistics(StatisticsBook statisticsBook){
        //this is blank on purpose
    }

    /**Stub function*/
    public void syncAddressBookExamBook(AddressBook addressBook, ExamBook examBook){
        //this is blank on purpose
    }

    public boolean getHasSaved() {
        return hasSaved;
    }

    public boolean getHasSavedExams() {
        return hasSavedExam;
    }

    public AddressBook load() {
        return new AddressBook();
    }
    public String getPath() {
        return path;
    }

    public ExamBook loadExam() {
        return new ExamBook();
    }
    public String getPathExam() {
        return pathExam;
    }

    public StatisticsBook loadStatistics() {
        return new StatisticsBook();
    }
    public String getPathStatistics() {
        return pathStatistics;
    }
}
