package com.example.madproject.ui.profile
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.madproject.data.FirestoreRepository
import com.example.madproject.data.Profile
import com.example.madproject.data.Trip
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.UploadTask
import java.io.File

class SharedProfileViewModel(): ViewModel() {
    val profile : MutableLiveData<Profile> = MutableLiveData(Profile())

    fun getUser(file: File?): LiveData<Profile> {
        FirestoreRepository().getUser().addSnapshotListener(EventListener { value, e ->
            if (e != null) {
                profile.value = null
                return@EventListener
            }

            val p = value?.toObject(Profile::class.java)

            /*
            val photo = File(p!!.currentPhotoPath!!)
            if(!photo.exists()){
                FirestoreRepository().getUserImage(file)
                    .addOnSuccessListener {
                        p.currentPhotoPath = file?.absolutePath
                        profile.value = p
                    }
                    .addOnFailureListener{
                        p.currentPhotoPath = ""
                        profile.value = p
                    }
            }*/
            profile.value = p
        })

        return profile
    }

    fun setUser(p:Profile) : Task<Void> {
        return FirestoreRepository().setUser(p)
    }

    fun setUserImage(profile: Profile) : UploadTask {
        return FirestoreRepository().setUserImage(profile)
    }

}