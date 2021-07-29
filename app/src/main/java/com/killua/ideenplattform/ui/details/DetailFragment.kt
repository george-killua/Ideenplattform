package com.killua.ideenplattform.ui.details

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.killua.ideenplattform.databinding.FragmentDetailBinding
import com.killua.ideenplattform.ui.newidee.ManagementIdeaFragmentArgs
import org.koin.android.ext.android.inject

class DetailFragment : Fragment() {

    private val detailViewModel: DetailViewModel by inject()
    private lateinit var binding: FragmentDetailBinding

    private val args: ManagementIdeaFragmentArgs by navArgs()
    var isManager = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ideaId = args.idOfIdea
        detailViewModel.onAction(DetailViewModel.Action.SetupFragment(ideaId!!))
        detailViewModel.stateLiveData.observe(viewLifecycleOwner, {
            isManager = it.isManager == true
            it.isMyIdea
        })
        setHasOptionsMenu(true)
    }

    private fun showToast(message: String?) {
        if (message != null) Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (android.R.id.home == item.itemId) {
            this.findNavController().popBackStack()
            return true
        }

        return false    }
}