package com.example.tellso.utils

import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.tellso.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.*

fun View.animateVisibility(isVisible: Boolean, duration: Long = 500) {
    ObjectAnimator
        .ofFloat(this, View.ALPHA, if (isVisible) 1f else 0f)
        .setDuration(duration)
        .start()
}

fun ImageView.setImageFromUrl(context: Context, url: String) {
    Glide
        .with(context)
        .load(url)
        .placeholder(R.drawable.image_loading_placeholder)
        .error(R.drawable.image_load_error)
        .into(this)
}

fun TextView.setDateFormat(timestamp: String) {
//    make sure the timestamp is in this format: "7 May 2023"
    val instant = Instant.parse(timestamp)
    val date = Date.from(instant)

    // format the date using the desired format
    val sdf = SimpleDateFormat("dd MMMM yyyy",Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone(ZoneId.systemDefault())

    val formattedDate = sdf.format(date)
    this.text = formattedDate
}