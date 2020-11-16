package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.firebase

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.Todo
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.User
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.Const
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.firebase.callback.TodoCallback
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.firebase.callback.TodoListCallback
import timber.log.Timber
import java.util.ArrayList

class FirestoreService : FirestoreMapper {

    // firestore reference
    private val db = FirebaseFirestore.getInstance()

    //todoList documents listener
    private var todoListener: ListenerRegistration? = null

    // add new todoItem to firestore
    fun addTodo(todo: Todo, callback: TodoCallback) {
        val todoObject = toTodoObject(todo)

        db.collection(Const.Collection.TODO)
            .add(todoObject)
            .addOnSuccessListener {
                todo.id = it.id
                callback.onResponse(todo, null)
            }
            .addOnFailureListener {
                Timber.e(it)
                callback.onResponse(todo, "Failed. Error: ${it.message}")
            }
    }

    // update an existing todoITem
    fun updateTodo(todo: Todo, callback: TodoCallback) {
        val todoDoc = db.collection(Const.Collection.TODO).document(todo.id)

        val tasks = ArrayList<Task<Void>>()
        tasks.add(todoDoc.update(Const.Key.Todo.TODO, todo.todo))
        tasks.add(todoDoc.update(Const.Key.Todo.DATE, todo.date))

        Tasks.whenAllSuccess<Void>(tasks).addOnSuccessListener {
            callback.onResponse(todo, null)
        }.addOnFailureListener {
            Timber.e(it)
            callback.onResponse(null, "Opps! ${it.message}")
        }
    }

    // update the complete status of bulk todoItems
    fun markTodoListAsComplete(todoList: ArrayList<Todo>, callback: TodoListCallback) {
        val todoDb = db.collection(Const.Collection.TODO)

        val tasks = ArrayList<Task<Void>>()
        todoList.forEach {
            tasks.add(todoDb.document(it.id).update(Const.Key.Todo.COMPLETED, it.completed))
        }

        Tasks.whenAllSuccess<Void>(tasks).addOnSuccessListener {
            callback.onResponse(todoList, null)
        }.addOnFailureListener {
            Timber.e(it)
            callback.onResponse(null, "Opps! ${it.message}")
        }
    }

    // delete todoItem
    fun deleteTodo(todo: Todo, callback: TodoCallback) {
        val todoDb = db.collection(Const.Collection.TODO).document(todo.id)

        todoDb
            .delete()
            .addOnSuccessListener {
                callback.onResponse(todo, null)
            }
            .addOnFailureListener {
                Timber.e(it)
                callback.onResponse(todo, "Opps! ${it.message}")
            }
    }

    // delete bulk todoItems
    fun deleteTodoList(todoList: ArrayList<Todo>, callback: TodoListCallback) {
        val todoDb = db.collection(Const.Collection.TODO)

        val tasks = ArrayList<Task<Void>>()
        todoList.forEach {
            tasks.add(todoDb.document(it.id).delete())
        }

        Tasks.whenAllSuccess<Void>(tasks).addOnSuccessListener {
            callback.onResponse(todoList, null)
        }.addOnFailureListener {
            Timber.e(it)
            callback.onResponse(null, "Opps! ${it.message}")
        }
    }

    // get todoList of a particuler user
    fun getTodoList(user: User, callback: TodoListCallback) {
        val todoDb = db.collection(Const.Collection.TODO)

        todoDb
            .whereEqualTo(Const.Key.Todo.USER, user.uid)
            .get()
            .addOnSuccessListener {
                callback.onResponse(toTodoEntityList(it), null)
            }
            .addOnFailureListener {
                callback.onResponse(null, "Failed. Error: ${it.message}")
            }
    }

    // register a listener for getting the live changes of todoDocuments stored in firestore
    fun addTodoListListener(user: User, callback: TodoListCallback) {
        val query = db.collection(Const.Collection.TODO)
            .whereEqualTo(Const.Key.Todo.USER, user.uid)

        todoListener = query.addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
            if (e != null) {
                Timber.e("Listen failed. %s", e.toString())
                callback.onResponse(null, "Failed. Error: ${e.message}")
                return@EventListener
            }

            callback.onResponse(toTodoEntityList(value!!), null)
        })
    }

    // unregister the listener
    fun removeTodoListListener() {
        todoListener?.remove()
    }
}