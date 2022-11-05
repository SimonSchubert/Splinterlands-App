package com.example.splinterlandstest.collection

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.splinterlandstest.Cache
import com.example.splinterlandstest.MainActivityViewModel
import com.example.splinterlandstest.R
import com.example.splinterlandstest.Requests
import com.example.splinterlandstest.databinding.FragmentCollectionBinding
import org.koin.android.ext.android.get

/**
 * Collection fragment
 */
class CollectionFragment : Fragment(), CollectionFilterDialogFragment.CollectionDialogInterface {

    private val activityViewModel: MainActivityViewModel by activityViewModels()

    val cache: Cache = get()
    val requests: Requests = get()

    private var _binding: FragmentCollectionBinding? = null

    private val binding get() = _binding!!

    private val model: CollectionFragmentViewModel = CollectionFragmentViewModel(cache, requests)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCollectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager =
            GridLayoutManager(context, calculateNoOfColumns(requireContext(), 120f))

        val adapter = CollectionAdapter(cache.getCardDetails())
        binding.recyclerView.adapter = adapter

        model.collection.observe(viewLifecycleOwner) { collection ->
            adapter.updateCollection(collection)
        }
        model.cardDetails.observe(viewLifecycleOwner) { cardDetails ->
            adapter.updateCardDetails(cardDetails)
        }

        model.loadCollection(activityViewModel.playerName)

        binding.fabFilter.setOnClickListener {
            showFilterDialog()
        }

        activity?.title = getString(R.string.collection)
    }

    private fun showFilterDialog() {
        val dialog = CollectionFilterDialogFragment()
        val arguments = Bundle()
        arguments.putIntArray("rarities", model.filterRarities.toIntArray())
        arguments.putIntArray("editions", model.filterEditions.toIntArray())
        dialog.arguments = arguments
        dialog.show(childFragmentManager, "FilterDialogFragment")
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

    override fun onFilterChange(rarities: List<Int>, editions: List<Int>) {
        model.updateFilter(rarities, editions)
    }
}