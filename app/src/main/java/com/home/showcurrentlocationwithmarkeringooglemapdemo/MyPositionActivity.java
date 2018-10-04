package com.home.showcurrentlocationwithmarkeringooglemapdemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MyPositionActivity extends AppCompatActivity {

    private int LOCATION_MIN_DISTANCE = 120, LOCATION_MIN_TIME = 4000;
    private MapView mapView;
    public GoogleMap mainGoogleMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Marker iAmHereMarker, godTempleMarker, longshanTempleMarker, plumLakeMarker;
    private Paint paint;
    private Bitmap.Config iAmHereConfig, godTempleConfig, longshanTempleConfig, plumLakeConfig;
    private Bitmap iAmHereBitmap, godTempleBitmap, longshanTempleBitmap, plumLakeBitmap;
    private Canvas iAmHereCanvas, godTempleCanvas, longshanTempleCanvas, plumLakeCanvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_position);

        initializationMapView(savedInstanceState);
        initializationLocationListener();
        initializationMap();
        getCurrentLocation();
    }

    private void initializationMapView(Bundle savedInstanceState) {
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(onMapReadyCallback());
    }

    private void initializationLocationListener() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                Log.d("more", "onLocationChanged, Longitude: " + location.getLongitude());
                Log.d("more", "onLocationChanged, Latitude: " + location.getLatitude());
                drawMarker(location);
                locationManager.removeUpdates(locationListener);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };
    }

    @NonNull
    private OnMapReadyCallback onMapReadyCallback() {
        return new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mainGoogleMap = googleMap;
                mainGoogleMap.setOnMarkerClickListener(onMarkerClickListener());
                mainGoogleMap.setOnInfoWindowClickListener(onInfoWindowClickListener());
            }
        };
    }

    @NonNull
    private GoogleMap.OnInfoWindowClickListener onInfoWindowClickListener() {
        return new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                /** 無法為自定義信息窗口的不同組件設置偵聽器，因為信息窗口將呈現為圖像。*/
                /** 但是，您可以為整個信息窗口設置一個偵聽器，如MarkerDemoActivity.java中的onInfoWindowClick和onInfoWindowLongClick所示 */
                marker.hideInfoWindow();
            }
        };
    }

    @NonNull
    private GoogleMap.OnMarkerClickListener onMarkerClickListener() {
        return new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                switch (marker.getTitle()) {
                    case "我在這裡！":
                        Toast.makeText(MyPositionActivity.this, "我在這裡！", Toast.LENGTH_SHORT).show();
                        break;
                    case "新竹都城隍廟":
                        Toast.makeText(MyPositionActivity.this, "新竹都城隍廟", Toast.LENGTH_SHORT).show();
                        break;
                    case "艋舺龍山寺":
                        Toast.makeText(MyPositionActivity.this, "艋舺龍山寺", Toast.LENGTH_SHORT).show();
                        break;
                    case "梅花湖":
                        Toast.makeText(MyPositionActivity.this, "梅花湖", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        };
    }

    /**
     * 初始化GoogleMap
     */
    private void initializationMap() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mainGoogleMap != null) {
                mainGoogleMap.setMyLocationEnabled(true);
                mainGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);                     // 顯示自己位置按鈕
                mainGoogleMap.getUiSettings().setAllGesturesEnabled(true);                          // 啟用所有手勢
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 12);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 13);
            }
        }
    }

    /**
     * 取得當前使用者的座標位置
     */
    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetWorkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetWorkEnabled) {
                Toast.makeText(this, "NetWork and GPS failed", Toast.LENGTH_SHORT).show();
            } else {
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            LOCATION_MIN_TIME, LOCATION_MIN_DISTANCE, locationListener);
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                if (isNetWorkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            LOCATION_MIN_TIME, LOCATION_MIN_DISTANCE, locationListener);
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
        }
    }

    /**
     * 在map上 添加多個Marker, 以及定位到指定地點
     */
    private void drawMarker(Location location) {
        if (mainGoogleMap != null) {
            mainGoogleMap.clear();
            paint = new Paint();
            iAmHereConfig = Bitmap.Config.ARGB_8888;
            iAmHereBitmap = Bitmap.createBitmap(160, 160, iAmHereConfig);
            iAmHereCanvas = new Canvas(iAmHereBitmap);
            iAmHereCanvas.drawBitmap(BitmapFactory.decodeResource(
                    getResources(), R.drawable.money), 22, 0, paint);
            iAmHereMarker = mainGoogleMap.addMarker(
                    new MarkerOptions()
                            .position(new LatLng(location.getLatitude(), location.getLongitude()))
                            .title("我在這裡！")
                            .anchor(0.5f, 0.5f)                                               //设置标记锚点, 锚点就是图标围绕旋转的中心, 这里设置的是以图标自身中心为锚点
                            .icon(BitmapDescriptorFactory.fromBitmap(iAmHereBitmap)));               // 设置自定义的标记图标
            godTempleConfig = Bitmap.Config.ARGB_8888;
            godTempleBitmap = Bitmap.createBitmap(160, 160, godTempleConfig);
            godTempleCanvas = new Canvas(godTempleBitmap);
            godTempleCanvas.drawBitmap(BitmapFactory.decodeResource(
                    getResources(), R.drawable.money), 22, 0, paint);
            godTempleMarker = mainGoogleMap.addMarker(
                    new MarkerOptions()
                            .position(new LatLng(24.804499, 120.965515))
                            .title("新竹都城隍廟")
                            .anchor(0.5f, 0.5f)                                               //设置标记锚点, 锚点就是图标围绕旋转的中心, 这里设置的是以图标自身中心为锚点
                            .icon(BitmapDescriptorFactory.fromBitmap(godTempleBitmap)));             // 设置自定义的标记图标
            longshanTempleConfig = Bitmap.Config.ARGB_8888;
            longshanTempleBitmap = Bitmap.createBitmap(160, 160, longshanTempleConfig);
            longshanTempleCanvas = new Canvas(longshanTempleBitmap);
            longshanTempleCanvas.drawBitmap(BitmapFactory.decodeResource(
                    getResources(), R.drawable.money), 22, 0, paint);
            longshanTempleMarker = mainGoogleMap.addMarker(
                    new MarkerOptions()
                            .position(new LatLng(25.036798, 121.499962))
                            .title("艋舺龍山寺")
                            .anchor(0.5f, 0.5f)                                               //设置标记锚点, 锚点就是图标围绕旋转的中心, 这里设置的是以图标自身中心为锚点
                            .icon(BitmapDescriptorFactory.fromBitmap(longshanTempleBitmap)));
            plumLakeConfig = Bitmap.Config.ARGB_8888;
            plumLakeBitmap = Bitmap.createBitmap(160, 160, plumLakeConfig);
            plumLakeCanvas = new Canvas(plumLakeBitmap);
            plumLakeCanvas.drawBitmap(BitmapFactory.decodeResource(
                    getResources(), R.drawable.money), 22, 0, paint);
            plumLakeMarker = mainGoogleMap.addMarker(
                    new MarkerOptions()
                            .position(new LatLng(24.648708, 121.735116))
                            .title("梅花湖")
                            .anchor(0.5f, 0.5f)                                              //设置标记锚点, 锚点就是图标围绕旋转的中心, 这里设置的是以图标自身中心为锚点
                            .icon(BitmapDescriptorFactory.fromBitmap(plumLakeBitmap)));

            mainGoogleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
            mainGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 9));
            iAmHereMarker.showInfoWindow();                                                         // 让信息窗口直接显示出来, 不用点击标记才显示
        }
    }

    /**
     * 自定義信息窗口和其內容
     */
    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View mWindow;

        CustomInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        private void render(final Marker marker, View view) {
            int badge;
            if (marker.equals(iAmHereMarker)) {
                badge = R.drawable.i_am_here;
            } else if (marker.equals(godTempleMarker)) {
                badge = R.drawable.hsinchu_city_god_temple;
            } else if (marker.equals(longshanTempleMarker)) {
                badge = R.drawable.longshan_temple;
            } else if (marker.equals(plumLakeMarker)) {
                badge = R.drawable.plum_lake;
            } else {
                badge = 0;
            }
            ((ImageView) view.findViewById(R.id.badge)).setImageResource(badge);
            TextView titleUi = ((TextView) view.findViewById(R.id.title));
            titleUi.setText(marker.getTitle());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        getCurrentLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
