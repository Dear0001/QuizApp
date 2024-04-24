package com.e2g16.quizapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.e2g16.quizapp.Withdrawal
import com.e2g16.quizapp.adaptor.HistoryAdaptor
import com.e2g16.quizapp.databinding.FragmentHistoryBinding
import com.e2g16.quizapp.model.HistoryModelClass
import com.e2g16.quizapp.model.User
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class HistoryFragment : Fragment() {
    val binding by lazy {
        FragmentHistoryBinding.inflate(layoutInflater)
    }

    lateinit var adaptor: HistoryAdaptor
    var listHistory = ArrayList<HistoryModelClass>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listHistory.add(HistoryModelClass("12:03", "200", false))
        listHistory.add(HistoryModelClass("12:03", "200",true))
        listHistory.add(HistoryModelClass("12:03", "200",false))
        listHistory.add(HistoryModelClass("12:03", "200",true))

        Firebase.database.reference.child("PlayerCoinHistory")
            .child(Firebase.auth.currentUser!!.uid)
            .addValueEventListener(object :ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //listHistory.clear()
                    for (datasnapshot in snapshot.children) {
                        var data = datasnapshot.getValue(HistoryModelClass::class.java)
                        listHistory.add(data!!)
                    }

                    adaptor = HistoryAdaptor(listHistory)
                    binding.HistoryRecycleView.adapter = adaptor
                    adaptor.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("HistoryFragment", "Error fetching data: ${error.message}")
                }


            })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding.coinWithdrawal.setOnClickListener {
            var bottomSheetDialog: BottomSheetDialogFragment = Withdrawal()
            bottomSheetDialog.show(requireActivity().supportFragmentManager, "TEST")
            bottomSheetDialog.enterTransition
        }
        binding.CoinWithdrawal1.setOnClickListener {
            var bottomSheetDialog: BottomSheetDialogFragment = Withdrawal()
            bottomSheetDialog.show(requireActivity().supportFragmentManager, "TEST")
            bottomSheetDialog.enterTransition
        }
        binding.HistoryRecycleView.layoutManager = LinearLayoutManager(requireContext())
        var adapter = HistoryAdaptor(listHistory)
        binding.HistoryRecycleView.adapter = adapter
        binding.HistoryRecycleView.setHasFixedSize(true)
        // Inflate the layout for this fragment


        Firebase.database.reference.child("Users")
            .child(Firebase.auth.currentUser!!.uid)
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var user = snapshot.getValue<User>()
                        binding.name.text = user?.name
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                }
            )

        Firebase.database.reference.child("PlayerCoin")
            .child(Firebase.auth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.value
                    if (value != null) {
                        binding.coinWithdrawal.text = snapshot.value.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO()
                }

            })

        return binding.root
    }

    companion object {

    }
}