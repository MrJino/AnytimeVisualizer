package noh.jinil.app.anytime;

import noh.jinil.app.anytime.library.LibraryPageInfo;
import noh.jinil.app.anytime.service.IMediaPlaybackService;

public interface IMainActivity {
    void requestToShowSettings();
    void requestToShowAppInfo();
    void requestToShowLibrary();
    void requestToShowLibraryPage(LibraryPageInfo pageInfo);
    void requestToShowNowPlaying();
    void requestToPlayItem(long[] audioIDs);
    IMediaPlaybackService getServiceObject();
}
