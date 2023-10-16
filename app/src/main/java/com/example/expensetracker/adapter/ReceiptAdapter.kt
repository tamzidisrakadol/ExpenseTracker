package com.example.expensetracker.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.expensetracker.R
import com.example.expensetracker.databinding.ReceiptitemlayoutBinding
import com.example.expensetracker.model.ReceiptModal

class ReceiptAdapter(private val receiptList:List<ReceiptModal>, val context: Context,private val receiptItemClickListener: ReceiptItemClickListener):RecyclerView.Adapter<ReceiptAdapter.ViewHolder>() {
    class ViewHolder(val receiptItemlayoutBinding: ReceiptitemlayoutBinding):RecyclerView.ViewHolder(receiptItemlayoutBinding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val receiptitemlayoutBinding = ReceiptitemlayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(receiptitemlayoutBinding)
    }

    override fun getItemCount(): Int {
     return receiptList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val receiptModal = receiptList[position]
        holder.receiptItemlayoutBinding.receiptDateTV.text = receiptModal.date
        Glide.with(context).load(receiptModal.imgUrl).into(holder.receiptItemlayoutBinding.receiptImg)

        holder.receiptItemlayoutBinding.imageButton.setOnClickListener {
            receiptItemClickListener.onItemClick(receiptModal,position)
        }

        holder.receiptItemlayoutBinding.root.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val customLayout: View = LayoutInflater.from(context).inflate(R.layout.custom_alert_dialog,null)
            builder.setView(customLayout)
            Glide.with(context).load(receiptModal.imgUrl).into(customLayout.findViewById(R.id.receiptImgAlert))
            val dialog:AlertDialog = builder.create()
            dialog.show()
        }
    }
}

interface ReceiptItemClickListener{
    fun onItemClick(receiptModal: ReceiptModal,pos:Int)
}