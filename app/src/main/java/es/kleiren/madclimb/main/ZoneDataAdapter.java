package es.kleiren.madclimb.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.kleiren.madclimb.R;
import es.kleiren.madclimb.data_classes.Zone;
import es.kleiren.madclimb.root.GlideApp;
import es.kleiren.madclimb.zone_activity.ZoneActivity;

public class ZoneDataAdapter extends RecyclerView.Adapter<ZoneDataAdapter.ViewHolder> implements Filterable {
    private ArrayList<Zone> zones;
    private ArrayList<Zone> filteredZones;
    private Context context;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private boolean isFiltered;
    private boolean isFavFragment;
    private AppCompatActivity activity;


    ZoneDataAdapter(ArrayList<Zone> zones, Context context, AppCompatActivity activity, Boolean isFavFragment) {
        this.context = context;
        this.zones = zones;
        this.filteredZones = zones;
        this.isFavFragment = isFavFragment;
        this.activity = activity;
    }

    public Zone getZone(int i) {
        return filteredZones.get(i);
    }

    public ArrayList<Zone> getFilteredZones() {
        return filteredZones;
    }

    @Override
    public ZoneDataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.zone_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ZoneDataAdapter.ViewHolder viewHolder, int i) {

        if (i == 0 && !isFiltered && !isFavFragment) {
            viewHolder.mapShortcutStub.setVisibility(View.VISIBLE);
            viewHolder.mapShortcutStub.setOnClickListener(v -> {
                ((MainActivity) activity).bottomNavigationView.setSelectedItemId(R.id.nav_map);
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, ZoneListMapFragment.newInstance())
                        .commit();
            });

        } else {
            viewHolder.mapShortcutStub.setVisibility(View.GONE);
        }

        viewHolder.zoneView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, ZoneActivity.class);
            intent.putExtra("zone", zones.get(i));
            activity.startActivityForResult(intent, 1);
        });



        viewHolder.txtZoneName.setText(filteredZones.get(i).getName());
        viewHolder.txtStats.setText(filteredZones.get(i).numberOfSectors + " " + context.getString(R.string.sectors) + "\n"+ filteredZones.get(i).numberOfRoutes
                + " " +context.getString(R.string.routes));


        final StorageReference load = mStorageRef.child(filteredZones.get(i).getImg());
        GlideApp.with(context)
                .load(load)
                .centerCrop()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        viewHolder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        viewHolder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(viewHolder.imgZone);
    }

    @Override
    public int getItemCount() {
        return filteredZones.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredZones = zones;
                } else {
                    ArrayList<Zone> tempList = new ArrayList<>();
                    for (Zone zone : zones) {
                        if (zone.getName().toLowerCase().contains(charString.toLowerCase())) {
                            tempList.add(zone);
                        }
                    }
                    filteredZones = tempList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredZones;
                isFiltered = true;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredZones = (ArrayList<Zone>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.zoneRow_txtZoneName)
        TextView txtZoneName;
        @BindView(R.id.zoneRow_txtStats)
        TextView txtStats;
        @BindView(R.id.zoneRow_imgZone)
        ImageView imgZone;
        @BindView(R.id.zoneRow_progressBar)
        ProgressBar progressBar;
        @BindView(R.id.zoneRow_zoneView)
        View zoneView;
        @BindView(R.id.zoneRow_mapShortcutView)
        View mapShortcutStub;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}