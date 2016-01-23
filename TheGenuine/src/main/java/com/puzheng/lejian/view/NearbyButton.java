package com.puzheng.lejian.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.puzheng.humanize.Humanize;
import com.puzheng.lejian.Const;
import com.puzheng.lejian.NearbyActivity;
import com.puzheng.lejian.R;
import com.puzheng.lejian.model.SPU;

public class NearbyButton extends Button {

    private SPU spu;

    public NearbyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NearbyButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NearbyActivity.class);
                intent.putExtra("current", NearbyActivity.NEARBY_LIST);
                intent.putExtra(Const.TAG_SPU_ID, spu.getId());
                intent.putExtra(Const.TAG_SPU_ID, spu.getId());
                getContext().startActivity(intent);
            }
        });
    }

    public void setSPU(SPU spu) {
        this.spu = spu;
        if (spu.getDistance() == 0) {
            setVisibility(View.INVISIBLE);
        } else {
            setVisibility(View.VISIBLE);
            setText(getContext().getString(R.string.nearest, Humanize.with(getContext()).distance(spu.getDistance())));
        }
    }
}
