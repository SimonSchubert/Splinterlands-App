package com.example.splinterlandstest.battles

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.splinterlandstest.Cache
import com.example.splinterlandstest.MainActivityViewModel
import com.example.splinterlandstest.R
import com.example.splinterlandstest.Requests
import com.example.splinterlandstest.databinding.FragmentSecondBinding
import org.koin.android.ext.android.get


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class BattlesFragment : Fragment() {

    val cache: Cache = get()
    val requests: Requests = get()

    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private var _binding: FragmentSecondBinding? = null

    private val model: BattlesFragmentViewModel = BattlesFragmentViewModel(cache, requests)

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

        val adapter = BattlesAdapter(
            activityViewModel.playerName,
            cache.getCardDetails(),
            cache.getSettings()
        ) {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://splinterlands.com/?p=battle&id=${it}"))
            startActivity(browserIntent)
        }
        binding.recyclerView.adapter = adapter

        model.battles.observe(viewLifecycleOwner) { battles ->
            adapter.updateBattles(battles)
        }
        model.playerDetails.observe(viewLifecycleOwner) { playerDetails ->
            adapter.updatePlayerDetails(playerDetails)
        }
        model.rewardsInfo.observe(viewLifecycleOwner) { playerQuest ->
            adapter.updateRewardsInfo(playerQuest)
        }
        model.cardDetails.observe(viewLifecycleOwner) { cardDetails ->
            adapter.updateCardDetails(cardDetails)
        }

        activity?.title = getString(R.string.battles)
    }

    override fun onResume() {
        super.onResume()

        model.loadBattles(activityViewModel.playerName)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}