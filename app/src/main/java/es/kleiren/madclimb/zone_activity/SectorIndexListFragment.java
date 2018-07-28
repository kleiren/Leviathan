package es.kleiren.madclimb.zone_activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.kleiren.madclimb.R;
import es.kleiren.madclimb.data_classes.Sector;
import es.kleiren.madclimb.data_classes.Zone;
import es.kleiren.madclimb.extra_activities.ImageViewerActivity;
import es.kleiren.madclimb.root.GlideApp;
import es.kleiren.madclimb.sector_activity.SectorActivity;


public class SectorIndexListFragment extends Fragment {

    private SectorDataAdapter adapter;
    private ArrayList<Sector> sectorsFromFirebase = new ArrayList<>();
    private Activity parentActivity;
    private Zone zone;
    private static final String ARG_ZONE = "zone";
    private ObservableSectorList observableSectorList;

    @BindView(R.id.card_sectorIndex_view)
    RecyclerView recyclerSector;
    @BindView(R.id.sectorIndex_initial_progress)
    ProgressBar initialProgress;
    @BindView(R.id.sectorIndex_imgCroquis)
    ImageView imgCroquis;

    public SectorIndexListFragment() {
    }

    private ArrayList<Sector> sectors;
    private Observer sectorListChanged = new Observer() {
        @Override
        public void update(Observable o, Object newValue) {

            sectors = (ArrayList<Sector>) newValue;
            adapter = new SectorDataAdapter(sectors, getActivity());
            recyclerSector.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            initialProgress.setVisibility(View.GONE);

        }
    };

    public static SectorIndexListFragment newInstance(Zone zone) {
        SectorIndexListFragment fragment = new SectorIndexListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ZONE, zone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = getActivity();
        if (getArguments() != null) {
            zone = (Zone) getArguments().getSerializable(ARG_ZONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View zoneView = inflater.inflate(R.layout.fragment_sector_index_list, container, false);
        ButterKnife.bind(this, zoneView);

        if (sectorsFromFirebase.isEmpty())
            prepareData();
        initViews();

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

        final StorageReference load = mStorageRef.child(zone.getImg());

        GlideApp.with(getActivity())
                .load(load)
                .placeholder(R.drawable.mountain_placeholder)
                .into(imgCroquis);

        imgCroquis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ImageViewerActivity.class);
                intent.putExtra("image", zone.getImg());
                intent.putExtra("title", zone.getName());
                startActivityForResult(intent, 1);
            }
        });

        return zoneView;
    }

    private void prepareData() {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("zones/" + zone.getId() + "/sectors").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Sector sector = postSnapshot.getValue(Sector.class);
                    sectorsFromFirebase.add(sector);
                }

                observableSectorList = new ObservableSectorList();
                observableSectorList.getSectorImagesFromFirebase(sectorsFromFirebase, getActivity());
                observableSectorList.addObserver(sectorListChanged);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("FIREBASE", "The read failed: " + databaseError.getCode());
            }
        });
    }

    private void initViews() {

        recyclerSector.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerSector.setHasFixedSize(false);

        adapter = new SectorDataAdapter(sectorsFromFirebase, getActivity());
        recyclerSector.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        recyclerSector.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int position = rv.getChildAdapterPosition(child);

                    Intent intent = new Intent(getActivity(), SectorActivity.class);
                    intent.putExtra("zone", zone);
                    intent.putExtra("sectors", sectorsFromFirebase);
                    intent.putExtra("currentSectorPosition", position);
                    startActivity(intent);
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

}