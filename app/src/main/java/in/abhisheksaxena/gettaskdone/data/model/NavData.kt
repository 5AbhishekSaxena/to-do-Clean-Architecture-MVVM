package `in`.abhisheksaxena.gettaskdone.data.model

import `in`.abhisheksaxena.gettaskdone.viewmodel.AddTaskState
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 * @author Abhishek Saxena
 * @since 30-06-2020 17:17
 */

@Parcelize
data class NavData(var state: AddTaskState = AddTaskState.NEW_TASK_STATE, var id: Long = -1L) :
    Parcelable