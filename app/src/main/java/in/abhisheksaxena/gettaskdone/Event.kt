package `in`.abhisheksaxena.gettaskdone

import androidx.lifecycle.Observer


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

class SnackBarEvent<out T>(private val content: T) : Event<T>(content) {
    var hasAction = false
    var actionText = ""
    var action: () -> Unit = {}


}

class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.getContentIfNotHandled()?.let {
            onEventUnhandledContent(it)
        }
    }

}