package com.example.arcgisapplication

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.mapping.view.LocationDisplay
import android.widget.Toast
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat

class AddCurrentLocationActivity : AppCompatActivity() {
    private lateinit var mMapView: MapView
    /* ** ADD ** */
    private lateinit var mLocationDisplay: LocationDisplay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_layer)

        mMapView = this.findViewById(R.id.mapView)

        setupMap()

        //add layer
        //addTrailheadsLayer()

        //add device location
        setupLocationDisplay()


    }

    private fun setupLocationDisplay() {
        mLocationDisplay = mMapView.locationDisplay

        mLocationDisplay.addDataSourceStatusChangedListener { dataSourceStatusChangedEvent ->
            if (dataSourceStatusChangedEvent.isStarted() || dataSourceStatusChangedEvent.getError() == null) {
                return@addDataSourceStatusChangedListener
            }

            val requestPermissionsCode = 2
            val requestPermissions =
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

            if (!(ContextCompat.checkSelfPermission(this, requestPermissions[0])
                        == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, requestPermissions[1])
                        == PackageManager.PERMISSION_GRANTED)
            ) {
                ActivityCompat.requestPermissions(this, requestPermissions, requestPermissionsCode)
            } else {
                val message = String.format(
                    "Error in DataSourceStatusChangedListener: %s",
                    dataSourceStatusChangedEvent.source.locationDataSource.error.message
                )
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }

        mLocationDisplay.autoPanMode = LocationDisplay.AutoPanMode.NAVIGATION
        mLocationDisplay.startAsync()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mLocationDisplay.startAsync()
        } else {
            Toast.makeText(this, resources.getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show()
        }
    }
    private fun addTrailheadsLayer() {
        val url = "https://services9.arcgis.com/IMlF46PlwBvgMDOB/arcgis/rest/services/griffith_park_access/FeatureServer/"
        val serviceFeatureTable = ServiceFeatureTable(url)
        val featureLayer = FeatureLayer(serviceFeatureTable)
        val map = mMapView.map
        map.operationalLayers.add(featureLayer)
    }

    private fun setupMap() {
        val basemapType = Basemap.Type.STREETS
        val latitude = 34.09042
        val longitude = -118.71511
        val levelOfDetail = 11
        val map = ArcGISMap(basemapType, latitude, longitude, levelOfDetail)
        mMapView.map = map
    }

    override fun onPause() {
        mMapView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mMapView.resume()
    }

    override fun onDestroy() {
        mMapView.dispose()
        super.onDestroy()
    }
}
