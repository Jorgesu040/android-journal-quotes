package com.example.practicaandroid.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practicaandroid.adapter.JournalAdapter
import com.example.practicaandroid.databinding.FragmentBookmarkBinding
import com.example.practicaandroid.model.Journal
import com.example.practicaandroid.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: JournalAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initInstances()
        setupRecyclerView()
        loadBookmarksAndJournals()
    }

    private fun initInstances() {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
    }

    private fun setupRecyclerView() {
        adapter = JournalAdapter(
            onDetalleClick = { journal ->
                val bundle = Bundle().apply {
                    putSerializable("journal", journal)
                }
                findNavController().navigate(R.id.action_bookmarkFragment_to_journalItemFragment, bundle)
            },
            onBookmarkClick = { journal ->
                toggleBookmark(journal)
            }
        )
        binding.rvBookmarks.layoutManager = LinearLayoutManager(context)
        binding.rvBookmarks.adapter = adapter
    }

    private fun loadBookmarksAndJournals() {
        val uid = auth.currentUser!!.uid
        val bookmarksRef = database.getReference("users").child(uid).child("bookmarks")

        bookmarksRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(Journal::class.java)?.let { journal ->
                    adapter.addJournal(journal)
                    adapter.addBookmark(journal.id)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(Journal::class.java)?.let { updatedJournal ->
                    adapter.updateJournal(updatedJournal)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                snapshot.getValue(Journal::class.java)?.let { removedJournal ->
                    adapter.removeJournal(removedJournal)
                    adapter.removeBookmark(removedJournal.id)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {
                if (isAdded) Snackbar.make(binding.root, "Error: ${error.message}", Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    private fun toggleBookmark(journal: Journal) {
        val uid = auth.currentUser!!.uid
        // Al estar en la pantalla de bookmarks, si pulsamos el botón, lo quitamos de la lista de favoritos
        database.getReference("users").child(uid).child("bookmarks").child(journal.id).removeValue()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}