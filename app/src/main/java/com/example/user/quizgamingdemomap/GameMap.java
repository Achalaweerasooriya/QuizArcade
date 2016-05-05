package com.example.user.quizgamingdemomap;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class GameMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView player;
    private TextView level;
    private TextView location;
    private Circle shape;
    String userName;
    String responseServer;
    JSONObject jobj = null;
    ClientServerInterface clientServerInterface = new ClientServerInterface();
    String ab;
    MarkerOptions options;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getValues();
        View v = getLayoutInflater().inflate(R.layout.info_window, null);
        ImageView image = (ImageView) v.findViewById(R.id.imageView);
        TextView txtPlayerName = (TextView) v.findViewById(R.id.txtPlayerName);
        location = (TextView) v.findViewById(R.id.txtLong);
        level = (TextView) v.findViewById(R.id.txtLat);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        String serverURL = "http://10.0.3.2/webService/WebServiceConnector.php";
        new LongOperation().execute(serverURL);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(7.038046,79.911206);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
//        userName = bundle.getString("gamingName");
//        mMap.addMarker(new MarkerOptions().position(new LatLng(7.038046,79.911206)).title("Nuwan"));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "GameMap Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.user.quizgamingdemomap/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "GameMap Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.user.quizgamingdemomap/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    private class LongOperation  extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(GameMap.this);
        String data = "";
        int sizeData = 0;

        protected void onPreExecute() {
             Dialog.setMessage("Please wait..");
            Dialog.show();
        }


        protected Void doInBackground(String... urls) {

            /************ Make Post Call To Web Server ***********/
            BufferedReader reader=null;

            // Send data
            try
            {

                // Defined URL  where to send data
                URL url = new URL(urls[0]);

                // Send POST data request

                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write( data );
                wr.flush();

                // Get the server response

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while((line = reader.readLine()) != null)
                {
                    // Append server response in string
                    sb.append(line + "");
                }

                // Append Server Response To Content String
                Content = sb.toString();
            }
            catch(Exception ex)
            {
                Error = ex.getMessage();
            }
            finally
            {
                try
                {

                    reader.close();
                }

                catch(Exception ex) {}
            }

            /*****************************************************/
            return null;
        }

        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.

            // Close progress dialog
            Dialog.dismiss();

            if (Error != null) {

//                uiUpdate.setText("Output : "+Error);

            } else {

                // Show Response Json On Screen (activity)
//                uiUpdate.setText( Content );

                /****************** Start Parse Response JSON Data *************/

                String OutputData = "";
                JSONObject jsonResponse;
                try {

                    /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
                    jsonResponse = new JSONObject(Content);

                    /***** Returns the value mapped by name if it exists and is a JSONArray. ***/
                    /*******  Returns null otherwise.  *******/
                    JSONArray jsonMainNode = jsonResponse.optJSONArray("Android");

                    /*********** Process each JSON Node ************/

                    int lengthJsonArr = jsonMainNode.length();

                    for(int i=0; i < lengthJsonArr; i++)
                    {
                        /****** Get Object for each JSON node.***********/

                        JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                        Double lantitude = Double.parseDouble(jsonChildNode.optString("lantitude"));
                        Double lontitude = Double.parseDouble(jsonChildNode.optString("longtitude"));
                        String title = jsonChildNode.getString("userName");
                        Integer levelVal = Integer.parseInt(jsonChildNode.getString("level"));
                        String fbId = jsonChildNode.getString("fbID");
                        if(levelVal == 1) {
                            mMap.addMarker(new MarkerOptions().position(new LatLng(lantitude,lontitude)).title(title+"-"+ levelVal.toString()+"-"+fbId).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)).snippet("Level One"));
                            level.setText("Level : One");
                        }
                        if(levelVal == 2) {
                            mMap.addMarker(new MarkerOptions().position(new LatLng(lantitude,lontitude)).title(title+"-"+ levelVal.toString()+"-"+fbId).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)).snippet("level Two"));
                            level.setText("Level : Two");
                        }
                        if (levelVal == 3) {
                            level.setText("Level : Three");
                            mMap.addMarker(new MarkerOptions().position(new LatLng(lantitude,lontitude)).title(title+"-"+ levelVal.toString()+"-"+fbId).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        }
                        if (levelVal == 4) {
                            level.setText("Level : Four");
                            mMap.addMarker(new MarkerOptions().position(new LatLng(lantitude,lontitude)).title(title+"-"+ levelVal.toString()+"-"+fbId).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        }
                        if (levelVal == 5) {
                            level.setText("Level : Five");
                            mMap.addMarker(new MarkerOptions().position(new LatLng(lantitude,lontitude)).title(title+"-"+ levelVal.toString()+"-"+fbId).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                        }
                        if (levelVal == 6) {
                            level.setText("Level : Six");
                            mMap.addMarker(new MarkerOptions().position(new LatLng(lantitude,lontitude)).title(title+"-"+ levelVal.toString()+"-"+fbId).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        }
                        if (levelVal == 7) {
                            level.setText("Level : Seven");
                            mMap.addMarker(new MarkerOptions().position(new LatLng(lantitude,lontitude)).title(title+"-"+ levelVal.toString()+"-"+fbId).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                        }
                        if (levelVal == 8) {
                            level.setText("Level : Eight");
                            mMap.addMarker(new MarkerOptions().position(new LatLng(lantitude,lontitude)).title(title+"-"+ levelVal.toString()+"-"+fbId).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                        }
                        if (levelVal == 9) {
                            level.setText("Level : Nine");
                            mMap.addMarker(new MarkerOptions().position(new LatLng(lantitude,lontitude)).title(title+"-"+ levelVal.toString()+"-"+fbId).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                        }
                        if (levelVal == 10) {
                            level.setText("Level : Ten");
                            mMap.addMarker(new MarkerOptions().position(new LatLng(lantitude,lontitude)).title(title+"-"+ levelVal.toString()+"-"+fbId).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).snippet("Level Ten"));
                        }
                        Circle c = drawCircle(new LatLng(lantitude,lontitude));
                        c.setVisible(true);
                    }
                    if (mMap != null) {
                        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                            @Override
                            public View getInfoWindow(Marker marker) {
                                return null;
                            }

                            @Override
                            public View getInfoContents(Marker marker) {
                                View v = getLayoutInflater().inflate(R.layout.info_window, null);
                                ImageView image = (ImageView) v.findViewById(R.id.imageView);
                                TextView txtPlayerName = (TextView) v.findViewById(R.id.txtPlayerName);
                                location = (TextView) v.findViewById(R.id.txtLong);
                                TextView level = (TextView) v.findViewById(R.id.txtLat);
                                String markerTitle = marker.getTitle().toString();
                                String []playerInfo = markerTitle.split("-");
                                String plevel = playerInfo[1];
                                String pname = playerInfo[0];
//                                Bitmap bMap = getFacebookProfilePicture(playerInfo[2]);
//                                if (bMap == null) {
//                                    for(int i=0;i<50;i++) {
//                                        Log.d("null pointer","**************");
//                                    }
//                                } else  {
//                                    image.setImageBitmap(bMap);
//                                }
                               Picasso.with(getApplicationContext()).load("https://graph.facebook.com/" + playerInfo[2] + "/picture?type=large").into(image);
//                                ProfilePictureView profilePictureView;
//
//                                profilePictureView = (ProfilePictureView) findViewById(R.id.friendProfilePicture);

                                //ProfilePictureView profilePicture = (ProfilePictureView) v.findViewById(R.id.imageView);
                                //Bitmap bitImage = getFacebookProfilePicture(playerInfo[2]);
                                LatLng locality = marker.getPosition();
                                txtPlayerName.setText(pname);
                                level.setText("Level : "+plevel);
                                return v;
                            }
                        });
                    }


                } catch (JSONException e) {

                    e.printStackTrace();
                }


            }
        }
    }

    private Circle drawCircle(LatLng object) {
        CircleOptions options = new CircleOptions()
                .center(object)
                .radius(500)
                .fillColor(0x330000FF)
                .strokeWidth(3);
        return mMap.addCircle(options);
    }

    public void getValues() {
        player = (TextView) findViewById(R.id.txtplayerName);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        player.setText(bundle.getString("gamingName"));
    }


    public Bitmap getFacebookProfilePicture(String userID){
        try {
            return new GetFacebookInformation(userID).execute().get();
        }catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private class GetFacebookInformation extends AsyncTask<Void, Void, Bitmap> {

        private String userId;

        public GetFacebookInformation(String userId) {
            this.userId = userId;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap = null;
            try {
                HttpURLConnection.setFollowRedirects(true);
                URL imageURL = new URL("httpS://graph.facebook.com/'"+ userId +"'/picture?type=small");
                Log.d("***", "*****************");
                Log.d("The con is",imageURL.openConnection().getInputStream().toString());
                bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());

                Integer widthx = bitmap.getWidth();
                Log.d("****","******************************");
                Log.d("width",widthx.toString());
                //Log.d("user ID :- "+params[0]);
                Log.d("****","******************************");

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }
    }

}
