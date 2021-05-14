package com.example.madproject.ui.yourtrips

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.madproject.data.FirestoreRepository
import com.example.madproject.data.Profile
import com.example.madproject.data.Trip
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.EventListener

class TripListViewModel: ViewModel() {

    private var userTrips: MutableLiveData<List<Trip>> = MutableLiveData()
    private var otherTrips: MutableLiveData<List<Trip>> = MutableLiveData()
    private var selectedDB: MutableLiveData<Trip> = MutableLiveData()

    var selectedLocal = Trip()
    var currentPhotoPath = ""
    var useDBImage = false

    // Flags to manage the Dialog when the orientation changes
    var bookingDialogOpened = false
    var changedOrientationBooking = false
    var deleteDialogOpened = false
    var changedOrientationDelete = false

    // Flags used to manage the trip booking
    var comingFromOther = false

    // Data used to manage the booking dialog restore state from OtherTripsFragment
    var tripIdInDialog = ""

    init {
        loadUserTrips()
        loadOtherTrips()
    }

    private fun loadUserTrips() {
        FirestoreRepository().getTrips().addSnapshotListener(EventListener { value, e ->
            if (e != null) {
                userTrips.value = null
                return@EventListener
            }

            val retrievedTrips: MutableList<Trip> = mutableListOf()
            for (doc in value!!) {
                val t = doc.toObject(Trip::class.java)
                retrievedTrips.add(t)
                if (t.id == selectedLocal.id) selectedDB.value = t
            }
            userTrips.value = retrievedTrips
        })
    }

    private fun loadOtherTrips() {
        FirestoreRepository().getUsersList()
            .addSnapshotListener(EventListener { value1, error1 ->
                if (error1!= null) {
                    return@EventListener
                }
                val retrievedTrips : MutableList<Trip> = mutableListOf()
                for (user in value1!!) {
                    user.reference.collection("createdTrips")
                        .whereNotEqualTo("availableSeat", "0")
                        .addSnapshotListener { value, error ->
                            if (error != null) {
                                otherTrips.value = null
                            } else {
                                val updatedList: MutableList<Trip> = mutableListOf()
                                for (trip in value!!) {
                                    val t = trip.toObject(Trip::class.java)
                                    if (t.id == selectedLocal.id) selectedDB.value = t
                                    val toUpdate = findUpdate(t, retrievedTrips)
                                    if (toUpdate.id != "-1")
                                        retrievedTrips[retrievedTrips.indexOf(toUpdate)] = t
                                    else
                                        retrievedTrips.add(t)
                                    updatedList.add(t)
                                }
                                val toRemove = findDeleted(updatedList, retrievedTrips)
                                if (toRemove.id != "-1") {
                                    retrievedTrips.remove(toRemove)
                                    if (selectedDB.value?.id == toRemove.id)
                                        selectedDB.value = null
                                }
                                otherTrips.value = retrievedTrips
                            }
                        }
                }
            })
    }

    fun getUserTrips(): LiveData<List<Trip>> {
        return userTrips
    }

    fun getOtherTrips(): LiveData<List<Trip>> {
        return otherTrips
    }

    private fun findUpdate(t: Trip, trips: List<Trip>): Trip {
        for (trip in trips) {
            if (t.id == trip.id) return trip
        }
        return Trip(id = "-1")
    }

    private fun findDeleted(upd: List<Trip>, comp: List<Trip>): Trip {

        // If the size of upd and the subList of comp belonging to ownerEmail is the same it means that
        // no trip was deleted
        if (upd.isEmpty()) return Trip(id = "-1")
        val filtered = comp.filter { t -> t.ownerEmail == upd[0].ownerEmail }
        if (upd.size == filtered.size) return Trip(id = "-1")

        for (t1 in filtered) {
            // If in upd there is no id with the id in comp, it means that id was deleted
            if (upd.none { trip -> trip.id == t1.id })
                return t1
        }

        return Trip(id = "-1")
    }

    fun saveTrip(t: Trip): Task<Void> {
        return FirestoreRepository().insertTrip(t)
    }

    fun getSelectedDB(t: Trip): LiveData<Trip> {
        // Check whether the selected trip is contained in the userTrips
        if (userTrips.value != null) {
            for (trip in userTrips.value!!) {
                if (trip.id == t.id) {
                    selectedDB.value = trip
                    return selectedDB
                }
            }
        }

        // Check whether the selected trip is contained in the otherTrips
        if (otherTrips.value == null) return selectedDB
        for (trip in otherTrips.value!!) {
            if (trip.id == t.id) {
                selectedDB.value = trip
                return selectedDB
            }
        }
        return selectedDB
    }
}