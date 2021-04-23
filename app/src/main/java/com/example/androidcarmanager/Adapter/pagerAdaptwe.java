package com.example.androidcarmanager.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.androidcarmanager.Expence_Fragments.Cleaning_layout;
import com.example.androidcarmanager.Expence_Fragments.Engine_Tuning_Layout;
import com.example.androidcarmanager.Expence_Fragments.Fuel_layout;
import com.example.androidcarmanager.Expence_Fragments.Maintance_layout;
import com.example.androidcarmanager.Expence_Fragments.Purchases_layout;

public class pagerAdaptwe extends FragmentPagerAdapter {
    private int numOftabs;

public pagerAdaptwe (FragmentManager fm ,int numOftabs){
    super(fm);
    this.numOftabs = numOftabs;
}

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return  new Cleaning_layout();
            case 1:
                return  new Fuel_layout();
            case 2:
                return  new Maintance_layout();
            case 3:
                return  new Purchases_layout();
            case 4:
                return  new Engine_Tuning_Layout();
                default:
                    return null;


        }

    }

    @Override
    public int getCount() {

        return numOftabs;
    }
}
