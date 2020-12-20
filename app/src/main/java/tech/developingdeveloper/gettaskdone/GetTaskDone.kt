package tech.developingdeveloper.gettaskdone

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp


/**
 * @author Abhishek Saxena
 * @since 30-07-2020 03:23
 */

@HiltAndroidApp
class GetTaskDone : Application() {

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}