package com.wizglobal.utils;

import com.wizglobal.app.InboxFragment;
import com.wizglobal.app.NewMessageFragment;
import com.wizglobal.app.OutboxFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Mathew.Godia on 6/25/2014.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int index){
        switch(index){
            case 0:
                return new NewMessageFragment();
            case 1:
                return new InboxFragment();
            case 2:
                return new OutboxFragment();
        }
        return null;
    }

    @Override
    public int getCount(){ //get item count - equal to teh number of tabs
        return 3;
    }
}
