package edu.iu.alex.notesapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NotesViewModel : ViewModel() {
    private val _selectedNote = MutableLiveData<Note>()
    val selectedNote: LiveData<Note> = _selectedNote

    private val _notesList = MutableLiveData<List<Note>>()
    val notesList: LiveData<List<Note>> = _notesList


    fun selectNote(note: Note?) {
        _selectedNote.value = note
    }

    fun clearNotes() {
        _selectedNote.value = Note("", "", "")
        _notesList.value = emptyList()
    }

    private fun deleteNote(noteId: String) {

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseDatabase.getInstance().reference
            .child("users").child(userId).child("notes").child(noteId)
            .removeValue()

    }

    fun saveNote(userId: String, note: Note, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("users").child(userId).child("notes").child(note.id)
        databaseReference.setValue(note)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
    fun deleteSelectedNote() {
        selectedNote.value?.let { note ->
            deleteNote(note.id)
        }
    }

    fun refreshNotesList() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseReference = FirebaseDatabase.getInstance().reference
        databaseReference.child("users").child(userId).child("notes")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val notesList = mutableListOf<Note>()
                    for (noteSnapshot in snapshot.children) {
                        val note = noteSnapshot.getValue(Note::class.java)
                        note?.let {
                            notesList.add(it)
                            Log.d("NotesViewModel", "Note received: ${it.title}")
                        }
                    }
                    _notesList.value = notesList
                    Log.d("NotesViewModel", "Total notes fetched: ${notesList.size}")
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("NotesViewModel", "Error fetching notes: ${databaseError.toException()}")

                }
            })
    }
}
