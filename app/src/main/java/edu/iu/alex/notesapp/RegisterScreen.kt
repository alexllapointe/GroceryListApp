package edu.iu.alex.notesapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class RegisterScreen : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var registerButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.register_fragment, container, false)
        auth = FirebaseAuth.getInstance()

        emailEditText = view.findViewById(R.id.email)
        passwordEditText = view.findViewById(R.id.password)
        registerButton = view.findViewById(R.id.btn_register)

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                registerUser(email, password)
            } else {
                Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registration successful
                    Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
                } else {
                    // Registration failed
                    Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }
}
