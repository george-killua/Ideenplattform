package com.killua.ideenplattform.ui.newidee

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.drjacky.imagepicker.ImagePicker
import com.killua.ideenplattform.MainActivity
import com.killua.ideenplattform.databinding.FragmentNewIdeeBinding
import org.koin.android.ext.android.inject


class ManagementIdeaFragment : Fragment() {
    private val args: ManagementIdeaFragmentArgs by navArgs()
    private val viewModel: ManagementIdeeViewModel by inject()
    private val navController by lazy(::findNavController)

    private lateinit var binding: FragmentNewIdeeBinding
    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onAttach(context: Context) {
        super.onAttach(context)

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                viewModel.onAction(
                    ManagementIdeeViewModel.Action.LoadImageFromGallery(
                        ImagePicker.getFile(
                            it!!.data!!
                        )!!
                    )
                )
                binding.imageView.setImageURI(uri)

            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewIdeeBinding.inflate(inflater, container, false)
        binding.btnUploadImage.setOnClickListener { selectImage() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = args.idOfIdea
        binding.vm = viewModel
        setHasOptionsMenu(true)

        //binding.spCategory.adapter=
        binding.executePendingBindings()
        with(viewModel) {
            binding.vm = this
            //binding.spCategory.adapter=
            binding.executePendingBindings()
            onAction(ManagementIdeeViewModel.Action.LoadIdToEdit(id, view))
            loadEffectLiveData.observe(viewLifecycleOwner, {
                binding.spCategory.setSelection(it.selectedCategoryPosition)
                binding.btnUploadImage.text = it.btnUploadText
                binding.btnAddIdea.text = it.btnSubmitText
                description = it.description
                titleQuality = it.name
                (requireActivity() as MainActivity).supportActionBar?.title = it.title

            })
            stateLiveData.observe(viewLifecycleOwner,
                {
                    if (it.toastMessage != null) showToast(it.toastMessage)
                    if (it.categoryNotSelected) showToast(it.toastMessage)
                    if (it.descriptionIsEmpty) binding.etDescription.error = it.toastMessage
                    if (it.titleISEmpty) binding.etName.error = it.toastMessage

                    if (it.navigateToOtherScreen) {
                        val action = ManagementIdeaFragmentDirections.managToDetail(id!!)
                        navController.navigate(action)
                    }

                })
        }

        binding.spCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.onAction(ManagementIdeeViewModel.Action.SelectCategory(position))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.onAction(ManagementIdeeViewModel.Action.SelectCategory(1))

            }

        }
    }

    private fun selectImage() {
        ImagePicker.with(requireActivity())
            .cropSquare()
            .maxResultSize(1024, 1024)
            .createIntentFromDialog { launcher.launch(it) }
    }


    private fun showToast(message: String?) {
        if (message != null) Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (android.R.id.home == item.itemId) {
            this.findNavController().popBackStack()
            return true
        }

        return false
    }
}