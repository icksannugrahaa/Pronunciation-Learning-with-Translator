package com.ninepm.english.learn.utils

import android.util.Log
import kotlinx.coroutines.*

class TimerService {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private fun startCoroutineTimer(delayMillis: Long = 0, repeatMillis: Long = 0, action: () -> Unit) = scope.launch(Dispatchers.IO) {
        delay(delayMillis)
        if (repeatMillis > 0) {
            while (true) {
                action()
                delay(repeatMillis)
            }
        } else {
            timer(action, repeatMillis).cancel()
        }
    }

    private fun timer(action: () -> Unit, time: Long) : Job = startCoroutineTimer(delayMillis = 0, repeatMillis = 3000) {
        Log.d("TIMER_TAG", "Background - tick")
//        doSomethingBackground()
//        action()
        scope.launch(Dispatchers.Main) {
            Log.d("TIMER_TAG", "Main thread - tick")
//            doSomethingMainThread()
            action()
        }
    }

    fun startTimer(action: () -> Unit, time: Long) {
        timer(action, time).start()
    }

    fun cancelTimer(action: () -> Unit, time: Long) {
        timer(action, time).start()
    }

}