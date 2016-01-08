package com.puzheng.the_genuine;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import com.puzheng.the_genuine.data_structure.SPU;
import com.puzheng.the_genuine.data_structure.SPUResponse;
import com.puzheng.the_genuine.data_structure.VerificationInfo;
import com.puzheng.the_genuine.image_utils.ImageFetcher;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.utils.BadResponseException;
import com.puzheng.the_genuine.utils.HttpUtil;
import com.puzheng.the_genuine.utils.Misc;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.*;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.viewpagerindicator.CirclePageIndicator;
import org.stringtemplate.v4.ST;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class SPUActivity extends FragmentActivity implements ViewPager.OnPageChangeListener,
        TabHost.OnTabChangeListener, RefreshInterface, ImageFetcherInteface {

    private static final String TAG = "SPUActivity";
    private ViewPager viewPager;
    private VerificationInfo verificationInfo;

    //只有二维码验证才需要展示 验证码
    private boolean verificationFinished;

    private SPUResponse spuResponse;
    private ViewPager viewPagerCover;
    private TabHost tabHost;
    private int spu_id;
    private MaskableManager maskableManager;
    private FavorTask mTask;
    private MyCoverAdapter mAdapter;
    private ImageFetcher mImageFetcher;
    private UMSocialService mController;

    public ImageFetcher getImageFetcher() {
        return mImageFetcher;
    }

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
        new GetSPUTask().execute(spu_id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
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
        verificationInfo = getIntent().getParcelableExtra(MainActivity.TAG_VERIFICATION_INFO);
        verificationFinished = getIntent().getBooleanExtra(MainActivity.TAG_VERIFICATION_FINISHED, false);
        spu_id = getIntent().getIntExtra(Constants.TAG_SPU_ID, -1);

        if (verificationInfo == null && spu_id == -1) {
            throw new IllegalArgumentException("必须传入产品信息或者验证信息");
        }

        viewPagerCover = (ViewPager) findViewById(R.id.viewPagerCover);

        Point point = new Point();
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        display.getSize(point);

        mImageFetcher = ImageFetcher.getImageFetcher(this, point.x, point.y / 2, 0.25f);
        mController = UMServiceFactory.getUMSocialService("com.umeng.share",
                RequestType.SOCIAL);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle();
        if (verificationInfo != null) {
            initViews();
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.good);
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
        } else {
            maskableManager = new MaskableManager(findViewById(R.id.main), this);
            new GetSPUTask().execute(spu_id);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void doAddFavor() {
        if (mTask == null) {
            mTask = new FavorTask();
            mTask.execute();
        }
    }

    private int getCommentsCnt() {
        return verificationInfo != null ? verificationInfo.getCommentsCnt() : spuResponse.getCommentsCnt();
    }

    private int getDistance() {
        return verificationInfo != null ? verificationInfo.getDistance() : spuResponse.getDistance();
    }

    private List<String> getPicUrlList() {
        return verificationInfo != null ? verificationInfo.getSKU().getSPU().getPicUrlList() : spuResponse.getSPU().getPicUrlList();
    }

    private float getRating() {
        return verificationInfo != null ? verificationInfo.getSKU().getSPU().getRating() : spuResponse.getSPU().getRating();
    }

    private int getSPUId() {
        return verificationInfo != null ? verificationInfo.getSKU().getSPU().getId() : spuResponse.getSPU().getId();
    }

    private int getSameTypeRecommendationsCnt() {
        return verificationInfo != null ? verificationInfo.getSameTypeRecommendationsCnt() : spuResponse.getSameTypeRecommendationsCnt();
    }

    private int getSameVendorRecommendationsCnt() {
        return verificationInfo != null ? verificationInfo.getSameVendorRecommendationsCnt() : spuResponse.getSameVendorRecommendationsCnt();
    }

    private SPU getSPU() {
        return verificationInfo != null ? verificationInfo.getSKU().getSPU() : spuResponse.getSPU();
    }

    private int getVendorId() {
        return verificationInfo != null ? verificationInfo.getSKU().getSPU().getVendorId() :
                spuResponse.getSPU().getVendorId();
    }

    private void initViews() {
        setTitle();
        shareInit();
        locate2Nearby();
        updateFavorView(isFavored());
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        if (verificationInfo == null) {
            imageView.setVisibility(View.GONE);
        } else {
            if (!verificationFinished) {
                // 二维码验证不提示真品伪品
                findViewById(R.id.checksumLayout).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.textViewChecksum)).setText(getString(R.string.verify_number, verificationInfo.getSKU().getChecksum()));
                imageView.setVisibility(View.GONE);
            }
        }


        RatingBar rb = (RatingBar) findViewById(R.id.productRatingBar);
        rb.setRating(getRating());

        Button button = (Button) findViewById(R.id.buttonComment);

        button.setText(getString(R.string.comment_number,  Misc.humanizeNum(getCommentsCnt(), SPUActivity.this)));
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SPUActivity.this, CommentsActivity.class);
                intent.putExtra(Constants.TAG_SPU_ID, getSPUId());
                startActivity(intent);
            }
        });


        mAdapter = new MyCoverAdapter(getSupportFragmentManager(), getPicUrlList());
        viewPagerCover.setAdapter(mAdapter);
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
        s = getString(R.string.sameType, Misc.humanizeNum(getSameTypeRecommendationsCnt(), SPUActivity.this));
        tabSpec = tabHost.newTabSpec("tab2").setIndicator(s);
        tabSpec.setContent(new MyTabFactory(this));
        tabHost.addTab(tabSpec);
        s = getString(R.string.sameVendor, Misc.humanizeNum(getSameVendorRecommendationsCnt(), SPUActivity.this));
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

    private boolean isFavored() {
        return verificationInfo != null ? verificationInfo.isFavored() : spuResponse.isFavored();
    }

    private void setFavored(boolean isFavord) {
        if (verificationInfo != null) {
            verificationInfo.setFavored(isFavord);
        } else {
            spuResponse.setFavored(isFavord);
        }
    }

    private void locate2Nearby() {
        Button button = (Button) findViewById(R.id.buttonNearby);
        final int distance = getDistance();
        if (distance == -1) {
            button.setText(getString(R.string.nearest, ""));
            button.setClickable(false);
        } else {
            button.setText(getString(R.string.nearest, Misc.humanizeDistance(distance, SPUActivity.this)));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SPUActivity.this, NearbyActivity.class);
                    intent.putExtra("current", NearbyActivity.NEARBY_LIST);
                    intent.putExtra(Constants.TAG_SPU_ID, getSPUId());
                    SPUActivity.this.startActivity(intent);
                }
            });
        }
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

    private void setTitle() {
        String spu_name = null;
        if (verificationInfo != null) {
            spu_name = verificationInfo.getSKU().getSPU().getName();
        } else if (spuResponse != null) {
            spu_name = spuResponse.getSPU().getName();
        }
        if (TextUtils.isEmpty(spu_name)) {
            spu_name = getIntent().getStringExtra(Constants.TAG_SPU_NAME);
        }
        getActionBar().setTitle(spu_name);
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

    private void shareInit() {
        String contentUrl = getShareURL();
        List<String> picUrlList = getPicUrlList();
        if (MyApp.SHAREMEDIA && picUrlList != null && picUrlList.size() > 1) {
            try {
                mController.setShareMedia(new UMImage(SPUActivity.this, HttpUtil.getURL(picUrlList.get(0)).toString()));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }


        mController.setShareContent(getShareContent(contentUrl));

        mController.getConfig().removePlatform(SHARE_MEDIA.EMAIL, SHARE_MEDIA.DOUBAN, SHARE_MEDIA.RENREN);
        String appID = getString(R.string.weichat_app_id);
        // 微信图文分享必须设置一个url
        // 添加微信平台，参数1为当前Activity, 参数2为用户申请的AppID, 参数3为点击分享内容跳转到的目标url
        UMWXHandler wxHandler = mController.getConfig().supportWXPlatform(this, appID, contentUrl);
        wxHandler.setWXTitle(getString(R.string.share_title));
        // 支持微信朋友圈
        mController.getConfig().supportWXCirclePlatform(this, appID, contentUrl);

        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButtonShare);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打开平台选择面板，参数2为打开分享面板时是否强制登录,false为不强制登录
                mController.openShare(SPUActivity.this, false);
            }
        });
    }

    private void updateFavorView(final boolean isFavored) {
        ImageButton button = (ImageButton) findViewById(R.id.imageButtonFavor);
        if (isFavored) {
            button.setImageResource(R.drawable.ic_action_important);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SPUActivity.this, getString(R.string.favored), Toast.LENGTH_SHORT).show();
                    setFavored(isFavored);
                }
            });
        } else {
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
                updateFavorView(true);
            } else {
                if (exception instanceof BadResponseException) {
                    if (((BadResponseException) exception).getStatusCode() == 403) {
                        updateFavorView(true);
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
            if (verificationInfo != null) {
                fragments.add(new VerificationInfoFragment().setVerificationInfo(verificationInfo));
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

    class MyCoverAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public MyCoverAdapter(FragmentManager fm, List<String> picUrlList) {
            super(fm);
            fragments = new ArrayList<Fragment>();
            for (String url : picUrlList) {
                CoverFragment coverFragment = new CoverFragment();
                coverFragment.setUrl(url);
                coverFragment.setmImageFetcherInteface(SPUActivity.this);
                fragments.add(coverFragment);
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
