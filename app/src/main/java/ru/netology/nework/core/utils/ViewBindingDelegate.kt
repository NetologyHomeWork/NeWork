package ru.netology.nework.core.utils

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KProperty

class ViewBindingDelegate<B : ViewBinding>(
    private val fragment: Fragment,
    private val viewBindingClass: Class<B>
) {
    var binding: B? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): B {
        val viewLifecycleOwner = fragment.viewLifecycleOwner
        when {
            viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.DESTROYED -> {
                throw IllegalStateException("Called after onDestroyView()")
            }
            fragment.view != null -> {
                return getOrCreateBinding(viewLifecycleOwner)
            }
            else -> {
                throw IllegalStateException("Called before onViewCreated()")
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getOrCreateBinding(owner: LifecycleOwner): B {
        return this.binding ?: let {
            val method = viewBindingClass.getMethod("bind", View::class.java)
            val binding = method.invoke(null, fragment.view) as B

            owner.lifecycle.addObserver(object :DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    this@ViewBindingDelegate.binding = null
                }
            })

            this.binding = binding
            binding
        }
    }
}

inline fun <reified B : ViewBinding> Fragment.viewBinding(): ViewBindingDelegate<B> {
    return ViewBindingDelegate(this, B::class.java)
}