package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.view.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.R

class IntroFirstFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_intro_first, container, false)
    }
}