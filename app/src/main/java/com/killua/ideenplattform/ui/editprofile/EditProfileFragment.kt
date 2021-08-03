package com.killua.ideenplattform.ui.editprofile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.drjacky.imagepicker.ImagePicker
import com.killua.ideenplattform.R
import com.killua.ideenplattform.databinding.FragmentEditProfileBinding
import com.killua.ideenplattform.ui.editprofile.EditProfileViewModel.Action
import com.killua.ideenplattform.ui.editprofile.EditProfileViewModel.Effect
import com.killua.ideenplattform.ui.safeNavigate
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditProfileFragment : BaseFragment() {
    private val viewModel by viewModel<EditProfileViewModel>()

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                if (activityResult.resultCode == Activity.RESULT_OK) {
                    activityResult.data?.let {
                        viewModel.setIntent(
                            Action.InsertImage(it.data.toString()))
                    }
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        setHasOptionsMenu(true)
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        binding.saveChanges = Action.SaveChangesClicked
        binding.chooseImage = Action.ChooseImageClicked
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        with(viewModel) {
            lifecycleScope.launchWhenStarted {
                getStateDataBinding.collect {
                    profileButtonHandler(it.imageUri.isNullOrBlank())
                    binding.stateDb = getStateDataBinding.value
                    binding.executePendingBindings()
                }
                getState.collect {
                    setFirstnameError(it.firstNameError)
                    setLastnameError(it.lastNameError)
                    setPasswordError(it.passwordError)
                    setConfirmError(it.passwordConfirmError)
                }
            }
            lifecycleScope.launchWhenStarted {
                viewModel.getViewEffects.collect {
                    when (it) {
                        Effect.ChooseImage -> selectImage()
                        is Effect.MakeToast -> showToast(it.toastMessage)
                        Effect.NavigateToProfile -> navigateToProfile()
                    }
                }
            }

        }
    }

    private fun profileButtonHandler(nullOrBlank: Boolean?) {
        if (nullOrBlank == true || nullOrBlank == null)
            binding.btnImageChooser.background = getDrawable(requireContext(), R.drawable.ic_add)
        else
            binding.btnImageChooser.background = getDrawable(requireContext(), R.drawable.ic_remove)
    }

    private fun navigateToProfile() {
        val action = EditProfileFragmentDirections.editProfileToProfile()
        findNavController().safeNavigate(action)
    }

    private fun setConfirmError(passwordConfirmError: Boolean) {
        binding.etPasswordConfirm.isErrorEnabled = passwordConfirmError
        binding.etPasswordConfirm.error = getString(R.string.passowrd_confirm_error)
    }

    private fun setFirstnameError(firstNameError: Boolean) {
        binding.etFirstName.isErrorEnabled = firstNameError
        binding.etFirstName.error = getString(R.string.firstname_error)
    }

    private fun setPasswordError(passwordError: Boolean) {
        binding.etPassword.isErrorEnabled = passwordError
        binding.etPassword.error = getString(R.string.password_error)
    }

    private fun setLastnameError(lastNameError: Boolean) {
        binding.etLastName.isErrorEnabled = lastNameError
        binding.etLastName.error = getString(R.string.lastname_error)
    }

    private fun selectImage() {

        if (viewModel.getStateDataBinding.value.imageUri.isNullOrBlank())
            ImagePicker.with(requireActivity())
                .setImageProviderInterceptor { imageProvider -> //Intercept ImageProvider
                    Log.d("ImagePicker", "Selected ImageProvider: $imageProvider.name")
                }
                .cropSquare()
                .maxResultSize(1024, 1024)
                .createIntentFromDialog { launcher.launch(it) }
        else {
            val title = getString(R.string.delete_image)
            val yesMessage = getString(R.string.yes)
            val noMessage = getString(R.string.no)
            showDialogYesNo(title = title,
                yesMessage = yesMessage,
                noMessage = noMessage) {
                viewModel.setIntent(Action.RemoveImage)
            }
        }
    }


}


