package com.elmz.shelfthing.fragment

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elmz.shelfthing.R
import com.elmz.shelfthing.adapter.StatusAdapter
import com.elmz.shelfthing.decoration.StatusDecoration
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HomeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
	private var mListener: OnFragmentInteractionListener? = null
	private var mMissingProducts: ArrayList<String> = ArrayList()
	private lateinit var mAdapter: StatusAdapter
	private var mLayoutInflater: LayoutInflater? = null
	private var mDialogBehavior: BottomSheetBehavior<*>? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mAdapter = StatusAdapter(context)
	}

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
							  savedInstanceState: Bundle?): View? {
		mLayoutInflater = inflater
		// Inflate the layout for this fragment
		return inflater?.inflate(R.layout.fragment_home, container, false)
	}

	override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
		header_card.setOnClickListener { _ -> showInfo() }
		right_card.setOnClickListener { _ -> onClickSettings() }
		update(mMissingProducts)
	}

	fun showInfo() {
		val view = mLayoutInflater?.inflate(R.layout.sheet, null)
		val recyclerView = view?.findViewById<RecyclerView>(R.id.list)
//		recyclerView.setHasFixedSize(true);
		recyclerView?.addItemDecoration(StatusDecoration())
		recyclerView?.layoutManager = LinearLayoutManager(mLayoutInflater?.context)
		mAdapter.update(mMissingProducts)
		recyclerView?.adapter = mAdapter
		val infoSheet = BottomSheetDialog(context)
		infoSheet.setContentView(view)
		mDialogBehavior = BottomSheetBehavior.from(view?.parent as View)
		infoSheet.show()
		mDialogBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
	}

	fun hideInfo() {
		mDialogBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
	}

	fun onClickSettings() {
		mListener?.onClickSettings()
	}

	override fun onAttach(context: Context) {
		super.onAttach(context)
		if (context is OnFragmentInteractionListener) {
			mListener = context
		} else {
			throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
		}
	}

	override fun onDetach() {
		super.onDetach()
		mListener = null
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 *
	 *
	 * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
	 */
	interface OnFragmentInteractionListener {
		fun onClickInfo()
		fun onClickSettings()
	}

	fun update(missingProducts: ArrayList<String>) {
		mMissingProducts = missingProducts
		if (mMissingProducts.isEmpty()) {
			mMissingProducts.add("No alerts!")
			header.text = resources.getString(R.string.alert_none)
			header_card.setCardBackgroundColor(resources.getColor(R.color.blue_300))
			header.setTextColor(resources.getColor(R.color.blue_900))
		} else {
			header.text = resources.getQuantityString(R.plurals.alert_header, mMissingProducts.size)
			header_card.setCardBackgroundColor(resources.getColor(R.color.red_300))
			header.setTextColor(resources.getColor(R.color.red_900))
		}
	}

	companion object {
		/**
		 * Use this factory method to create a new instance of
		 * this fragment using the provided parameters.
		 *
		 * @param param1 Parameter 1.
		 * @param param2 Parameter 2.
		 * @return A new instance of fragment HomeFragment.
		 */
		// TODO: Rename and change types and number of parameters
		fun newInstance(): HomeFragment {
			return HomeFragment()
		}
	}
}// Required empty public constructor
