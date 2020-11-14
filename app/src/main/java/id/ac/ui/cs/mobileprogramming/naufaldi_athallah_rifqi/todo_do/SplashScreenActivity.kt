package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.User
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.Constants.USER
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.view.auth.IntroSliderActivity

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var splashScreenViewModel: SplashScreenViewModel
    // Splash screen timer
    private val SPLASH_TIME_OUT = 3000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        setContentView(R.layout.splash_screen)
        initSplashScreenViewModel()
        Handler().postDelayed(
            {
//                intent = Intent(this, IntroSliderActivity::class.java)
//                startActivity(intent)
                checkIfUserIsAuthenticated()
            }, SPLASH_TIME_OUT)
    }

    private fun initSplashScreenViewModel() {
        Log.d("INIT", "INIT VIEW MODEL")
        splashScreenViewModel = ViewModelProvider(this).get(SplashScreenViewModel::class.java)
    }

    private fun checkIfUserIsAuthenticated() {
        Log.d("INIT", "CHECK USER")
        splashScreenViewModel.checkIfUserIsAuthenticated()
        splashScreenViewModel.isUserAuthenticatedLiveData.observe(this, Observer {
            Log.d("OBSERVE", "OBSERVE IS AUTHENTICATED OR NOT")
            if (!it?.isAuthenticated!!) {
                Log.d("OBSERVE", "OBSERVE IS NOT AUTHENTICATED")
                goToIntroActivity()
                finish()
            } else {
                Log.d("OBSERVE", "OBSERVE IS AUTHENTICATED")
                getUserFromDatabase(it.uid)
            }
        })
    }

    private fun goToIntroActivity() {
        Log.d("INTENT", "MASUK INTRO")
        intent = Intent(this, IntroSliderActivity::class.java)
        startActivity(intent)
    }

    private fun getUserFromDatabase(uid: String) {
        Log.d("GET", "GET USER FROM DATABASE")
        Log.d("UID", uid)
        splashScreenViewModel.setUid(uid)
        splashScreenViewModel.userLiveData.observe(this, Observer {
            Log.d("GO", "GO TO MAIN AFTER GET USER")
            goToMainActivity(it)
            finish()
        })
    }

    private fun goToMainActivity(user : User) {
        Log.d("INTENT", "MASUK MAIN ACTIVITY")
        intent = Intent(this, MainActivity::class.java)
        intent.putExtra(USER, user)
        startActivity(intent)
    }
}