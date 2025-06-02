package com.example.timemanagement.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.timemanagement.databinding.ItemReminderBinding
import java.time.format.DateTimeFormatter

class ReminderListAdapter(private val items: MutableList<ItemReminder>, private val onClickInsert: OnClickInsert) : RecyclerView.Adapter<ReminderListAdapter.ReminderListViewHolder>() {
    var onClick: OnClickAdapter? = null

    interface OnClickAdapter {
        fun btnOnDisableClick(position:Int)
    }

    fun setOnClickListener(onClickAdapter: OnClickAdapter) {
        onClick = onClickAdapter
    }
    inner class ReminderListViewHolder(private val binding: ItemReminderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemReminder, position: Int) {
            when(item.type){
                0 -> binding.textViewTime.text = item.time.format(DateTimeFormatter.ofPattern("HH:mm, dd LLLL"))
                1 -> binding.textViewTime.text = item.time.format(DateTimeFormatter.ofPattern("HH:mm"))
            }

            binding.textViewMessage.text = item.message

            binding.switchDisable.setOnClickListener {
                items.removeAt(position)
                notifyItemRemoved(position)
                //onClickInsert.onDisableClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderListViewHolder {
        val binding =
            ItemReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReminderListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderListViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size

    fun getItems(): MutableList<ItemReminder>{
        return items
    }

    fun addItem(item: ItemReminder){
        items.add(item)
        sortByTime()
    }

    fun clearItems(){
        items.clear()
        notifyDataSetChanged()
    }

    fun sortByTime(){
        items.sortWith(Comparator { lhs, rhs ->
            // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
            if (lhs.time < rhs.time) -1 else if (lhs.time > rhs.time) 1 else 0
        })
        notifyDataSetChanged()
    }
}