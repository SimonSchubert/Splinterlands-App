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

    fun loadCollection(context: Context, player: String) {
        viewModelScope.launch {
            collection.value = cache.getCollection(context, player)
            cardDetails.value = cache.getCardDetails(context)
            collection.value = requests.getCollection(context, player)
            cardDetails.value = requests.getCardDetails(context)
        }
    }
}