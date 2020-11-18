package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.view.todo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.format.DateFormat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.MainActivity
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.R
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.SplashScreenViewModel
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.local.TodoLocal
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.User
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.databinding.PromptTodoBinding
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.Const
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.firebase.callback.TodoCallback
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.firebase.callback.TodoListCallback
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.helper.*
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.view.auth.AuthViewModel
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.view.auth.IntroSliderActivity
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.view.profile.ProfileViewModel
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.view.setting.SettingsActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_profile.view.*
import kotlinx.android.synthetic.main.prompt_todo.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TodoLocalActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private var day = 0
    private var month: Int = 0
    private var year: Int = 0
    private var hour: Int = 0
    private var minute: Int = 0
    private var myDay = 0
    private var myMonth: Int = 0
    private var myYear: Int = 0
    private var myHour: Int = 0
    private var myMinute: Int = 0
    private var textDate: String = ""

    private val adapter = TodoLocalAdapter()
    private lateinit var binding: PromptTodoBinding
    private val calendar: Calendar = Calendar.getInstance()
    private lateinit var todoViewModel: TodoViewModel
    private lateinit var userViewModel: ProfileViewModel

    private lateinit var googleSignInClient: GoogleSignInClient
    //Firebase Auth
    private lateinit var mAuth: FirebaseAuth

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(bottom_app_bar)
        initViewModel()
        initGoogleSignInClient()
        initSharedPreferences()
//        initBinding()
        initView()
        updateStatus()
        loadAllTodoList()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.bottomappbarlocal_menu, menu)
        return true
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        myDay = dayOfMonth
        myYear = year
        myMonth = month+1
        hour = calendar.get(Calendar.HOUR)
        minute = calendar.get(Calendar.MINUTE)
//        updateDateInView()
        Log.d("TODAY", DateFormat.format(FormatUtil.dd_MMM_yyyy, Date()).toString())
//        val binding = DataBindingUtil.inflate<PromptTodoBinding>(
//            layoutInflater, R.layout.prompt_todo, null, false
//        )
        textDate = "$myDay/$myMonth/$myYear"
        Log.d("INPUTTED DATE", textDate)
        binding.tietTodoDate.text = SpannableStringBuilder(textDate)
        Log.d("DATE ON FORM", binding.tietTodoDate.text.toString())
        val timePickerDialog = TimePickerDialog(this@TodoLocalActivity, this@TodoLocalActivity, hour, minute,
            DateFormat.is24HourFormat(this))
        timePickerDialog.show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        myHour = hourOfDay
        myMinute = minute
