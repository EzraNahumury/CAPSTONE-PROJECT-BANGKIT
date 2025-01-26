package com.bangkit.trashup.ui.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

@Suppress("LiftReturnOrAssignment")
class TextCustomView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    companion object {
        private const val EMAIL_DOMAIN = "@gmail.com"
    }

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString()
                when (inputType) {
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD or android.text.InputType.TYPE_CLASS_TEXT -> {
                        if (input.length < 8) {
                            error = "Password harus 8 karakter lebih"
                        } else {
                            error = null
                        }
                    }

                    android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS or android.text.InputType.TYPE_CLASS_TEXT -> {
                        if (!input.endsWith(EMAIL_DOMAIN)) {
                            error = "Email harus berformat $EMAIL_DOMAIN"
                        } else {
                            error = null
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}
