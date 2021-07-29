package com.killua.ideenplattform.ui.toprank

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.killua.ideenplattform.databinding.FragmentTopRankBinding
import com.killua.ideenplattform.ui.home.HomeViewModel
import org.koin.android.ext.android.inject

class TopRankFragment : Fragment() {

    companion object {
        @JvmStatic val fragment = TopRankFragment()
    }

    private val viewModel: HomeViewModel by inject()
    private var _binding: FragmentTopRankBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTopRankBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.onAction(HomeViewModel.Action.SetupFragment(view, true))
        viewModel.stateLiveData.observe(viewLifecycleOwner, { state ->
            binding.progressBar.visibility =
                if (state.isLoadingProgressBar) View.VISIBLE else View.GONE
            state.toastMessage?.let { message -> showToast(message) }
        })

        binding.topRankMv = viewModel
        binding.executePendingBindings()
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}