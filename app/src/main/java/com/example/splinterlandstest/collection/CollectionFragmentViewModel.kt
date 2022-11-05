package com.example.splinterlandstest.collection

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splinterlandstest.Cache
import com.example.splinterlandstest.Requests
import com.example.splinterlandstest.models.Card
import com.example.splinterlandstest.models.CardDetail
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CollectionFragmentViewModel(val cache: Cache, val requests: Requests) : ViewModel() {

    val collection: MutableLiveData<List<Card>> = MutableLiveData()
    val cardDetails: MutableLiveData<List<CardDetail>> = MutableLiveData()

    var filterRarities = listOf<Int>()
    var filterEditions = listOf<Int>()
    private var unfilteredCollection = listOf<Card>()

    fun loadCollection(player: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            unfilteredCollection = cache.getCollection(player)
            updateCollection()
            unfilteredCollection = requests.getCollection(player)
            updateCollection()
            cardDetails.postValue(requests.getCardDetails())
        }
    }

    private fun updateCollection() {
        var filteredCollection = unfilteredCollection
        if (unfilteredCollection.isNotEmpty()) {
            if (filterRarities.isNotEmpty()) {
                val cardIds =
                    cardDetails.value?.filter { filterRarities.contains(it.rarity) }?.map { it.id } ?: emptyList()
                filteredCollection = filteredCollection.filter { cardIds.contains(it.card_detail_id) }
            }
            if (filterEditions.isNotEmpty()) {
                filteredCollection = filteredCollection.filter { filterEditions.contains(it.edition) }
            }
        }
        collection.postValue(filteredCollection)
    }

    fun updateFilter(rarities: List<Int>, editions: List<Int>) {
        filterRarities = rarities
        filterEditions = editions
        updateCollection()
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }
}