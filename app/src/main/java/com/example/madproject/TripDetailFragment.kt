package com.example.madproject

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.example.madproject.lib.FixOrientation
import java.io.File

class TripDetailFragment : Fragment(R.layout.fragment_trip_detail) {
    private var currentCarPath: String? = ""
    private lateinit var imageCar : ImageView
    private lateinit var photoCarURI: Uri
    private lateinit var departure : TextView
    private lateinit var arrival : TextView
    private lateinit var departureDate : TextView
    private lateinit var departureTime : TextView
    private lateinit var duration : TextView
    private lateinit var availableSeats : TextView
    private lateinit var price : TextView
    private lateinit var additionalInfo : TextView
    private lateinit var intermediateStop : TextView
    private lateinit var sharedPref: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        imageCar = view.findViewById(R.id.imageCar)
        departure = view.findViewById(R.id.departure_location)
        arrival = view.findViewById(R.id.arrival_location)
        departureDate = view.findViewById(R.id.date)
        departureTime = view.findViewById(R.id.time)
        duration = view.findViewById(R.id.duration)
        availableSeats = view.findViewById(R.id.seats)
        price = view.findViewById(R.id.price)
        additionalInfo = view.findViewById(R.id.info)
        intermediateStop = view.findViewById(R.id.intermediate_stops)
        sharedPref = this.requireActivity().getPreferences(Context.MODE_PRIVATE)

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_trip, menu)
    }

    private fun setPic() {
        if (currentCarPath != "") {
            val imgFile = File(currentCarPath!!)
            photoCarURI = FileProvider.getUriForFile(this.requireActivity().applicationContext, "com.example.android.fileprovider", imgFile)
            val pic = FixOrientation.handleSamplingAndRotationBitmap(this.requireActivity().applicationContext, photoCarURI)
            imageCar.setImageBitmap(pic)
        } else imageCar.setImageResource(R.drawable.car_example)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.editTrip -> {
                val action = TripDetailFragmentDirections.actionTripDetailToTripEdit(
                        group11Lab2DEPARTURE = departure.text.toString(),
                        group11Lab2ARRIVAL = arrival.text.toString(),
                        group11Lab2DATE = departureDate.text.toString(),
                        group11Lab2TIME = departureTime.text.toString(),
                        group11Lab2DURATION = duration.text.toString(),
                        group11Lab2SEATS = availableSeats.text.toString(),
                        group11Lab2PRICE = price.text.toString(),
                        group11Lab2INFO = additionalInfo.text.toString(),
                        group11Lab2STOPS = intermediateStop.text.toString(),
                        group11Lab2CURRENTCARPATH = currentCarPath!!
                )
                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}