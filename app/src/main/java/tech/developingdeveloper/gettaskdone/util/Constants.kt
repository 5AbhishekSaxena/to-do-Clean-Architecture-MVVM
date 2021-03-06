package tech.developingdeveloper.gettaskdone.util


/**
 * @author Abhishek Saxena
 * @since 03-07-2020 13:56
 */

class Constants {
    interface MESSAGE {
        companion object {
            const val ADD_TASK_OK = 1
            const val UPDATE_TASK_OK = 2
            const val UPDATE_TASK_NOT_OK = 3
            const val DELETE_TASK_OK = 4
            const val ERROR_LOADING_TASK = 21
        }
    }

    companion object {
        val TITLE_CHARACTER_LIMIT = 75
    }
}