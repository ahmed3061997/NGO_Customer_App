package com.belal.projects.ngo.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.belal.projects.ngo.R;
import com.belal.projects.ngo.activities.MainActivity;
import com.belal.projects.ngo.adapters.SectionsPagerAdapter;

public class HomeFragment extends Fragment {
    // view pager & sections pager adapter
    private ViewPager mViewPager ;
    private SectionsPagerAdapter mSectionsPagerAdapter ;
    // tab layout
    private TabLayout mTabLayout ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_home, container, false);

        // view pager
        mViewPager = (ViewPager) view.findViewById( R.id.home_tab_pager );

        // view pager adapter
        mSectionsPagerAdapter = new SectionsPagerAdapter( getChildFragmentManager() );
        mViewPager.setAdapter( mSectionsPagerAdapter );

        // tab layout
        mTabLayout = (TabLayout) view.findViewById( R.id.home_tabs );
        mTabLayout.setupWithViewPager( mViewPager );

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title bar
        ((MainActivity) getActivity()).setActionBarTitle("Home");
    }
}

