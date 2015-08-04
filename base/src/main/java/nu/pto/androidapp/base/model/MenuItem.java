package nu.pto.androidapp.base.model;

import android.app.Fragment;

public class MenuItem {

    private int icon;
    private int highlightedIcon;
    private int title;
    private Class<? extends Fragment> fragmentClass;
    private int badgeValue = 0;

    public MenuItem() {

    }


    public MenuItem(int icon, int highlightedIcon, int title, Class fragmentClass) {
        this.icon = icon;
        this.highlightedIcon = highlightedIcon;
        this.title = title;
        this.fragmentClass = fragmentClass;
    }

    public void setBadgeValue(int badgeValue) {
        this.badgeValue = badgeValue;
    }

    public int getBadgeValue() {
        return badgeValue;
    }

    public int getIcon() {
        return icon;
    }

    public int getHighlightedIcon() {
        return highlightedIcon;
    }

    public int getTitle() {
        return title;
    }

    public Class getFragmentClass() {
        return fragmentClass;
    }
}
