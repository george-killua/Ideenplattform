package com.killua.ideenplattform.ideamain.newidee

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.drjacky.imagepicker.ImagePicker
import com.killua.ideenplattform.R
import com.killua.ideenplattform.databinding.FragmentIdeaManagementBinding
import com.killua.ideenplattform.ideamain.DataBindingAdapters.setCustomAdapter
import com.killua.ideenplattform.ideamain.editprofile.BaseFragment
import com.killua.ideenplattform.ideamain.safeNavigate
import ideamain.newidee.ManagementIdeaFragmentArgs
import ideamain.newidee.ManagementIdeaFragmentDirections
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class ManagementIdeaFragment : BaseFragment() {
    private val args: ManagementIdeaFragmentArgs by navArgs()
    private val viewModel by viewModel<ManagementIdeeViewModel>()

    private lateinit var binding: FragmentIdeaManagementBinding
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private var imageFile: File? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                imageFile = ImagePicker.getFile(it!!.data!!)

                val uri = it.data!!.data
                binding.ivIdeaImage
                uri?.let {
                    viewModel.reducerDB {
                        copy(imagePathUri = it)
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentIdeaManagementBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = args.idOfIdea

        setHasOptionsMenu(true)
        viewModel.apply {
            //fragment setup ideamain
            setIntent(ManagementIdeeViewModel.Action.SetupMe(id))

            //assigned effect, state and stateUI data binding
            lifecycleScope.launchWhenCreated {
                getViewEffects.collect {
                    when (it) {
                        is ManagementIdeeViewModel.Effect.MakeToast ->
                            showToast(it.toastMessage)
                        ManagementIdeeViewModel.Effect.NavigateToDetailScreen ->
                            navigateToDetail(id!!)
                        is ManagementIdeeViewModel.Effect.NavigateToDetailWithIdea ->
                            navigateToDetail(it.idea.id)
                    }
                }
            }
            lifecycleScope.launchWhenCreated {
                getStateDataBinding.collect {
                    binding.ivIdeaImage.setImageURI(it.imagePathUri)
                    setBtnUpload(it.btnSubmitTextIsToSave)
                    setBtnUploadImage(it.imagePathUri?.path)
                    setCustomAdapter(binding.spCategory, it.categoriesArray)
                    if (it.selectedCategory > 0) selectedInitializer(it.selectedCategory)
                    binding.etName.editText?.setText(it.ideaName)
                    binding.etDescription.editText?.setText(it.description)
                }
            }
            lifecycleScope.launchWhenStarted {
                getState.collect {
                    if (it.categoryNotSelected) setCategorySpinnerError()
                    if (it.internetConnectionError) showToast(getString(R.string.internet_error))
                    if (it.descriptionIsEmpty) setDescriptionError()
                    else descriptionRemoveError()
                    if (it.titleISEmpty) setTitleIdeaError()
                    else titleRemoveError()
                }
            }
            //end

            //setup ideamain changes and buttons
            binding.etName.editText?.doAfterTextChanged {
                viewModel.reducerDB {
                    copy(ideaName = it.toString())
                }
            }

            binding.etDescription.editText?.doAfterTextChanged {
                viewModel.reducerDB {
                    copy(description = it.toString())
                }
            }
            binding.btnUploadImage.setOnClickListener { selectImage() }
            binding.btnAddIdea.setOnClickListener {
                setIntent(ManagementIdeeViewModel.Action.SaveChanges(id,
                    imageFile))
            }

            binding.spCategory.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long,
                    ) {
                        reducerDB {
                            copy(selectedCategory = position)
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                        val errorTextview = binding.spCategory.selectedView as TextView
                        errorTextview.error = ""
                        errorTextview.setTextColor(Color.RED)
                        errorTextview.text = ""
                    }

                }

        }

    }

    private fun selectedInitializer(selectedCategory: Int) {
        binding.spCategory.setSelection(selectedCategory)
    }

    private fun descriptionRemoveError() {
        binding.etDescription.error = null
        binding.etDescription.isErrorEnabled = false
    }

    private fun titleRemoveError() {
        binding.etName.isErrorEnabled = false

    }

    private fun setBtnUploadImage(imagePathUri: String?) {
        if (imagePathUri.isNullOrBlank())
            binding.btnUploadImage.text = getString(R.string.upload)
        else
            binding.btnUploadImage.text = getString(R.string.edit)

    }

    private fun setBtnUpload(toSave: Boolean) {
        if (toSave)
            binding.btnAddIdea.text = getString(R.string.save_changes)
        else

            binding.btnAddIdea.text = getString(R.string.submit)
    }

    private fun navigateToDetail(ideaId: String) {
        val action = ManagementIdeaFragmentDirections.managToDetail(ideaId)
        findNavController().safeNavigate(action)
    }

    private fun setTitleIdeaError() {
        binding.etName.error = getString(R.string.idea_name_error)
        binding.etName.errorIconDrawable = getDrawable(requireContext(), R.drawable.ic_error)
    }

    private fun setDescriptionError() {
        binding.etDescription.error = getString(R.string.description_error)
        binding.etDescription.errorIconDrawable = getDrawable(requireContext(), R.drawable.ic_error)
    }

    private fun setCategorySpinnerError() {
        val selectedView: View = binding.spCategory.selectedView
        if (selectedView is TextView) {
            val selectedTextView = selectedView

            val errorString = getString(R.string.select_category)
            selectedTextView.error = errorString

        }
    }

    private fun selectImage() {
        ImagePicker.with(requireActivity())
            .cropSquare()
            .maxResultSize(1024, 1024)
            .createIntentFromDialog { launcher.launch(it) }
    }



}