package com.elmz.shelfthing.decoration

import android.content.res.Resources
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class StatusDecoration: RecyclerView.ItemDecoration() {
	private var space: Int

	init {
		val dp = 16
		val metrics = Resources.getSystem().displayMetrics
		space = (dp * metrics.density + 0.5).toInt()
	}

	override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
		super.getItemOffsets(outRect, view, parent, state)
		val position = parent.getChildLayoutPosition(view)
		if (position == 0) {
			outRect.top = space
		}

//		outRect.left = space
//		outRect.right = space
		outRect.bottom = space
	}
}
