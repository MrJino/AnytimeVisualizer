package noh.jinil.app.anytime.music.db;

import android.net.Uri;
import android.provider.MediaStore;

public class DatabaseURI {
	
	public static enum CategoryType {
        CATEGORY_SONG,
        CATEGORY_ALBUM,
        CATEGORY_ARTIST,
        CATEGORY_FOLDER,
	}
	
	public static Uri getUri(CategoryType category) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        switch(category) 
        {
		case CATEGORY_ALBUM:
		    uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
		    break;		
		
		case CATEGORY_ARTIST:
		    uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
		    break;
		
		case CATEGORY_FOLDER:
		    uri = Uri.parse("content://media/external/audio/folders");
		    break;
		
		case CATEGORY_SONG:
		    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		    break;
		    
		default:
			uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
			break;
        }
        return uri;
    }
}
