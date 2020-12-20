package tech.developingdeveloper.gettaskdone.util

import android.app.Activity
import android.view.View


/**
 * @author Abhishek Saxena
 * @since 04-07-2020 12:29
 */

class HideSoftKeyboardOnFocusChange(
    private val activity: Activity,
    private val isAlwaysDisabled: Boolean = false
) : View.OnFocusChangeListener {
    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (isAlwaysDisabled)
            hideKeyboard(activity)
        else if (!hasFocus) {
            hideKeyboard(activity)
        }
    }

}