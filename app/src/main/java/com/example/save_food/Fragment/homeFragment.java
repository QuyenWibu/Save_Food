package com.example.save_food.Fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.save_food.MapsActivity;
import com.example.save_food.R;
import com.example.save_food.UploadActivity;
import com.example.save_food.models.KhoangCachLocaitonSort;
import com.example.save_food.models.KhoangCachLocation;
import com.example.save_food.models.UserLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class homeFragment extends Fragment {
    Location location;
    UserLocation userLocation;
    ArrayList<UserLocation> userLocations = new ArrayList<>();
    Location currentLocation;
    List<KhoangCachLocation> khoangCachLocationList = new ArrayList<>();
    public List<KhoangCachLocaitonSort> khoangCachLocaitonSorts = new ArrayList<>();
    FusedLocationProviderClient fusedLocationProviderClient;
    double latitude, longitude;
    FirebaseAuth firebaseAuth;
    Button showmap, post;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        showmap = view.findViewById(R.id.showMap);
        post = view.findViewById(R.id.upload);

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        khoangCachLocationList.clear();
        userLocations.clear();
        GetSumUID();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UploadActivity.class);
                startActivity(intent);
            }
        });
        return view;


    }

    private void GetSumUID() {
        ArrayList<String> uidList = new ArrayList<>();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String uid = userSnapshot.getKey();
                    // do something with uid
                    uidList.add(uid);
                }
                // Log toàn bộ các phần tử trong ArrayList uidList
                for (int i = 0; i < uidList.size(); i++) {
                    Log.d("UID", uidList.get(i));
                }
                KiemtraFireBase(uidList);
            }

            private void KiemtraFireBase(ArrayList<String> uidList) {
                ArrayList<String> uidListupload = new ArrayList<>();
                for (int i = 0; i < uidList.size(); i++) {
                    int userid = i;
                    DatabaseReference databaseReference = FirebaseDatabase
                            .getInstance().getReference("ThongTin_UpLoad");
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Kiểm tra xem nút con với ID của người dùng có tồn tại hay không
                            if (dataSnapshot.hasChild(uidList.get(userid))) {
                                uidListupload.add(uidList.get(userid));
                                if (userid == uidList.size() - 1) {
                                    GetToaDo(uidListupload);
                                }
                                Log.d("EEE", uidList.get(userid));
                                // Thực hiện các thuật toán khác
                                // ...
                            } else {
                                Log.d("CCC", "không có nút " + uidList.get(userid));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Xử lý lỗi
                            Log.d("CCC", "không có nút " + uidList.get(userid));


                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // handle error
            }
        });

    }

    private void GetToaDo(ArrayList<String> uidListupload) {
        ArrayList<UserLocation> userLocationsCopy = new ArrayList<>();
        for (int i = 0; i < uidListupload.size(); i++) {
            int uiduser = i;
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users/" + uidListupload.get(i));
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.hasChild("Latitude") && dataSnapshot.hasChild("Longitude")) {
                        double latitude = dataSnapshot.child("Latitude").getValue(Double.class);
                        double longitude = dataSnapshot.child("Longitude").getValue(Double.class);
                        String url = dataSnapshot.child("image").getValue(String.class);
                        userLocation = new UserLocation(uidListupload.get(uiduser), latitude, longitude, url);
                        userLocations.add(userLocation);
                        userLocationsCopy.add(userLocation);
                        if (uiduser == uidListupload.size() - 1) {
                            showmap.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    processUserLocations(getActivity(), userLocations);


                                }
                            });
                            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            fusedLocationProviderClient.getLastLocation()
                                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                                        @Override
                                        public void onSuccess(Location location) {
                                            if (location != null) {
                                                // Lấy được vị trí hiện tại của người dùng
                                                currentLocation = location;
                                                TinhKhoangCach(userLocations, currentLocation);
                                            }
                                        }
                                    });

                        }

                        // Di chuyển lệnh Log.d vào bên trong phương thức onDataChange()
                        //Log.d("AAA" + uiduser + " ", userLocations.get(uiduser).getLatitude() + " - " + userLocations.get(uiduser).getLongitude() + " - " + userLocations.get(uiduser).getUid() );
                        Log.d("Size", String.valueOf(userLocations.size()));

                    }
                    else {
                        Log.d("CCC", "Lỗi!!!!");
                        Intent intent = new Intent(getActivity(), MapsActivity.class);
                        startActivity(intent);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // handle error
                    Log.d("BBB", "Lỗi!!!");
                }
            });
        }
        Log.d("Size", String.valueOf(userLocations.size()));
    }

    private void TinhKhoangCach(ArrayList<UserLocation> userLocations, Location currentLocation) {
            for(int i = 0; i < userLocations.size(); i++){
//                    latitude = location.getLatitude();
//                    longitude = location.getLongitude();

                    double khoangcach = Math.sqrt(Math.pow(userLocations.get(i).getLatitude() - currentLocation.getLatitude(), 2) + Math.pow(userLocations.get(i).getLongitude() - currentLocation.getLongitude(), 2));
                    khoangCachLocationList.add(new KhoangCachLocation(khoangcach, userLocations.get(i).getUid()));


            }
            Collections.sort(khoangCachLocationList, new Comparator<KhoangCachLocation>() {
                public int compare(KhoangCachLocation o1, KhoangCachLocation o2) {
                    return Double.compare(o1.getDistance(), o2.getDistance());
                }
            });
            for (KhoangCachLocation khoangCachLocation : khoangCachLocationList) {
                double distance = khoangCachLocation.getDistance();
                String uid = khoangCachLocation.getUid();
                Log.d("khoangcachsapxep", distance + " - " + uid);
                KhoangCachLocaitonSort khoangCachLocaitonSort = new KhoangCachLocaitonSort(distance, uid);
                khoangCachLocaitonSorts.add(khoangCachLocaitonSort);
            }
        for(int i=0;i<khoangCachLocaitonSorts.size();i++){
            Log.d("khoangcach", khoangCachLocaitonSorts.get(i).getDistanceSort() + " - " + khoangCachLocaitonSorts.get(i).getUidSort() );
        }
    }

    private void processUserLocations(Context context, ArrayList<UserLocation> userLocations) {
        Intent intent = new Intent(context, MapsActivity.class);
        Gson gson = new Gson();
        String userLocationsJson = gson.toJson(userLocations);
        intent.putExtra("userLocationsJson", userLocationsJson);
        context.startActivity(intent);
    }

}
