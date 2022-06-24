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
import com.example.splinterlandstest.databinding.FragmentCollectionBinding

/**
 * Collection fragment
 */
class CollectionFragment : Fragment(), CollectionFilterDialogFragment.CollectionDialogInterface {

    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private var _binding: FragmentCollectionBinding? = null

    private val binding get() = _binding!!

    val model: CollectionFragmentViewModel by viewModels()

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

        val adapter = CollectionAdapter(Cache().getCardDetails(requireContext()))
        binding.recyclerView.adapter = adapter

        model.collection.observe(this) { collection ->
            adapter.updateCollection(collection)
        }
        model.cardDetails.observe(this) { cardDetails ->
            adapter.updateCardDetails(cardDetails)
        }

        model.loadCollection(requireContext(), activityViewModel.playerName)

        binding.fabFilter.setOnClickListener {
            showFilterDialog()
        }

        activity?.title = "Collection"
    }

    private fun showFilterDialog() {
        val dialog = CollectionFilterDialogFragment()
        val arguments = Bundle()
        arguments.putIntArray("rarities", model.filterRarities.toIntArray())
        arguments.putIntArray("editions", model.filterEditions.toIntArray())
        dialog.arguments = arguments
        dialog.show(childFragmentManager, "NoticeDialogFragment")
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