package com.example.expensetracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.databinding.ReminderItemLayoutBinding
import com.example.expensetracker.model.ReminderDataModel
import java.text.SimpleDateFormat
import java.util.*

class ReminderAdapter(private val reminderDataModelList:List<ReminderDataModel>,val onReminderItemClickListener: ReminderItemClickListener):RecyclerView.Adapter<ReminderAdapter.ViewHolder>() {

    class ViewHolder(private val reminderItemLayoutBinding: ReminderItemLayoutBinding):RecyclerView.ViewHolder(reminderItemLayoutBinding.root){
        private lateinit var onItemClickListener: ReminderItemClickListener
         fun bind(reminderDataModel: ReminderDataModel){

             //converting date
             val date = reminderDataModel.reminderDate
             val sdf = SimpleDateFormat("dd/MM/yyy", Locale.ENGLISH)
             val formateDate = sdf.format(date)

             //converting time
             val time = reminderDataModel.reminderTime
             val sdfTime=SimpleDateFormat("HH:mm", Locale.ENGLISH)
             val formatedTime = sdfTime.format(time)

             reminderItemLayoutBinding.reminderDataTv.text = reminderDataModel.reminderData
             reminderItemLayoutBinding.reminderCategoryTV.text = reminderDataModel.reminderCategory
             reminderItemLayoutBinding.reminderTimeTV.text= formatedTime
             reminderItemLayoutBinding.reminderSelectedDateTV.text=formateDate
             reminderItemLayoutBinding.reminderDeleteBtn.setOnClickListener {
              onItemClickListener.deleteItem(reminderDataModel)
             }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val reminderItemLayoutBinding=ReminderItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(reminderItemLayoutBinding)
    }

    override fun getItemCount(): Int {
        return reminderDataModelList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reminderDataModel = reminderDataModelList[position]
        holder.bind(reminderDataModel)
    }
}

interface ReminderItemClickListener{
    fun deleteItem(reminderDataModel: ReminderDataModel)
}
