package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.view.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.R
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.helper.AppPreferences
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.helper.Toaster
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.view.todo.TodoLocalActivity
import kotlinx.android.synthetic.main.activity_user_form.*

class UserFormActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_form)
        Log.d("USER FORM", "MUNCUL")
        initSharedPreference()
        initView()
    }

    private fun initSharedPreference() {
        AppPreferences.init(this)
    }

    private fun initView() {
        save_name.setOnClickListener {
            val username = et_name.text.toString()
            AppPreferences.username = username
            AppPreferences.isLogin = true
            Toaster(this).showToast("Hello $username")
            intent = Intent(this, TodoLocalActivity::class.java)
            startActivity(intent)
        }
    }
}