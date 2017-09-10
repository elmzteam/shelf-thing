package com.elmz.shelfthing.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elmz.shelfthing.R
import com.elmz.shelfthing.adapter.StatusAdapter
import com.elmz.shelfthing.decoration.StatusDecoration
import kotlinx.android.synthetic.main.fragment_status.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [StatusFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [StatusFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StatusFragment : Fragment() {
	private lateinit var missingProducts: ArrayList<String>
	private var mListener: OnFragmentInteractionListener? = null
	private lateinit var mAdapter: StatusAdapter
	private var mLayoutManager: LinearLayoutManager? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mAdapter = StatusAdapter(context)
		if (arguments != null) {
			missingProducts = arguments.getStringArrayList(ARG_MISSING_PRODUCTS)
			mAdapter.update(missingProducts)
		} else {
			throw IllegalArgumentException()
		}
	}

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
							  savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		mLayoutManager = LinearLayoutManager(inflater?.context)
		return inflater?.inflate(R.layout.fragment_status, container, false)
	}

	override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
		list.addItemDecoration(StatusDecoration())
		list.layoutManager = mLayoutManager
		list.adapter = mAdapter
	}

	// TODO: bind to button
	fun onClickBack() {
		mListener?.onClickBack()
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
		fun onClickBack()
	}

	companion object {
		// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
		private val ARG_MISSING_PRODUCTS = "param1"

		/**
		 * Use this factory method to create a new instance of
		 * this fragment using the provided parameters.
		 *
		 * @param missingProducts List of missing products (can be empty).
		 * @return A new instance of fragment HomeFragment.
		 */
		// TODO: Rename and change types and number of parameters
		fun newInstance(missingProducts: ArrayList<String>): StatusFragment {
			val fragment = StatusFragment()
			val args = Bundle()
			args.putStringArrayList(ARG_MISSING_PRODUCTS, missingProducts)
			fragment.arguments = args
			return fragment
		}
	}
}// Required empty public constructor