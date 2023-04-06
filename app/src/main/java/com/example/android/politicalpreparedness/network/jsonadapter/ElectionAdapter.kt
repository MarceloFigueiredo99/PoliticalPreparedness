package com.example.android.politicalpreparedness.network.jsonadapter

import android.util.Log
import com.example.android.politicalpreparedness.network.models.Division
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

private const val TAG = "#PLP ElectionAdapter"

class ElectionAdapter {
    @FromJson
    fun divisionFromJson (ocdDivisionId: String): Division {
        Log.i(TAG, "Division retrieved: $ocdDivisionId")

        val countryDelimiter = "country:"
        var stateDelimiter = "state:"

        if (!ocdDivisionId.contains(stateDelimiter)) {
            stateDelimiter = "district:"
        }

        val country = ocdDivisionId.substringAfter(countryDelimiter,"")
                .substringBefore("/")
        val state = ocdDivisionId.substringAfter(stateDelimiter,"")
                .substringBefore("/")

        Log.i(TAG, "Division after parsing: $ocdDivisionId -> Country:$country State:$state")

        return Division(ocdDivisionId, country, state)
    }

    @ToJson
    fun divisionToJson (division: Division): String {
        return division.id
    }
}
