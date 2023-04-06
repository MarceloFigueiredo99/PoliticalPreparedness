package com.example.android.politicalpreparedness.election.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("electionDate")
fun TextView.setElectionDate(electionDate: Date) {
    text =
        SimpleDateFormat("EEE MMM dd HH':'mm':'ss z yyyy", Locale.US).format(electionDate)
}
