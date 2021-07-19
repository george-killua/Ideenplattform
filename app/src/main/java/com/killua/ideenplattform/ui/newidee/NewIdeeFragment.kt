package com.killua.ideenplattform.ui.newidee

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.killua.ideenplattform.R

class NewIdeeFragment : Fragment() {

    companion object {
        fun newInstance() = NewIdeeFragment()
    }

    private lateinit var viewModel: NewIdeeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_idee, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(NewIdeeViewModel::class.java)
        // TODO: Use the ViewModel
    }

}