package xaviernadalreales.com.lifealbum.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "people")
data class Person(
    @PrimaryKey(autoGenerate = true) var id: Int,
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "profilePicture") var profilePicture: String = "",
    @ColumnInfo(name = "description") var descriptionText: String = "",
    @ColumnInfo(name = "notesIn") var notesInside: String = ""
) : Parcelable
