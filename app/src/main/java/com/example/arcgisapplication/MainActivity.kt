package com.example.arcgisapplication

import android.content.DialogInterface
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer
import com.esri.arcgisruntime.layers.ArcGISTiledLayer
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.portal.Portal
import com.esri.arcgisruntime.security.AuthenticationManager
import com.esri.arcgisruntime.security.DefaultAuthenticationChallengeHandler
import java.util.*


class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName

    private val MIN_SCALE = 60000000
    private lateinit var mMapView: MapView
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mBasemapListView: ListView
    private lateinit var mLayerListView: ListView
    private lateinit var mDrawerTitle: CharSequence
    private lateinit var mDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

// Create a DefaultAuthenticationChallengeHandler, passing in an Android Context (e.g. the current Activity)
        val handler = DefaultAuthenticationChallengeHandler(this)
// Set the challenge handler onto the AuthenticationManager
        AuthenticationManager.setAuthenticationChallengeHandler(handler)

        mDrawerTitle = title
        // inflate MapView from layout
        mMapView = findViewById(R.id.mapView)
// create a map with Topographic Basemap
        val map = ArcGISMap(Basemap.Type.STREETS, 20.5937, 78.9629, 3)
        // set the map to be displayed in this view
        mMapView.map = map

        // inflate the Basemap and Layer list views
        mBasemapListView = findViewById(R.id.basemap_list)
        mLayerListView = findViewById(R.id.layer_list)
        mDrawerLayout = findViewById(R.id.drawer_layout)

        val tiledLayer = ArcGISTiledLayer(this.getString(R.string.world_time_zones))
        val mapImageLayer = ArcGISMapImageLayer(this.getString(R.string.us_census))

        // setting the scales at which the map image layer layer can be viewed
        mapImageLayer.minScale = MIN_SCALE.toDouble()
        mapImageLayer.maxScale = (MIN_SCALE / 100).toDouble()

        // create base map array and set it to a list view adapter
        val basemapTiles = resources.getStringArray(R.array.basemap_array)
        mBasemapListView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, basemapTiles)
        mBasemapListView.setItemChecked(0, true)

        val operationalLayerTiles = resources.getStringArray(R.array.operational_layer_array)
        mLayerListView.adapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, operationalLayerTiles)

        createDrawer(tiledLayer, mapImageLayer)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

    }

    private fun createDrawer(tiledLayer: ArcGISTiledLayer, mapImageLayer: ArcGISMapImageLayer) {
        val map = mMapView.map
        mDrawerToggle = object : ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name) {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                supportActionBar!!.title = title

                map.addDoneLoadingListener {
                    if (map.loadStatus == LoadStatus.LOADED) {
                        if (mLayerListView.checkedItemCount > 1) {
                            map.operationalLayers.clear()
                            map.operationalLayers.add(tiledLayer)
                            map.operationalLayers.add(mapImageLayer)
                        } else {
                            when {
                                mLayerListView.isItemChecked(0) -> {
                                    map.operationalLayers.clear()
                                    map.operationalLayers.add(tiledLayer)
                                }
                                mLayerListView.isItemChecked(1) -> {
                                    map.operationalLayers.clear()
                                    map.operationalLayers.add(mapImageLayer)
                                }
                                else -> map.operationalLayers.clear()
                            }
                        }
                    }
                }
                invalidateOptionsMenu()
            }

            override fun onDrawerOpened(drawerView: View) {
                supportActionBar!!.title = mDrawerTitle
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu()
            }
        }

        mBasemapListView.setOnItemClickListener { _, _, position, _ ->
            mMapView.map.operationalLayers.clear()
            when (position) {
                0 -> mMapView.map.basemap = Basemap.createStreets()
                1 -> mMapView.map.basemap = Basemap.createImagery()
                2 -> mMapView.map.basemap = Basemap.createTopographic()
                3 -> mMapView.map.basemap = Basemap.createOceans()
                else -> Toast.makeText(this, R.string.unsupported_option, Toast.LENGTH_SHORT).show()
            }
        }

        mDrawerToggle.isDrawerIndicatorEnabled = true
        mDrawerLayout.addDrawerListener(mDrawerToggle)
    }

    private fun showSaveMapDialog() {
        val inflater = LayoutInflater.from(this)
        val saveMapDialogView = inflater.inflate(R.layout.save_map_dialog, null, false)

        // get references to edit text views
        val titleEditText = saveMapDialogView.findViewById<EditText>(R.id.titleEditText)
        val tagsEditText = saveMapDialogView.findViewById<EditText>(R.id.tagsEditText);
        val descriptionEditText = saveMapDialogView.findViewById<EditText>(R.id.descriptionEditText);

        val saveMapDialog = AlertDialog.Builder(this)
            .setView(saveMapDialogView)
            .setPositiveButton(R.string.save_map, null)
            .setNegativeButton(R.string.cancel, null)
            .show()

        // click handling for the save map button
        val saveMapButton = saveMapDialog.getButton(DialogInterface.BUTTON_POSITIVE)
        saveMapButton.setOnClickListener {
            val tags = Arrays.asList(tagsEditText.text.toString().split(","))
            // make sure the title edit text view has text
            if (titleEditText.text.isNotEmpty()) {
                saveMap(
                    titleEditText.text.toString(),
                    tags as Iterable<String>,
                    descriptionEditText.getText().toString()
                )
                saveMapDialog.dismiss();
            } else {
                Toast.makeText(this, "A title is required to save your map.", Toast.LENGTH_LONG).show();
            }
        }
        // click handling for the cancel button
        val cancelButton = saveMapDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        cancelButton.setOnClickListener { v -> saveMapDialog.dismiss() }
    }

    private fun saveMap(title: String, tags: Iterable<String>, description: String) {
// create a portal to arcgis
        val portal = Portal("https://www.arcgis.com", true);
        portal.addDoneLoadingListener {
            if (portal.getLoadStatus() == LoadStatus.LOADED) {
                // call save as async and pass portal info, as well as details of the map including title, tags and description
                val saveAsAsyncFuture = mMapView.getMap()
                    .saveAsAsync(portal, null, title, tags, description, null, true)
                saveAsAsyncFuture.addDoneListener {
                    Toast.makeText(this, "Map saved to portal!", Toast.LENGTH_LONG).show()
                }
            } else {
                val error = "Error loading portal: " + portal.loadError.message
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                Log.e(TAG, error)
            }
        }
        portal.loadAsync()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() == R.id.action_save) {
            showSaveMapDialog()
        }
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }


    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        // if nav drawer is opened, hide the action items
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            if (menu != null) {
                menu.findItem(R.id.action_save).isVisible = false
            }
        } else {
            if (menu != null) {
                menu.findItem(R.id.action_save).isVisible = true
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    override fun setTitle(title: CharSequence?) {
        actionBar.title = title;
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        // Pass any configuration change to the drawer toggle
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
