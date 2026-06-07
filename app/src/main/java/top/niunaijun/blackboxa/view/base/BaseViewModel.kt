package top.niunaijun.blackboxa.view.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

/**
 * Base ViewModel providing coroutine launching utilities for all ViewModels in the application.
 *
 * Executes coroutine blocks on the IO dispatcher within the [viewModelScope] and handles
 * exceptions by printing the stack trace. Cancels the scope when the ViewModel is cleared.
 */
open class BaseViewModel : ViewModel() {

    /**
     * Launches the given suspend block on the IO dispatcher within the viewModelScope.
     *
     * Exceptions thrown during execution are caught and printed to stderr.
     *
     * @param block the suspend lambda to execute on the IO dispatcher.
     */
    fun launchOnUI(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    block()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }

            }
        }
    }


    /**
     * Cancels the viewModelScope when the ViewModel is being destroyed.
     * Ensures all running coroutines are properly cleaned up.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

}