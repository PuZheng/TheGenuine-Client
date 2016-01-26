package com.puzheng.lejian;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.puzheng.humanize.Humanize;
import com.puzheng.lejian.adapter.SPUCoverAdapter;
import com.puzheng.lejian.model.Pic;
import com.puzheng.lejian.model.SPU;
import com.puzheng.lejian.model.SPUResponse;
import com.puzheng.lejian.model.Vendor;
import com.puzheng.lejian.model.Verification;
import com.puzheng.lejian.netutils.WebService;
import com.puzheng.lejian.util.BadResponseException;
import com.puzheng.lejian.util.ConfigUtil;
import com.puzheng.lejian.util.FakeUtil;
import com.puzheng.lejian.view.FavorButton;
import com.puzheng.lejian.view.NearbyButton;
import com.puzheng.lejian.view.SPUTabHost;
import com.puzheng.lejian.view.ShareButton;
import com.viewpagerindicator.CirclePageIndicator;

import org.stringtemplate.v4.ST;

import java.util.ArrayList;
import java.util.List;

public class SPUActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, RefreshInterface {

    private static final String TAG = "SPUActivity";
    private ViewPager viewPager;
    private Verification authentication;

    //只有二维码验证才需要展示 验证码
    private boolean verificationFinished;

    private SPUResponse spuResponse;
    private ViewPager viewPagerCover;
    private TabHost tabHost;
    private SPU spu;
    private MaskableManager maskableManager;
    private SPUCoverAdapter adapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onPageScrollStateChanged(int i) {
        int pos = viewPager.getCurrentItem();
        tabHost.setCurrentTab(pos);
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void refresh() {
        //new GetSPUTask().execute(spu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        UMSsoHandler ssoHandler = umSocialService.getConfig().getSsoHandler(requestCode);
//        if (ssoHandler != null) {
//            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
//        }
        if (resultCode == RESULT_OK) {
            FavorButton favorButton = (FavorButton) findViewById(R.id.favorButton);
            if (requestCode == favorButton.LOGIN_ACTION) {
                favorButton.performClick();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spu);

        retrieveExtra();


        if (authentication == null && spu == null) {
            throw new IllegalArgumentException("必须传入产品信息或者验证信息");
        }

        viewPagerCover = (ViewPager) findViewById(R.id.viewPagerCover);

        Point point = new Point();
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        display.getSize(point);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        if (authentication != null) {
            initViews();
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.good);
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
        } else {
            maskableManager = new MaskableManager(findViewById(R.id.main), this);
            //new GetSPUTask().execute(spu);
            getActionBar().setTitle(spu.getName());
            ((ShareButton) findViewById(R.id.shareButton)).setSPU(spu);
            ((CommentButton) findViewById(R.id.commentButton)).setSPU(spu);
            ((NearbyButton) findViewById(R.id.nearbyButton)).setSPU(spu);
            ((FavorButton) findViewById(R.id.favorButton)).setSPU(spu);

            RatingBar rb = (RatingBar) findViewById(R.id.productRatingBar);
            rb.setRating(spu.getRating());

            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            if (authentication == null) {
                imageView.setVisibility(View.GONE);
            } else {
                if (!verificationFinished) {
                    // 二维码验证不提示真品伪品
                    findViewById(R.id.checksumLayout).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.textViewChecksum)).setText(getString(R.string.verify_number, authentication.getSKU().getChecksum()));
                    imageView.setVisibility(View.GONE);
                }
            }

            List<String> urls = new ArrayList<String>();
            for (Pic pic: spu.getPics()) {
                urls.add(pic.getURL());
            }
            adapter = new SPUCoverAdapter(getSupportFragmentManager(), urls);
            viewPagerCover.setAdapter(adapter);
            CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.circlePageIndicator);
            circlePageIndicator.setViewPager(viewPagerCover);

