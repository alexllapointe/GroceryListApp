package edu.iu.alex.notesapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController

class MainActivity : AppCompatActivity() {

    private lateinit var notesViewModel: NotesViewModel
    var isUserLoggedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notesViewModel = ViewModelProvider(this).get(NotesViewModel::class.java)

        val toolbar = findViewById<Toolbar>(R.id.top_toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = "Notes"

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController



        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar.setupWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.note_fragment -> {
                    toolbar.menu.findItem(R.id.action_delete_note)?.isVisible = true
                    toolbar.menu.findItem(R.id.action_add_user)?.isVisible = false
                }

                R.id.login_fragment -> {
                    isUserLoggedIn = false
                }

                else -> {
                    toolbar.menu.findItem(R.id.action_delete_note)?.isVisible = false
                    toolbar.menu.findItem(R.id.action_add_user)?.isVisible = true
                }
            }
            toolbar.title = "Notes"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val toolbar = findViewById<Toolbar>(R.id.top_toolbar)

        return when (item.itemId) {
            R.id.action_add_note -> {
                notesViewModel.selectNote(Note("", "", ""))
                navController.navigate(R.id.note_fragment)
                toolbar.menu.findItem(R.id.action_delete_note).isVisible = true
                true
            }

            R.id.action_add_user -> {
                if (isUserLoggedIn) {
                    navController.navigate(R.id.user_fragment)
                } else {
                    navController.navigate(R.id.login_fragment)
                }
                true
            }

            R.id.action_delete_note -> {
                showDeleteConfirmationDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }

    /**
     * Method used to show the delete dialog.
     *
     * Handles delete logic.
     *
     */

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Delete") { _, _ ->
                notesViewModel.deleteSelectedNote()

                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                val navController = navHostFragment.navController
                navController.navigate(R.id.main_fragment)

                Toast.makeText(this, "Note was deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
