package com.example.lap.stschat;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;





public class TabsAccessorAdapter extends FragmentPagerAdapter
{
    public TabsAccessorAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                  return chatsFragment;


            case 1:
                AchivementFragment achivementFragment = new AchivementFragment();
                    return achivementFragment;

            case 2:
               NoticeFragment noticeFragments = new NoticeFragment();
               return noticeFragments;

            case 3:
                RequestFragment requestFragment = new RequestFragment ();
                return requestFragment;

            default:
                return null;

        }

    }
    @Override
    public int getCount()
    {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position) {
            case 0:
                return "Talks";

            case 1:
                return "Notes";
            case 2:
                return "Contacts";

            case 3:
                return "Requests";


            default:
                return null;
        }
    }

}
