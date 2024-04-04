package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 1) Получить элемент по нажатию на него
 * 2) конвертировать его в json-строку
 * 3) json-строку сохранить в sharedPreferences
 * 4) достаем из sharedPreferences строку
 * 4) десериализуем её обратно в обЪект с помощью библиотеки Gson
 * 5) добавляем в коллекцию "история"
 * 6) отобразить историю согласно макета **/

class SearchActivity : AppCompatActivity(), OnClickListenerItem {

    companion object {
        private const val INPUT_TEXT = "INPUT_EDIT"
        private const val TAG_12 = "TAG FOR 12TH SPRINT"
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
    lateinit var historyTracks: ArrayList<Track>
    lateinit var trackAdapter: TrackAdapter
    lateinit var historyAdapter: TrackAdapter
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderMessageExtra: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderButton: Button
    lateinit var historyPrefs : HistoryPrefs


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        historyPrefs = HistoryPrefs(this)
        val imageArrow = findViewById<ImageView>(R.id.arrow2)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val recyclerView = findViewById<RecyclerView>(R.id.rv_recycleView)
        inputEditText = findViewById(R.id.inputEditText)
        historyTracks = historyPrefs.get()
        historyAdapter = TrackAdapter(historyTracks,this)
        placeholderMessage = findViewById(R.id.tv_placeholderMessage)
        placeholderMessageExtra = findViewById(R.id.tv_placeholderMessageExtra)
        placeholderImage = findViewById(R.id.iv_placeholderImage)
        placeholderButton = findViewById(R.id.b_update_btn)



        savedText = savedInstanceState?.getString(INPUT_TEXT)

        imageArrow.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            val inputMM =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager  //константа для скрытия клавиатуры
            inputMM.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

// TextWatcher

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                savedText = s.toString()
                clearButton.visibility = clearButtonVisibility(s)
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        inputEditText.addTextChangedListener(textWatcher)

        trackList = arrayListOf()
        trackAdapter = TrackAdapter(trackList, this)
        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()){
                if(historyTracks.isNotEmpty()) {
                    recyclerView.adapter = historyAdapter
                    Toast.makeText(this, "вот список истории : ${historyTracks}", Toast.LENGTH_LONG)
                        .show()
                }else{
                    recyclerView.adapter = trackAdapter
                    Toast.makeText(this, "значит список истории пуст ${historyTracks}",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "косяк с условием",Toast.LENGTH_LONG).show()
            }
        }
        recyclerView.adapter = trackAdapter


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
                        placeholderButton.visibility = View.GONE
                        placeholderImage.visibility = View.GONE
                        placeholderMessageExtra.visibility = View.GONE

                        if (resp?.isNotEmpty() == true) {
                            trackList.addAll(resp)
                            trackAdapter.notifyDataSetChanged()

                        }
                        if (trackList.isEmpty()) {
                            showMessage(getString(R.string.not_found))
                            placeholderImage.setImageResource(R.drawable.light_mode_error)
                            placeholderImage.visibility = View.VISIBLE
                        } else {
                            showMessage("")
                        }
                    } else {
                        showMessage(getString(R.string.no_internet_connection))
                        placeholderImage.setImageResource(R.drawable.light_mode_no_connection)
                        placeholderImage.visibility = View.VISIBLE

                    }
                }

                override fun onFailure(call: Call<ItunesTrackResponse>, t: Throwable) {
                    showMessage(getString(R.string.no_internet_connection))
                    placeholderImage.setImageResource(R.drawable.light_mode_no_connection)
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
        Toast.makeText(this, "Нажали на ${track.trackName}", Toast.LENGTH_LONG)
            .show()  // добавили элемент в sharedPreferences по клику на него
            historyPrefs.add(track)
    }

}
