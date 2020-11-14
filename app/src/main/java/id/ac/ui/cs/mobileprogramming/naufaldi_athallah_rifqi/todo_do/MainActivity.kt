package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.User
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.Constants.USER
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.helper.ImageLoader
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.view.setting.SettingsActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_profile.*

class MainActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val user : User = getUserFromIntent()
        initGoogleSignInClient()
        setMessageToMessageTextView(user)
        initView()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.bottomappbar_menu, menu)
        actionBar?.elevation = 0f
        return true
    }

    private fun getUserFromIntent() : User {
        return intent.getSerializableExtra(USER) as User
    }

    private fun initGoogleSignInClient() {
        val googleSignInOptions : GoogleSignInOptions = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
    }

    private fun setMessageToMessageTextView(user: User) {
        val message = user.name
        tv_name.text = message
        tv_status.text = "Today you have 99 tasks"
        tv_dd.text = "12"
        tv_MMM.text = "Nov"
        tv_day.text = "Thursday"
        img_profile.load(user.image)
    }

    private fun ImageView.load(url: String?){
        ImageLoader.load(url, this)
    }

    private fun initView() {
        add_todo.setOnClickListener { addTodo() }
    }

    private fun addTodo() {
        //TODO: bikin logic masukin todo
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_edit_todo -> editTodo()
            R.id.app_bar_delete_todo -> deleteTodo()
            R.id.app_bar_settings -> goToSettingsActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun editTodo() {

    }

    private fun deleteTodo() {

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


}