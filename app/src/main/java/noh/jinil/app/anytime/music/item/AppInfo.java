package noh.jinil.app.anytime.music.item;

import android.content.ComponentName;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

public class AppInfo {
    private Drawable icon = null;
    private String packageName = null;
    private ComponentName componentName = null;
    private String appName = null;
    private String appId = null;
    
    public AppInfo() {   	
    }
    
    public AppInfo(ApplicationInfo info, PackageManager pm) {
    	setAppId(info.loadDescription(pm)+"");
        setAppName(info.loadLabel(pm)+"");
        setPackageName(info.packageName);     
        setIcon(info.loadIcon(pm));
    }
     
    public Drawable getIcon() {
        return icon;
    }
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
    public String getPackageName() {
        return packageName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public ComponentName getComponentName() {
        return componentName;
    }
    public void setComponentName(ComponentName componentName) {
        this.componentName = componentName;
    }
    public String getAppName() {
        return appName;
    }
    public void setAppName(String appName) {
        this.appName = appName;
    }
    public String getAppId() {
        return appId;
    }
    public void setAppId(String appId) {
        this.appId = appId;
    }	     
}
