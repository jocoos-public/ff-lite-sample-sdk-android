package com.jocoos.flipflop.sample.utils

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialog
import com.jocoos.flipflop.sample.R
import com.jocoos.flipflop.sample.databinding.ShortDialogBinding

private class ShortDialog(
    context: Context,
    private val title: String,
    private val content: String,
) : AppCompatDialog(context, R.style.AppDialog), DialogInterface {
    private lateinit var binding: ShortDialogBinding
    private var confirmListener: (() -> Unit)? = null
    private var cancelListener: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ShortDialogBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.dialogTitle.text = title
        binding.dialogContent.text = content

        binding.dialogConfirm.setOnClickListener {
            confirmListener?.invoke()
        }
        binding.dialogCancel.setOnClickListener {
            cancelListener?.invoke()
        }
    }

    fun setConfirmListener(confirmListener: (() -> Unit)? = null) {
        this.confirmListener = confirmListener
    }

    fun setCancelListener(cancelListener: (() -> Unit)? = null) {
        this.cancelListener = cancelListener
    }
}

object DialogBuilder {
    fun showShortDialog(context: Context, title: String, content: String,
                        confirmListener: (() -> Unit)? = null, cancelListener: (() -> Unit)? = null, dismissListener: (() -> Unit)? = null, cancelable: Boolean = true) {
        val dialog = ShortDialog(context, title, content)
        dialog.setCancelable(cancelable)
        dialog.setConfirmListener {
            confirmListener?.invoke()
            dialog.dismiss()
        }
        dialog.setCancelListener {
            cancelListener?.invoke()
            dialog.dismiss()
        }
        dialog.setOnDismissListener {
            dismissListener?.invoke()
        }
        dialog.show()
    }
}
