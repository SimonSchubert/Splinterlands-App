package com.example.splinterlandstest.collection

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import androidx.fragment.app.DialogFragment
import com.example.splinterlandstest.R


class CollectionFilterDialogFragment : DialogFragment() {
    private lateinit var listener: CollectionDialogInterface

    interface CollectionDialogInterface {
        fun onFilterChange(rarities: List<Int>)
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
            val view = inflater.inflate(R.layout.fragment_filter, null)

            val selectedRarities = arguments?.getIntArray("rarities")

            val checkBoxCommon = view.findViewById<CheckBox>(R.id.cbCommon)
            val checkBoxRare = view.findViewById<CheckBox>(R.id.cbRare)
            val checkBoxEpic = view.findViewById<CheckBox>(R.id.cbEpic)
            val checkBoxLegendary = view.findViewById<CheckBox>(R.id.cbLegendary)
            if (selectedRarities != null) {
                if (selectedRarities.contains(1)) {
                    checkBoxCommon.isChecked = true
                }
                if (selectedRarities.contains(2)) {
                    checkBoxRare.isChecked = true
                }
                if (selectedRarities.contains(3)) {
                    checkBoxEpic.isChecked = true
                }
                if (selectedRarities.contains(4)) {
                    checkBoxLegendary.isChecked = true
                }
            }
            checkBoxCommon.setOnCheckedChangeListener { buttonView, isChecked ->
                updateCollection(checkBoxCommon, checkBoxRare, checkBoxEpic, checkBoxLegendary)
            }
            checkBoxRare.setOnCheckedChangeListener { buttonView, isChecked ->
                updateCollection(checkBoxCommon, checkBoxRare, checkBoxEpic, checkBoxLegendary)
            }
            checkBoxEpic.setOnCheckedChangeListener { buttonView, isChecked ->
                updateCollection(checkBoxCommon, checkBoxRare, checkBoxEpic, checkBoxLegendary)
            }
            checkBoxLegendary.setOnCheckedChangeListener { buttonView, isChecked ->
                updateCollection(checkBoxCommon, checkBoxRare, checkBoxEpic, checkBoxLegendary)
            }

            dialog.setContentView(view)

            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun updateCollection(
        checkBoxCommon: CheckBox,
        checkBoxRare: CheckBox,
        checkBoxEpic: CheckBox,
        checkBoxLegendary: CheckBox
    ) {
        val rarities = mutableListOf<Int>().apply {
            if (checkBoxCommon.isChecked) {
                add(1)
            }
            if (checkBoxRare.isChecked) {
                add(2)
            }
            if (checkBoxEpic.isChecked) {
                add(3)
            }
            if (checkBoxLegendary.isChecked) {
                add(4)
            }
        }.toList()
        listener.onFilterChange(rarities)
    }
}