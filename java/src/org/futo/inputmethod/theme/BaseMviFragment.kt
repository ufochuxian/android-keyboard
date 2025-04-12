package org.futo.inputmethod.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.flow.collectLatest

abstract class BaseMviFragment<B : ViewBinding, I, S, VM : BaseMviViewModel<I, S>> : Fragment() {
    protected lateinit var binding: B
    protected abstract val viewModel: VM

    abstract fun createBinding(inflater: LayoutInflater, container: ViewGroup?): B
    abstract fun setupViews()
    abstract fun render(state: S)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = createBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        lifecycleScope.launchWhenStarted {
            viewModel.state.collectLatest { render(it) }
        }
    }
}
