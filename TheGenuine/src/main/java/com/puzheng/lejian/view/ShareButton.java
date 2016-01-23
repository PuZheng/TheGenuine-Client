package com.puzheng.lejian.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import com.puzheng.lejian.model.SPU;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.UMWXHandler;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.SinaSsoHandler;

public class ShareButton extends ImageButton {


    public ShareButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShareButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setSPU(SPU spu) {
        UMSocialService umSocialService = UMServiceFactory.getUMSocialService("com.umeng.share",
                RequestType.SOCIAL);
//        String contentUrl = getShareURL();
//        if (MyApp.SHAREMEDIA) {
//            umSocialService.setShareMedia(new UMImage(getContext(), spu.getIcon().getURL()));
//        }
//
//        umSocialService.setShareContent(getShareContent(contentUrl));
//
//        umSocialService.getConfig().removePlatform(SHARE_MEDIA.EMAIL,
//                SHARE_MEDIA.DOUBAN,
//                SHARE_MEDIA.RENREN);
//        String appID = getString(R.string.weichat_app_id);
//        // 微信图文分享必须设置一个url
//        // 添加微信平台，参数1为当前Activity, 参数2为用户申请的AppID, 参数3为点击分享内容跳转到的目标url
//        UMWXHandler wxHandler = umSocialService.getConfig().supportWXPlatform(this,
//                appID,
//                contentUrl);
//        wxHandler.setWXTitle(getString(R.string.share_title));
//        // 支持微信朋友圈
//        umSocialService.getConfig().supportWXCirclePlatform(this, appID,
//                contentUrl);
//        umSocialService.getConfig().setSsoHandler(new SinaSsoHandler());
//        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButtonShare);
//        imageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 打开平台选择面板，参数2为打开分享面板时是否强制登录,false为不强制登录
//                umSocialService.openShare(getContext(), false);
//            }
//        });
    }
}
