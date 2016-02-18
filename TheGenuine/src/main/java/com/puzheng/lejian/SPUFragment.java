package com.puzheng.lejian;

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

import com.puzheng.lejian.model.SPU;
import com.umeng.socialize.bean.UMFriend;

import java.util.List;

/**
 * Created by xc on 13-12-4.
 */
public class SPUFragment extends Fragment {
    private SPU spu;

//    private UMSocialService umSocialService;
    private View rootView;

    public SPUFragment() {

    }

    public void setSPU(SPU spu) {
        rootView = getView();
        this.spu = spu;
        TextView textView = (TextView) rootView.findViewById(R.id.textViewCode);
        textView.setText(spu.getCode());
        textView = (TextView) rootView.findViewById(R.id.textViewName);
        textView.setText(spu.getName());
        textView = (TextView) rootView.findViewById(R.id.textViewVendorName);
        textView.setText(spu.getVendor().getName());
        textView = (TextView) rootView.findViewById(R.id.textViewVendorAddress);
        textView.setText(spu.getVendor().getAddr());

        textView = (TextView) rootView.findViewById(R.id.textViewVendorWebsite);
        final String website = spu.getVendor().getWebsite();
        textView.setText(spu.getVendor().getWebsite());
        if (!TextUtils.isEmpty(website)) {
            textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
            textView.setTextColor(Color.BLUE);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                    getActivity().startActivity(Intent.createChooser(browserIntent, getString(R.string.choose_browser)));
                }
            });
        }

        final String telephone = spu.getVendor().getTel();
        textView = (TextView) rootView.findViewById(R.id.textViewVendorTel);
        textView.setText(telephone);
        if (!TextUtils.isEmpty(telephone)) {
            rootView.findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telephone));
                    startActivity(callIntent);
                }
            });
        }

        final String weixinAccount = spu.getVendor().getWeixinAccount();
        textView = (TextView) rootView.findViewById(R.id.textViewWeixinAccount);
        textView.setText(weixinAccount);

        textView = (TextView) rootView.findViewById(R.id.textViewWeiboHomepage);
        final String weiboHomepage = spu.getVendor().getWeiboHomepage();
        textView.setText(weiboHomepage);
        if (!TextUtils.isEmpty(weiboHomepage)) {
            textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
            textView.setTextColor(Color.BLUE);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(weiboHomepage));
                    getActivity().startActivity(Intent.createChooser(browserIntent, getString(R.string.choose_browser)));
                }
            });
        }

//        getFriendsAndUpdateView(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        umSocialService = UMServiceFactory.getUMSocialService("com.umeng.login",
//                RequestType.SOCIAL);
//
//        if (umSocialService.getConfig().getSinaSsoHandler() == null) {
//            umSocialService.getConfig().setSsoHandler(new SinaSsoHandler());
//        }

        SPU spu = getArguments().getParcelable(Const.TAG_SPU);
        if (spu != null) {
            setSPU(spu);
        }
    }

    private void authVerify() {
////        umSocialService.doOauthVerify(getActivity(), SHARE_MEDIA.SINA, new SocializeListeners.UMAuthListener() {
//            @Override
//            public void onCancel(SHARE_MEDIA platform) {
//            }
//
//            @Override
//            public void onComplete(Bundle value, SHARE_MEDIA platform) {
//                if (value != null && !TextUtils.isEmpty(value.getString("uid"))) {
//                    Toast.makeText(getActivity(), getString(R.string.binding_succeed), Toast.LENGTH_SHORT).show();
//                    getFriendsAndUpdateView(true);
//                } else {
//                    Toast.makeText(getActivity(), getString(R.string.binding_failed), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onError(SocializeException e, SHARE_MEDIA platform) {
//            }
//
//            @Override
//            public void onStart(SHARE_MEDIA platform) {
//            }
//        });
    }

    private void follow(String weibo) {
//        umSocialService.follow(getActivity(), SHARE_MEDIA.SINA, new SocializeListeners.MulStatusListener() {
//            @Override
//            public void onComplete(MultiStatus multiStatus, int i, SocializeEntity socializeEntity) {
//                if (i == 200) {
//                    Toast.makeText(getActivity(), getString(R.string.watch_succeed), Toast.LENGTH_SHORT).show();
//                    updateFollowedView(true);
//                } else {
//                    Toast.makeText(getActivity(), getString(R.string.watch_failed), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onStart() {
//
//            }
//        }, weibo);
    }

    private void getFriendsAndUpdateView(final boolean doFollow) {
//        if (OauthHelper.isAuthenticated(getActivity(), SHARE_MEDIA.SINA)) {
//            umSocialService.getFriends(getActivity(), new SocializeListeners.FetchFriendsListener() {
//                @Override
//                public void onStart() {
//
//                }
//
//                @Override
//                public void onComplete(int i, List<UMFriend> umFriends) {
//                    if (i == 200 && umFriends != null) {
//                        final boolean followed = isFollowed(umFriends);
//                        if (!followed && doFollow) {
//                            follow(getWeiboAccount());
//                        } else {
//                            updateFollowedView(followed);
//                        }
//
//                    }
//                }
//            }, SHARE_MEDIA.SINA);
//        } else {
//            updateFollowedView(false);
//        }
    }

    private String getWeiboAccount() {
        return spu.getVendor().getWeiboUserId();
    }

    private boolean isFollowed(List<UMFriend> umFriends) {
        if (umFriends != null) {
            final String weiboAccount = getWeiboAccount();
            for (UMFriend umFriend : umFriends) {
                if (umFriend.getFid().equals(weiboAccount)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void updateFollowedView(boolean isFollowed) {
//        final String weibo = getWeiboAccount();
//        Button addFocus = (Button) rootView.findViewById(R.id.btnAddFocus);
//        rootView.findViewById(R.id.layoutAddFocus).setOnClickListener(null);
//        if (!TextUtils.isEmpty(weibo)) {
//            addFocus.setVisibility(View.VISIBLE);
//            if (isFollowed) {
//                addFocus.setText(R.string.watched);
//                addFocus.setOnClickListener(null);
//            } else {
//                addFocus.setText(R.string.watch);
//                addFocus.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (!OauthHelper.isAuthenticated(getActivity(), SHARE_MEDIA.SINA)) {
//                            authVerify();
//                        } else {
//                            follow(weibo);
//                        }
//
//                    }
//                });
//
//            }
//        } else {
//            addFocus.setVisibility(View.GONE);
//        }
    }
}
