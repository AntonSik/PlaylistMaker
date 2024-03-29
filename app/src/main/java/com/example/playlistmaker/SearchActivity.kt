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


class SearchActivity : AppCompatActivity() {
    private val itunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesApi::class.java)

    private var savedText: String? = null
    private lateinit var inputEditText: EditText
    lateinit var trackList: ArrayList<Track>
    lateinit var trackAdapter: TrackAdapter
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderMessageExtra: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        inputEditText = findViewById(R.id.inputEditText)
        val imageArrow = findViewById<ImageView>(R.id.arrow2)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val recyclerView = findViewById<RecyclerView>(R.id.rv_recycleView)
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
// EmptyTextWartcher

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //empty
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                savedText = s.toString()
                clearButton.visibility = clearButtonVisibility(s)

            }

            override fun afterTextChanged(s: Editable?) {}

        }

        inputEditText.addTextChangedListener(textWatcher)

        trackList = arrayListOf()
        trackAdapter = TrackAdapter(trackList)
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


    companion object {
        private const val INPUT_TEXT = "INPUT_EDIT"

    }
}
