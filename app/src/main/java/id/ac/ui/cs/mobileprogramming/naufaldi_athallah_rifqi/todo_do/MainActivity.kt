package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.User
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.Constants.USER
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.helper.ImageLoader
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.view.auth.IntroSliderActivity
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.view.setting.SettingsActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_profile.*

class MainActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(bottom_app_bar)
        initView()
        val user : User = getUserFromIntent()
        initGoogleSignInClient()
        setMessageToMessageTextView(user)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.bottomappbar_menu, menu)
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
        Toast.makeText(this, "ADD CLICKED", Toast.LENGTH_LONG).show()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        Log.d("ON OPTIONS ITEM", "CLICKED")
        when (item!!.itemId) {
            R.id.app_bar_settings -> {
                goToSettingsActivity()
                return true

            }
            R.id.app_bar_delete_todo -> {
                deleteTodo()
                return true

            }
            R.id.app_bar_edit_todo -> {
                editTodo()
                return true
            }
            R.id.app_bar_sign_out -> {
                signOut()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun editTodo() {

    }

    private fun deleteTodo() {

    }

    private fun goToSettingsActivity(): Boolean {
        Log.d("SETTINGS", "GO TO SETTINGS")
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