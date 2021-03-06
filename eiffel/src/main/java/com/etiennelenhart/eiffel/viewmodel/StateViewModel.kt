package com.etiennelenhart.eiffel.viewmodel

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.etiennelenhart.eiffel.state.ViewState

/**
 * A [ViewModel] with an observable view state.
 *
 * To ensure an immutable state, always set a new state using the
 * [updateState] function and calling copy(...) on the provided current state.
 *
 * @param[T] Type of associated view state.
 * @property[state] Observable view state.
 * @property[stateInitialized] `true` when an initial view state has been set.
 * @property[currentState] Current value of the view state.
 * Throws [KotlinNullPointerException] when the state's initial value has not been set.
 */
abstract class StateViewModel<T : ViewState> : ViewModel() {

    protected abstract val state: MutableLiveData<T>

    protected val stateInitialized
        get() = state.value != null
    protected val currentState
        get() = state.value!!

    /**
     * Sets the initial view state if not already initialized.
     *
     * @param[viewState] Initial view state.
     */
    protected fun initState(viewState: T) {
        if (!stateInitialized) state.value = viewState
    }

    /**
     * Updates the current state by applying the supplied lambda expression.
     *
     * May be used like this:
     *
     * ```
     * updateState { it.copy(sample = true) }
     * ```
     *
     * *Note: States updated with `post = true` could be emitted after later ones updated without `post`. So [currentState] may have unexpected values.*
     *
     * @param[post] If set to `true`, the updated state is posted to the main thread using [LiveData.postValue], defaults to `false`.
     * @param[update] Lambda expression that receives the current state and should return a new updated state.
     * @throws[KotlinNullPointerException] when the state's initial value has not been set.
     */
    protected inline fun updateState(post: Boolean = false, update: (currentState: T) -> T) {
        val updatedState = update(currentState)
        if (post) state.postValue(updatedState) else state.value = updatedState
    }

    /**
     * Used to observe this [StateViewModel]'s view state from a [LifecycleOwner] like [FragmentActivity] or [Fragment].
     *
     * @param[owner] [LifecycleOwner] that controls observation.
     * @param[onChanged] Lambda expression that is called with a newly emitted state.
     */
    fun observeState(owner: LifecycleOwner, onChanged: (newState: T) -> Unit) {
        state.observe(owner, Observer { onChanged(it!!) })
    }
}
