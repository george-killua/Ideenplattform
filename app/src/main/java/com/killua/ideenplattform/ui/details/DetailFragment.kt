package com.killua.ideenplattform.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.killua.ideenplattform.databinding.FragmentDetailBinding
import com.killua.ideenplattform.ui.home.HomeFragmentDirections
import com.killua.ideenplattform.ui.safeNavigate
import org.koin.android.ext.android.inject

class DetailFragment : Fragment() {

    private val detailViewModel: DetailViewModel by inject()
    private var _binding: FragmentDetailBinding? = null


    private val binding get() = _binding!!
    private val args: DetailFragmentArgs by navArgs()
    private var isManager = false
    private var ideaId = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        ideaId = args.idOfDetails
        detailViewModel.onAction(DetailViewModel.Action.SetupFragment(ideaId))
        detailViewModel.stateLiveData.observe(viewLifecycleOwner, {
            isManager = it.isManager == true
         //   if(it.isMyIdea)  navigateToEditFrag()

            if(it.showComment)  binding.recyclerView.visibility=View.VISIBLE else binding.recyclerView.visibility=View.GONE
            if(it.toastMessage.isNullOrBlank())  showToast(it.toastMessage)
            if(it.commentEmpty)  binding.etComment.error="is empty"
            if(it.refreshLayout)  {
                detailViewModel.onAction(DetailViewModel.Action.SetupFragment(ideaId))
            binding.executePendingBindings()
            }
        })
        return binding.root
    }

    private fun navigateToEditFrag() {
        val detailsToEdit: NavDirections =
            DetailFragmentDirections.detailToEdit(ideaId)
        this.findNavController().safeNavigate(detailsToEdit)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.detailVM = detailViewModel
        binding.executePendingBindings()
    }

    private fun showToast(message: String?) {
        if (message != null) Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        return false
    }
}


