package com.ziyad.pencatatuang.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notes (
    var noteId: String,
    var uid: String,
    var nama: String,
    var hari: String,
    var bulan: String,
    var tahun: String,
    var kategori: String,
    var nominal: String) : Parcelable