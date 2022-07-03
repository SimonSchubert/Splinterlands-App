package com.example.splinterlandstest.battles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.splinterlandstest.Cache
import com.example.splinterlandstest.MainActivityViewModel
import com.example.splinterlandstest.R
import com.example.splinterlandstest.databinding.FragmentSecondBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class BattlesFragment : Fragment() {

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

        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = BattlesAdapter(activityViewModel.playerName, Cache().getCardDetails(requireContext()))
        binding.recyclerView.adapter = adapter

        val model: BattlesFragmentViewModel by viewModels()
        model.battles.observe(this) { battles ->
            adapter.updateBattles(battles)
        }
        model.playerDetails.observe(this) { playerDetails ->
            adapter.updatePlayerDetails(playerDetails)
        }
        model.playerQuest.observe(this) { playerQuest ->
            adapter.updatePlayerQuest(playerQuest)
        }
        model.cardDetails.observe(this) { cardDetails ->
            adapter.updateCardDetails(cardDetails)
        }

        model.loadBattles(requireContext(), activityViewModel.playerName)

        activity?.title = getString(R.string.battles)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}