package com.elmz.shelfthing.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elmz.shelfthing.R

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

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (arguments != null) {
			missingProducts = arguments.getStringArrayList(ARG_MISSING_PRODUCTS)
		} else {
			throw IllegalArgumentException()
		}
	}

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
							  savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater?.inflate(R.layout.fragment_home, container, false)
	}

	// TODO: Rename method, update argument and hook method into UI event
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
		// TODO: Rename parameter arguments, choose names that match
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