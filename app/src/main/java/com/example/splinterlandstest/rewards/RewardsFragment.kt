package com.example.splinterlandstest.rewards

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.splinterlandstest.MainActivityViewModel
import com.example.splinterlandstest.R
import com.example.splinterlandstest.databinding.FragmentRewardsBinding


/**
 * Rewards fragment
 */
class RewardsFragment : Fragment() {

    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private var _binding: FragmentRewardsBinding? = null

    private val binding get() = _binding!!

    private val model: RewardsFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRewardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager =
            GridLayoutManager(context, calculateNoOfColumns(requireContext(), 120f))

        val adapter = RewardsAdapter()
        binding.recyclerView.adapter = adapter

        model.cardDetails.observe(viewLifecycleOwner) { cardDetails ->
            adapter.updateCardDetails(cardDetails)
        }
        model.rewards.observe(viewLifecycleOwner) { balances ->
            adapter.updateRewards(balances)
            binding.progressBar.isVisible = false
        }

        activity?.title = getString(R.string.rewards)
    }

    fun calculateNoOfColumns(context: Context, columnWidthDp: Float): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        return (screenWidthDp / columnWidthDp + 0.5).toInt()
    }

    override fun onResume() {
        super.onResume()

        model.loadRewards(requireContext(), activityViewModel.playerName)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}