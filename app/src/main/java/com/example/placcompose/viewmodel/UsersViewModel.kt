package com.example.placcompose.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.placcompose.dataclasses.UsersData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class UsersViewModel : ViewModel() {


    private val databaseReference = FirebaseDatabase.getInstance().getReference("Users")





    private val _myData = mutableStateOf<List<UsersData>>(emptyList())
    val MyData: State<List<UsersData>> get() = _myData

    private val _myData2 = mutableStateOf<List<UsersData>>(emptyList())
    val MyData2: State<List<UsersData>> get() = _myData2

    private val _selectedUsersData = mutableStateOf<UsersData?>(null)
    val selectedUserData: State<UsersData?> get() = _selectedUsersData

    init {
        filterData()
        searchData(searchQuery = "")
    }

    fun setSearchQuery(searchQuery: String) {
        searchData(searchQuery)
    }

    fun setSelectedUsersData(userData: UsersData) {
        _selectedUsersData.value = userData
    }

    private fun filterData() {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        if (userId != null) {
            val query = databaseReference
                .orderByChild("id")
                .equalTo(userId)

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dataList = mutableListOf<UsersData>()
                    for (dataSnapshot in snapshot.children) {
                        val userData = dataSnapshot.getValue(UsersData::class.java)
                        userData?.let {
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
            .orderByChild("bio_lower")
            .startAt(searchQuery)
            .endAt(searchQuery + "\uf8ff")

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataList = mutableListOf<UsersData>()
                for (dataSnapshot in snapshot.children) {
                    val userData = dataSnapshot.getValue(UsersData::class.java)
                    userData?.let {
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
