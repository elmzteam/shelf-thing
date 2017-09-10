package com.elmz.shelfthing.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.elmz.shelfthing.R

class StatusAdapter(context: Context) : RecyclerView.Adapter<StatusAdapter.ViewHolder>() {
	private val mLayoutInflater: LayoutInflater = LayoutInflater.from(context)
	var mItems: ArrayList<String> = ArrayList()

	// View lookup cache
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		internal var text: TextView? = null

		init {
			text = itemView.findViewById(R.id.item)
		}
	}

	override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(mLayoutInflater.inflate(viewType, viewGroup, false))
	}

	override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
		val item: String = mItems[position]
		// Item name
		viewHolder.text?.text = item
	}

	override fun getItemCount(): Int {
		return mItems.size
	}

	override fun getItemViewType(position: Int): Int {
		return R.layout.row_status
	}

	fun update(items: List<String>) {
		val oldCount = mItems.size
		mItems.clear()
		mItems.addAll(items)
		if (oldCount > mItems.size) {
			notifyItemRangeRemoved(mItems.size, oldCount - mItems.size)
			notifyItemRangeChanged(0, mItems.size)
		} else {
			notifyItemRangeChanged(0, oldCount)
			notifyItemRangeInserted(oldCount, mItems.size - oldCount)
		}
	}
}