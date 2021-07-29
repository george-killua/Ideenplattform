package com.killua.ideenplattform.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.killua.ideenplattform.databinding.FragmentDetailBinding
import com.killua.ideenplattform.databinding.FragmentHomeBinding
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
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.detailVM =detailViewModel
        binding.executePendingBindings()
        detailViewModel.onAction(DetailViewModel.Action.SetupFragment(ideaId))
        detailViewModel.stateLiveData.observe(viewLifecycleOwner, {
            isManager = it.isManager == true
            it.isMyIdea
        })
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