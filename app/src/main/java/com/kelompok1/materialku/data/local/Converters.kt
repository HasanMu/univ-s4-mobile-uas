package com.kelompok1.materialku.data.local

import androidx.room.TypeConverter
import com.kelompok1.materialku.domain.model.JenisStok
import com.kelompok1.materialku.domain.model.RoleEnum
import com.kelompok1.materialku.domain.model.StatusTransaksi
import java.time.LocalDate
import java.time.LocalDateTime

class Converters {

    @TypeConverter
    fun localDateToString(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun stringToLocalDate(value: String?): LocalDate? = value?.let(LocalDate::parse)

    @TypeConverter
    fun localDateTimeToString(value: LocalDateTime?): String? = value?.toString()

    @TypeConverter
    fun stringToLocalDateTime(value: String?): LocalDateTime? = value?.let(LocalDateTime::parse)

    @TypeConverter
    fun roleToString(value: RoleEnum?): String? = value?.name

    @TypeConverter
    fun stringToRole(value: String?): RoleEnum? = value?.let(RoleEnum::valueOf)

    @TypeConverter
    fun statusToString(value: StatusTransaksi?): String? = value?.name

    @TypeConverter
    fun stringToStatus(value: String?): StatusTransaksi? = value?.let(StatusTransaksi::valueOf)

    @TypeConverter
    fun jenisStokToString(value: JenisStok?): String? = value?.name

    @TypeConverter
    fun stringToJenisStok(value: String?): JenisStok? = value?.let(JenisStok::valueOf)
}
