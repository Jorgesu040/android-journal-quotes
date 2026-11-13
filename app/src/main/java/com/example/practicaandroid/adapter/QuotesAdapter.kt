package com.example.practicaandroid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practicaandroid.databinding.ItemQuotesCardBinding
import com.example.practicaandroid.model.Quote

class QuotesAdapter(
    private val lista: ArrayList<Quote>
) : RecyclerView.Adapter<QuotesAdapter.QuoteHolder>() {

    class QuoteHolder(var binding: ItemQuotesCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteHolder {
        val binding = ItemQuotesCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return QuoteHolder(binding)
    }

    override fun onBindViewHolder(holder: QuoteHolder, position: Int) {
        val quote = lista[position]

        with(holder.binding) {
            tvQuoteText.text = "“${quote.q}”"
            tvAuthorName.text = "- ${quote.a}"

            // Cargar imagen del autor con Glide
            if (quote.i.isNotEmpty()) {
                Glide.with(ivAuthor.context)
                    .load(quote.i)
                    .circleCrop()
                    .into(ivAuthor)
            }
        }
    }

    override fun getItemCount(): Int = lista.size

    fun getQuotes(): ArrayList<Quote> {
        return lista
    }

    fun addQuote(quote: Quote) {
        lista.add(quote)
        notifyItemInserted(lista.size - 1)
    }

    fun addQuotes(quotes: List<Quote>) {
        val startIndex = lista.size
        lista.addAll(quotes)
        notifyItemRangeInserted(startIndex, quotes.size)
    }
}
