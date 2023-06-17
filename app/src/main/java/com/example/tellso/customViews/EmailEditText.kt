package com.example.tellso.customViews

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doOnTextChanged
import com.example.tellso.R

class EmailEditText : AppCompatEditText {


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        compoundDrawablePadding = 16

        setHint(R.string.email)
        setAutofillHints(AUTOFILL_HINT_EMAIL_ADDRESS)
        setDrawable()

        doOnTextChanged { text, _, _, _ ->
            // Email validation
            // Display error automatically if the email is invalid
            if (!text.isNullOrEmpty() && !Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                error = context.getString(R.string.et_email_error_message)
            }
        }
    }

    private fun setDrawable(
        start: Drawable? = null,
        top: Drawable? = null,
        end: Drawable? = null,
        bottom: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom)
    }
}