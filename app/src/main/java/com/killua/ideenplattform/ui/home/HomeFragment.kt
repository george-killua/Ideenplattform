package com.killua.ideenplattform.ui.home

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.killua.ideenplattform.databinding.FragmentHomeBinding
import com.killua.ideenplattform.ui.editprofile.BaseFragment
import com.killua.ideenplattform.ui.safeNavigate
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : BaseFragment() {
    private val viewModel by viewModel<HomeViewModel>()
    private var _binding: FragmentHomeBinding? = null


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.addIdea = HomeViewModel.Action.AddIdea
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.setIntent(HomeViewModel.Action.SetupFragment(false))
        with(viewModel) {
            lifecycleScope.launchWhenStarted {

                getViewEffects.collect {
                    when (it) {
                        is HomeViewModel.Effect.NavToDetail -> toDetailNavigation(it.id)
                        HomeViewModel.Effect.NavToNewIdea -> toNewIdeaNavigation()
                        is HomeViewModel.Effect.ToastMessage -> showToast(it.message)
                    }
                }
            }
            lifecycleScope.launchWhenStarted {
                getState.collect { state ->
                    binding.progressBar.visibility =
                        if (state.isLoadingProgressBar) View.VISIBLE else View.GONE
                }
            }
            lifecycleScope.launchWhenCreated {
                getStateDataBinding.collect {
                    binding.dataBinding = it
                    binding.executePendingBindings()



                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun toDetailNavigation(ideaId: String) {
        val homeToDetail: NavDirections =
            HomeFragmentDirections.homeToDetail(ideaId)
        findNavController().safeNavigate(homeToDetail)
    }

    private fun toNewIdeaNavigation() {
        val homeToNewIdea: NavDirections =
            HomeFragmentDirections.homeToAdd()
        findNavController().safeNavigate(homeToNewIdea)
    }

}