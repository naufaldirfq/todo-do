package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.Todo
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.User
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.databinding.PromptTodoBinding
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.Const
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.firebase.FirestoreService
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.firebase.callback.TodoCallback
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.firebase.callback.TodoListCallback
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.helper.FormatUtil
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.helper.ImageLoader
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.helper.Toaster
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.helper.Validator
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.view.auth.IntroSliderActivity
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.view.setting.SettingsActivity
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.view.todo.TodoAdapter
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.view.todo.TodoClickEvent
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_profile.*
import kotlinx.android.synthetic.main.layout_profile.view.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val remote: FirestoreService by lazy { FirestoreService() }

    private val adapter = TodoAdapter()

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(bottom_app_bar)
        val user : User = getUserFromIntent()
        initGoogleSignInClient()
        initView(user)
        updateStatus(user)
        addTodoListListener(user)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.bottomappbar_menu, menu)
        return true
    }

    private fun getUserFromIntent() : User {
        return intent.getSerializableExtra(Const.Collection.USER) as User
    }

    private fun initGoogleSignInClient() {
        val googleSignInOptions : GoogleSignInOptions = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
    }

    private fun ImageView.load(url: String?){
        ImageLoader.load(url, this)
    }

    private fun initView(user: User) {
        adapter.setListener(object: TodoClickEvent {
            override fun onClickTodo(todo: Todo, action: String, position: Int) {
                when(action) {
                    TodoClickEvent.ACTION_COMPLETE -> toggleMarkAsComplete(todo, position)
                    TodoClickEvent.ACTION_DETAILS -> showDetails(todo)
                    TodoClickEvent.ACTION_EDIT -> editTodo(todo, position)
                    TodoClickEvent.ACTION_DELETE -> deleteTodo(todo, position)
                }
            }
        })

        rv_todo_list.layoutManager = LinearLayoutManager(this)
        rv_todo_list.adapter = adapter


        add_todo.setOnClickListener { addTodo(user) }
        swipe_refresh.setOnRefreshListener {
            Handler().postDelayed(Runnable {
                swipe_refresh.isRefreshing = false
            }
                , 4000)
            loadTodoList(user) }
    }

    private fun addTodo(user: User) {
        val binding = DataBindingUtil.inflate<PromptTodoBinding>(
            layoutInflater, R.layout.prompt_todo, null, false
        )

        val dateNow = FormatUtil().formatDate(Date(), FormatUtil.dd_MMM_yyyy)
//        binding.tietTodoDate.text = SpannableStringBuilder(dateNow)

        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.label_add_todo)
            .setView(binding.root)
            .setPositiveButton(R.string.label_add_todo) {
                    _, _ ->
                swipe_refresh.isRefreshing = true

                val todoTitle = binding.tietTodoTitle.text.toString()
//                val date = binding.tietTodoDate.text.toString()

                val todo = Todo(
                    "", todoTitle, false, "", user.uid!!, ""
                )

                remote.addTodo(todo, object: TodoCallback {
                    override fun onResponse(todo: Todo?, error: String?) {
                        swipe_refresh.isRefreshing = false
                        if(error == null) {
                            Toaster(this@MainActivity).showToast(getString(R.string.add_todo_success_message))
                            img_no_data.visibility = View.INVISIBLE
                            rv_todo_list.visibility = View.VISIBLE
                            adapter.addTodo(todo!!)
                        }else {
                            Toaster(this@MainActivity).showToast(error)
                        }
                    }
                })
            }
            .setNegativeButton(R.string.label_cancel) { _,_-> }
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        }
        dialog.show()

         Validator.forceValidation(arrayOf(binding.tietTodoTitle, binding.tietTodoDate), dialog)
    }

    private fun addTodoListListener(user: User) {
        remote.addTodoListListener(user, object: TodoListCallback {
            override fun onResponse(todoList: ArrayList<Todo>?, error: String?) {
                if (error != null) {
                    Toaster(this@MainActivity).showToast(error)
                }else {
                    if(todoList!!.size > 0) {
                        img_no_data.visibility = View.INVISIBLE
                        rv_todo_list.visibility = View.VISIBLE
                        adapter.setTodoList(todoList)
                        updateStatus(user)
                    }else {
                        img_no_data.visibility = View.VISIBLE
                        rv_todo_list.visibility = View.INVISIBLE
                    }
                }
            }
        })
    }

    private fun loadTodoList(user: User) {
        swipe_refresh.isRefreshing = true
        remote.getTodoList(user, object: TodoListCallback {
            override fun onResponse(todoList: ArrayList<Todo>?, error: String?) {
                swipe_refresh.isRefreshing = false
                if (error != null) {
                    Toaster(this@MainActivity).showToast(error)
                }else {
                    if(todoList!!.size > 0) {
                        img_no_data.visibility = View.INVISIBLE
                        rv_todo_list.visibility = View.VISIBLE
                        adapter.setTodoList(todoList)
                        updateStatus(user)
                    }else {
                        img_no_data.visibility = View.VISIBLE
                        rv_todo_list.visibility = View.INVISIBLE
                    }
                }
            }
        })
    }

    private fun updateStatus(user: User) {

        container_profile.img_profile.load(user.image)
        container_profile.tv_name.text = user.name
        container_profile.visibility = View.VISIBLE

        var status = getString(R.string.label_no_todo_list_found)
        if(adapter.itemCount > 0) {
            status = "${adapter.itemCount} to-do(s) found"
        }

        container_profile.tv_status.text = status

        val calender = Calendar.getInstance()
        val day = calender.get(Calendar.DAY_OF_MONTH)

        container_profile.tv_dd.text = day.toString()
        container_profile.tv_MMM.text = FormatUtil().toMonth(calender.time)
        container_profile.tv_day.text = FormatUtil().toDay(calender.time)
    }

    private fun toggleMarkAsComplete(todo: Todo, position: Int) {

    }

    private fun showDetails(todo: Todo) {

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        Log.d("ON OPTIONS ITEM", "CLICKED")
        when (item!!.itemId) {
            R.id.app_bar_settings -> {
                goToSettingsActivity()
                return true

            }
            R.id.app_bar_delete_todo -> {
                deleteAllTodo()
                return true

            }
            R.id.app_bar_edit_todo -> {
                markAllAsCompletedTodo()
                return true
            }
            R.id.app_bar_sign_out -> {
                signOut()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteAllTodo() {

    }

    private fun markAllAsCompletedTodo() {

    }

    private fun editTodo(todo: Todo, position: Int) {

    }

    private fun deleteTodo(todo: Todo, position: Int) {
    }

    private fun goToSettingsActivity(): Boolean {
        intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
        return true
    }


    override fun onAuthStateChanged(p0: FirebaseAuth) {
        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
        if (firebaseUser == null) {
            goToAuthInActivity()
        }
    }

    private fun goToAuthInActivity() {
        intent = Intent(this, IntroSliderActivity::class.java)
        startActivity(intent)
    }

    private fun signOut() {
        singOutFirebase()
        signOutGoogle()
        val i = baseContext.packageManager
            .getLaunchIntentForPackage(baseContext.packageName)
        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(i)
    }

    private fun singOutFirebase() {
        firebaseAuth.signOut()
    }

    private fun signOutGoogle() {
        googleSignInClient.signOut()
    }


}