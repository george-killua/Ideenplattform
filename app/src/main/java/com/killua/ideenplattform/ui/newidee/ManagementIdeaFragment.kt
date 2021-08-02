package com.killua.ideenplattform.ui.newidee

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log.e
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.drjacky.imagepicker.ImagePicker
import com.killua.ideenplattform.R
import com.killua.ideenplattform.databinding.FragmentIdeaManagementBinding
import com.killua.ideenplattform.ui.safeNavigate
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class ManagementIdeaFragment : Fragment() {
    private val args: ManagementIdeaFragmentArgs by navArgs()
    private val viewModel by viewModel<ManagementIdeeViewModel>()

    private lateinit var binding: FragmentIdeaManagementBinding
    private lateinit var launcher: ActivityResultLauncher<Intent>
    var imageFile: File? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                imageFile = ImagePicker.getFile(it!!.data!!)
                e("uriLoader", it.data.toString())
                e("uriLoader", it.data!!.data.toString())
                e("uriLoader", it.data!!.data!!.path.toString())
                binding.ivIdeaImage.setImageURI(uri)
                viewModel.reducerDB {
                    copy(imagePathUri = it.data?.data!!.path!!.toString())
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
            //fragment setup ui
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
                    setBtnUpload(it.btnSubmitTextIsToSave)
                    setBtnUploadImage(it.imagePathUri)
                   if(it.selectedCategory>0) selectedInitializer(it.selectedCategory)
                    binding.vm = it
                    binding.executePendingBindings()
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

            //setup ui changes and buttons
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
// for show error message on spinner
                        // for show error message on spinner
                        errorTextview.text = "Your Error Message here"
                    }

                }

        }

    }

    private fun selectedInitializer(selectedCategory: Int) {
        binding.spCategory.setSelection(selectedCategory,true)
    }

    private fun descriptionRemoveError() {
        binding.etDescription.error = null
        binding.etDescription.isErrorEnabled = false
    }
    private fun titleRemoveError() {
        binding.etName.isErrorEnabled = false

    }
    private fun setBtnUploadImage(imagePathUri: String) {
        if (imagePathUri.isBlank())
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
        if (selectedView != null && selectedView is TextView) {
            val selectedTextView = selectedView

                val errorString =  getString(R.string.select_category)
                selectedTextView.error = errorString

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

}