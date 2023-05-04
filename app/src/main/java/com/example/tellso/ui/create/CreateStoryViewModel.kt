package com.example.tellso.ui.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CreateStoryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Create story Fragment"
    }
    val text: LiveData<String> = _text
}