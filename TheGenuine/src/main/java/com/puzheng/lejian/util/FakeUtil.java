package com.puzheng.lejian.util;

import android.net.Uri;

import com.puzheng.lejian.model.Pic;
import com.puzheng.lejian.model.SPU;
import com.puzheng.lejian.model.SPUType;
import com.puzheng.lejian.model.Vendor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.bloco.faker.Faker;

/**
 * Created by xc on 16-1-24.
 */
public class FakeUtil {

    private static volatile FakeUtil instance;
    private final Faker faker;
    private final Random random;

    private FakeUtil() {
        this.faker = new Faker();
        this.random = new Random();
    }

    public static synchronized FakeUtil getInstance() {
        if (instance == null) {
            instance = new FakeUtil();
        }
        return instance;
    }

    public SPU spu() {
        List<Pic> pics = new ArrayList<Pic>();
        for (int i = 0; i < 5; ++i) {
            pics.add(pic());
        }
        return new SPU.Builder()
                .id(random.nextInt())
                .name(faker.lorem.word())
                .code(faker.lorem.word())
                .spuType(spuType())
                .rating((float) faker.number.between(0.0, 5.0))
                .msrp(faker.number.between(1, 1000))
                .desc(faker.lorem.paragraph())
                .pics(pics)
                .icon(pic())
                .vendor(vendor())
                .distance(faker.number.positive())
                .favored(faker.number.between(0, 3) == 1)
                .commentCnt(faker.number.positive())
                .favorCnt(faker.number.positive()).build();
    }

    public SPUType spuType() {
        return new SPUType(random.nextInt(), "foo",
                random.nextInt(5), true, random.nextInt(), pic());
    }

    public Pic pic() {
        Uri.Builder builder = Uri.parse(ConfigUtil.getInstance().getBackend()).buildUpon();
        Pic[] pics = {
                new Pic("", builder.path("assets/sample1.png").toString()),
                new Pic("", builder.path("assets/sample2.png").toString()),
                new Pic("", builder.path("assets/sample3.png").toString()),
                new Pic("", builder.path("assets/sample4.png").toString()),
                new Pic("", builder.path("assets/sample5.png").toString()),
                new Pic("", builder.path("assets/sample6.png").toString()),
                new Pic("", builder.path("assets/sample7.png").toString()),
                new Pic("", builder.path("assets/sample8.png").toString())
        };

        return pics[random.nextInt(pics.length)];

    }

    public Vendor vendor() {
        return new Vendor.Builder()
                .id(random.nextInt())
                .addr(faker.address.streetAddress())
                .desc(faker.lorem.paragraph())
                .email(faker.internet.email())
                .enabled(true)
                .name(faker.company.name())
                .tel(faker.phoneNumber.phoneNumber())
                .website(faker.internet.url())
                .weiboHomepage(faker.internet.url())
                .weiboUserId(faker.name.firstName())
                .weixinAccount(faker.name.firstName())
                .build();
    }

}
