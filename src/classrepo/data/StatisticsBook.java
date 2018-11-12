package classrepo.data;

import classrepo.data.person.AssignmentStatistics;
import classrepo.data.person.UniqueStatisticsList;

/**
 * Represents the entire statistics book. Contains the data of the statistics book.
 */
public class StatisticsBook {

    private final UniqueStatisticsList statisticsList;

    /**
     * Creates an empty statistics book.
     */
    public StatisticsBook() {
        statisticsList = new UniqueStatisticsList();
    }

    /**
     * Constructs a statistics book with the given data.
     *
     * @param statisticsList external changes to this will not affect this statistic book
     */
    public StatisticsBook(UniqueStatisticsList statisticsList) {
        this.statisticsList = new UniqueStatisticsList(statisticsList);
    }

    public static StatisticsBook empty() {
        return new StatisticsBook();
    }

    /**
     * Adds a statistic to the statistics book.
     *
     * @throws UniqueStatisticsList.DuplicateStatisticsException if an equivalent statistic already exists.
     */
    public void addStatistic(AssignmentStatistics toAdd) throws UniqueStatisticsList.DuplicateStatisticsException {
        statisticsList.add(toAdd);
    }

    /**
     * Checks if an equivalent statistic exists in the statistics book.
     */
    public boolean containsStatistic(AssignmentStatistics key) {
        return statisticsList.contains(key);
    }

    /**
     * Removes the equivalent statistic from the statistic book.
     *
     * @throws UniqueStatisticsList.StatisticsNotFoundException if no such statistic could be found.
     */
    public void removeStatistic(AssignmentStatistics toRemove) throws UniqueStatisticsList.StatisticsNotFoundException {
        statisticsList.remove(toRemove);
    }

    /**
     * Defensively copied UniqueStatisticsList of all statistics in the statistics book at the time of the call.
     */
    public UniqueStatisticsList getAllStatistics() {
        return new UniqueStatisticsList(statisticsList);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof StatisticsBook // instanceof handles nulls
                && this.statisticsList.equals(((StatisticsBook) other).statisticsList));
    }

    @Override
    public int hashCode() {
        return statisticsList.hashCode();
    }

    /**
     * Clears all statistics from the statistics book.
     */
    public void clear() {
        statisticsList.clear();
    }
}
