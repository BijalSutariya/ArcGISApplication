package com.example.arcgisapplication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.portal.Portal
import com.esri.arcgisruntime.portal.PortalItem

class AddLayerActivity : AppCompatActivity() {

    private lateinit var mFeatureLayer: FeatureLayer
    private lateinit var mMapView: MapView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_layer2)

        mMapView = this.findViewById(R.id.mapView)
        setupMap()

    }

    private fun setupMap() {
        val basemapType = Basemap.Type.TOPOGRAPHIC
        val latitude = 23.0225
        val longitude = 72.5714
        val levelOfDetail = 11
        val map = ArcGISMap(basemapType, latitude, longitude, levelOfDetail)
        mMapView.map = map

        addLayer(map);


    }

    private fun addLayer(map: ArcGISMap) {
        val itemID = "843575935e454e16b465fb15e569bb5b"
        val portal = Portal("http://www.arcgis.com",false)
        val portalItem = PortalItem(portal, itemID)

        val table = ServiceFeatureTable("https://services9.arcgis.com/IMlF46PlwBvgMDOB/ArcGIS/rest/services/ahmedabad_city/FeatureServer/0")
        mFeatureLayer = FeatureLayer(table)
        mFeatureLayer.addDoneLoadingListener {
            if (mFeatureLayer.loadStatus == LoadStatus.LOADED) {
                map.operationalLayers.add(mFeatureLayer);
                Log.d("operational data", map.operationalLayers.size.toString())
            }
        }
        mFeatureLayer.loadAsync()
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
