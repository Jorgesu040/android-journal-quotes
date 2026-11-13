package com.example.practicaandroid.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.example.practicaandroid.databinding.FragmentWriteBinding
import com.example.practicaandroid.model.Journal
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class WriteFragment : Fragment() {
    private var _binding: FragmentWriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Configurar el desplegable de las caras
        val faces = arrayOf(":)", ":|", ":(")
        val facesAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, faces)
        binding.actvFace.setAdapter(facesAdapter)
        binding.btnSave.setOnClickListener {
            saveJournal()
        }
    }

    private fun saveJournal() {
        val title = binding.etTitle.text.toString().trim()
        val text = binding.etBody.text.toString().trim()
        val face = binding.actvFace.text.toString()
        val moodText = binding.etMood.text.toString().trim()

        if (title.isEmpty() || text.isEmpty()) {
            if (isAdded) Snackbar.make(binding.root, "Por favor, escribe un título y tus pensamientos", Snackbar.LENGTH_SHORT).show()
            return
        }
        
        val finalMood = if (moodText.isNotEmpty()) "$face - $moodText" else face

        val uid = auth.currentUser!!.uid
        val journalRef = database.getReference("users").child(uid).child("journals").push()
        val journalId = journalRef.key!!

        // Generar una URL de imagen aleatoria (Picsum)
        val randomImgURL = "https://picsum.photos/seed/$journalId/800/600"

        val journal = Journal(
            id = journalId,
            title = title,
            text = text,
            imgURL = randomImgURL,
            mood = finalMood,
            timestamp = System.currentTimeMillis()
        )

        binding.btnSave.isEnabled = false

        journalRef.setValue(journal).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                if (isAdded) Snackbar.make(binding.root, "Diario guardado con éxito", Snackbar.LENGTH_SHORT).show()
                binding.etTitle.text?.clear()
                binding.etBody.text?.clear()
                binding.etMood.text?.clear()
                binding.btnSave.isEnabled = true

            } else {
                if (isAdded) Snackbar.make(binding.root, "Error al guardar el diario", Snackbar.LENGTH_SHORT).show()
                binding.btnSave.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
