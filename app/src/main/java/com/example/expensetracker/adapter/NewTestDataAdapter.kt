package com.example.expensetracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.databinding.NewTestItemLayoutBinding
import com.example.expensetracker.model.TestDataModel


class NewTestDataAdapter(private val newTestDataModelLIst:List<TestDataModel>):RecyclerView.Adapter<NewTestDataAdapter.ViewHolder>(){

    class ViewHolder(private val newTestItemLayoutBinding: NewTestItemLayoutBinding):RecyclerView.ViewHolder(newTestItemLayoutBinding.root){

        fun bind(testDataModel: TestDataModel){
            newTestItemLayoutBinding.dataTV.text = testDataModel.newData
            newTestItemLayoutBinding.monthNameTV.text = testDataModel.month
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val newTestItemLayoutBinding = NewTestItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(newTestItemLayoutBinding)
    }

    override fun getItemCount(): Int {
        return newTestDataModelLIst.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val testDataModelClass = newTestDataModelLIst[position]
        holder.bind(testDataModelClass)
    }
}