package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity(), OnClickListenerItem {

    companion object {
        private const val INPUT_TEXT = "INPUT_EDIT"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 2000L
        const val CLICKED_ITEM = "clicked track"

    }

    private val itunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesApi::class.java)

    private var savedText: String? = null
    private var searchRunnable = Runnable { tracksSearch() }
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())


    private lateinit var inputEditText: EditText
    lateinit var trackList: ArrayList<Track>
    private lateinit var historyTracks: ArrayList<Track>
    lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderMessageExtra: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderButton: Button
    private lateinit var clearHistoryButton: Button
    private lateinit var placeholder: LinearLayout
    private lateinit var historyPrefs: HistoryPrefs
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchedText: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        historyPrefs = HistoryPrefs(this)

        val imageArrow = findViewById<ImageView>(R.id.arrow2)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)

        savedText = savedInstanceState?.getString(INPUT_TEXT)
        init()

        imageArrow.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            trackList.clear()
            hideKeyboard()

        }
        clearHistoryButton.setOnClickListener {
            historyPrefs.clearHistory()
            historyTracks.clear()
            historyAdapter.tracks = historyPrefs.getHistoryList()
            historyAdapter.notifyDataSetChanged()
            clearHistoryButton.visibility = View.GONE
            searchedText.visibility = View.GONE
        }

// TextWatcher

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                savedText = s.toString()
                placeholder.visibility = View.GONE
                clearButton.visibility = clearButtonVisibility(s)
                if (inputEditText.hasFocus() && inputEditText.text.isNotEmpty()) {
                    trackAdapter.tracks.clear()
                    recyclerView.adapter = trackAdapter
                    clearHistoryButton.visibility = View.GONE
                    searchedText.visibility = View.GONE
                    searchDebounce()

                } else {
                    historyAdapter.tracks = historyPrefs.getHistoryList()
                    recyclerView.adapter = historyAdapter
                    trackAdapter.notifyDataSetChanged()
                    clearHistoryButton.visibility = View.VISIBLE
                    searchedText.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        inputEditText.addTextChangedListener(textWatcher)

        trackList = arrayListOf()
        trackAdapter = TrackAdapter(trackList, this)
        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty() && historyTracks.isNotEmpty()) {
                recyclerView.adapter = historyAdapter
                clearHistoryButton.visibility =
                    View.VISIBLE                                   // Кейс когда история поиска не пуста
                searchedText.visibility = View.VISIBLE

            } else {
                recyclerView.adapter = trackAdapter
                clearHistoryButton.visibility = View.GONE         // Кейс когда история пуста
                searchedText.visibility = View.GONE
                trackAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }

    private fun hideKeyboard() {
        val inputMM =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager  //константа для скрытия клавиатуры
        inputMM.hideSoftInputFromWindow(inputEditText.windowToken, 0)
    }

    fun tracksSearch() {

        recyclerView.visibility = View.GONE
        searchedText.visibility = View.GONE
        placeholder.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        itunesService.search(inputEditText.text.toString()).enqueue(object :
            Callback<ItunesTrackResponse> {
            override fun onResponse(
                call: Call<ItunesTrackResponse>,
                response: Response<ItunesTrackResponse>
            ) {
                val resp = response.body()?.results
                if (response.isSuccessful) {
                    trackList.clear()
                    placeholder.visibility = View.GONE
                    placeholderButton.visibility = View.GONE
                    placeholderImage.visibility = View.GONE
                    placeholderMessage.visibility = View.GONE
                    placeholderMessageExtra.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE

                    if (resp?.isNotEmpty() == true) {
                        trackList.addAll(resp)
                        trackAdapter.notifyDataSetChanged()

                    }
                    if (trackList.isEmpty()) {
                        showMessage(getString(R.string.not_found))
                        placeholder.visibility = View.VISIBLE
                        placeholderImage.setImageResource(R.drawable.light_mode_error)
                        placeholderImage.visibility = View.VISIBLE
                        searchedText.visibility = View.GONE
                    } else {
                        showMessage("")
                    }
                } else {
                    showMessage(getString(R.string.no_internet_connection))
                    placeholder.visibility = View.VISIBLE
                    placeholderImage.setImageResource(R.drawable.light_mode_no_connection)
                    placeholderImage.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<ItunesTrackResponse>, t: Throwable) {
                showMessage(getString(R.string.no_internet_connection))
                placeholderImage.setImageResource(R.drawable.light_mode_no_connection)
                placeholder.visibility = View.VISIBLE
                placeholderImage.visibility = View.VISIBLE
                placeholderMessageExtra.visibility = View.VISIBLE
                placeholderMessageExtra.setText(R.string.no_internet_connection_extra)
                placeholderButton.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                placeholderButton.setOnClickListener { tracksSearch() }
            }

        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_TEXT, savedText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedText = savedInstanceState.getString(INPUT_TEXT)
        inputEditText.setText(savedText)


    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun showMessage(text: String) {
        if (text.isNotEmpty()) {
            placeholderMessage.visibility = View.VISIBLE
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            placeholderMessage.text = text

        } else {
            placeholderMessage.visibility = View.GONE
        }
    }

    override fun onItemClick(track: Track) {
        if(clickDebounce()) {
            historyPrefs.addTrack(track)
            val playerIntent = Intent(this, AudioPlayerActivity::class.java)
            playerIntent.putExtra(CLICKED_ITEM, track)
            startActivity(playerIntent)
        }
    }

    private fun init() {
        inputEditText = findViewById(R.id.inputEditText)
        historyTracks = historyPrefs.getHistoryList()
        historyAdapter = TrackAdapter(historyTracks, this)
        placeholder = findViewById(R.id.placeholder)
        placeholderMessage = findViewById(R.id.tv_placeholderMessage)
        placeholderMessageExtra = findViewById(R.id.tv_placeholderMessageExtra)
        placeholderImage = findViewById(R.id.iv_placeholderImage)
        placeholderButton = findViewById(R.id.b_update_btn)
        clearHistoryButton = findViewById(R.id.b_clear_history_btn)
        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.rv_recycleView)
        searchedText = findViewById(R.id.tv_searched)

    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
    private fun clickDebounce():Boolean{
        val current = isClickAllowed
        if(isClickAllowed){
            isClickAllowed= false
            handler.postDelayed({isClickAllowed = true}, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

}
