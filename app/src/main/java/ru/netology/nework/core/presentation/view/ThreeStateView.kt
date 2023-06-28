package ru.netology.nework.core.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.StateFlow
import ru.netology.nework.R
import ru.netology.nework.core.utils.observeWhenOnCreated
import ru.netology.nework.databinding.ThreeStateViewBinding

class ThreeStateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: ThreeStateViewBinding

    init {
        val inflater = LayoutInflater.from(context)
        binding = ThreeStateViewBinding.inflate(inflater, this, true)
        this.isVisible = false
        setAttrs(attrs)
    }

    private fun setAttrs(attrs: AttributeSet?) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.ThreeStateView
        )

        typedArray.recycle()
    }

    fun setErrorButtonClickListener(block: () -> Unit) {
        binding.btnError.setOnClickListener {
            block.invoke()
        }
    }

    fun observeStateView(flow: StateFlow<State>, owner: LifecycleOwner) {
        flow.observeWhenOnCreated(owner) { state ->
            when (state) {
                State.Loading -> showLoading()
                State.Content -> showContent()
                is State.Error -> showError(state.message)
            }
        }
    }

    private fun showContent() {
        this.isVisible = false
        binding.errorContainer.isVisible = false
        binding.loading.isVisible = false
    }

    private fun showError(message: String) {
        this.isVisible = true
        binding.errorContainer.isVisible = true
        binding.loading.isVisible = false

        binding.tvError.text = message
    }

    private fun showLoading() {
        this.isVisible = true
        binding.errorContainer.isVisible = false
        binding.loading.isVisible = true
    }

    sealed interface State {
        object Loading : State
        object Content : State
        data class Error(
            val message: String
        ) : State
    }

    companion object {
        const val DELAY_LOADING = 400L
    }
}