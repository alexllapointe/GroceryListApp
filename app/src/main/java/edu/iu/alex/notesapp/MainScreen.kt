package edu.iu.alex.notesapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.database.*
import com.google.firebase.auth.FirebaseAuth

class MainScreen : Fragment() {

    private lateinit var viewModel: NotesViewModel
    private lateinit var mainAdapter: MainAdapter
    private lateinit var emptyTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(NotesViewModel::class.java)

        //Base TextView, appears when USER is NOT logged in.
        emptyTextView = view.findViewById(R.id.empty_text_view)

        // Clear notes when the fragment view is created
        if (FirebaseAuth.getInstance().currentUser == null) {
            viewModel.clearNotes()
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_notes)
        mainAdapter = MainAdapter(listOf(), viewModel)
        recyclerView.adapter = mainAdapter
        recyclerView.layoutManager = StaggeredGridLayoutManager(
            2,
            StaggeredGridLayoutManager.VERTICAL
        ) // or any other layout manager

        viewModel.notesList.observe(viewLifecycleOwner, Observer { notesList ->
            mainAdapter.updateNotes(notesList)
        })

        // Fetch the latest notes list (after clearing)
        viewModel.refreshNotesList()
        checkIfUserLoggedIn()
    }

        fun checkIfUserLoggedIn() {
            if (FirebaseAuth.getInstance().currentUser == null) {
                emptyTextView.visibility = View.VISIBLE
                viewModel.clearNotes()
            } else {
                emptyTextView.visibility = View.GONE
                viewModel.refreshNotesList()
            }
        }
    }



