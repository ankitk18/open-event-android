package org.fossasia.openevent.activities;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.dbutils.DataDownload;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.fragments.BookmarksFragment;
import org.fossasia.openevent.fragments.SpeakerFragment;
import org.fossasia.openevent.fragments.SponsorsFragment;
import org.fossasia.openevent.fragments.TracksFragment;
import org.fossasia.openevent.utils.IntentStrings;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        DataDownload download = new DataDownload();
        download.downloadVersions();

        setUpToolbar();
        setUpNavDrawer();
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        this.findViewById(android.R.id.content).setBackgroundColor(Color.LTGRAY);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, new TracksFragment()).commit();

    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        setupDrawerContent(navigationView, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tracks, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Will close the drawer if the home button is pressed
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
    }

    private void setUpNavDrawer() {
        if (mToolbar != null) {
            final android.support.v7.app.ActionBar ab = getSupportActionBar();
            assert ab != null;
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
            ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

            ImageView header_drawer = (ImageView) findViewById(R.id.headerDrawer);
            DbSingleton dbSingleton = DbSingleton.getInstance();
//            Log.d("PICASSO", dbSingleton.getEventDetails().getLogo());
//            Picasso.with(getApplicationContext()).load(dbSingleton.getEventDetails().getLogo()).into(header_drawer);

            mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
            mActionBarDrawerToggle.syncState();
        }
    }

    private void setupDrawerContent(NavigationView navigationView, final Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        menuItem.setChecked(true);
                        int id = menuItem.getItemId();
                        menu.clear();
                        switch (id) {
                            case R.id.nav_tracks:
                                fragmentManager.beginTransaction()
                                        .replace(R.id.content_frame, new TracksFragment()).commit();
                                getSupportActionBar().setTitle(R.string.menu_tracks);
                                inflater.inflate(R.menu.menu_tracks, menu);
                                break;
                            case R.id.nav_bookmarks:
                                fragmentManager.beginTransaction()
                                        .replace(R.id.content_frame, new BookmarksFragment()).commit();
                                getSupportActionBar().setTitle(R.string.menu_bookmarks);
                                inflater.inflate(R.menu.menu_bookmarks, menu);
                                break;
                            case R.id.nav_speakers:
                                fragmentManager.beginTransaction()
                                        .replace(R.id.content_frame, new SpeakerFragment()).commit();
                                getSupportActionBar().setTitle(R.string.menu_speakers);
                                inflater.inflate(R.menu.menu_speakers, menu);
                                break;
                            case R.id.nav_sponsors:
                                fragmentManager.beginTransaction()
                                        .replace(R.id.content_frame, new SponsorsFragment()).commit();
                                getSupportActionBar().setTitle(R.string.menu_sponsor);
                                inflater.inflate(R.menu.menu_sponsors, menu);
                                break;
                            case R.id.nav_map:
                                Bundle latlng = new Bundle();
                                DbSingleton dbSingleton = DbSingleton.getInstance();
                                float latitude = dbSingleton.getEventDetails().getLatitude();
                                float longitude = dbSingleton.getEventDetails().getLongitude();

                                String location_name = dbSingleton.getEventDetails().getLocationName();
                                latlng.putFloat(IntentStrings.LATITUDE, latitude);
                                latlng.putFloat(IntentStrings.LONGITUDE, longitude);
                                latlng.putString(IntentStrings.LOCATION, location_name);

                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                Fragment fragment = new Fragment();
                                fragment.setArguments(latlng);
                                fragmentTransaction.replace(R.id.content_frame,
                                        ((OpenEventApp) getApplication())
                                                .getMapModuleFactory()
                                                .provideMapModule()
                                                .provideMapFragment()).commit();
                                getSupportActionBar().setTitle(R.string.menu_map);
                                inflater.inflate(R.menu.menu_map, menu);
                                break;
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }


}