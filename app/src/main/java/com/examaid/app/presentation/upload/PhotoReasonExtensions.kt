package com.examaid.app.presentation.upload

import android.content.Context
import com.examaid.app.R
import com.examaid.app.domain.model.PhotoReason

fun PhotoReason.toDisplayText(context: Context): String = when (this) {
    PhotoReason.NEW_LEARNING -> context.getString(R.string.photo_reason_new_learning)
    PhotoReason.GOOD_QUESTION -> context.getString(R.string.photo_reason_good_question)
    PhotoReason.COULD_NOT_SOLVE -> context.getString(R.string.photo_reason_could_not_solve)
}



