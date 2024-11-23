package com.insight24app.db

import androidx.room.TypeConverter
import com.insight24app.model.Source


class Converters {

    @TypeConverter
    fun fromSource(source: Source): String{
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source{
        return Source(name, name)
    }

}