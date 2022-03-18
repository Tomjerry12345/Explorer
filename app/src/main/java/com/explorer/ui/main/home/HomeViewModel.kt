package com.explorer.ui.main.home

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.explorer.R
import com.explorer.utils.system.moveNavigationTo

class HomeViewModel : ViewModel() {

    fun onMoveIdentifikasi(view: View) {
        moveNavigationTo(view, R.id.action_homeFragment_to_identifikasiFragment)
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel() as T
        }
    }
}