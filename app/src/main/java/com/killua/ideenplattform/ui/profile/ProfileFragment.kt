package com.killua.ideenplattform.ui.profile

import android.os.Bundle
import android.view.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.killua.ideenplattform.R
import com.killua.ideenplattform.databinding.FragmentProfileBinding
import com.killua.ideenplattform.ui.editprofile.BaseFragment
import com.killua.ideenplattform.ui.safeNavigate
import com.squareup.picasso.Picasso
import kotlinx.coroutines.flow.collect
import okhttp3.internal.wait
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : BaseFragment() {

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

    }

    override fun onStart() {
        super.onStart()
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