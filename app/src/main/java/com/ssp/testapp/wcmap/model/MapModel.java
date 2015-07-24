package com.ssp.testapp.wcmap.model;

import android.content.Context;
import android.database.Cursor;

import com.cocoahero.android.geojson.FeatureCollection;
import com.cocoahero.android.geojson.GeoJSON;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Icon;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.PathOverlay;
import com.mapbox.mapboxsdk.util.DataLoadingUtils;
import com.mapbox.mapboxsdk.views.MapView;
import com.ssp.testapp.wcmap.data.SQLDatabaseHelper;

import java.util.ArrayList;

public class MapModel {

    private static MapModel instance;
    private Context context;
    private ArrayList<Marker> markers;
    private SQLDatabaseHelper dbHelper;

    private MapModel(Context context) {
        this.context = context;
        this.dbHelper = new SQLDatabaseHelper(context);
    }

    public static synchronized MapModel getInstance(Context context) {
        if (instance == null) {
            instance = new MapModel(context);
        }
        return instance;
    }

    public boolean isExistMapData(){
        return dbHelper.checkIfExist();
    }

    public void parseJSONMarkers(MapView mapView, String json){
        markers = new ArrayList<Marker>();
        try {
            FeatureCollection features = (FeatureCollection)GeoJSON.parse(json);
            ArrayList<Object> uiObjects = DataLoadingUtils.createUIObjectsFromGeoJSONObjects(features,
                    null);

            for (Object obj : uiObjects) {
                if (obj instanceof Marker) {
                    markers.add((Marker) obj);
                } else if (obj instanceof PathOverlay) {
                    mapView.getOverlays().add((PathOverlay) obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void placeJSONMarkers(MapView mapView){
        mapView.clear();
        for(Marker marker : markers){
            marker.setIcon(new Icon(context, Icon.Size.LARGE, "toilets", "F44336"));
            mapView.addMarker(marker);
        }
    }

    public void saveJSONMarkers(){
        dbHelper.deleteMarkers();
        for(Marker marker : markers){
            dbHelper.insertMarker(marker.getTitle(),
                    marker.getDescription(),
                    marker.getPosition().getLatitude(),
                    marker.getPosition().getLongitude());
        }
    }

    public void loadJSONMarkers(){
        markers = new ArrayList<Marker>();
        Cursor cursor = dbHelper.getMarkers();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            markers.add(new Marker(cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.COLUMN_DESC)),
                    new LatLng(cursor.getDouble(cursor.getColumnIndex(SQLDatabaseHelper.COLUMN_LAT)),
                            cursor.getDouble(cursor.getColumnIndex(SQLDatabaseHelper.COLUMN_LONG)))));
            cursor.moveToNext();
        }
    }
}