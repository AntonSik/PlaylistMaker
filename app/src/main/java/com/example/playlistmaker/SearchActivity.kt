package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.google.gson.Gson
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
        private const val SHARED_PREFERENCES_HISTORY = "Shared pref's key"
        private const val KEY_FOR_NEW_TRACK = "New track's key"
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
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderMessageExtra: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderButton: Button
    private lateinit var listener: OnSharedPreferenceChangeListener
    lateinit var sharedPrefs : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        sharedPrefs = getSharedPreferences(SHARED_PREFERENCES_HISTORY, MODE_PRIVATE)
        inputEditText = findViewById(R.id.inputEditText)
        val imageArrow = findViewById<ImageView>(R.id.arrow2)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val recyclerView = findViewById<RecyclerView>(R.id.rv_recycleView)
        historyTracks = arrayListOf()
        val historyAdapter = TrackAdapter(historyTracks, this)
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
        listener =
            OnSharedPreferenceChangeListener { sharedPreferences, key ->   //достаем json-строку из sharedPreferences, конвертируем обратно в объект и добавляем ее к списку треков в адаптре.
                if (key == KEY_FOR_NEW_TRACK) {
                    val track = sharedPrefs?.getString(KEY_FOR_NEW_TRACK, null)
                    if (track != null) {
                        historyAdapter.tracks.add(0, jsonToObj(track))
                        historyAdapter.notifyItemInserted(0)                 // Обновляем
                    }
                }
            }
        sharedPrefs.registerOnSharedPreferenceChangeListener(listener)

        inputEditText.addTextChangedListener(textWatcher)

        trackList = arrayListOf()
        trackAdapter = TrackAdapter(trackList, this)
//       recyclerView.adapter = trackAdapter
//        inputEditText.setOnFocusChangeListener { view, hasFocus ->
//            if (hasFocus && inputEditText.text.isEmpty() && historyTracks.isNotEmpty()){
//                trackAdapter  = TrackAdapter(historyTracks,this)
//            } else {
//            trackAdapter = TrackAdapter(trackList,this)                           //Ошибка где-то здесь
//            }
//        }
        recyclerView.adapter = trackAdapter
        sharedPrefs.registerOnSharedPreferenceChangeListener(listener)

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

    fun objToJson(track: Track): String {
        return Gson().toJson(track)
    }

    fun jsonToObj(json: String): Track {
        return Gson().fromJson(json, Track::class.java)
    }

    override fun onItemClick(track: Track) {
        Toast.makeText(this, "Нажали на ${track.trackName}", Toast.LENGTH_LONG)
            .show()  // добавили элемент в sharedPreferences по клику на него
        sharedPrefs.edit()
            .putString(KEY_FOR_NEW_TRACK, objToJson(track))
            .apply()
    }

}
