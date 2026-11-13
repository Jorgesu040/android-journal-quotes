package com.example.practicaandroid.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.practicaandroid.R
import com.example.practicaandroid.databinding.FragmentJournalItemBinding
import com.example.practicaandroid.model.Journal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JournalItemFragment : Fragment() {

    private var _binding: FragmentJournalItemBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJournalItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recuperar el diario pasado como argumento
        val journal = requireArguments().getSerializable("journal") as Journal


        initGUI(journal)

    }

    private fun initGUI(journal: Journal) {
        with(binding) {
            btnBack.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            tvTitle.text = journal.title
            tvBody.text = journal.text
            chipMood.text = "Estado de Ánimo ${journal.mood}"

            // Formatear fecha
            val sdf = SimpleDateFormat("dd 'DE' MMM, yyyy", Locale.getDefault())
            tvDate.text = sdf.format(Date(journal.timestamp)).uppercase()

            // Cargar imagen con Glide
            if (journal.imgURL.isNotEmpty()) {
                Glide.with(ivHeaderImage.context)
                    .load(journal.imgURL)
                    .placeholder(R.drawable.bg_redondeado)
                    .into(ivHeaderImage)
            } else {
                // Imagen picsum
                Glide.with(ivHeaderImage.context)
                    .load("https://picsum.photos/seed/${journal.id}/800/600")
                    .into(ivHeaderImage)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
