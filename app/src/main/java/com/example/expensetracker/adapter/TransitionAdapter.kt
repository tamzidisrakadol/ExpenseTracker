package com.example.expensetracker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.databinding.ItemLayoutBinding
import com.example.expensetracker.model.TransactionModel
import java.text.SimpleDateFormat
import java.util.Locale


class TransitionAdapter(private val tlist:List<TransactionModel>, private val onItemClickListener: OnItemClickListener):RecyclerView.Adapter<TransitionAdapter.ViewHolder>(){

    class ViewHolder(val itemLayoutBinding: ItemLayoutBinding):RecyclerView.ViewHolder(itemLayoutBinding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemLayoutBinding = ItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(itemLayoutBinding)
    }

    override fun getItemCount(): Int {
        return tlist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transitionModel = tlist[position]
        val date = transitionModel.date.toDate()
        val sdf = SimpleDateFormat("MMM dd", Locale.ENGLISH)
        val formattedDate = sdf.format(date)

        holder.itemLayoutBinding.amountTV.text = "TK "+transitionModel.amount.toString()
        holder.itemLayoutBinding.categoryTV.text = transitionModel.category
        holder.itemLayoutBinding.noteTV.text = transitionModel.note
        holder.itemLayoutBinding.typeTV.text = transitionModel.type
        holder.itemLayoutBinding.transactionDate.text = formattedDate

        holder.itemLayoutBinding.deleteImgBtn.setOnClickListener {
            onItemClickListener.onItemClick(transitionModel,position)
        }
    }

}

interface OnItemClickListener{
   fun onItemClick(transactionModel: TransactionModel, post:Int)
}