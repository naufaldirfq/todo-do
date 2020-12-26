package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.view.todo

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.SpannableStringBuilder
import android.text.format.DateFormat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.MainActivity
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.R
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.User
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.local.TodoLocal
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.databinding.PromptTodoBinding
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.Const
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.helper.*
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.view.auth.AuthViewModel
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.view.opengl.GL2JNIActivity
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.view.profile.ProfileActivity
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.view.setting.SettingsActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_profile.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class TodoLocalActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private var day = 0
    private var month: Int = 0
    private var year: Int = 0
    private var hour: Int = 0
    private var minute: Int = 0
    private var textDate: String = ""

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "10001"
        private val default_notification_channel_id = "default"
    }

    var todo: TodoLocal? = null

    private val adapter = TodoLocalAdapter()
    private lateinit var binding: PromptTodoBinding
    private val calendar: Calendar = Calendar.getInstance()

    private lateinit var todoViewModel: TodoViewModel
    private lateinit var cm: ConnectivityManager

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
        initConnectionManager()
        initView()
        updateStatus()
        loadTodoList()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.bottomappbarlocal_menu, menu)
        return true
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        hour = calendar.get(Calendar.HOUR)
        minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            this@TodoLocalActivity, this@TodoLocalActivity, hour, minute,
            DateFormat.is24HourFormat(this)
        )
        timePickerDialog.show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        updateDateInView()
    }

    private fun scheduleNotification(notification: Notification, delay: Long) {
        intent = Intent(this, MyNotificationPublisher::class.java)
        intent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, 1)
        intent.putExtra(MyNotificationPublisher.NOTIFICATION, notification)
        val pendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, delay, pendingIntent)
    }

    private fun getNotification(content: String, title: String): Notification {
        val builder : NotificationCompat.Builder = NotificationCompat.Builder(this, default_notification_channel_id)
        builder.setContentTitle(title)
        builder.setContentText(content)
        builder.setSmallIcon(R.drawable.ic_notif)
        builder.setAutoCancel(true)
        builder.setChannelId(NOTIFICATION_CHANNEL_ID)
        return builder.build()
    }

    private fun ImageView.load(url: String?) {
        ImageLoader.load(url, this)
    }

    private fun initBinding() {
        binding = DataBindingUtil.inflate(
            layoutInflater, R.layout.prompt_todo, null, false
        )
    }

    private fun initView() {
        adapter.setListener(object : TodoLocalClickEvent {
            override fun onClickTodoLocal(todoLocal: TodoLocal, action: String, position: Int) {
                when (action) {
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
            loadTodoList()
            updateStatus()
        }
    }

    private fun initViewModel() {
        todoViewModel = ViewModelProvider(this).get(TodoViewModel::class.java)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
    }

    private fun initSharedPreferences() {
        AppPreferences.init(this)
    }

    private fun initConnectionManager() {
        cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private fun initGoogleSignInClient() {
        val googleSignInOptions: GoogleSignInOptions = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
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
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.label_add_todo)
            .setView(binding.root)
            .setPositiveButton(R.string.label_add_todo) { _, _ ->
                swipe_refresh.isRefreshing = true

                val todoTitle = binding.tietTodoTitle.text.toString()
                val todoDate = binding.tietTodoDate.text.toString()
                val id = todo?.id
                val todo = TodoLocal(
                    id, todoTitle, false, todoDate, ""
                )
                todoViewModel.addTodo(todo)
                swipe_refresh.isRefreshing = false
                img_no_data.visibility = View.INVISIBLE
                txt_nodata.visibility = View.INVISIBLE
                rv_todo_list.visibility = View.VISIBLE
                adapter.addTodo(todo)
                Log.d("CALENDAR TIME", calendar.time.time.toString())
                scheduleNotification(getNotification(todoDate, todoTitle), calendar.time.time)
            }
            .setNegativeButton(R.string.label_cancel) { _, _ -> }
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        }
        dialog.show()

        Validator.forceValidation(arrayOf(binding.tietTodoTitle, binding.tietTodoDate), dialog)
    }

    private fun updateDateInView() {
        val format = "dd/MM/yyyy hh:mm a"
        val sdf = SimpleDateFormat(format, Locale.US)
        textDate = sdf.format(calendar.time)
        binding.tietTodoDate.text = SpannableStringBuilder(textDate)
    }

    private fun loadTodoList() {
        swipe_refresh.isRefreshing = true
        todoViewModel.getAllTodoList().observe(this, Observer {
            swipe_refresh.isRefreshing = false
            if (it.isNotEmpty()) {
                img_no_data.visibility = View.INVISIBLE
                rv_todo_list.visibility = View.VISIBLE
                adapter.setTodoList(it)
                updateStatus()
            } else {
                img_no_data.visibility = View.VISIBLE
                txt_nodata.visibility = View.VISIBLE
                txt_nodata.text = "Belum ada Todo!"
                rv_todo_list.visibility = View.INVISIBLE
            }
        })
    }

    private fun updateStatus() {
        container_profile.img_profile.load(AppPreferences.image)
        container_profile.tv_name.text = AppPreferences.username
        container_profile.visibility = View.VISIBLE
        container_profile.img_profile.setOnClickListener {
            goToProfile()
        }

        var status = getString(R.string.label_no_todo_list_found)
        if (adapter.itemCount > 0) {
            status = "${adapter.itemCount} to-do(s) found"
        }

        container_profile.tv_status.text = status

        val calender = Calendar.getInstance()
        val day = calender.get(Calendar.DAY_OF_MONTH)

        container_profile.tv_dd.text = day.toString()
        container_profile.tv_MMM.text = FormatUtil().toMonth(calender.time)
        container_profile.tv_day.text = FormatUtil().toDay(calender.time)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val builder: NetworkRequest.Builder = NetworkRequest.Builder()
            cm.registerNetworkCallback(

                builder.build(),
                object : ConnectivityManager.NetworkCallback() {

                    override fun onAvailable(network: Network) {
                        lifecycleScope.launch {
                            Log.i("MainActivity", "onAvailable!")

                            // check if NetworkCapabilities has TRANSPORT_WIFI
                            val isWifi:Boolean = cm.getNetworkCapabilities(network).hasTransport(
                                NetworkCapabilities.TRANSPORT_WIFI)

                            doSomething(true, isWifi)
                        }
                    }

                    override fun onLost(network: Network) {
                        lifecycleScope.launch {
                            Log.i("MainActivity", "onLost!")
                            doSomething(false)
                        }
                    }
                }
            )
        }
    }

    private suspend fun doSomething(isConnected:Boolean, isWifi:Boolean= false){
        withContext(Dispatchers.Main){
            if(isConnected) {
                img_no_data.visibility = View.INVISIBLE
                txt_nodata.visibility = View.INVISIBLE
                rv_todo_list.visibility = View.VISIBLE
            }else {
                img_no_data.visibility = View.VISIBLE
                txt_nodata.visibility = View.VISIBLE
                txt_nodata.text = "Tidak ada koneksi internet!"
                rv_todo_list.visibility = View.INVISIBLE
            }
        }
    }

    private fun goToProfile() {
        intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun toggleMarkAsComplete(todoLocal: TodoLocal, position: Int) {
        val id = todoLocal.id
        Log.d("ISCOMPLETED", todoLocal.isCompleted.toString())
        val todo: TodoLocal = if (todoLocal.isCompleted) {
            TodoLocal(id, todoLocal.todo, false, todoLocal.date, "")
        } else {
            TodoLocal(id, todoLocal.todo, true, todoLocal.date, "")
        }
        Log.d("Updated IsCompleted>", todo.isCompleted.toString())
        todoViewModel.updateTodo(todo)

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
            R.id.app_bar_opengl -> {
                showOpenGLExample()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteAllTodo() {
        swipe_refresh.isRefreshing = true
        todoViewModel.deleteAllTodoList()
        adapter.deleteAllTodo()
        swipe_refresh.isRefreshing = false
        updateStatus()
    }

    private fun markAllAsCompletedTodo() {

    }

    private fun showOpenGLExample() {
        intent = Intent(this, GL2JNIActivity::class.java)
        startActivity(intent)
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
                DatePickerDialog(this@TodoLocalActivity, this@TodoLocalActivity, year, month, day)
            datePickerDialog.show()
        }


        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.label_add_todo)
            .setView(binding.root)
            .setPositiveButton(R.string.label_update_todo) { _, _ ->
                swipe_refresh.isRefreshing = true

                val todoTitle = binding.tietTodoTitle.text.toString()
                val todoDateUpdated = binding.tietTodoDate.text.toString()
                Log.d("UPDATED TODO", todoTitle)
                Log.d("UPDATED TIME", todoDateUpdated)
                val id = todoLocal.id
                Log.d("TODO ID", id.toString())
                val todo = TodoLocal(
                    id, todoTitle, false, todoDateUpdated, ""
                )
                todoViewModel.updateTodo(todo)
                swipe_refresh.isRefreshing = false
                scheduleNotification(getNotification(todoDateUpdated, todoTitle), calendar.time.time)
            }
            .setNegativeButton(R.string.label_cancel) { _, _ -> }
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        }
        dialog.show()

        Validator.forceValidation(arrayOf(binding.tietTodoTitle, binding.tietTodoDate), dialog)

    }

    private fun deleteTodo(todoLocal: TodoLocal, position: Int) {
        swipe_refresh.isRefreshing = true
        todoViewModel.deleteTodo(todoLocal)
        adapter.deleteTodo(todoLocal)
        updateStatus()
        swipe_refresh.isRefreshing = false
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
        val googleTokenId: String? = acct.idToken
        val googleAuthCredential: AuthCredential =
            GoogleAuthProvider.getCredential(googleTokenId, null)
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

    private fun createNewUser(authenticatedUser: User) {
        authViewModel.createUser(authenticatedUser)
        authViewModel.createdUserLiveData.observe(this, Observer {
            if (it.isCreated) {
                Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
            }
            goToMainActivity(it)
        })
    }

    private fun goToMainActivity(user: User) {
        intent = Intent(this, MainActivity::class.java)
        intent.putExtra(Const.Collection.USER, user)
        startActivity(intent)
        finish()
    }

}