            SPUTabHost spuTabHost = (SPUTabHost) findViewById(R.id.spuTabHost);
            spuTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
            spuTabHost.setSPU(spu);
        }

    }


    private void retrieveExtra() {
        authentication = getIntent().getParcelableExtra(NFCAuthenticationActivity.TAG_VERIFICATION_INFO);
        verificationFinished = getIntent().getBooleanExtra(NFCAuthenticationActivity.TAG_VERIFICATION_FINISHED, false);
        spu = getIntent().getParcelableExtra(Const.TAG_SPU);
        if (BuildConfig.DEBUG) {
            if (spu == null) {
                spu = FakeUtil.getInstance().spu();
            }
        }
        Logger.json(new Gson().toJson(spu));
    }


    private void doAddFavor() {
    }

    private int getCommentsCnt() {
        return authentication != null ? authentication.getCommentsCnt() : spuResponse.getCommentsCnt();
    }

    private int getDistance() {
        return authentication != null ? authentication.getDistance() : spuResponse.getDistance();
    }

    private List<Pic> getPics() {
        return authentication != null ? authentication.getSKU().getSPU().getPics() : spuResponse.getSPU().getPics();
    }

    private float getRating() {
        return authentication != null ? authentication.getSKU().getSPU().getRating() : spuResponse.getSPU().getRating();
    }

    private int getSPUId() {
        return authentication != null ? authentication.getSKU().getSPU().getId() : spuResponse.getSPU().getId();
    }

    private int getSameTypeRecommendationsCnt() {
        return authentication != null ? authentication.getSameTypeRecommendationsCnt() : spuResponse.getSameTypeRecommendationsCnt();
    }

    private int getSameVendorRecommendationsCnt() {
        return authentication != null ? authentication.getSameVendorRecommendationsCnt() : spuResponse.getSameVendorRecommendationsCnt();
    }

    private SPU getSPU() {
        return authentication != null ? authentication.getSKU().getSPU() : spuResponse.getSPU();
    }

    private int getVendorId() {
        return authentication != null ? authentication.getSKU().getSPU().getVendorId() :
                spuResponse.getSPU().getVendorId();
    }

    private void initViews() {
////        setTitle();
////        initShare();
////        setupNearbyButton();
////        updateFavorView(isFavored());
//        ImageView imageView = (ImageView) findViewById(R.id.imageView);
//        if (authentication == null) {
//            imageView.setVisibility(View.GONE);
//        } else {
//            if (!verificationFinished) {
//                // 二维码验证不提示真品伪品
//                findViewById(R.id.checksumLayout).setVisibility(View.VISIBLE);
//                ((TextView) findViewById(R.id.textViewChecksum)).setText(getString(R.string.verify_number, authentication.getSKU().getChecksum()));
//                imageView.setVisibility(View.GONE);
//            }
//        }
//
//
//        RatingBar rb = (RatingBar) findViewById(R.id.productRatingBar);
//        rb.setRating(getRating());
//
//
//
//        List<String> urls = new ArrayList<String>();
//        for (Pic pic: getPics()) {
//            urls.add(pic.getURL());
//        }
//        adapter = new SPUCoverAdapter(getSupportFragmentManager(), urls);
//        viewPagerCover.setAdapter(adapter);
//        CirclePageIndicator titleIndicator = (CirclePageIndicator) findViewById(R.id.circlePageIndicator);
//        titleIndicator.setViewPager(viewPagerCover);
//
//
//        tabHost = (TabHost) findViewById(R.id.spuTabHost);
//        tabHost.setup();
//
//        String s = getString(R.string.verify_info);
//        if (spuResponse != null) {
//            s = getString(R.string.product_info);
//        }
//        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tab1").setIndicator(s);
//        tabSpec.setContent(new MyTabFactory(this));
//        tabHost.addTab(tabSpec);
//        s = getString(R.string.sameType,
//                Humanize.with(this).num(getSameTypeRecommendationsCnt()));
//        tabSpec = tabHost.newTabSpec("tab2").setIndicator(s);
//        tabSpec.setContent(new MyTabFactory(this));
//        tabHost.addTab(tabSpec);
//        s = getString(R.string.sameVendor,
//                Humanize.with(this).num(getSameVendorRecommendationsCnt()));
//        tabSpec = tabHost.newTabSpec("tab3").setIndicator(s);
//        tabSpec.setContent(new MyTabFactory(this));
//        tabHost.addTab(tabSpec);
////        tabHost.setOnTabChangedListener(this);
//
//        TabWidget tabWidget = tabHost.getTabWidget();
//        int tabCounts = tabHost.getTabWidget().getTabCount();
//
//        for (int i = 0; i < tabCounts; i++) {
//            TextView textView = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
//            textView.setAllCaps(false);
//        }
//
//        setBottomTabs();
////        viewPager = (ViewPager) findViewById(R.id.viewPagerBottom);
////        viewPager.setAdapter(new SPUListPagerAdapter(getSupportFragmentManager()));
////        viewPager.setOnPageChangeListener(this);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setBottomTabs() {
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); ++i) {
            View v = tabHost.getTabWidget().getChildTabViewAt(i);
            v.setBackground(getResources().getDrawable(R.drawable.tab_indicator_holo));
            TextView title = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            int color = getResources().getColor(android.R.color.darker_gray);
            if (i == 0) {
                color = getResources().getColor(R.color.base_color1);
            }
            title.setTextColor(color);
        }
    }

    private String getShareContent(String shareURL) {
        if (!TextUtils.isEmpty(MyApp.SHARETEMPLATE)) {
            try {
                ST template = new ST(MyApp.SHARETEMPLATE);
                template.add("spu", getSPU());
                return template.render();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return getString(R.string.share_template) + " " + shareURL;
    }

    private String getShareURL() {
        if (!TextUtils.isEmpty(MyApp.SHAREURL)) {
             try {
                ST template = new ST(MyApp.SHAREURL);
                template.add("spu", getSPU());
                return template.render();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return getString(R.string.share_url_template);
    }

}
