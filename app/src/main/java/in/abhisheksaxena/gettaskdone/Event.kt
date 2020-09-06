package `in`.abhisheksaxena.gettaskdone

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.Observer
import `in`.abhisheksaxena.gettaskdone.Event


/**
 * @author Abhishek Saxena
 * @since 02-07-2020 11:55
 */

open class Event<out T>(private val content: T) {

    var intExtras: List<Int> = emptyList()

    fun containsIntExtras(): Boolean = intExtras.isNotEmpty()

    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}

class SnackBarEvent<out T>(private val content: T, val action: () -> Unit = {}): Event<T>(content){
    var hasAction = false
    var actionText = ""


}

class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.getContentIfNotHandled()?.let {
            onEventUnhandledContent(it)
        }
    }

}