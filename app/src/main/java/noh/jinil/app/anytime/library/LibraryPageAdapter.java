package noh.jinil.app.anytime.library;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class LibraryPageAdapter extends FragmentStatePagerAdapter {

    ArrayList<LibraryPageInfo> pageArray = new ArrayList<>();
    Context mContext;

    public LibraryPageAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        pageArray.add(new LibraryPageInfo(LibraryPageInfo.TYPE.TRACK));
        pageArray.add(new LibraryPageInfo(LibraryPageInfo.TYPE.ALBUM));
        pageArray.add(new LibraryPageInfo(LibraryPageInfo.TYPE.ARTIST));
        pageArray.add(new LibraryPageInfo(LibraryPageInfo.TYPE.FOLDER));
    }

    @Override
    public Fragment getItem(int position) {
        if(position >= getCount())
            return null;

        Bundle bundle = new Bundle();
        bundle.putParcelable(LibraryPageInfo.BUNDLE_KEY, pageArray.get(position));

        LibraryPageFragment musicListFragment = new LibraryPageFragment();
        musicListFragment.setArguments(bundle);

        return musicListFragment;
    }

    @Override
    public int getCount() {
        return pageArray.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position >= getCount())
            return "";
        return pageArray.get(position).getPageTitle(mContext);
    }
}
