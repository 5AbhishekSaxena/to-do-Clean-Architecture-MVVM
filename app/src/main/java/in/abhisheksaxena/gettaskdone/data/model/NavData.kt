package `in`.abhisheksaxena.gettaskdone.data.model

import android.os.Message
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 * @author Abhishek Saxena
 * @since 30-06-2020 17:17
 */


interface MESSAGE {
    companion object {
        const val ADD_TASK_OK = 1
        const val UPDATE_TASK_OK = 2
        const val DELETE_TASK_OK = 3
    }
}

