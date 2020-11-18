package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.view.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.MainActivity
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.R
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.SplashScreenViewModel
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.User
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.Const
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.view.todo.TodoLocalActivity
import kotlinx.android.synthetic.main.activity_intro_slider.*

class IntroSliderActivity : AppCompatActivity() {

    //Google Sign In Client
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private lateinit var googleSignInClient: GoogleSignInClient
    //Firebase Auth
    private lateinit var mAuth: FirebaseAuth

    private lateinit var authViewModel: AuthViewModel

    private lateinit var splashScreenViewModel: SplashScreenViewModel

    private val fragmentList = ArrayList<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // making the status bar transparent
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        setContentView(R.layout.activity_intro_slider)

        mAuth = FirebaseAuth.getInstance()

        val adapter =
            IntroSliderAdapter(
                this
            )
        vpIntroSlider.adapter = adapter
        fragmentList.addAll(listOf(
            IntroFirstFragment(),
            IntroSecondFragment()
        ))
        adapter.setFragmentList(fragmentList)
        indicatorLayout.setIndicatorCount(adapter.itemCount)
        indicatorLayout.selectCurrentPosition(0)
        registerListeners()

        initAuthViewModel()
        initGoogleSignInClient()

    }

    private fun initAuthViewModel() {
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        splashScreenViewModel = ViewModelProvider(this).get(SplashScreenViewModel::class.java)
    }

    private fun initGoogleSignInClient() {
        val googleSignInOptions : GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
    }

    private fun signIn() {
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

    private fun goToUserFormActivity() {
        intent = Intent(this, UserFormActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun registerListeners() {
        vpIntroSlider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                indicatorLayout.selectCurrentPosition(position)
                if (position < fragmentList.lastIndex) {
                    tvSkip.visibility = View.VISIBLE
                    tvNext.text = "Next"
                } else {
                    tvSkip.visibility = View.VISIBLE
                    tvNext.text = "Continue With Google"
                }
            }
        })
        tvSkip.setOnClickListener {
            val position = vpIntroSlider.currentItem
            vpIntroSlider.currentItem = position + 1
            goToUserFormActivity()
        }
        tvNext.setOnClickListener {
            val position = vpIntroSlider.currentItem
            if (position < fragmentList.lastIndex) {
                vpIntroSlider.currentItem = position + 1
            } else {
                signIn()
            }
        }
    }
}