package com.example.android.politicalpreparedness.representative.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "representative_table")
data class RepresentativesList(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "list") val list: List<Representative>
)
