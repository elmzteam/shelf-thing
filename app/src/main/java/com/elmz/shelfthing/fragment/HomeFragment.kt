package com.elmz.shelfthing.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elmz.shelfthing.R
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
	private var numMissing = 0

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
	}

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
							  savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater?.inflate(R.layout.fragment_home, container, false)
	}

	override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
		header_card.setOnClickListener { _ -> onClickInfo() }
		header.text = resources.getQuantityString(R.plurals.alert_header, numMissing, numMissing)
		right_card.setOnClickListener { _ -> onClickSettings() }
	}

	fun onClickInfo() {
		mListener?.onClickInfo()
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

	fun update(missing: Int) {
		numMissing = missing
		header.text = resources.getQuantityString(R.plurals.alert_header, numMissing, numMissing)
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
