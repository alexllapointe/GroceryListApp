package edu.iu.alex.notesapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth

class UserScreen : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var notesViewModel: NotesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.user_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnLogout = view.findViewById<Button>(R.id.btn_logout)

        notesViewModel = ViewModelProvider(this).get(NotesViewModel::class.java)

        btnLogout.setOnClickListener {
            auth.signOut()

            (activity as? MainActivity)?.isUserLoggedIn = false

            notesViewModel.clearNotes()

            findNavController().navigate(R.id.login_fragment)
        }
    }
}
