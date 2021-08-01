package com.killua.ideenplattform.ui.profile

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.killua.ideenplattform.R
import com.killua.ideenplattform.databinding.FragmentProfileBinding
import com.killua.ideenplattform.ui.safeNavigate
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {

    private val viewModel by viewModel<ProfileViewModel>()
    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        lifecycleScope.launchWhenCreated {
            viewModel.getViewEffects.collect {
                onViewEffectReceived(it)
            }
        }
        //If you want you can decide to handle your view state changes by simply observing it
        lifecycleScope.launchWhenCreated {
            viewModel.getStateDataBinding.collect {
                onStateDataBinding(it)
            }
        }
        lifecycleScope.launchWhenCreated {
            viewModel.getState.collect {
                onState(it)
            }
        }
    }

    private fun onViewEffectReceived(effect: ProfileEffect?) {
        when (effect) {
            ProfileEffect.NavigateToAddIdea -> {
                val action = ProfileFragmentDirections.profileToNewIdea()
                findNavController().safeNavigate(action)
            }
            ProfileEffect.NavigateToEditProfile -> {
                val action = ProfileFragmentDirections.profileToEditProfile()
                findNavController().safeNavigate(action)
            }
            ProfileEffect.NavigateToLoginFragment -> {
                val action = ProfileFragmentDirections.profileToLogin()
                findNavController().safeNavigate(action)
            }
        }
    }

    private fun onStateDataBinding(stateViewDb: StateViewDataBinding?) {
        binding.state = stateViewDb
        binding.executePendingBindings()
    }

    private fun onState(state: ProfileState?) {

        when {
            state == null -> {
                return
            }
            state.toastMessage != null -> showToast(state.toastMessage)
            state.isLoading != null -> {
                binding.progressBar.visibility =
                    if (state.isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.pm_edit_profile -> viewModel.setIntent(ProfileAction.OnEditProfileAction)
            R.id.pm_new_idea -> viewModel.setIntent(ProfileAction.OnAddNewIdeaAction)
            R.id.pm_sign_out -> viewModel.setIntent(ProfileAction.OnSignOutAction)
        }
        return super.onOptionsItemSelected(item)
    }
}