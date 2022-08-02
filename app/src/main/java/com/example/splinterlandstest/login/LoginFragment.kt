package com.example.splinterlandstest.login

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
import com.example.splinterlandstest.databinding.FragmentLoginBinding

/**
 * Collection fragment
 */
class LoginFragment : Fragment() {

    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    private val model: LoginFragmentViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val players = Cache().getPlayerList(requireContext()).toMutableList()

        val adapter = LoginAdapter(
            players,
            activityViewModel,
            object : OnItemClickListener {
                override fun onClickPlayer(player: String) {
                    activityViewModel.setPlayer(requireContext(), player)
                }

                override fun onDeletePlayer(player: String) {
                    activityViewModel.deletePlayer(requireContext(), player)
                }
            })

        model.quests.observe(viewLifecycleOwner) { quests ->
            adapter.updateQuests(quests)
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)


        activity?.title = "Login"
    }

    override fun onResume() {
        super.onResume()

        val players = Cache().getPlayerList(requireContext()).toMutableList()
        model.loadUsers(requireContext(), players)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}