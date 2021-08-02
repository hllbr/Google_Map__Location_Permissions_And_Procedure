package com.hllbr.google_map__location_permissions_and_procedure;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.hllbr.google_map__location_permissions_and_procedure.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    ActivityResultLauncher<String> permissionLauncher;
    LocationManager locationManager ;
    LocationListener locationListener ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        registerLauncher();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
         locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);//bu alada getSharedPreferences da kullanılabilir fakat güncel servislerle çalışmak daha mantıklı bir yöntem olduğu için getSystemServices kullanmak mantıklı bir durum alıyor.
        //Konum değişikliklerini algılamadan bu durumlarla ilgili işlemler gerçekleştiremeyiz bu sebeple konum değşikliklerini dinleyen bir yapı ihtiyacımız var buda locationlİSTENER olarak ifade edilen konum dğeşikliklerini izleyen ve haber veren bir yapı bulunmakta bu yapıdan faydalanarak işlemlerimizi ilerletebiliriz.

         locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                System.out.println(location.getAltitude() +" yükseklik :)");
                System.out.println("Location : " +location.toString());

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        };
        //Alt satırdaki işlemi ancak kullanıcı bize konuma erişim izni veridiğinde gerçekleştirebilirim.oyüzden önce kullanıcıdan gerekli izinleri almam şarttır.

       // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,20000,2,locationListener,);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //eşit değilse izin istemem gereken yer bu alandır
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                //Eğer bu yapım True dönüyorsa kullanıcıya SnackBar ile göstermem gerekiyor.
                Snackbar.make(binding.getRoot(),"Permission needed for Maps",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //request permission
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                    }
                }).show();
            }else{
                //request permission
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }else{
            //Eğer izin daha önce verildiyse
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }
    }
    public void registerLauncher(){
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result){
                    //permission granteed izin var verildi
                    if (ContextCompat.checkSelfPermission(MapsActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        //We need is Extra Control
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                    }

                }else {
                    //izin verilmedi kullanıcı izni reddetti permission denited
                    Toast.makeText(MapsActivity.this, "Permission needed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}