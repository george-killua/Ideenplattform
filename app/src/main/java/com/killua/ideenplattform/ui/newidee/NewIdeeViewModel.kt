package com.killua.ideenplattform.ui.newidee


import androidx.databinding.BaseObservable
import com.killua.ideenplattform.data.repository.MainRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NewIdeeViewModel : BaseObservable(), KoinComponent {
    private val userRepository: MainRepository by inject()


}