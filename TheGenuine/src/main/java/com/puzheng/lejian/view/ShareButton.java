package com.puzheng.lejian.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.puzheng.lejian.R;
import com.puzheng.lejian.model.SPU;
import com.puzheng.lejian.util.ConfigUtil;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import org.stringtemplate.v4.ST;

public class ShareButton extends ImageButton {


    private SPU spu;

    public ShareButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShareButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);


    }

    public void setSPU(SPU spu) {
        this.spu = spu;
        Logger.i(ConfigUtil.getInstance().getWechatAppId(), ConfigUtil.getInstance().getWechatAppSecret());
        PlatformConfig.setWeixin(ConfigUtil.getInstance().getWechatAppId(),
                ConfigUtil.getInstance().getWechatAppSecret());
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
                        {
                                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA,
                                SHARE_MEDIA.QQ,
                        };
                new ShareAction((Activity) getContext()).setDisplayList(displaylist)
                        .setListenerList(umShareListener, umShareListener)
                        .setShareboardclickCallback(shareBoardlistener)
                        .open();
            }
        });
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA share_media) {
            Toast.makeText(getContext(), "分享成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {

        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {

        }
    };

    private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {

        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
            new ShareAction((Activity) getContext()).setPlatform(share_media).setCallback(umShareListener)
                    .withText(getContext().getString(R.string.share_template))
                    .withMedia(new UMImage(getContext(), spu.getIcon().getURL()))
                    .withTargetUrl(genShareURL())
                    .share();
        }


    };

    private String genShareURL() {
        ST template = new ST(ConfigUtil.getInstance().getShareURLTemplate());
        template.add("spu", spu.getId());
        return template.render();
    }
}
