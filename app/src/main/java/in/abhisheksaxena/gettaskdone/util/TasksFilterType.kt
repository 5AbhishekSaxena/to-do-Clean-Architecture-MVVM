package `in`.abhisheksaxena.gettaskdone.util


/**
 * @author Abhishek Saxena
 * @since 07-07-2020 14:38
 */

/**
 * Used with the filter spinner in the tasks list.
 */
enum class TasksFilterType {
    /**
     * Do not filter tasks.
     */
    ALL_TASKS,

    /**
     * Filters only the active (not completed yet) tasks.
     */
    ACTIVE_TASKS,

    /**
     * Filters only the completed tasks.
     */
    COMPLETED_TASKS
}