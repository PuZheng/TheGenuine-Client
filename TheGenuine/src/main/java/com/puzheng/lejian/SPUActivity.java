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

import com.puzheng.humanize.Humanize;
import com.puzheng.lejian.adapter.SPUCoverAdapter;
import com.puzheng.lejian.model.SPU;
import com.puzheng.lejian.model.SPUResponse;
import com.puzheng.lejian.model.Verification;
import com.puzheng.lejian.image_utils.ImageFetcher;
import com.puzheng.lejian.netutils.WebService;
import com.puzheng.lejian.util.BadResponseException;
import com.puzheng.lejian.util.ConfigUtil;
import com.puzheng.lejian.view.FavorButton;
import com.puzheng.lejian.view.NearbyButton;
import com.puzheng.lejian.view.ShareButton;
import com.umeng.socialize.controller.*;
import com.viewpagerindicator.CirclePageIndicator;
import org.stringtemplate.v4.ST;

import java.util.ArrayList;
import java.util.List;

public class SPUActivity extends FragmentActivity implements ViewPager.OnPageChangeListener,
        TabHost.OnTabChangeListener, RefreshInterface {

    private static final String TAG = "SPUActivity";
    private ViewPager viewPager;
    private Verification verification;

    //只有二维码验证才需要展示 验证码
    private boolean verificationFinished;

    private SPUResponse spuResponse;
    private ViewPager viewPagerCover;
    private TabHost tabHost;
    private SPU spu;
    private MaskableManager maskableManager;
    private FavorTask mTask;
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
    public void onTabChanged(String s) {
        int pos = tabHost.getCurrentTab();
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); ++i) {
            int color = getResources().getColor(android.R.color.darker_gray);
            if (i == pos) {
                color = getResources().getColor(R.color.base_color1);
            }
            TextView title = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            title.setTextColor(color);
        }
        viewPager.setCurrentItem(pos);
    }

    @Override
    public void refresh() {
        //new GetSPUTask().execute(spu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        UMSsoHandler ssoHandler = umSocialService.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == MyApp.LOGIN_ACTION) {
                doAddFavor();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spu);

        retrieveExtra();


        if (verification == null && spu == null) {
            throw new IllegalArgumentException("必须传入产品信息或者验证信息");
        }

        viewPagerCover = (ViewPager) findViewById(R.id.viewPagerCover);

        Point point = new Point();
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        display.getSize(point);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        if (verification != null) {
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
            if (verification == null) {
                imageView.setVisibility(View.GONE);
            } else {
                if (!verificationFinished) {
                    // 二维码验证不提示真品伪品
                    findViewById(R.id.checksumLayout).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.textViewChecksum)).setText(getString(R.string.verify_number, verification.getSKU().getChecksum()));
                    imageView.setVisibility(View.GONE);
                }
            }

            List<String> urls = new ArrayList<String>();
            for (SPU.Pic pic: spu.getPics()) {
                urls.add(pic.getURL());
            }
            adapter = new SPUCoverAdapter(getSupportFragmentManager(), urls);
            viewPagerCover.setAdapter(adapter);
            CirclePageIndicator titleIndicator = (CirclePageIndicator) findViewById(R.id.titles);
            titleIndicator.setViewPager(viewPagerCover);

        }

    }


    private void retrieveExtra() {
        verification = getIntent().getParcelableExtra(MainActivity.TAG_VERIFICATION_INFO);
        verificationFinished = getIntent().getBooleanExtra(MainActivity.TAG_VERIFICATION_FINISHED, false);
        spu = getIntent().getParcelableExtra(Const.TAG_SPU);
        if (BuildConfig.DEBUG) {
            if (spu == null) {
                List<SPU.Pic> pics = new ArrayList<SPU.Pic>();
                pics.add(new SPU.Pic("",
                        Uri.parse(ConfigUtil.getInstance().getBackend())
                                .buildUpon().path("assets/sample1.png").build().toString()));
                pics.add(new SPU.Pic("",
                        Uri.parse(ConfigUtil.getInstance().getBackend())
                                .buildUpon().path("assets/sample2.png").build().toString()));
                pics.add(new SPU.Pic("",
                        Uri.parse(ConfigUtil.getInstance().getBackend())
                                .buildUpon().path("assets/sample3.png").build().toString()));

                spu = new SPU.Builder().id(1).distance(1200).favored(true)
                        .commentCnt(1238).rating(4.3f).name("foo spu")
                        .pics(pics).build();
            }
        }
    }


    private void doAddFavor() {
        if (mTask == null) {
            mTask = new FavorTask();
            mTask.execute();
        }
    }

    private int getCommentsCnt() {
        return verification != null ? verification.getCommentsCnt() : spuResponse.getCommentsCnt();
    }

    private int getDistance() {
        return verification != null ? verification.getDistance() : spuResponse.getDistance();
    }

    private List<SPU.Pic> getPics() {
        return verification != null ? verification.getSKU().getSPU().getPics() : spuResponse.getSPU().getPics();
    }

    private float getRating() {
        return verification != null ? verification.getSKU().getSPU().getRating() : spuResponse.getSPU().getRating();
    }

    private int getSPUId() {
        return verification != null ? verification.getSKU().getSPU().getId() : spuResponse.getSPU().getId();
    }

    private int getSameTypeRecommendationsCnt() {
        return verification != null ? verification.getSameTypeRecommendationsCnt() : spuResponse.getSameTypeRecommendationsCnt();
    }

    private int getSameVendorRecommendationsCnt() {
        return verification != null ? verification.getSameVendorRecommendationsCnt() : spuResponse.getSameVendorRecommendationsCnt();
    }

    private SPU getSPU() {
        return verification != null ? verification.getSKU().getSPU() : spuResponse.getSPU();
    }

    private int getVendorId() {
        return verification != null ? verification.getSKU().getSPU().getVendorId() :
                spuResponse.getSPU().getVendorId();
    }

    private void initViews() {
//        setTitle();
//        initShare();
//        setupNearbyButton();
//        updateFavorView(isFavored());
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        if (verification == null) {
            imageView.setVisibility(View.GONE);
        } else {
            if (!verificationFinished) {
                // 二维码验证不提示真品伪品
                findViewById(R.id.checksumLayout).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.textViewChecksum)).setText(getString(R.string.verify_number, verification.getSKU().getChecksum()));
                imageView.setVisibility(View.GONE);
            }
        }


        RatingBar rb = (RatingBar) findViewById(R.id.productRatingBar);
        rb.setRating(getRating());



        List<String> urls = new ArrayList<String>();
        for (SPU.Pic pic: getPics()) {
            urls.add(pic.getURL());
        }
        adapter = new SPUCoverAdapter(getSupportFragmentManager(), urls);
        viewPagerCover.setAdapter(adapter);
        CirclePageIndicator titleIndicator = (CirclePageIndicator) findViewById(R.id.titles);
        titleIndicator.setViewPager(viewPagerCover);


        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        String s = getString(R.string.verify_info);
        if (spuResponse != null) {
            s = getString(R.string.product_info);
        }
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tab1").setIndicator(s);
        tabSpec.setContent(new MyTabFactory(this));
        tabHost.addTab(tabSpec);
        s = getString(R.string.sameType,
                Humanize.with(this).num(getSameTypeRecommendationsCnt()));
        tabSpec = tabHost.newTabSpec("tab2").setIndicator(s);
        tabSpec.setContent(new MyTabFactory(this));
        tabHost.addTab(tabSpec);
        s = getString(R.string.sameVendor,
                Humanize.with(this).num(getSameVendorRecommendationsCnt()));
        tabSpec = tabHost.newTabSpec("tab3").setIndicator(s);
        tabSpec.setContent(new MyTabFactory(this));
        tabHost.addTab(tabSpec);
        tabHost.setOnTabChangedListener(this);

        TabWidget tabWidget = tabHost.getTabWidget();
        int tabCounts = tabHost.getTabWidget().getTabCount();

        for (int i = 0; i < tabCounts; i++) {
            TextView textView = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
            textView.setAllCaps(false);
        }

        setBottomTabs();
        viewPager = (ViewPager) findViewById(R.id.viewPagerBottom);
        viewPager.setAdapter(new MyPageAdapter(getSupportFragmentManager()));
        viewPager.setOnPageChangeListener(this);
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


    private class FavorTask extends AsyncTask<Void, Void, Void> {
        private Exception exception;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                WebService.getInstance(SPUActivity.this).addFavor(getSPUId());
            } catch (Exception e) {
                exception = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (exception == null) {
                Toast.makeText(SPUActivity.this, getString(R.string.favor_succeed), Toast.LENGTH_SHORT).show();
//                updateFavorView(true);
            } else {
                if (exception instanceof BadResponseException) {
                    if (((BadResponseException) exception).getStatusCode() == 403) {
//                        updateFavorView(true);
                    }
                    Toast.makeText(SPUActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SPUActivity.this, getString(R.string.favor_failed), Toast.LENGTH_SHORT).show();
                }
            }
            mTask = null;
        }
    }

    class MyTabFactory implements TabHost.TabContentFactory {

        private final Context mContext;

        public MyTabFactory(Context context) {
            mContext = context;
        }

        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
    }

    class MyPageAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public MyPageAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<Fragment>();
            if (verification != null) {
                fragments.add(new VerificationInfoFragment().setVerificationInfo(verification));
            } else {
                fragments.add(new SPUFragment().setSPU(spuResponse.getSPU()));
            }
            fragments.add(RecommendationsFragment.createSameTypeProductsFragment(getSPUId()));
            fragments.add(RecommendationsFragment.createSameVendorProductsFragment(getSPUId()));
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }
    }

    private class GetSPUTask extends AsyncTask<Integer, Void, SPUResponse> {

        private Exception exception;

        @Override
        protected SPUResponse doInBackground(Integer... params) {
            try {
                return WebService.getInstance(SPUActivity.this).getSPUResponse(params[0]);
            } catch (Exception e) {
                exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(SPUResponse spuResponse) {
            if (maskableManager.unmask(exception)) {
                SPUActivity.this.spuResponse = spuResponse;
                SPUActivity.this.initViews();
            }
        }

        @Override
        protected void onPreExecute() {
            maskableManager.mask();
        }
    }
}
