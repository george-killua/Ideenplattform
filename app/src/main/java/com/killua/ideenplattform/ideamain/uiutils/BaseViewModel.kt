package com.killua.ideenplattform.ideamain.uiutils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext

/***
 *
 * @param SD : stateDataBinding
 * @param E  :  Effect
 * @param S  : error handler usw
 *
 *
 */
open class BaseViewModel<SD, E, S>(instanceOfState: S,instanceOfStateDataBinding: SD) : ViewModel() {
    private val workedThread = newSingleThreadContext("hisoka")


    val stateDataBinding = MutableStateFlow(instanceOfStateDataBinding)
    private val viewEffect = Channel<E>(Channel.BUFFERED)
    protected val state = MutableStateFlow(instanceOfState)


   val getStateDataBinding: MutableStateFlow<SD> = stateDataBinding
    val getViewEffects: Flow<E> = viewEffect.receiveAsFlow()
    val getState: MutableStateFlow<S> = state

    protected fun getStateDataBinding(): SD =stateDataBinding.value
    internal fun reducerDB(sender: SD.() -> SD) {
        viewModelScope.launch(workedThread) {
            stateDataBinding.value = stateDataBinding.value.sender()
        }
    }

   protected fun postEffect(effect: E)= viewModelScope.launch(workedThread) {
            viewEffect.send(effect)
        }


    protected fun getState(): S? = state.value
    protected fun reducer(sender: S.() -> S) {
        viewModelScope.launch(workedThread) {
            state.value = state.value.sender()
        }
    }
    protected fun launchCoroutine(block: suspend BaseViewModel<SD, E, S>.() -> Unit): Job {
        return viewModelScope.launch(workedThread) {
            block()

        }
    }

}
