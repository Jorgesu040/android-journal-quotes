package com.example.practicaandroid.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.practicaandroid.adapter.QuotesAdapter
import com.example.practicaandroid.databinding.FragmentQuotesBinding
import com.example.practicaandroid.model.Quote
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.json.JSONArray

class QuotesFragment : Fragment() {

    private var _binding: FragmentQuotesBinding? = null
    private val binding get() = _binding!!

    private lateinit var quotesAdapter: QuotesAdapter
    private lateinit var quotesList: ArrayList<Quote>
    private var requestQueue: com.android.volley.RequestQueue? = null

    private fun getQueue(): com.android.volley.RequestQueue? {
        if (requestQueue == null) {
            requestQueue = context?.let { Volley.newRequestQueue(it.applicationContext) }
        }
        return requestQueue
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        instances()
        initGUI()
        getQuotes()
        actions()
    }

    private fun instances() {
        quotesList = ArrayList()
        quotesAdapter = QuotesAdapter(quotesList)
    }

    private fun initGUI() {
        binding.zenQuotesRecyclerView.adapter = quotesAdapter
        binding.zenQuotesRecyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    private fun getQuotes() {
        val url = "https://zenquotes.io/api/random"
        val queue = getQueue() ?: return

        // Ejecutamos 4 peticiones individuales rápidas
        repeat(4) {
            val request: JsonArrayRequest = JsonArrayRequest(
                url,
                { response: JSONArray ->
                    if (context == null) return@JsonArrayRequest
                    val gson = Gson()
                    if (response.length() > 0) {
                        val quoteJson = response.getJSONObject(0)
                        val quote = gson.fromJson(quoteJson.toString(), Quote::class.java)
                        fetchAuthorImage(quote)
                    }
                },
                { error ->
                    view?.let {
                        Snackbar.make(it, "Error al obtener quotes: ${error.message}", Snackbar.LENGTH_SHORT).show()
                    }
                }
            )
            queue.add(request)
        }
    }

    private fun fetchAuthorImage(quote: Quote) {
        val wikiUrl = "https://en.wikipedia.org/api/rest_v1/page/summary/${quote.a.replace(" ", "_")}"
        val queue = getQueue() ?: return
        val request = JsonObjectRequest(
            Request.Method.GET, wikiUrl, null,
            { response ->
                if (context == null) return@JsonObjectRequest
                val thumbnailUrl = response.optJSONObject("thumbnail")?.optString("source")
                val updatedQuote = if (!thumbnailUrl.isNullOrEmpty()) {
                    quote.copy(i = thumbnailUrl)
                } else {
                    // Si Wikipedia no tiene imagen, usamos picsum como fallback con un tamaño mayor
                    quote.copy(i = "https://picsum.photos/seed/${quote.a.hashCode()}/400")
                }
                quotesAdapter.addQuote(updatedQuote)
            },
            {
                if (context == null) return@JsonObjectRequest
                // Si falla la Wikipedia, usamos picsum como fallback con un tamaño mayor
                val updatedQuote = quote.copy(i = "https://picsum.photos/seed/${quote.a.hashCode()}/400")
                quotesAdapter.addQuote(updatedQuote)
            }
        )
        queue.add(request)
    }

    private fun actions() {
        binding.fabAddQuotes.setOnClickListener {
            getRandomQuote()
        }
    }

    private fun getRandomQuote() {
        val url = "https://zenquotes.io/api/random"

        val queue = getQueue() ?: return
        val request = JsonArrayRequest(
            url,
            { response ->
                if (context == null) return@JsonArrayRequest
                val gson = Gson()
                if (response.length() > 0) {
                    val quoteJson = response.getJSONObject(0)
                    val quote = gson.fromJson(quoteJson.toString(), Quote::class.java)
                    fetchAuthorImage(quote)
                }
            },
            { error ->
                view?.let {
                    Snackbar.make(it, "Error al obtener nueva Quote: ${error.message}", Snackbar.LENGTH_SHORT).show()
                }
            }
        )

        queue.add(request)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
