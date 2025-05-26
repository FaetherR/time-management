package com.example.timemanagement.bottomsheet

import android.content.Context
import android.app.DatePickerDialog
import android.graphics.Color
import android.icu.util.Calendar
import android.icu.util.TimeUnit
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.timemanagement.databinding.FragmentEditReminderBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.OffsetDateTime

class EditReminderBottomSheetFragment : BottomSheetDialogFragment() {

    private var callback: BottomSheetCallback? = null

    private var date: Calendar = Calendar.getInstance()
    private lateinit var reminderFullDate : OffsetDateTime

    private lateinit var binding: FragmentEditReminderBottomSheetBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = when {
            context is BottomSheetCallback -> context
            parentFragment is BottomSheetCallback -> parentFragment as BottomSheetCallback
            else -> throw RuntimeException("Parent must implement BottomSheetCallback")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditReminderBottomSheetBinding.inflate(inflater, container, false)

        binding.timePicker.setIs24HourView(true)

        var currentTime = OffsetDateTime.now()

        binding.timePicker.hour = currentTime.hour
        binding.timePicker.minute = currentTime.minute

        reminderFullDate = currentTime
        binding.timePicker.setOnTimeChangedListener { timePicker, hour, minute ->
            reminderFullDate = OffsetDateTime.of(date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1,
                date.get(Calendar.DAY_OF_MONTH), hour, minute, 0, 0, reminderFullDate.offset)
        }

        setDateClickListener()

        binding.buttonConfirm.setOnClickListener {
            reminderFullDate = OffsetDateTime.of(date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1,
                date.get(Calendar.DAY_OF_MONTH), reminderFullDate.hour, reminderFullDate.minute, 0,
                0, reminderFullDate.offset)

            var type = 0
            if(reminderFullDate.toLocalDate().equals(OffsetDateTime.now().toLocalDate())){
                type = 1
            }

            if(type == 1 && reminderFullDate.isBefore(OffsetDateTime.now())){
                reminderFullDate.plusDays(1)
            }

            callback?.onConfirm(binding.editTextMessage.text.toString(), reminderFullDate, type)
            dismiss()
        }

        binding.buttonCancel.setOnClickListener {
            dismiss()
        }

        return binding.root
    }
    private fun setDateClickListener() {
        // Create a Calendar instance to get the current date
        val calendar = Calendar.getInstance()

        // DatePickerDialog.OnDateSetListener handles the selected date
        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            date = calendar
        }

        // Open DatePickerDialog when the date selector button is clicked
        binding.buttonDay.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                datePicker,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Remove default background to see your color
        (view.parent as View).setBackgroundColor(Color.TRANSPARENT)

        // Set your background color here (or in XML)
        //view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.my_sheet_background))
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }
}
