package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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
    }

    private val itunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesApi::class.java)

    private var savedText: String? = null
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        historyPrefs = HistoryPrefs(this)
        val searchedText = findViewById<TextView>(R.id.tv_searched)
        val imageArrow = findViewById<ImageView>(R.id.arrow2)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val recyclerView = findViewById<RecyclerView>(R.id.rv_recycleView)

        savedText = savedInstanceState?.getString(INPUT_TEXT)
        init()

        imageArrow.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            trackList.clear()
            val inputMM =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager  //константа для скрытия клавиатуры
            inputMM.hideSoftInputFromWindow(inputEditText.windowToken, 0)
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
                clearButton.visibility = clearButtonVisibility(s)
                if (inputEditText.hasFocus() && inputEditText.text.isNotEmpty()) {
                    trackAdapter.tracks.clear()
                    recyclerView.adapter = trackAdapter
                    clearHistoryButton.visibility = View.GONE
                    searchedText.visibility = View.GONE
                }else {
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


        fun tracksSearch() {
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
                        placeholderMessageExtra.visibility = View.GONE

                        if (resp?.isNotEmpty() == true) {
                            trackList.addAll(resp)
                            trackAdapter.notifyDataSetChanged()

                        }
                        if (trackList.isEmpty()) {
                            showMessage(getString(R.string.not_found))
                            placeholder.visibility = View.VISIBLE
                            placeholderImage.setImageResource(R.drawable.light_mode_error)
                            placeholderImage.visibility = View.VISIBLE
                        } else {
                            showMessage("")
                        }
                    } else {
                        showMessage(getString(R.string.no_internet_connection))
                        placeholder.visibility = View.VISIBLE
                        placeholderImage.setImageResource(R.drawable.light_mode_no_connection)
                        placeholderImage.visibility = View.VISIBLE

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
                    placeholderButton.setOnClickListener { tracksSearch() }
                }

            })
        }
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                recyclerView.adapter = trackAdapter
                clearHistoryButton.visibility = View.GONE
                searchedText.visibility = View.GONE
                tracksSearch()
                true
            }
            false
        }
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
        historyPrefs.addTrack(track)
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

    }

}
