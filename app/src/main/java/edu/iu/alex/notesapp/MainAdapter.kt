package edu.iu.alex.notesapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView

class MainAdapter(private var noteList: List<Note>, private val viewModel: NotesViewModel) : RecyclerView.Adapter<MainAdapter.NoteViewHolder>() {

    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val noteTitle: TextView = view.findViewById(R.id.note_title)
        val noteDesc: TextView = view.findViewById(R.id.note_desc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = noteList[position]
        holder.noteTitle.text = note.title
        holder.noteDesc.text = note.description
        holder.itemView.setOnClickListener {
            // Use the ViewModel to handle note selection
            viewModel.selectNote(note)

            val bundle = bundleOf("title" to note.title, "description" to note.description)
            it.findNavController().navigate(R.id.mainFragment_to_NoteFragment, bundle)

        }
    }

    override fun getItemCount() = noteList.size

    fun updateNotes(newNotes: List<Note>) {
        noteList = newNotes
        notifyDataSetChanged()
    }
}
