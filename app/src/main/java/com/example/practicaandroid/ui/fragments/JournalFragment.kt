package com.example.practicaandroid.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practicaandroid.R
import com.example.practicaandroid.adapter.JournalAdapter
import com.example.practicaandroid.databinding.FragmentJournalBinding
import com.example.practicaandroid.model.Journal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class JournalFragment : Fragment() {
    private var _binding: FragmentJournalBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: JournalAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJournalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initInstances()
        setupRecyclerView()
        getJournalsFromFirebase()
        getBookmarksFromFirebase()
        fetchUserName()
    }

    private fun fetchUserName() {
        val uid = auth.currentUser?.uid ?: return
        database.getReference("users").child(uid).get().addOnSuccessListener { snapshot ->
            val name = snapshot.child("nombre").value as? String
            if (context != null && name != null) {
                binding.tvWelcome.text = "¡Bienvenido de vuelta, $name!"
            }
        }
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
                findNavController().navigate(R.id.action_journalFragment_to_journalItemFragment, bundle)
            },
            onBookmarkClick = { journal ->
                toggleBookmark(journal)
            }
        )
        binding.rvJournals.layoutManager = LinearLayoutManager(context)
        binding.rvJournals.adapter = adapter
    }

    private fun getJournalsFromFirebase() {
        val uid = auth.currentUser!!.uid

        database.getReference("users").child(uid).child("journals")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    snapshot.getValue(Journal::class.java)?.let { journal ->
                        adapter.addJournal(journal)
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
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(error: DatabaseError) {
                    if (isAdded) Snackbar.make(binding.root, "Error: ${error.message}", Snackbar.LENGTH_SHORT).show()
                }
            })
    }

    private fun getBookmarksFromFirebase() {
        val uid = auth.currentUser!!.uid

        database.getReference("users").child(uid).child("bookmarks")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val journalId = snapshot.key ?: return
                    adapter.addBookmark(journalId)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val journalId = snapshot.key ?: return
                    adapter.removeBookmark(journalId)
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun toggleBookmark(journal: Journal) {
        val uid = auth.currentUser!!.uid
        if (journal.id.isEmpty()) return

        val bookmarkRef = database.getReference("users").child(uid).child("bookmarks").child(journal.id)

        val isCurrentlyBookmarked = adapter.bookmarks.contains(journal.id)
        if (isCurrentlyBookmarked) {
            bookmarkRef.removeValue()
        } else {
            // Guardamos el objeto completo como referencia para que BookmarkFragment sea más eficiente
            bookmarkRef.setValue(journal)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
