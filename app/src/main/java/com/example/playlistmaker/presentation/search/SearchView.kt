package com.example.playlistmaker.presentation.search


import com.example.playlistmaker.ui.search.models.SearchState
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

interface SearchView : MvpView{

@StateStrategyType(AddToEndSingleStrategy::class)
    fun render(state: SearchState)


}