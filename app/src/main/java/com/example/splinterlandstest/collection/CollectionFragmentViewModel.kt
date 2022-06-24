package com.example.splinterlandstest.collection

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splinterlandstest.Cache
import com.example.splinterlandstest.Requests
import kotlinx.coroutines.launch

class CollectionFragmentViewModel : ViewModel() {

    private val requests = Requests()
    private val cache = Cache()

    val collection: MutableLiveData<List<Requests.Card>> = MutableLiveData()
    val cardDetails: MutableLiveData<List<Requests.CardDetail>> = MutableLiveData()

    var filterRarities = listOf<Int>()
    var filterEditions = listOf<Int>()
    private var unfilteredCollection = listOf<Requests.Card>()

    fun loadCollection(context: Context, player: String) {
        viewModelScope.launch {
            unfilteredCollection = cache.getCollection(context, player)
            updateCollection()
            unfilteredCollection = requests.getCollection(context, player)
            updateCollection()
            cardDetails.value = requests.getCardDetails(context)
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
        collection.value = filteredCollection
    }

    fun updateFilter(rarities: List<Int>, editions: List<Int>) {
        filterRarities = rarities
        filterEditions = editions
        updateCollection()
    }
}