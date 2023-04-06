package com.example.android.politicalpreparedness.network.jsonadapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.*

class DateJsonAdapter {
    @FromJson
    fun dateFromJson(date: String): Date {
        return SimpleDateFormat("dd-MM-yyyy", Locale.US).parse(date)!!
    }

    @ToJson
    fun dateToJson(date: Date): String {
        return date.toString()
    }
}
