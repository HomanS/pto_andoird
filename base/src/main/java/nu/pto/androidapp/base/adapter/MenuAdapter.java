package nu.pto.androidapp.base.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import nu.pto.androidapp.base.R;
import nu.pto.androidapp.base.model.MenuItem;
import nu.pto.androidapp.base.ui.BadgeView;

import java.util.List;

public class MenuAdapter extends BaseAdapter {

    public static interface MenuItemClickListener {

        public void onMenuItemClick(int position);
    }

    Context context;
    List<MenuItem> menuItems;
    MenuItemClickListener clickListener;

    private boolean downActionOccurred;
    private float touchX, touchY;

    public MenuAdapter(Context context, List<MenuItem> items, MenuItemClickListener clickListener) {
        this.context = context;
        this.clickListener = clickListener;
        menuItems = items;
    }

    public void setMenuItems(List<MenuItem> items) {
        menuItems = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return menuItems.size();
    }

    @Override
    public MenuItem getItem(int i) {
        return menuItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        TextView titleTv;
        BadgeView badgeTv;
        ImageView iconIv;
        if (view == null) {
            view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item_menu, null);
            titleTv = (TextView) view.findViewById(R.id.title_tv);
            iconIv = (ImageView) view.findViewById(R.id.icon_iv);
            final ImageView finalIconIV = iconIv;
            final TextView finalTitleTV = titleTv;

            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    if (getItem(i).getTitle() == 0) {
                        return false;
                    }

                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            if (getItem(i).getHighlightedIcon() != 0) {
                                finalIconIV.setImageResource(getItem(i).getHighlightedIcon());
                            }
                            finalTitleTV.setTextColor(Color.WHITE);
                            view.setBackgroundColor(context.getResources().getColor(R.color.menu_list_selector));
                            view.invalidate();
                            downActionOccurred = true;
                            touchX = motionEvent.getX();
                            touchY = motionEvent.getY();
                            break;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            float diffX = Math.abs(touchX - motionEvent.getX());
                            float diffY = Math.abs(touchY - motionEvent.getY());
                            if (diffX > 10 || diffY > 10) {
                                downActionOccurred = false;
                            }
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            if (downActionOccurred) {
                                clickListener.onMenuItemClick(i);
                            }
                        }
                        case MotionEvent.ACTION_CANCEL: {
                            if (getItem(i).getIcon() != 0) {
                                finalIconIV.setImageResource(getItem(i).getIcon());
                            }
                            finalTitleTV.setTextColor(Color.BLACK);
                            view.setBackgroundColor(Color.WHITE);
                            downActionOccurred = false;
                            touchX = -1;
                            touchY = -1;
                            break;
                        }
                    }
                    return true;
                }
            });
        } else {
            titleTv = (TextView) view.findViewById(R.id.title_tv);
            iconIv = (ImageView) view.findViewById(R.id.icon_iv);
        }
        badgeTv = (BadgeView) view.findViewById(R.id.badge_tv);
        iconIv.setVisibility(View.GONE);

        if (getItem(i).getTitle() == 0) {
            titleTv.setVisibility(View.GONE);
            view.setBackgroundColor(Color.TRANSPARENT);
        } else {
            titleTv.setVisibility(View.VISIBLE);
            if (getItem(i).getIcon() != 0) {
                iconIv.setVisibility(View.VISIBLE);
                iconIv.setImageResource(getItem(i).getIcon());
            }
            titleTv.setText(getItem(i).getTitle());
            view.setBackgroundColor(Color.WHITE);
        }

        badgeTv.setVisibility(View.GONE);
        if (getItem(i).getBadgeValue() > 0) {
            badgeTv.setVisibility(View.VISIBLE);
            badgeTv.setText("" + getItem(i).getBadgeValue());
        }

        return view;
    }

    @Override
    public boolean isEnabled(int position) {
        if (getItem(position).getTitle() == 0) {
            return false;
        }
        return true;
    }
}
