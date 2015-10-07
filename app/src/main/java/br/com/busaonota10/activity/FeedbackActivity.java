package br.com.busaonota10.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.widget.EditText;

import com.dd.CircularProgressButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.soulwolf.widget.materialradio.MaterialRadioButton;
import net.soulwolf.widget.materialradio.MaterialRadioGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

import br.com.busaonota10.R;
import br.com.busaonota10.service.BusaoService;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedbackActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    OkHttpClient client;

    @Bind(R.id.radioGroup)
    MaterialRadioGroup radioGroup;

    @Bind(R.id.edit_text_number_car)
    EditText numberCar;

    @Bind(R.id.edit_text_number_line)
    EditText numberLine;

    @Bind(R.id.edit_text_message)
    EditText message;

    private static final String TAG = "feedback.activity";
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
        client = new OkHttpClient();

        googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @OnClick(R.id.send_button)
    public void submitFeedback(CircularProgressButton circularProgressButton) {
        circularProgressButton.setIndeterminateProgressMode(true);
        circularProgressButton.setProgress(50);

        JSONObject form = null;

        try {
            form = buildFormAsJSON();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        BusaoService service = new BusaoService(this);

        service.post("/feedbacks.json", form.toString(), new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                stopLocationUpdates();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                stopLocationUpdates();

                try {
                    JSONObject responseJSON = new JSONObject(response.body().string());
                    Intent intent = new Intent(FeedbackActivity.this, FeedbackCreatedActivity.class);
                    intent.putExtra("feedback", responseJSON.toString());

                    finish();
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private JSONObject buildFormAsJSON() throws JSONException {
        JSONObject form = new JSONObject();
        JSONObject feedback = new JSONObject();
        JSONObject bus = new JSONObject();
        Date current = new Date();

        bus.put("line", numberLine.getText().toString());
        bus.put("identification_number", numberCar.getText().toString());

        feedback.put("bus", bus);

        feedback.put("content", message.getText().toString());
        feedback.put("user", getPhoneNumber());
        feedback.put("opinion", getOpinion());
        feedback.put("sent_at", current.toString());

        if (currentLocation != null) {
            feedback.put("latitude", currentLocation.getLatitude());
            feedback.put("longitude", currentLocation.getLongitude());
        }

        form.put("feedback", feedback);
        return form;
    }

    private String getPhoneNumber() {
        TelephonyManager phoneManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return phoneManager.getDeviceId();
    }

    private String getOpinion() {
        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        MaterialRadioButton radioButton = (MaterialRadioButton) radioGroup.findViewById(radioButtonID);
        if (radioButton.getId() == R.id.radio_button_good) {
            return "good";
        } else {
            return "bad";
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    protected void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        stopLocationUpdates();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
    }
}