//        textView.text = "Year: " + myYear + "\n" + "Month: " + myMonth + "\n" + "Day: " + myDay + "\n" + "Hour: " + myHour + "\n" + "Minute: " + myMinute
    }

    private fun ImageView.load(url: String?){
        ImageLoader.load(url, this)
    }

    private fun initBinding() {
        binding = DataBindingUtil.inflate(
            layoutInflater, R.layout.prompt_todo, null, false
        )
    }

    private fun initView() {
        adapter.setListener(object: TodoLocalClickEvent {
            override fun onClickTodoLocal(todoLocal: TodoLocal, action: String, position: Int) {
                when(action) {
                    TodoClickEvent.ACTION_COMPLETE -> toggleMarkAsComplete(todoLocal, position)
                    TodoClickEvent.ACTION_DETAILS -> showDetails(todoLocal)
                    TodoClickEvent.ACTION_EDIT -> editTodo(todoLocal, position)
                    TodoClickEvent.ACTION_DELETE -> deleteTodo(todoLocal, position)
                }
            }
        })

        rv_todo_list.layoutManager = LinearLayoutManager(this)
        rv_todo_list.adapter = adapter

        add_todo.setOnClickListener { addTodo() }
        swipe_refresh.setOnRefreshListener {
            Handler().postDelayed(Runnable {
                swipe_refresh.isRefreshing = false
            }
                , 4000)
            loadTodoList() }
    }

    private fun initViewModel() {
        todoViewModel = ViewModelProvider(this).get(TodoViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
    }

    private fun initSharedPreferences() {
        AppPreferences.init(this)
    }

    private fun initGoogleSignInClient() {
        val googleSignInOptions : GoogleSignInOptions = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
    }

    private fun loadAllTodoList() {
        todoViewModel.getAllTodoList().observe(this, Observer {
            adapter.setTodoList(it)
            if(it.isNotEmpty()) {
                img_no_data.visibility = View.INVISIBLE
                rv_todo_list.visibility = View.VISIBLE
                adapter.setTodoList(it)
                updateStatus()
            }else {
                img_no_data.visibility = View.VISIBLE
                rv_todo_list.visibility = View.INVISIBLE
            }
        })
    }

    private fun addTodo() {
        initBinding()
        binding.tietTodoDate.setOnClickListener {
            Log.d("TODO DATE", "Clicked")
            val calendar: Calendar = Calendar.getInstance()
            day = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
            val datePickerDialog =
                DatePickerDialog(this@TodoLocalActivity, this@TodoLocalActivity, year, month, day)
            datePickerDialog.show()
            Log.d("INPUTTED DATE", textDate)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.label_add_todo)
            .setView(binding.root)
            .setPositiveButton(R.string.label_add_todo) {
                    _, _ ->
                swipe_refresh.isRefreshing = true

                val todoTitle = binding.tietTodoTitle.text.toString()
                val date = binding.tietTodoDate.text.toString()

                val todo = TodoLocal(
                    todoTitle, false, date, ""
                )
                todoViewModel.addTodo(todo)
                swipe_refresh.isRefreshing = false
                img_no_data.visibility = View.INVISIBLE
                rv_todo_list.visibility = View.VISIBLE
                adapter.addTodo(todo)
            }
            .setNegativeButton(R.string.label_cancel) { _, _-> }
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        }
        dialog.show()

        Validator.forceValidation(arrayOf(binding.tietTodoTitle, binding.tietTodoDate), dialog)
    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        tiet_todo_date!!.text = SpannableStringBuilder(sdf.format(calendar.time))
    }

    private fun loadTodoList() {
        swipe_refresh.isRefreshing = true
        todoViewModel = ViewModelProvider(this).get(TodoViewModel::class.java)
        todoViewModel.getAllTodoList().observe(this, Observer {
            adapter.setTodoList(it)
            if(it.isNotEmpty()) {
                img_no_data.visibility = View.INVISIBLE
                rv_todo_list.visibility = View.VISIBLE
                adapter.setTodoList(it)
                updateStatus()
            }else {
                img_no_data.visibility = View.VISIBLE
                rv_todo_list.visibility = View.INVISIBLE
            }
        })
    }

    private fun updateStatus() {
        container_profile.img_profile.load("")
        container_profile.tv_name.text = AppPreferences.username
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

    private fun toggleMarkAsComplete(todoLocal: TodoLocal, position: Int) {

    }

    private fun showDetails(todoLocal: TodoLocal) {

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
            R.id.app_bar_sign_in_with_google -> {
                signInWithGoogle()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteAllTodo() {

    }

    private fun markAllAsCompletedTodo() {

    }

    private fun editTodo(todoLocal: TodoLocal, position: Int) {
        initBinding()
        val currentTitle = todoLocal.todo
        val todoDate = todoLocal.date
        binding.tietTodoDate.text = SpannableStringBuilder(todoDate)
        binding.tietTodoTitle.text = SpannableStringBuilder(currentTitle)
        binding.tietTodoTitle.setSelection(todoLocal.todo.length)

        binding.tietTodoDate.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            day = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
            val datePickerDialog =
                DatePickerDialog(this@TodoLocalActivity, this@TodoLocalActivity, year, month,day)
            datePickerDialog.show()
        }


        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.label_add_todo)
            .setView(binding.root)
            .setPositiveButton(R.string.label_update_todo) {
                    _, _ ->
                swipe_refresh.isRefreshing = true

                val todoTitle = binding.tietTodoTitle.text.toString()
                val date = binding.tietTodoDate.text.toString()

                val todo = TodoLocal(
                    todoTitle, false, date, ""
                )
                todoViewModel.updateTodo(todo)
                swipe_refresh.isRefreshing = false
            }
            .setNegativeButton(R.string.label_cancel) { _, _-> }
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        }
        dialog.show()

        Validator.forceValidation(arrayOf(binding.tietTodoTitle, binding.tietTodoDate), dialog)

    }

    private fun deleteTodo(todoLocal: TodoLocal, position: Int) {

    }

    private fun goToSettingsActivity(): Boolean {
        Log.d("SETTINGS", "GO TO SETTINGS")
        intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
        return true
    }

    private fun signInWithGoogle() {
        AppPreferences.isLogin = false
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, Const.RequestCode.RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == Const.RequestCode.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                getGoogleAuthCredential(account!!)
//                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("Login", "Google sign in failed", e)
                // ...
            }

        }
    }

    private fun getGoogleAuthCredential(acct: GoogleSignInAccount) {
        val googleTokenId : String? = acct.idToken
        val googleAuthCredential : AuthCredential = GoogleAuthProvider.getCredential(googleTokenId, null)
        signInWithGoogleAuthCredential(googleAuthCredential)
    }

    private fun signInWithGoogleAuthCredential(googleAuthCredential: AuthCredential) {
        authViewModel.signInWithGoogle(googleAuthCredential)
        authViewModel.authenticatedUserLiveData.observe(this, Observer {
            print("OBSERVE SIGN IN GOOGLE")
            if (it.isNew) {
                createNewUser(it)
            } else {
                goToMainActivity(it)
            }
        })
    }

    private fun createNewUser(authenticatedUser : User) {
        authViewModel.createUser(authenticatedUser)
        authViewModel.createdUserLiveData.observe(this, Observer {
            if (it.isCreated) {
                Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
            }
            goToMainActivity(it)
        })
    }

    private fun goToMainActivity(user : User) {
        intent = Intent(this, MainActivity::class.java)
        intent.putExtra(Const.Collection.USER, user)
        startActivity(intent)
        finish()
    }

}