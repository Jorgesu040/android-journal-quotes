package com.example.practicaandroid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.practicaandroid.databinding.ItemJournalCardBinding
import com.example.practicaandroid.model.Journal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// nota: las clases inner puede acceder al padre y viceversa (vs no inner)

// Pasos para crear un Adapter
// 1- extiendo de Recycler.Adapter
// 5- Por constructor pasamos todos los parametros que son necesarios
class JournalAdapter(
    var bookmarks: ArrayList<String> = ArrayList(),
    var onDetalleClick: (Journal) -> Unit,
    var onBookmarkClick: (Journal) -> Unit
) : RecyclerView.Adapter<JournalAdapter.JournalHolder>() {

    private val lista: ArrayList<Journal> = ArrayList()
    // 2- Creamos una clase anidada inner : RecyclerViewHolder que hereda de ViewHolder
    // 3- Mediante el binding creado junto al XML, le pasamos una vista (binding.root)
    inner class JournalHolder(var binding: ItemJournalCardBinding) : RecyclerView.ViewHolder(binding.root)

    // 4- Implementar los metodos

    // Metodo que crea la plantilla de las filas
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): JournalHolder {
        val binding = ItemJournalCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        return JournalHolder(binding)
    }

    // Metodo que enlaza la plantilla con el dato en una posicion
    override fun onBindViewHolder(
        holder: JournalHolder,
        position: Int
    ) {
        val journal = lista[position]
        val isBookmarked = bookmarks.contains(journal.id)

        with(holder.binding) {

            tvTitle.text = journal.title
            tvDescription.text = journal.text
            chipMood.text = "Ánimo ${journal.mood}"

            // Formatear la fecha (timestamp a DD MMM)
            val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
            tvDate.text = sdf.format(Date(journal.timestamp)).uppercase()

            // Aplicar estado visual de la estrella según si está guardado
            btnBookmark.isChecked = isBookmarked

            // Click en toda la tarjeta para ver detalle
            root.setOnClickListener {
                onDetalleClick(journal)
            }

            // Click en el botón de bookmark
            btnBookmark.setOnClickListener {
                onBookmarkClick(journal)
            }
        }
    }

    // Metodo que dice cuantos elementos tiene la lista
    override fun getItemCount(): Int {
        return lista.size
    }

    fun addJournal(journal: Journal) {
        // Encontrar la posición correcta para mantener orden descendente (más reciente primero)
        val index = lista.indexOfFirst { journal.timestamp > it.timestamp }
        if (index == -1) {
            // Si es el más antiguo o la lista está vacía, añadir al final
            this.lista.add(journal)
            notifyItemInserted(lista.size - 1)
        } else {
            // Insertar en la posición encontrada
            this.lista.add(index, journal)
            notifyItemInserted(index)
        }
    }

    fun updateList(newList: List<Journal>) {
        this.lista.clear()
        this.lista.addAll(newList)
        notifyDataSetChanged()
    }

    fun updateJournal(journal: Journal) {
        val index = lista.indexOfFirst { it.id == journal.id }
        if (index != -1) {
            lista[index] = journal
            notifyItemChanged(index)
        }
    }

    fun removeJournal(journal: Journal) {
        val index = lista.indexOfFirst { it.id == journal.id }
        if (index != -1) {
            lista.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun addBookmark(journalId: String) {
        if (!bookmarks.contains(journalId)) {
            bookmarks.add(journalId)
            // Buscamos la posición del journal en la lista de datos para notificar solo esa fila
            val index = lista.indexOfFirst { it.id == journalId }
            if (index != -1) {
                notifyItemChanged(index)
            }
        }
    }

    fun removeBookmark(journalId: String) {
        if (bookmarks.remove(journalId)) {
            val index = lista.indexOfFirst { it.id == journalId }
            if (index != -1) {
                notifyItemChanged(index)
            }
        }
    }

    fun updateBookmarks(newBookmarks: ArrayList<String>) {
        this.bookmarks = newBookmarks
        notifyDataSetChanged()
    }

    fun clearList() {
        this.lista.clear()
        notifyDataSetChanged()
    }
}
