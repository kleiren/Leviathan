/*
 * Copyright (C) 2017
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.kleiren.madclimb.zone_activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.kleiren.madclimb.R;
import es.kleiren.madclimb.data_classes.Sector;
import es.kleiren.madclimb.data_classes.Zone;
import es.kleiren.madclimb.extra_activities.ImageViewerActivity;
import es.kleiren.madclimb.extra_activities.InfoActivity;
import es.kleiren.madclimb.extra_activities.InfoFragment;
import es.kleiren.madclimb.root.GlideApp;
import es.kleiren.madclimb.sector_activity.RouteListFragment;
import es.kleiren.madclimb.sector_activity.SectorActivity;
import es.kleiren.madclimb.util.SlidingTabLayout;

public class SectorIndexActivity extends AppCompatActivity {


    @BindView(R.id.sectorIndexAct_tabLayout)
    SlidingTabLayout tabLayout;
    @BindView(R.id.sectorIndexAct_toolbar)
    Toolbar toolbar;
    @BindView(R.id.sectorIndexAct_pager)
    ViewPager viewPager;

    private NavigationAdapter mSectionsPagerAdapter;
    private ArrayList<Sector> sectorsFromFirebase;
    private ArrayList<String> sectorTitles = new ArrayList<>();
    private Zone zone;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sector_index);

        ButterKnife.bind(this);

        zone = (Zone) getIntent().getSerializableExtra("zone");
        int currentSectorPosition = getIntent().getIntExtra("currentSectorPosition", 0);
        sectorsFromFirebase = (ArrayList<Sector>) getIntent().getSerializableExtra("sectors");

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setTitle(zone.getName());

        for (Sector sector : sectorsFromFirebase) sectorTitles.add(sector.getName());

        mSectionsPagerAdapter = new NavigationAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mSectionsPagerAdapter);
        viewPager.setCurrentItem(currentSectorPosition);
        tabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        tabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.colorAccent));
        tabLayout.setDistributeEvenly(true);
        tabLayout.setViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sector_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.info) {

            Intent intent = new Intent(this, InfoActivity.class);
            intent.putExtra("type", "zone");
            intent.putExtra("datum", zone);
            startActivity(intent);
            return true;
        }

        if (id == R.id.error){
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto","madclimbapp@gmail.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "MADClimb error");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class NavigationAdapter extends FragmentPagerAdapter {

        public NavigationAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return sectorTitles.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return sectorTitles.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f;
            f = SectorIndexListFragment.newInstance(zone);
            return f;
        }
    }
}