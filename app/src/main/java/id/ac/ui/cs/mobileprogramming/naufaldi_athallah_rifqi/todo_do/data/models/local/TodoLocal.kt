package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.local

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "TodoLocal")
@Parcelize
data class TodoLocal(@PrimaryKey(autoGenerate = true) var id : Long? = null,
                     @ColumnInfo(name = "todo") val todo: String,
                     @ColumnInfo(name = "isCompleted") val isCompleted: Boolean,
                     @ColumnInfo(name = "date") val date: String,
                     @ColumnInfo(name = "createdAt") val createdAt: String) : Parcelable
