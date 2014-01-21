package com.puzheng.the_genuine;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.puzheng.the_genuine.data_structure.SPU;
import com.umeng.socialize.bean.MultiStatus;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.UMFriend;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.db.OauthHelper;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.SinaSsoHandler;

import java.util.List;

/**
 * Created by xc on 13-12-4.
 */
public class SPUFragment extends Fragment {
    private final SPU spu;

    private UMSocialService mController;
    private View mView;

    public SPUFragment(SPU spu) {
        this.spu = spu;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mController = UMServiceFactory.getUMSocialService("com.umeng.login",
                RequestType.SOCIAL);

        if (mController.getConfig().getSinaSsoHandler() == null) {
            mController.getConfig().setSsoHandler(new SinaSsoHandler());
        }
        mView = inflater.inflate(R.layout.fragment_product, container, false);
        TextView textView = (TextView) mView.findViewById(R.id.textViewCode);
        textView.setText(spu.getCode());
        textView = (TextView) mView.findViewById(R.id.textViewName);
        textView.setText(spu.getName());
        textView = (TextView) mView.findViewById(R.id.textViewVendorName);
        textView.setText(spu.getVendorName());
        textView = (TextView) mView.findViewById(R.id.textViewVendorAddress);
        textView.setText(spu.getVendor().getAddress());

        textView = (TextView) mView.findViewById(R.id.textViewVendorWebsite);
        final String website = spu.getVendor().getWebsite();
        textView.setText(spu.getVendor().getWebsite());
        if (!TextUtils.isEmpty(website)) {
            textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
            textView.setTextColor(Color.BLUE);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                    getActivity().startActivity(Intent.createChooser(browserIntent, "选择浏览器"));
                }
            });
        }

        final String telephone = spu.getVendor().getTel();
        textView = (TextView) mView.findViewById(R.id.textViewVendorTel);
        textView.setText(telephone);
        if (!TextUtils.isEmpty(telephone)) {
            mView.findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telephone));
                    startActivity(callIntent);
                }
            });
        }

        final String weiXin = spu.getVendor().getWeixin();
        textView = (TextView) mView.findViewById(R.id.textViewWeixin);
        textView.setText(weiXin);

        final String weibo = getWeibo();
        textView = (TextView) mView.findViewById(R.id.textViewWeibo);
        textView.setText(weibo);

        getFriendsAndUpdateView(false);
        return mView;
    }

    private void authVerify() {
        mController.doOauthVerify(getActivity(), SHARE_MEDIA.SINA, new SocializeListeners.UMAuthListener() {
            @Override
            public void onCancel(SHARE_MEDIA platform) {
            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                if (value != null && !TextUtils.isEmpty(value.getString("uid"))) {
                    Toast.makeText(getActivity(), "授权成功.", Toast.LENGTH_SHORT).show();
                    getFriendsAndUpdateView(true);
                } else {
                    Toast.makeText(getActivity(), "授权失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
            }

            @Override
            public void onStart(SHARE_MEDIA platform) {
            }
        });
    }

    private void follow(String weibo) {
        mController.follow(getActivity(), SHARE_MEDIA.SINA, new SocializeListeners.MulStatusListener() {
            @Override
            public void onComplete(MultiStatus multiStatus, int i, SocializeEntity socializeEntity) {
                if (i == 200) {
                    Toast.makeText(getActivity(), "关注成功", Toast.LENGTH_SHORT).show();
                    updateFollowedView(true);
                } else {
                    Toast.makeText(getActivity(), "关注失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onStart() {

            }
        }, weibo);
    }

    private void getFriendsAndUpdateView(final boolean doFollow) {
        if (OauthHelper.isAuthenticated(getActivity(), SHARE_MEDIA.SINA)) {
            mController.getFriends(getActivity(), new SocializeListeners.FetchFriendsListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onComplete(int i, List<UMFriend> umFriends) {
                    if (i == 200 && umFriends != null) {
                        final boolean followed = isFollowed(umFriends);
                        if (!followed && doFollow) {
                            follow(getWeibo());
                        }
                        updateFollowedView(followed);
                    }
                }
            }, SHARE_MEDIA.SINA);
        } else {
            updateFollowedView(false);
        }
    }

    private String getWeibo() {
        return spu.getVendor().getWeibo();
    }

    private boolean isFollowed(List<UMFriend> umFriends) {
        if (umFriends != null) {
            final String weibo = getWeibo();
            for (UMFriend umFriend : umFriends) {
                if (umFriend.getFid().equals(weibo)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void updateFollowedView(boolean isFollowed) {
        final String weibo = getWeibo();
        TextView textView = (TextView) mView.findViewById(R.id.textViewAddFocus);
        mView.findViewById(R.id.layoutAddFocus).setOnClickListener(null);
        if (!TextUtils.isEmpty(weibo)) {
            if (isFollowed) {
                textView.setText("已关注");
                mView.findViewById(R.id.layoutAddFocus).setOnClickListener(null);
            } else {
                textView.setText("加关注");
                mView.findViewById(R.id.layoutAddFocus).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!OauthHelper.isAuthenticated(getActivity(), SHARE_MEDIA.SINA)) {
                            authVerify();
                        } else {
                            follow(weibo);
                        }

                    }
                });

            }
        } else {
            textView.setText("");
        }
    }
}
