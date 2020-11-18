package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TodoLocal(var todo: String,
                     var completed: Boolean,
                     var date: String,
                     var createdAt: String){
    @PrimaryKey(autoGenerate = true)
    var id : Long? = null
}