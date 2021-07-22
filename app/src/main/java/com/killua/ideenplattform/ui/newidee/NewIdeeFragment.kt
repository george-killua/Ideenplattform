package com.killua.ideenplattform.ui.newidee

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.killua.ideenplattform.MainActivity
import com.killua.ideenplattform.databinding.FragmentNewIdeeBinding
import org.koin.android.ext.android.inject

class NewIdeeFragment : Fragment() {

    private val viewModel: NewIdeeViewModel by inject()
    private val binding by lazy {
        FragmentNewIdeeBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.vm = viewModel
        binding.executePendingBindings()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


}