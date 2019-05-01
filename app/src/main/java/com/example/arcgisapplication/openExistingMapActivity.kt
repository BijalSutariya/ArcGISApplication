package com.example.arcgisapplication

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.portal.Portal
import com.esri.arcgisruntime.portal.PortalItem


class openExistingMapActivity : AppCompatActivity() {

    private lateinit var mMapView: MapView
    private lateinit var mMap: ArcGISMap
    private lateinit var mPortal: Portal
    private lateinit var mPortalItem: PortalItem

    private lateinit var mDrawerList: ListView

    private lateinit var mDrawerToggle: ActionBarDrawerToggle
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mActivityTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_existing_map)

        mDrawerList = findViewById(R.id.navList)
        mDrawerLayout = findViewById(R.id.drawer_layout)
        mActivityTitle = title.toString()

        // inflate MapView from layout
        mMapView = findViewById(R.id.mapView);

        // get the portal url for ArcGIS Online
        mPortal = Portal(resources.getString(R.string.portal_url))
        // get the pre-defined portal id and portal url
        mPortalItem = PortalItem(mPortal, resources.getString(R.string.webmap_houses_with_mortgages_id))
        // create a map from a PortalItem
        mMap = ArcGISMap(mPortalItem)
        // set the map to be displayed in this view
        mMapView.map = mMap

        // create a map from a PortalItem
        mMap = ArcGISMap(mPortalItem)
        // set the map to be displayed in this view
        mMapView.map = mMap

        // add the webmap titles to the drawer
        addDrawerItems()
        setupDrawer()

        // set icons on action bar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
    }

    private fun addDrawerItems() {
        val webmapTitles = arrayOf(
            resources.getString(R.string.webmap_houses_with_mortgages_title),
            resources.getString(R.string.webmap_usa_tapestry_segmentation_title),
            resources.getString(R.string.webmap_geology_us_title)
        )

        val mAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, webmapTitles)
        mDrawerList.adapter = mAdapter

        mDrawerList.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position == 0) {
                    mPortalItem = PortalItem(mPortal, resources.getString(R.string.webmap_houses_with_mortgages_id))
                    // create a map from a PortalItem
                    mMap = ArcGISMap(mPortalItem)
                    // set the map to be displayed in this view
                    mMapView.map = mMap
                    // close the drawer
                    mDrawerLayout.closeDrawer(adapterView)
                } else if (position == 1) {
                    mPortalItem = PortalItem(mPortal, resources.getString(R.string.webmap_usa_tapestry_segmentation_id))
                    // create a map from a PortalItem
                    mMap = ArcGISMap(mPortalItem)
                    // set the map to be displayed in this view
                    mMapView.map = mMap
                    // close the drawer
                    mDrawerLayout.closeDrawer(adapterView)
                } else if (position == 2) {
                    mPortalItem = PortalItem(mPortal, resources.getString(R.string.webmap_geology_us))
                    // create a map from a PortalItem
                    mMap = ArcGISMap(mPortalItem)
                    // set the map to be displayed in this view
                    mMapView.map = mMap
                    // close the drawer
                    mDrawerLayout.closeDrawer(adapterView)
                }
            }
        }
    }

    private fun setupDrawer() {
        mDrawerToggle =
            object : ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

                override fun onDrawerOpened(drawerView: View) {
                    super.onDrawerOpened(drawerView)
                    // change the title to the nav bar
                    supportActionBar!!.title = resources.getString(R.string.navbar_title);
                    // invalidate options menu
                    invalidateOptionsMenu();
                }

                override fun onDrawerClosed(drawerView: View) {
                    super.onDrawerClosed(drawerView)
                    // set title to the app
                    supportActionBar!!.title = mActivityTitle;
                    // invalidate options menu
                    invalidateOptionsMenu();
                }
            }
        mDrawerToggle.isDrawerIndicatorEnabled = true;
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        mDrawerToggle.syncState();
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
}
