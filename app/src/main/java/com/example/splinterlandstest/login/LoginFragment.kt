package com.example.splinterlandstest.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.splinterlandstest.MainActivityViewModel
import com.example.splinterlandstest.databinding.FragmentLoginBinding

/**
 * Collection fragment
 */
class LoginFragment : Fragment() {

    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val player = binding.etPlayerName.text.toString()
            activityViewModel.setPlayer(requireContext(), player)
        }

        activity?.title = "Login"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}