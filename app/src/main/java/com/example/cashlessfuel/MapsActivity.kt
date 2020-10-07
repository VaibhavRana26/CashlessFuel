package com.example.cashlessfuel

import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.cashlessfuel.model.mapModel.NearByApiResponse
import com.example.cashlessfuel.network.NearByApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.okhttp.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit


class MapsActivity : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.uiSettings.isZoomControlsEnabled = true
        var marker : MarkerOptions = MarkerOptions().position(sydney).title("Sydney Gas Station")
        marker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.gas_station))
        mMap.addMarker(marker)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10.0f))
        mMap.setOnMarkerClickListener(this)
        setUpMap()

    }

    private fun placeMarkerOnMap(location: LatLng) {
        // 1
        val markerOptions = MarkerOptions().position(location)
        // 2
        val titleStr = getAddress(location)  // add these two lines
        markerOptions.title(titleStr)
        mMap.addMarker(markerOptions)
        markerOptions.icon(
            BitmapDescriptorFactory.fromBitmap(
                BitmapFactory.decodeResource(resources, R.drawable.gas_station)
            )
        )

    }
    private fun getAddress(latLng: LatLng): String {
        // 1
        val geocoder = Geocoder(this)
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""

        try {
            // 2
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            // 3
            if (null != addresses && !addresses.isEmpty()) {
                address = addresses[0]
                for (i in 0 until address.maxAddressLineIndex) {
                    addressText += if (i == 0) address.getAddressLine(i) else "\n" + address.getAddressLine(
                        i
                    )
                }
            }
        } catch (e: IOException) {
            Log.e("MapsActivity", e.localizedMessage)
        }

        return addressText
    }

    override fun onMarkerClick(p0: Marker?) =  false

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        mMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLng)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
    }
//    fun findPlaces(placeType: String?) {
//        val call: Call<NearByApiResponse> = MyApplication.getApp().getApiService().getNearbyPlaces(
//            placeType,
//            location.getLatitude().toString() + "," + location.getLongitude(),
//            PROXIMITY_RADIUS
//        )
//        call.enqueue(object : Callback<NearByApiResponse?>() {
//            override fun onResponse(
//                call: Call<NearByApiResponse?>?,
//                response: Response<NearByApiResponse?>
//            ) {
//                try {
//                    mMap.clear()
//                    // This loop will go through all the results and add marker on each location.
//                    for (i in 0 until response.body().getResults().size()) {
//                        val lat: Double =
//                            response.body().getResults().get(i).getGeometry().getLocation().getLat()
//                        val lng: Double =
//                            response.body().getResults().get(i).getGeometry().getLocation().getLng()
//                        val placeName: String = response.body().getResults().get(i).getName()
//                        val vicinity: String = response.body().getResults().get(i).getVicinity()
//                        val markerOptions = MarkerOptions()
//                        val latLng = LatLng(lat, lng)
//                        // Position of Marker on Map
//                        markerOptions.position(latLng)
//                        // Adding Title to the Marker
//                        markerOptions.title("$placeName : $vicinity")
//                        // Adding Marker to the Camera.
//                        val m: Marker = mMap.addMarker(markerOptions)
//                        // Adding colour to the marker
//                        markerOptions.icon(
//                            BitmapDescriptorFactory.defaultMarker(
//                                BitmapDescriptorFactory.HUE_RED
//                            )
//                        )
//                        // move map camera
//                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
//                        mMap.animateCamera(CameraUpdateFactory.zoomTo(11f))
//                    }
//                } catch (e: Exception) {
//                    Log.d("onResponse", "There is an error")
//                    e.printStackTrace()
//                }
//            }
//
//            override fun onFailure(call: Call<NearByApiResponse?>?, t: Throwable) {
//                Log.d("onFailure", t.toString())
//                t.printStackTrace()
//              //  PROXIMITY_RADIUS += 10000
//            }
//        })
//    }

//    fun getApiService(): NearByApi? {
//        return if (nearByApi == null) {
//            val interceptor = HttpLoggingInterceptor()
//            interceptor.level = HttpLoggingInterceptor.Level.BODY
//            val client: OkHttpClient =
//                Builder().retryOnConnectionFailure(true).readTimeout(80, TimeUnit.SECONDS)
//                    .connectTimeout(80, TimeUnit.SECONDS).addInterceptor(interceptor).build()
//            //val retrofit: Retrofit = Retrofit.Builder().baseUrl(Constant.PLACE_API_BASE_URL)
//                .addConverterFactory(getApiConvertorFactory()).client(client).build()
//            nearByApi = retrofit.create(NearByApi::class.java)
//            nearByApi
//        } else {
//            nearByApi
//        }
//    }

    private fun getApiConvertorFactory(): GsonConverterFactory? {
        return GsonConverterFactory.create()
    }
}