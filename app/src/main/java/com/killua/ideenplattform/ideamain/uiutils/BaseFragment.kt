package com.killua.ideenplattform.ideamain.editprofile

import android.app.AlertDialog
import android.widget.Toast
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {
    protected fun showToast(message: String?) {
        if (message != null) Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showDialogYesNo(
        title: String,
        yesMessage: String,
        noMessage: String,
        yesPressed: () -> Unit,
    ) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle(title)
        alertDialog.setPositiveButton(yesMessage
        ) { dialog, _ ->
            yesPressed()
            dialog.dismiss()
        }
        alertDialog.setNegativeButton(noMessage) { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.setCancelable(true)

        alertDialog.create().show()
    }
}
