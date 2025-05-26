package com.example.timemanagement

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.timemanagement.MainActivity.ReminderCommunicationInterface
import com.example.timemanagement.adapter.ItemReminder
import com.example.timemanagement.adapter.OnClickInsert
import com.example.timemanagement.adapter.ReminderListAdapter
import com.example.timemanagement.databinding.FragmentFirstBinding
import com.example.timemanagement.datastore.DataStoreInstance
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import java.lang.reflect.Type
import java.time.OffsetDateTime

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(), ReminderCommunicationInterface {

    private lateinit var binding: FragmentFirstBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFirstBinding.inflate(inflater, container, false)

        (activity as MainActivity).reminderCommunicationInterface = this
        var list = mutableListOf<ItemReminder>()

        viewLifecycleOwner.lifecycleScope.launch {
            list = DataStoreInstance.getInstance().getList(requireContext()).first().toMutableList()
            initializeReminderAdapter(list)
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun initializeReminderAdapter(items : MutableList<ItemReminder>){
        binding.recyclerView.adapter = ReminderListAdapter(items, object: OnClickInsert{
            override fun onClickInsert() {

            }
        })
        (binding.recyclerView.adapter as ReminderListAdapter).sortByTime()
    }

    override fun onReminderCreation(message: String, date: OffsetDateTime, type: Int) {
        var adapter = binding.recyclerView.adapter as ReminderListAdapter
        adapter.addItem(ItemReminder(date, message, type))

        CoroutineScope(Dispatchers.IO).launch {
            DataStoreInstance.getInstance().saveList(requireContext(), adapter.getItems())
        }
    }

    override fun onReminderClear() {
        var adapter = binding.recyclerView.adapter as ReminderListAdapter
        Log.d("FirstFragment", "onReminderClear")
        adapter.clearItems()
    }
}