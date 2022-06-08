package com.example.splinterlandstest.collection

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.splinterlandstest.Cache
import com.example.splinterlandstest.MainActivityViewModel
import com.example.splinterlandstest.databinding.FragmentSecondBinding

/**
 * Collection fragment
 */
class CollectionFragment : Fragment() {

    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private var _binding: FragmentSecondBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager =
            GridLayoutManager(context, calculateNoOfColumns(requireContext(), 120f))

        val adapter = CollectionAdapter(Cache().getCardDetails(requireContext()))
        binding.recyclerView.adapter = adapter

        val model: CollectionFragmentViewModel by viewModels()
        model.collection.observe(this) { collection ->
            adapter.updateCollection(collection)
        }
        model.cardDetails.observe(this) { cardDetails ->
            adapter.updateCardDetails(cardDetails)
        }

        model.loadCollection(requireContext(), activityViewModel.playerName)

        activity?.title = "Collection"
    }

    fun calculateNoOfColumns(context: Context, columnWidthDp: Float): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        return (screenWidthDp / columnWidthDp + 0.5).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}