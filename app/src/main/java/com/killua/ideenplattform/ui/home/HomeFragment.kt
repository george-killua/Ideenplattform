package com.killua.ideenplattform.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.killua.ideenplattform.databinding.FragmentHomeBinding
import com.killua.ideenplattform.ui.safeNavigate
import org.koin.android.ext.android.inject

class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by inject()
    private var _binding: FragmentHomeBinding? = null


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.onAction( HomeViewModel.Action.post)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.onAction(HomeViewModel.Action.SetupFragment(view, false))
        viewModel.stateLiveData.observe(viewLifecycleOwner, { state ->
            binding.progressBar.visibility =
                if (state.isLoadingProgressBar) View.VISIBLE else View.GONE
            state.toastMessage?.let { message -> showToast(message) }
            if (state.navToNewIdea) toNewIdeaNavigation()
            if (state.navToDetail) {
                Log.e("errorNav","i'm here ")
                toDetailNavigation(state.ideaIDNavigationHelper!!)
            }
        })
        val layoutRecyclerViewCustomized=LinearLayoutManager(context)
        layoutRecyclerViewCustomized.orientation=LinearLayoutManager.HORIZONTAL
        layoutRecyclerViewCustomized.reverseLayout = true
//binding.textHome.layoutManager=layoutRecyclerViewCustomized
        binding.mv = viewModel
        binding.executePendingBindings()

    }


    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun toDetailNavigation(ideaId:String) {
        val homeToDetail: NavDirections =
            HomeFragmentDirections.homeToDetail(ideaId)
        this.findNavController().navigate(homeToDetail)
    }
    private fun toNewIdeaNavigation() {
        val homeToNewIdea: NavDirections =
            HomeFragmentDirections.homeToAdd()
        this.findNavController().safeNavigate(homeToNewIdea)
    }

}