package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.Todo
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.Const
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.helper.FormatUtil
import java.util.*

interface FirestoreMapper {
    // map todoEntity to firestore todoObject
    fun toTodoObject(todo: Todo): HashMap<String, Any> {
        val todoObject = HashMap<String, Any>()
        todoObject[Const.Key.Todo.TODO] = todo.todo
        todoObject[Const.Key.Todo.COMPLETED] = todo.completed
        todoObject[Const.Key.Todo.DATE] = todo.date
        todoObject[Const.Key.Todo.USER] = todo.user
        todoObject[Const.Key.Todo.CREATED_AT] = FieldValue.serverTimestamp()

        return todoObject
    }

    // map firestore todoObject to todoEntity
    private fun toTodoEntity(document: QueryDocumentSnapshot): Todo {
        val todoId = document.id
        val data = document.data

        val todo = data[Const.Key.Todo.TODO] as String
        val completed = data[Const.Key.Todo.COMPLETED] as Boolean
        val date = data[Const.Key.Todo.DATE] as String
        val user = data[Const.Key.Todo.USER] as String
        val timestamp = data[Const.Key.Todo.CREATED_AT]
        var timestampToDate = Date()
        if(timestamp != null) timestampToDate = (timestamp as Timestamp).toDate()
        val createdAt = FormatUtil().formatDate(timestampToDate, FormatUtil.dd_MMM_yyyy)

        return Todo(
            todoId, todo, completed, date, user, createdAt
        )
    }

    // map firestore todoObject list to todoEntity list
    fun toTodoEntityList(data: QuerySnapshot): ArrayList<Todo> {
        val todoEntityList = ArrayList<Todo>()
        for (document in data)
            todoEntityList.add(toTodoEntity(document))

        return todoEntityList
    }
}