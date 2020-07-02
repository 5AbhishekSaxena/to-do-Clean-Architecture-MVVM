package `in`.abhisheksaxena.gettaskdone

import androidx.lifecycle.Observer


/**
 * @author Abhishek Saxena
 * @since 02-07-2020 09:25
 */

class Event<out T>(private val content: T){
    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T?{
        return if (hasBeenHandled){
            null
        }else{
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}

class EventObserver<T>(private val onEventUnHandledContent: (T) -> Unit): Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.getContentIfNotHandled()?.let{
            onEventUnHandledContent(it)
        }
    }


}
