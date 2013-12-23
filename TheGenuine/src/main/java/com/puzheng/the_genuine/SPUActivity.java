package com.puzheng.the_genuine;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.puzheng.the_genuine.data_structure.SPUResponse;
import com.puzheng.the_genuine.data_structure.VerificationInfo;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.utils.Misc;
import com.puzheng.the_genuine.utils.PoliteBackgroundTask;
import com.puzheng.the_genuine.views.NavBar;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SPUActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, TabHost.OnTabChangeListener, RefreshInterface {

    private static final String TAG = "SPUActivity";
    private ViewPager viewPager;
    private VerificationInfo verificationInfo;
    private SPUResponse spuResponse;
    private ViewPager viewPagerCover;
    private TabHost tabHost;
    private int spu_id;
    private MaskableManager maskableManager;

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
                color = getResources().getColor(R.color.highlighted_tab);
            }
            TextView title = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            title.setTextColor(color);
        }
        viewPager.setCurrentItem(pos);
    }

    @Override
    public void refresh() {
        new GetSPUTask().execute(spu_id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == MyApp.LOGIN_ACTION) {
                doAddFavor();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spu);
        verificationInfo = getIntent().getParcelableExtra(MainActivity.TAG_VERIFICATION_INFO);
        spu_id = getIntent().getIntExtra(Constants.TAG_SPU_ID, -1);

        if (verificationInfo == null && spu_id == -1) {
            throw new IllegalArgumentException("必须传入产品信息或者验证信息");
        }
        if (verificationInfo != null) {
            initViews();
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.good);
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
        } else {
            maskableManager = new MaskableManager(findViewById(R.id.main), this);
            new GetSPUTask().execute(spu_id);
        }

        NavBar navBar = (NavBar) findViewById(R.id.navBar);
        navBar.setContext(SPUActivity.this);
    }

    private void initViews() {
        setupActionBar();
        shareInit();
        favorInit();

        if (verificationInfo == null) {
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setVisibility(View.GONE);
        }


        RatingBar rb = (RatingBar) findViewById(R.id.productRatingBar);
        rb.setRating(getRating());

        Button button = (Button) findViewById(R.id.buttonComment);

        button.setText("评论\n(" + Misc.humanizeNum(getCommentsCnt()) + ")");
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SPUActivity.this, CommentsActivity.class);
                intent.putExtra(Constants.TAG_SPU_ID, getSPUId());
                startActivity(intent);
            }
        });


        viewPagerCover = (ViewPager) findViewById(R.id.viewPagerCover);
        viewPagerCover.setAdapter(new MyCoverAdapter(getSupportFragmentManager(), getPicUrlList()));
        CirclePageIndicator titleIndicator = (CirclePageIndicator) findViewById(R.id.titles);
        titleIndicator.setViewPager(viewPagerCover);


        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        String s = "验证信息";
        if (spuResponse != null) {
            s = "产品信息";
        }
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tab1").setIndicator(s);
        tabSpec.setContent(new MyTabFactory(this));
        tabHost.addTab(tabSpec);

        s = "同类推荐(" + Misc.humanizeNum(getNearbyRecommendationsCnt()) + ")";
        tabSpec = tabHost.newTabSpec("tab2").setIndicator(s);
        tabSpec.setContent(new MyTabFactory(this));
        tabHost.addTab(tabSpec);

        s = "同厂推荐(" + Misc.humanizeNum(getSameVendorRecommendationsCnt()) + ")";
        tabSpec = tabHost.newTabSpec("tab3").setIndicator(s);
        tabSpec.setContent(new MyTabFactory(this));
        tabHost.addTab(tabSpec);
        tabHost.setOnTabChangedListener(this);


        setBottomTabs();
        viewPager = (ViewPager) findViewById(R.id.viewPagerBottom);
        viewPager.setAdapter(new MyPageAdapter(getSupportFragmentManager()));
        viewPager.setOnPageChangeListener(this);
    }

    private void doAddFavor() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SPUActivity.this);
        builder.setTitle("收藏");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PoliteBackgroundTask.Builder<Boolean> task = new PoliteBackgroundTask.Builder<Boolean>(SPUActivity.this);
                task.msg("正在收藏");
                task.run(new PoliteBackgroundTask.XRunnable<Boolean>() {
                    @Override
                    public Boolean run() throws Exception {
                        try {
                            return WebService.getInstance(SPUActivity.this).addFavor(getSPUId());
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                }).after(new PoliteBackgroundTask.OnAfter<Boolean>() {
                    @Override
                    public void onAfter(Boolean aBoolean) {
                        if (aBoolean) {
                            Toast.makeText(SPUActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SPUActivity.this, "收藏失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).create().start();
            }
        }).setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }

    private void favorInit() {
        ImageButton button = (ImageButton) findViewById(R.id.imageButtonFavor);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApp.getCurrentUser() == null) {
                    MyApp.doLoginIn(SPUActivity.this);
                } else {
                    doAddFavor();
                }

            }
        });
    }

    private int getCommentsCnt() {
        return verificationInfo != null ? verificationInfo.getCommentsCnt() : spuResponse.getCommentsCnt();
    }

    private int getNearbyRecommendationsCnt() {
        return verificationInfo != null ? verificationInfo.getNearbyRecommendationsCnt() : spuResponse.getNearbyRecommendationsCnt();
    }

    private List<String> getPicUrlList() {
        return verificationInfo != null ? verificationInfo.getSKU().getSPU().getPicUrlList() : spuResponse.getSPU().getPicUrlList();
    }

    private int getSPUId() {
        return verificationInfo != null ? verificationInfo.getSKU().getSPU().getId() : spuResponse.getSPU().getId();
    }

    private float getRating() {
        return verificationInfo != null ? verificationInfo.getSKU().getSPU().getRating() : spuResponse.getSPU().getRating();
    }

    private int getSameVendorRecommendationsCnt() {
        return verificationInfo != null ? verificationInfo.getSameVendorRecommendationsCnt() : spuResponse.getSameVendorRecommendationsCnt();
    }

    private int getVendorId() {
        return verificationInfo != null ? verificationInfo.getSKU().getSPU().getVendorId() :
                spuResponse.getSPU().getVendorId();
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

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            if (verificationInfo != null) {
                getActionBar().setTitle(verificationInfo.getSKU().getSPU().getName());
            } else {
                getActionBar().setTitle(spuResponse.getSPU().getName());
            }
        }
    }

    private void shareInit() {
        final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share",
                RequestType.SOCIAL);

        mController.setShareContent("360真品鉴别让您不再上当, http://www.foo.com");
    /*
            mController.setShareMedia(new UMImage(this,
                    "http://www.umeng.com/images/pic/banner_module_social.png"));
    */
        mController.getConfig().removePlatform(SHARE_MEDIA.EMAIL, SHARE_MEDIA.DOUBAN, SHARE_MEDIA.RENREN);
        String appID = "wx061490cf3011fbd0";
        // 微信图文分享必须设置一个url
        String contentUrl = "http://www.umeng.com/social";
        // 添加微信平台，参数1为当前Activity, 参数2为用户申请的AppID, 参数3为点击分享内容跳转到的目标url
        mController.getConfig().supportWXPlatform(this, appID, contentUrl);
        // 支持微信朋友圈
        mController.getConfig().supportWXCirclePlatform(this, appID, contentUrl);

        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButtonShare);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打开平台选择面板，参数2为打开分享面板时是否强制登录,false为不强制登录
                mController.openShare(SPUActivity.this, false);
            }
        });
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
            if (verificationInfo != null) {
                fragments.add(new VerificationInfoFragment(verificationInfo));
            } else {
                fragments.add(new SPUFragment(spuResponse.getSPU()));
            }
            fragments.add(RecommendationsFragment.createNearByProductsFragment(getSPUId()));
            fragments.add(RecommendationsFragment.createSameVendorProductsFragment( getSPUId()));
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

    class MyCoverAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public MyCoverAdapter(FragmentManager fm, List<String> picUrlList) {
            super(fm);
            fragments = new ArrayList<Fragment>();
            for (String url : picUrlList) {
                fragments.add(new CoverFragment(SPUActivity.this, url));
                Log.d(TAG, url);
            }
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }
    }

    private class GetSPUTask extends AsyncTask<Integer, Void, SPUResponse> {

        private Exception exception;
        @Override
        protected void onPreExecute() {
            maskableManager.mask();
        }

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
    }
}
