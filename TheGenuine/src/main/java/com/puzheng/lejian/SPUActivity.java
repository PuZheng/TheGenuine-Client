package com.puzheng.lejian;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.puzheng.lejian.adapter.SPUPicListAdapter;
import com.puzheng.lejian.model.Pic;
import com.puzheng.lejian.model.SPU;
import com.puzheng.lejian.model.Verification;
import com.puzheng.lejian.util.FakeUtil;
import com.puzheng.lejian.util.LoginRequired;
import com.puzheng.lejian.view.FavorButton;
import com.puzheng.lejian.view.NearbyButton;
import com.puzheng.lejian.view.SPUTabHost;
import com.puzheng.lejian.view.ShareButton;
import com.viewpagerindicator.CirclePageIndicator;

import org.stringtemplate.v4.ST;

import java.util.ArrayList;
import java.util.List;

public class SPUActivity extends FragmentActivity implements LoginRequired.ILoginHandler, RefreshInterface {

    private static final String TAG = "SPUActivity";
    private static final int LOGIN_ACTION = 1;
    private Verification authentication;

    //只有二维码验证才需要展示 验证码
    private boolean verificationFinished;

    private ViewPager picListViewPager;
    private SPU spu;
    private MaskableManager maskableManager;
    private SPUPicListAdapter spuPicListAdapter;
    private LoginRequired.LoginHandler loginHandler;

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
    public void refresh() {
        //new GetSPUTask().execute(spu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        UMSsoHandler ssoHandler = umSocialService.getConfig().getSsoHandler(requestCode);
//        if (ssoHandler != null) {
//            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
//        }
        loginHandler.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spu);

        retrieveExtra();

        loginHandler = new LoginRequired.LoginHandler(LOGIN_ACTION);

        if (authentication == null && spu == null) {
            throw new IllegalArgumentException("必须传入产品信息或者验证信息");
        }

        picListViewPager = (ViewPager) findViewById(R.id.viewPagerCover);

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
            ((FavorButton) findViewById(R.id.favorButton)).setup(spu, LOGIN_ACTION);
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
            spuPicListAdapter = new SPUPicListAdapter(getSupportFragmentManager(), urls);
            picListViewPager.setAdapter(spuPicListAdapter);
            CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.circlePageIndicator);
            circlePageIndicator.setViewPager(picListViewPager);

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
//        spuPicListAdapter = new SPUPicListAdapter(getSupportFragmentManager(), urls);
//        picListViewPager.setAdapter(spuPicListAdapter);
//        CirclePageIndicator titleIndicator = (CirclePageIndicator) findViewById(R.id.circlePageIndicator);
//        titleIndicator.setViewPager(picListViewPager);
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


    private String getShareContent(String shareURL) {
        if (!TextUtils.isEmpty(MyApp.SHARETEMPLATE)) {
            try {
                ST template = new ST(MyApp.SHARETEMPLATE);
                template.add("spu", spu);
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
                template.add("spu", spu);
                return template.render();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return getString(R.string.share_url_template);
    }

    @Override
    public void onLoginDone(LoginRequired.Runnable runnable) {
        loginHandler.onLoginDone(runnable);
    }
}
