package noh.jinil.app.anytime;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import noh.jinil.app.anytime.help.HelpFragment;
import noh.jinil.app.anytime.library.LibraryPageFragment;
import noh.jinil.app.anytime.main.MainLibraryFragment;
import noh.jinil.app.anytime.music.PlaybackFragment;
import noh.jinil.app.anytime.music.miniPlayerFragment;
import noh.jinil.app.anytime.queue.NowPlayingFragment;
import noh.jinil.app.anytime.setting.SettingFragment;

public class MainPageHelper {

    public enum TYPE {
        PLAYBACK,
        LIBRARY,
        LIBRARY_PAGE,
        SETTINGS,
        INFORMATION,
        miniPLAYER,
        nowPLAYING;

        public static TYPE fromOrdinal(int ordinal) {
            for(TYPE type : values()) {
                if(type.ordinal() == ordinal)
                    return type;
            }
            return PLAYBACK;
        }
    }

    private TYPE type;

    public MainPageHelper(TYPE type) {
        this.type = type;
    }

    public static IMainFragment attachFragment(FragmentManager fragmentManager, TYPE type, Bundle bundle) {
        Fragment fragment = fragmentManager.findFragmentByTag(type.toString());
        if(fragment == null) {
            fragment = createFragment(type, bundle);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(MainPageHelper.getContainerID(type), fragment, type.toString());
            transaction.commit();
        }
        return (IMainFragment) fragment;
    }

    public static IMainFragment findFragment(FragmentManager fragmentManager, TYPE type) {
        Fragment fragment = fragmentManager.findFragmentByTag(type.toString());
        return (IMainFragment) fragment;
    }

    public static void detachFragment(FragmentManager fragmentManager, TYPE type) {
        Fragment fragment = fragmentManager.findFragmentByTag(type.toString());
        if(fragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.remove(fragment);
            transaction.commit();
        }
    }

    private static int getContainerID(TYPE type) {
        switch (type) {
            case PLAYBACK:
            case LIBRARY:
            case LIBRARY_PAGE:
            case SETTINGS:
            case INFORMATION:
            case nowPLAYING:
                return R.id.activity_fragment_mainWindow;
            case miniPLAYER:
                return R.id.activity_fragment_miniPlayer;
        }
        return 0;
    }

    public static Fragment createFragment(TYPE type, Bundle bundle) {
        switch (type) {
            case PLAYBACK:
                return new PlaybackFragment(); //new MainPlaybackFragment();
            case LIBRARY:
                return new MainLibraryFragment();
            case LIBRARY_PAGE:
                LibraryPageFragment fragment = new LibraryPageFragment();
                fragment.setArguments(bundle);
                return fragment;
            case miniPLAYER:
                return new miniPlayerFragment();
            case SETTINGS:
                return new SettingFragment();
            case INFORMATION:
                return new HelpFragment();
            case nowPLAYING:
                return new NowPlayingFragment();
        }
        return null;
    }
}
