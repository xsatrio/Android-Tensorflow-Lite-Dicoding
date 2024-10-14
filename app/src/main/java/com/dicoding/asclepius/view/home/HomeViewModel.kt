package com.dicoding.asclepius.view.home

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> = _imageUri

    fun setImageUri(uri: Uri) {
        _imageUri.value = uri
    }

    fun getImageUri(): Uri? {
        return _imageUri.value
    }
}