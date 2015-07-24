package com.ssp.testapp.wcmap.fragment;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFloat;
import com.mapbox.mapboxsdk.api.ILatLng;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Icon;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.views.MapView;
import com.mapbox.mapboxsdk.views.MapViewListener;
import com.ssp.testapp.wcmap.R;
import com.ssp.testapp.wcmap.model.MapModel;
import com.ssp.testapp.wcmap.service.GPSTracker;

import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * ֿטרט כואבעü
 */

public class MainFragment extends Fragment {

    private GPSTracker mGps;

    private Marker userMarker;
    private MapView mapView;
    private MapModel map;
    private LatLng customUserMarkerLatLang;

    //secret link oops
    private final String MARKERS_URL = "";

    private String json;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        map = MapModel.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mapView = (MapView) rootView.findViewById(R.id.mapview);

        if(map.isExistMapData()){
            map.loadJSONMarkers();
            map.placeJSONMarkers(mapView);
        } else{
            new DownloadJSON().execute();
        }

        mGps = new GPSTracker(getActivity());

        //set initial position of a map / Tomsk city
        mapView.setCenter(new LatLng(56.477, 84.954));
        mapView.setZoom(13);

        mapView.setMapViewListener(new MapViewListener() {
            @Override
            public void onShowMarker(MapView mapView, Marker marker) {
            }

            @Override
            public void onHideMarker(MapView mapView, Marker marker) {
            }

            @Override
            public void onTapMarker(MapView mapView, Marker marker) {
            }

            @Override
            public void onLongPressMarker(MapView mapView, Marker marker) {
            }

            @Override
            public void onTapMap(MapView mapView, ILatLng iLatLng) {
            }

            @Override
            public void onLongPressMap(MapView mapView, ILatLng iLatLng) {
                customUserMarkerLatLang = (LatLng) iLatLng;
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, SendFragment.newInstance(
                                customUserMarkerLatLang.getLatitude(),
                                customUserMarkerLatLang.getLongitude()))
                        .addToBackStack(null)
                        .commit();

            }
        });

        ButtonFloat button = (ButtonFloat) rootView.findViewById(R.id.buttonFloat);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userMarker != null) {
                    mapView.removeMarker(userMarker);
                }
                getLocation();
                userMarker = new Marker(mapView, null, null, new LatLng(getLocation()));
                userMarker.setIcon(new Icon(getActivity(), Icon.Size.LARGE, null, "42A5F5"));
                mapView.addMarker(userMarker);
            }
        });

        return rootView;
    }

    private Location getLocation(){
        if(mGps.canGetLocation()){
            return mGps.getLocation();
        }else{
            mGps.showSettingsAlert();
            return null;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_update) {
            new DownloadJSON().execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private class DownloadJSON extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                json = Jsoup.connect(MARKERS_URL).ignoreContentType(true).execute().body();
                map.parseJSONMarkers(mapView, json);
                map.saveJSONMarkers();
                return "done";
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            map.placeJSONMarkers(mapView);
            Toast.makeText(getActivity(), "Done!", Toast.LENGTH_SHORT).show();
        }
    }

}
