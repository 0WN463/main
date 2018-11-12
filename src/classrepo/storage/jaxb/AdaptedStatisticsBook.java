package classrepo.storage.jaxb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import classrepo.data.StatisticsBook;
import classrepo.data.exception.IllegalValueException;
import classrepo.data.person.AssignmentStatistics;
import classrepo.data.person.UniqueStatisticsList;


/**
 * JAXB-friendly adapted statistics book data holder class.
 */
@XmlRootElement(name = "StatisticsBook")
public class AdaptedStatisticsBook {

    @XmlElement
    private List<AdaptedStatistics> statisticsList = new ArrayList<>();

    /**
     * No-arg constructor for JAXB use.
     */
    public AdaptedStatisticsBook() {}

    /**
     * Converts a given StatisticsBook into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created AdaptedStatisticsBook
     */
    public AdaptedStatisticsBook(StatisticsBook source) {
        statisticsList = new ArrayList<>();
        source.getAllStatistics().forEach(statistics -> statisticsList.add(new AdaptedStatistics(statistics)));
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
        return statisticsList.stream().anyMatch(AdaptedStatistics::isAnyRequiredFieldMissing);
    }


    /**
     * Converts this jaxb-friendly {@code AdaptedStatisticsBook} object into the corresponding(@code StatisticsBook}
     * object.
     * @throws IllegalValueException if there were any data constraints violated in the adapted statistics
     */
    public StatisticsBook toModelType() throws IllegalValueException {
        final List<AssignmentStatistics> statisticsTempList = new ArrayList<>();
        for (AdaptedStatistics exam : statisticsList) {
            statisticsTempList.add(exam.toModelType());
        }
        return new StatisticsBook(new UniqueStatisticsList(statisticsTempList));
    }
}
