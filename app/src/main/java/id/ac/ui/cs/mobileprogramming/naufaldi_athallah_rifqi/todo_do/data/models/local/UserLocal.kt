package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserLocal(var name: String,
                     var image: String){
    @PrimaryKey(autoGenerate = true)
    var id : Int? = null
}