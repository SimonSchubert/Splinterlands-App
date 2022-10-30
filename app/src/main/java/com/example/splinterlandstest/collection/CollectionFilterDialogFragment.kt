package com.example.splinterlandstest.collection

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import com.example.splinterlandstest.databinding.FragmentFilterBinding

class CollectionFilterDialogFragment : DialogFragment() {
    private lateinit var listener: CollectionDialogInterface

    private var _binding: FragmentFilterBinding? = null

    private val binding get() = _binding!!

    interface CollectionDialogInterface {
        fun onFilterChange(rarities: List<Int>, editions: List<Int>)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = parentFragment as CollectionDialogInterface
        } catch (e: ClassCastException) {
            throw ClassCastException(
                (context.toString() +
                        " must implement NoticeDialogListener")
            )
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val dialog = Dialog(it)

            val inflater = requireActivity().layoutInflater

            _binding = FragmentFilterBinding.inflate(inflater, null, false)

            val selectedRarities = arguments?.getIntArray("rarities")
            val selectedEditions = arguments?.getIntArray("editions")

            if (selectedRarities != null) {
                if (selectedRarities.contains(1)) {
                    binding.cbCommon.isChecked = true
                }
                if (selectedRarities.contains(2)) {
                    binding.cbRare.isChecked = true
                }
                if (selectedRarities.contains(3)) {
                    binding.cbEpic.isChecked = true
                }
                if (selectedRarities.contains(4)) {
                    binding.cbLegendary.isChecked = true
                }
            }
            if (selectedEditions != null) {
                if (selectedEditions.contains(0)) {
                    binding.cbAlpha.isChecked = true
                }
                if (selectedEditions.contains(1)) {
                    binding.cbBeta.isChecked = true
                }
                if (selectedEditions.contains(2)) {
                    binding.cbPromo.isChecked = true
                }
                if (selectedEditions.contains(3)) {
                    binding.cbReward.isChecked = true
                }
                if (selectedEditions.contains(4)) {
                    binding.cbUntamed.isChecked = true
                }
                if (selectedEditions.contains(5)) {
                    binding.cbDice.isChecked = true
                }
                if (selectedEditions.contains(6)) {
                    binding.cbGladius.isChecked = true
                }
                if (selectedEditions.contains(7)) {
                    binding.cbChaos.isChecked = true
                }
                if (selectedEditions.contains(8)) {
                    binding.cbRift.isChecked = true
                }
            }

            recursiveAddChangeListener(binding.root)

            dialog.setContentView(binding.root)

            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun recursiveAddChangeListener(viewGroup: ViewGroup) {
        viewGroup.children.forEach {
            if (it is CheckBox) {
                it.setOnCheckedChangeListener { _, _ ->
                    updateCollection()
                }
            }
            if (it is ViewGroup) {
                recursiveAddChangeListener(it)
            }
        }
    }

    private fun updateCollection() {
        val rarities = mutableListOf<Int>().apply {
            if (binding.cbCommon.isChecked) {
                add(1)
            }
            if (binding.cbRare.isChecked) {
                add(2)
            }
            if (binding.cbEpic.isChecked) {
                add(3)
            }
            if (binding.cbLegendary.isChecked) {
                add(4)
            }
        }.toList()
        val editions = mutableListOf<Int>().apply {
            if (binding.cbAlpha.isChecked) {
                add(0)
            }
            if (binding.cbBeta.isChecked) {
                add(1)
            }
            if (binding.cbPromo.isChecked) {
                add(2)
            }
            if (binding.cbReward.isChecked) {
                add(3)
            }
            if (binding.cbUntamed.isChecked) {
                add(4)
            }
            if (binding.cbDice.isChecked) {
                add(5)
            }
            if (binding.cbGladius.isChecked) {
                add(6)
            }
            if (binding.cbChaos.isChecked) {
                add(7)
            }
            if (binding.cbRift.isChecked) {
                add(8)
            }
        }.toList()
        listener.onFilterChange(rarities, editions)
    }
}