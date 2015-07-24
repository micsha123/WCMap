package com.ssp.testapp.wcmap.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;

import com.gc.materialdesign.views.ButtonRectangle;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Icon;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.views.MapView;
import com.ssp.testapp.wcmap.R;

public class SendFragment extends Fragment {

    private static final String ARG_LAT = "latitude";
    private static final String ARG_LONG = "longitude";

    private RadioButton buttonFree;
    private RadioButton buttonPaid;
    private EditText editDesc;

    private double latitude;
    private double longitude;

    public static SendFragment newInstance(Double latitude, Double longitude) {
        SendFragment fragment = new SendFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_LAT, latitude);
        args.putDouble(ARG_LONG, longitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            latitude = getArguments().getDouble(ARG_LAT);
            longitude = getArguments().getDouble(ARG_LONG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_send, container, false);
        MapView mapView = (MapView) rootView.findViewById(R.id.mapview_send);

        mapView.setCenter(new LatLng(latitude, longitude));
        mapView.setZoom(18);

        Marker customUserMarker = new Marker(mapView, null, null, new LatLng(latitude, longitude));
        customUserMarker.setIcon(new Icon(getActivity(), Icon.Size.LARGE, "post", "9CCC65"));

        mapView.addMarker(customUserMarker);

        buttonFree = (RadioButton) rootView.findViewById(R.id.radioButton_free);
        buttonPaid = (RadioButton) rootView.findViewById(R.id.radioButton_paid);

        editDesc = (EditText) rootView.findViewById(R.id.edit_desc);

        ButtonRectangle buttonSend = (ButtonRectangle) rootView.findViewById(R.id.send_button);

        // old but works on API 10
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_TEXT, infoToSend());
                startActivity(Intent.createChooser(intent, null));
            }
        });

        return rootView;
    }

    private String infoToSend(){
        String info = "Type: ";
        if (buttonFree.isChecked()){
            info += getString(R.string.free);
        } else{
            info += getString(R.string.paid);
        }
        info += ", Desc: " + editDesc.getText() + ", lat: " + latitude + ", long: " + longitude;
        return info;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
