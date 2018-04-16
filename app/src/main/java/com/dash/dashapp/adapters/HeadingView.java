package com.dash.dashapp.adapters;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dash.dashapp.R;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.expand.Collapse;
import com.mindorks.placeholderview.annotations.expand.Expand;
import com.mindorks.placeholderview.annotations.expand.Parent;
import com.mindorks.placeholderview.annotations.expand.ParentPosition;
import com.mindorks.placeholderview.annotations.expand.SingleTop;
import com.mindorks.placeholderview.annotations.expand.Toggle;

/**
 * Created by sebas on 10/26/2017.
 */

@Parent
@SingleTop
@Layout(R.layout.portfolio_header)
public class HeadingView {

    @View(R.id.headingTxt)
    public TextView headingTxt;

    @View(R.id.toggleIcon)
    public ImageView toggleIcon;

    @Toggle(R.id.toggleView)
    public LinearLayout toggleView;

    @ParentPosition
    public int mParentPosition;

    private Context mContext;
    private String mHeading;

    public HeadingView(Context context, String heading) {
        mContext = context;
        mHeading = heading;
    }

    @Resolve
    public void onResolved() {
        toggleIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_white_24dp));
        headingTxt.setText(mHeading);
    }

    @Expand
    public void onExpand(){
        toggleIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_white_24dp));
    }

    @Collapse
    public void onCollapse(){
        toggleIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_white_24dp));
    }
}