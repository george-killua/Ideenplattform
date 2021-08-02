package com.killua.ideenplattform.ui.editprofile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.drjacky.imagepicker.ImagePicker
import com.killua.ideenplattform.R
import com.killua.ideenplattform.databinding.FragmentEditProfileBinding
import com.killua.ideenplattform.ui.newidee.ManagementIdeaFragmentArgs
import com.killua.ideenplattform.ui.newidee.ManagementIdeeViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditProfileFragment : Fragment() {
    private val viewModel by viewModel<EditProfileViewModel>()
    private val navController by lazy(::findNavController)

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                viewModel.setIntent(
                    EditProfileViewModel.Action.LoadImageFromGallery(
                        ImagePicker.getFile(
                            it!!.data!!
                        )!!
                    )
                )
                binding.ivProfileImage.setImageURI(uri)

            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        setHasOptionsMenu(true)
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        binding.etFirstName.error
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (android.R.id.home == item.itemId) {
            this.findNavController().popBackStack()
            return true
        }
        return false
    }

}