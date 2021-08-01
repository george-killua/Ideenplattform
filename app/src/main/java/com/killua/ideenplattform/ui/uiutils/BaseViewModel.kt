package com.killua.ideenplattform.ui.uiutils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.killua.ideenplattform.ui.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/***
 * Prams SD : stateDataBinding
 *       E  :  Effect
 *       S  : error handler usw
 */
abstract class BaseViewModel<SD, E,S> : ViewModel() {
     val stateDataBinding: SingleLiveEvent<SD> by lazy { SingleLiveEvent() }
    val viewEffect: SingleLiveEvent<E> by lazy { SingleLiveEvent() }
     val state: MutableLiveData<S> by lazy { MutableLiveData() }


    val getStateDataBinding:SingleLiveEvent<SD> =stateDataBinding
    val getViewEffects: SingleLiveEvent<E> = viewEffect
    val getState: LiveData<S> = state


    protected fun launchCoroutine(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
             block()
        }
    }

    protected fun getState(): S? = state.value

}
