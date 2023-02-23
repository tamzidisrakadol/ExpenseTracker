package com.example.expensetracker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.databinding.ItemLayoutBinding
import com.example.expensetracker.model.Transition

class TransitionAdapter(val tlist:List<Transition>,val context: Context):RecyclerView.Adapter<TransitionAdapter.ViewHolder>(){
    private lateinit var itemLayoutBinding: ItemLayoutBinding

    class ViewHolder(val itemLayoutBinding: ItemLayoutBinding):RecyclerView.ViewHolder(itemLayoutBinding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        itemLayoutBinding = ItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(itemLayoutBinding)
    }

    override fun getItemCount(): Int {
        return tlist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transitionModel = tlist[position]

        holder.itemLayoutBinding.amountTV.text = transitionModel.amount.toString()
        holder.itemLayoutBinding.categoryTV.text = transitionModel.category
        holder.itemLayoutBinding.noteTV.text = transitionModel.note
        holder.itemLayoutBinding.typeTV.text = transitionModel.type
    }

}