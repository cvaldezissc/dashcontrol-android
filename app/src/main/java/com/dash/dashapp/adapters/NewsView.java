package com.dash.dashapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dash.dashapp.R;
import com.dash.dashapp.activities.ContentRSSActivity;
import com.dash.dashapp.models.News;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sebas on 8/8/2017.
 */

@Layout(R.layout.news_view)
public class NewsView {

    private static final String TITLE_NEWS = "title_rss";
    private static final String CONTENT_NEWS = "content_rss";
    @View(R.id.title)
    private TextView titleTxt;

    @View(R.id.date)
    private TextView dateTxt;

    private News mNews;
    private Context mContext;

    public NewsView(Context context, News news) {
        mContext = context;
        mNews = news;
    }

    @Resolve
    private void onResolved() {
        titleTxt.setText(mNews.getTitle());

        SimpleDateFormat rssFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date dateRSS = new Date();
        try {
            dateRSS = rssFormat.parse(mNews.getPubDate());
        } catch (ParseException ex) {
            ex.getMessage();
        }

        SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateSQL = sqlFormat.format(dateRSS);

        dateTxt.setText(dateSQL);

        if (mNews.getThumbnail() != null) {
            Glide.with(mContext).asBitmap()
                    .load(mNews.getThumbnail())
                    .into(new SimpleTarget<Bitmap>(100, 100) {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            titleTxt.setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(titleTxt.getResources(), resource), null, null, null);
                        }
                    });
        }
    }


    @Click(R.id.news_row)
    private void onClick() {
        Intent intent = new Intent(mContext, ContentRSSActivity.class);
        intent.putExtra(TITLE_NEWS, mNews.getTitle());
        intent.putExtra(CONTENT_NEWS, mNews.getContent());
        mContext.startActivity(intent);
    }
}
