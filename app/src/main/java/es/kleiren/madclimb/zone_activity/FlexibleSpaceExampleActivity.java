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
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
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
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.kleiren.madclimb.R;
import es.kleiren.madclimb.data_classes.Zone;
import es.kleiren.madclimb.extra_activities.ImageViewerActivity;
import es.kleiren.madclimb.extra_activities.InfoFragment;
import es.kleiren.madclimb.main.MainActivity;
import es.kleiren.madclimb.root.GlideApp;

public class FlexibleSpaceExampleActivity extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener, ViewPager.OnPageChangeListener {
    private static final int PERCENTAGE_TO_SHOW_IMAGE = 20;
    private int mMaxScrollSize;
    private boolean mIsImageHidden;
    private Zone zone;
    private NestedScrollView nestedScrollView;
    private AppBarLayout collapsingAppbar;
    private ViewPager viewPager;
    private NavigationAdapter navigationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flexible_space);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.materialup_tabs);

        zone = (Zone) getIntent().getSerializableExtra("zone");

        Toolbar toolbar = (Toolbar) findViewById(R.id.flexible_example_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        collapsingAppbar = (AppBarLayout) findViewById(R.id.flexible_example_appbar);
        collapsingAppbar.addOnOffsetChangedListener(this);
        collapsingAppbar = (AppBarLayout) findViewById(R.id.flexible_example_appbar);
        CollapsingToolbarLayout mCollapsing = findViewById(R.id.flexible_example_collapsing);
        mCollapsing.setTitle(zone.getName());

        viewPager = (ViewPager) findViewById(R.id.materialup_viewpager);
        navigationAdapter = new NavigationAdapter(getSupportFragmentManager());
        viewPager.setAdapter(navigationAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(this);

        nestedScrollView = findViewById(R.id.nestedScroll);


        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) collapsingAppbar.getLayoutParams();
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(AppBarLayout appBarLayout) {
                return false;
            }
        });
        params.setBehavior(behavior);

        View mImageView = findViewById(R.id.imageZone);

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

        final StorageReference load = mStorageRef.child(zone.getImg());
        final ProgressBar progressBar = findViewById(R.id.zoneAct_progressBar);


        GlideApp.with(getApplicationContext())
                .load(load)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into((ImageView) mImageView);

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ImageViewerActivity.class);
                intent.putExtra("image", zone.getImg());
                intent.putExtra("title", zone.getName());
                startActivityForResult(intent, 1);
            }
        });
    }

    public void disableScroll(){
        final SectorListMapFragment mSupportMapFragment;
        mSupportMapFragment = (SectorListMapFragment) navigationAdapter.getFragment(1);
        if (mSupportMapFragment != null)
            mSupportMapFragment.setListener(new SectorListMapFragment.OnTouchListener() {
                @Override
                public void onTouch() {
                    nestedScrollView.requestDisallowInterceptTouchEvent(true);
                }
            });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int currentScrollPercentage = (Math.abs(i)) * 100
                / mMaxScrollSize;

        if (currentScrollPercentage >= PERCENTAGE_TO_SHOW_IMAGE) {
            if (!mIsImageHidden) {
                mIsImageHidden = true;
            }
        }

        if (currentScrollPercentage < PERCENTAGE_TO_SHOW_IMAGE) {
            if (mIsImageHidden) {
                mIsImageHidden = false;
            }
        }
    }

    public void collapseToolbar() {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) collapsingAppbar.getLayoutParams();
        final AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();

        params.setBehavior(behavior);
        if (behavior != null) {
            ValueAnimator valueAnimator = ValueAnimator.ofInt();
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    behavior.setTopAndBottomOffset((Integer) animation.getAnimatedValue());
                    collapsingAppbar.requestLayout();
                    behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                        @Override
                        public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                            return false;
                        }
                    });
                }
            });
            valueAnimator.setIntValues(0, -900);
            valueAnimator.setDuration(400);
            valueAnimator.start();
        }
    }

    public void expandToolbar() {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) collapsingAppbar.getLayoutParams();
        final AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();

        params.setBehavior(behavior);
        if (behavior != null) {
            ValueAnimator valueAnimator = ValueAnimator.ofInt();
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    behavior.setTopAndBottomOffset((Integer) animation.getAnimatedValue());
                    collapsingAppbar.requestLayout();
                    behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                        @Override
                        public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                            return false;
                        }
                    });
                }
            });
            valueAnimator.setIntValues(0, 900);
            valueAnimator.setDuration(400);
            valueAnimator.start();
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 1) {
            collapseToolbar();
        } else {
            expandToolbar();
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class NavigationAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = new String[]{"Sectores", "Mapa", "Información"};
        private Map<Integer, String> mFragmentTags;
        private FragmentManager mFragmentManager;

        public NavigationAdapter(FragmentManager fm) {

            super(fm);
            mFragmentManager = fm;
            mFragmentTags = new HashMap<>();
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f;
            final int pattern = position % 5;
            switch (pattern) {
                case 0:
                default:
                    f = SectorListFragment.newInstance(zone);
                    break;
                case 1:
                    f = SectorListMapFragment.newInstance(zone);
                    break;
                case 2:
                    f = InfoFragment.newInstance("zone", zone);
                    break;
            }
            return f;
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Object obj = super.instantiateItem(container, position);
            if (obj instanceof Fragment) {
                Fragment f = (Fragment) obj;
                String tag = f.getTag();
                mFragmentTags.put(position, tag);
            }
            return obj;
        }

        public Fragment getFragment(int position) {
            String tag = mFragmentTags.get(position);
            if (tag == null)
                return null;
            return mFragmentManager.findFragmentByTag(tag);
        }
    }
}