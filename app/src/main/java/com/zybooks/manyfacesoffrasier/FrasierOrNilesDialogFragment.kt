package menu

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.zybooks.manyfacesoffrasier.R

class FrasierOrNilesDialogFragment : DialogFragment() {
    interface FrasierOrNilesListener {
        fun userSelectsFraiser(dialog: DialogFragment)
        fun userSelectsNiles(dialog: DialogFragment)
    }

     lateinit var listener: FrasierOrNilesListener

    override fun onCreateDialog(savedInstanceState: Bundle?)
            : Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Frasier or Niles")
        builder.setMessage("You Choose")
        builder.setPositiveButton("Frasier", DialogInterface.OnClickListener {dialog, id ->
            listener.userSelectsFraiser(this)
        })
        builder.setNegativeButton("Niles", DialogInterface.OnClickListener { dialog, id ->
            listener.userSelectsNiles(this)
        })
        return builder.create()
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as FrasierOrNilesListener
    }
}