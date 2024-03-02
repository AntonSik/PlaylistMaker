package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView


class SearchActivity : AppCompatActivity() {
    private var savedText: String? = null
    private lateinit var inputEditText: EditText
    lateinit var trackList : ArrayList<Track>
    lateinit var trackAdapter : TrackAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        inputEditText = findViewById(R.id.inputEditText)
        val imageArrow = findViewById<ImageView>(R.id.arrow2)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val recyclerView = findViewById<RecyclerView>(R.id.rv_recycleView)

        savedText = savedInstanceState?.getString(INPUT_TEXT)


        imageArrow.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
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

        trackList = arrayListOf(
                Track("Smells Like Teen Spirit","Nirvana","5:01","https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"),
                Track("Billie Jean","Michael Jackson","4:35","https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"),
                Track("Stayin' Alive","Bee Gees","4:10","https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"),
                Track("Whole Lotta Love","Led Zeppelin","5:33","https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"),
                Track("Sweet Child O'Mine","Guns N' Roses","5:03","https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"),
                Track("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB","5:03","CCCCCCCCCCCCCCCCCCCCCCCCCCC")
        )
        trackAdapter = TrackAdapter(trackList)
        recyclerView.adapter = trackAdapter
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

    companion object {
        private const val INPUT_TEXT = "INPUT_EDIT"
    }
}
