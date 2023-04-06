package com.example.android.politicalpreparedness.representative.database

import android.util.Log
import androidx.room.TypeConverter
import com.example.android.politicalpreparedness.representative.model.Representative
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson

private const val TAG = "#PLP RepresentativesConverter"

class RepresentativesConverter {

    @TypeConverter
    fun fromList(value: List<Representative>): String {
        Log.i(TAG, "fromList: $value")
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toList(value: String): List<Representative> {
        return try {
            Log.i(TAG, "toList: $value")
            Gson().fromJson<List<Representative>>(value)
        } catch (e: Exception) {
            arrayListOf()
        }
    }
}

inline fun <reified T> Gson.fromJson(json: String) =
    fromJson<T>(json, object : TypeToken<T>() {}.type)
