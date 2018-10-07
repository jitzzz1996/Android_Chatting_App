package com.example.jitesh.android_brill_training_task_2;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class SectionsPagerAdapter extends FragmentPagerAdapter
{
    public SectionsPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0: RequestsFragement requestsFragement=new RequestsFragement();
                     return requestsFragement;
            case 1: ChatsFragment chatsFragment=new ChatsFragment();
                return chatsFragment;
            case 2:  FriendsFragement friendsFragement=new FriendsFragement();
                return friendsFragement;
            default: return null;
        }
    }

    @Override
    public int getCount()
    {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0: return "REQUESTS";
            case 1: return "CHATS";
            case 2: return "FRIENDS";
            default: return null;
        }
    }
}
