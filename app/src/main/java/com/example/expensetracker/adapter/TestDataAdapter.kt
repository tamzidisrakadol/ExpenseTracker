package com.example.expensetracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.databinding.TestitemlayoutBinding
import com.example.expensetracker.model.TestDataModel

class TestDataAdapter(private val testDataList:List<TestDataModel>) : RecyclerView.Adapter<TestDataAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var testItemLayoutBinding = TestitemlayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(testItemLayoutBinding)
    }

    override fun getItemCount(): Int {
        return testDataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var testDataModel = testDataList[position]
        holder.bind(testDataModel)



    }
    class ViewHolder(testItemLayoutBinding: TestitemlayoutBinding):RecyclerView.ViewHolder(testItemLayoutBinding.root){
        private lateinit var testItemLayoutBinding:TestitemlayoutBinding

         fun bind(testDataModel: TestDataModel){
             testItemLayoutBinding.testdataTV.text = testDataModel.nData
             testItemLayoutBinding.testCategoryTV.text = testDataModel.categoryData
             testItemLayoutBinding.testdataTV.text = testDataModel.date.toString()
        }

    }

}