package com.puzheng.lejian;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import com.puzheng.lejian.model.SKU;
import com.puzheng.lejian.model.SPU;
import com.puzheng.lejian.util.FakeUtil;
import com.puzheng.lejian.util.LoginRequired;
import com.puzheng.lejian.view.FavorButton;
import com.puzheng.lejian.view.NearbyButton;
import com.puzheng.lejian.view.SPUTabHost;
import com.puzheng.lejian.view.ShareButton;
import com.umeng.socialize.UMShareAPI;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class SPUActivity extends AppCompatActivity implements LoginRequired.ILoginHandler, RefreshInterface {

    private static final int LOGIN_ACTION = 1;
    private SKU sku;

    private ViewPager picListViewPager;
    private SPU spu;
    private MaskableManager maskableManager;
    private SPUPicListAdapter spuPicListAdapter;
    private LoginRequired.LoginHandler loginHandler;
    private ActionBar supportActionBar;
    private AuthenticationActivity.VerificationMethod verificationMethod;

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
        loginHandler.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spu);

        retrieveExtra();

        loginHandler = new LoginRequired.LoginHandler(LOGIN_ACTION);

        if (sku == null && spu == null) {
            throw new IllegalArgumentException("必须传入产品信息或者验证信息");
        }

        picListViewPager = (ViewPager) findViewById(R.id.viewPagerCover);

        supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        maskableManager = new MaskableManager(findViewById(R.id.main), this);
        supportActionBar.setTitle(spu.getName());
        ((ShareButton) findViewById(R.id.shareButton)).setSPU(spu);
        ((CommentButton) findViewById(R.id.commentButton)).setSPU(spu);
        ((NearbyButton) findViewById(R.id.nearbyButton)).setSPU(spu);
        ((FavorButton) findViewById(R.id.favorButton)).setup(spu, LOGIN_ACTION);
        RatingBar rb = (RatingBar) findViewById(R.id.productRatingBar);
        rb.setRating(spu.getRating());

        ImageView imageViewVerified = (ImageView) findViewById(R.id.imageViewVerified);
        if (sku == null) {
            imageViewVerified.setVisibility(View.GONE);
        } else {
            if (verificationMethod == AuthenticationActivity.VerificationMethod.QR) {
                // 二维码可以复制，所以无法证明是否为真品， 只能比对验证码
                findViewById(R.id.checksumLayout).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.textViewChecksum)).setText(getString(R.string.verify_number,
                        sku.getChecksum()));
                imageViewVerified.setVisibility(View.GONE);
            } else {
                MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.good);
                mediaPlayer.setLooping(false);
                mediaPlayer.start();
            }
        }

        List<String> urls = new ArrayList<>();
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


    private void retrieveExtra() {
        sku = getIntent().getParcelableExtra(AuthenticationActivity.TAG_SKU);
        verificationMethod =
                (AuthenticationActivity.VerificationMethod) getIntent().getSerializableExtra(AuthenticationActivity.TAG_VERIFICATION_METHOD);
        if (sku != null) {
            spu = sku.getSPU();
        } else {
            spu = getIntent().getParcelableExtra(Const.TAG_SPU);
            if (BuildConfig.DEBUG) {
                if (spu == null) {
                    spu = FakeUtil.getInstance().spu();
                }
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
//        if (sku == null) {
//            imageView.setVisibility(View.GONE);
//        } else {
//            if (!verificationFinished) {
//                // 二维码验证不提示真品伪品
//                findViewById(R.id.checksumLayout).setVisibility(View.VISIBLE);
//                ((TextView) findViewById(R.id.textViewChecksum)).setText(getString(R.string.verify_number, sku.getSKU().getChecksum()));
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




    @Override
    public void onLoginDone(LoginRequired.Runnable runnable) {
        loginHandler.onLoginDone(runnable);
    }
}
