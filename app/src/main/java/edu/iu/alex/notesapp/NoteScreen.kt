package edu.iu.alex.notesapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class NoteScreen : Fragment() {

    private lateinit var viewModel: NotesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(requireActivity()).get(NotesViewModel::class.java)

        val view = inflater.inflate(R.layout.note_fragment, container, false)

        val titleEditText = view.findViewById<EditText>(R.id.editTextTitle)
        val descriptionEditText = view.findViewById<EditText>(R.id.editTextDescription)
        val saveButton = view.findViewById<Button>(R.id.save_button)

        viewModel.selectedNote.observe(viewLifecycleOwner, Observer { note ->
            if (note != null && note.id.isNotEmpty()) {
                titleEditText.setText(note.title)
                descriptionEditText.setText(note.description)
            } else {
                titleEditText.setText("")
                descriptionEditText.setText("")
            }
        })

        // Check if there's a selected note, if not, it's a new note
        if (viewModel.selectedNote.value == null) {
            Log.d("NoteScreen", "Creating a new note")
        }

        saveButton.setOnClickListener {
            val noteTitle = titleEditText.text.toString().trim()
            val noteDescription = descriptionEditText.text.toString().trim()

            if (noteTitle.isNotEmpty() && noteDescription.isNotEmpty()) {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener

                val note = viewModel.selectedNote.value
                if (note == null || note.id.isEmpty()) {
                    val newNoteId = FirebaseDatabase.getInstance().reference.child("users").child(userId).child("notes").push().key ?: return@setOnClickListener
                    val newNote = Note(newNoteId, noteTitle, noteDescription)
                    viewModel.saveNote(userId, newNote, {
                        showToast("Note saved successfully")
                        findNavController().navigate(R.id.main_fragment)
                    }, { exception ->
                        showToast("Failed to save note: ${exception.message}")
                    })
                } else {
                    val updatedNote = note.copy(title = noteTitle, description = noteDescription)
                    viewModel.saveNote(userId, updatedNote, {
                        showToast("Note updated successfully")
                        findNavController().navigate(R.id.main_fragment)
                    }, { exception ->
                        showToast("Failed to update note: ${exception.message}")
                    })
                }
            } else {
                showToast("Please enter a title and description")
            }
        }

        return view
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
