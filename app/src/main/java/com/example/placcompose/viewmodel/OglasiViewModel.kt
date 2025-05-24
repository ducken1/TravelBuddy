package com.example.placcompose.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.placcompose.dataclasses.OglasData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OglasiViewModel : ViewModel() {


    private val databaseReference = FirebaseDatabase.getInstance().getReference("Oglasi")

//    private val _filteredData = mutableStateOf<List<OglasData>>(emptyList())
//    val AllData: State<List<OglasData>> get() = _filteredData

//    private val _myData = mutableStateOf<FirebaseRecyclerOptions<OglasData>>(FirebaseRecyclerOptions.Builder<OglasData>().build())
//    val MyData: MutableState<FirebaseRecyclerOptions<OglasData>> get() = _myData

    private val _myData = mutableStateOf<List<OglasData>>(emptyList())
    val MyData: State<List<OglasData>> get() = _myData

    private val _myData2 = mutableStateOf<List<OglasData>>(emptyList())
    val MyData2: State<List<OglasData>> get() = _myData2

    private val _selectedOglasData = mutableStateOf<OglasData?>(null)
    val selectedOglasData: State<OglasData?> get() = _selectedOglasData

    init {
        //loadAllData()
        filterData()
        searchData(searchQuery = "")
    }

    fun setSearchQuery(searchQuery: String) {
        searchData(searchQuery)
    }

    fun setSelectedOglasData(oglasData: OglasData) {
        _selectedOglasData.value = oglasData
    }

//    fun loadAllData() {
//        databaseReference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val dataList = mutableListOf<OglasData>()
//                for (dataSnapshot in snapshot.children) {
//                    val oglasData = dataSnapshot.getValue(OglasData::class.java)
//                    oglasData?.let {
//                        dataList.add(it)
//                    }
//                }
//                _filteredData.value = dataList
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//            }
//        })
//    }

    private fun filterData() {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        if (userId != null) {
            val query = databaseReference
                .orderByChild("userId")
                .equalTo(userId)

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dataList = mutableListOf<OglasData>()
                    for (dataSnapshot in snapshot.children) {
                        val oglasData = dataSnapshot.getValue(OglasData::class.java)
                        oglasData?.let {
                            dataList.add(it)
                        }
                    }
                    _myData.value = dataList
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

    private fun searchData(searchQuery: String) {

        val query = databaseReference
            .orderByChild("name")
            .startAt(searchQuery)
            .endAt(searchQuery + "\uf8ff")

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataList = mutableListOf<OglasData>()
                for (dataSnapshot in snapshot.children) {
                    val oglasData = dataSnapshot.getValue(OglasData::class.java)
                    oglasData?.let {
                        dataList.add(it)
                    }
                }
                _myData2.value = dataList
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}
