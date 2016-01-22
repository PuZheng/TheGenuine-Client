package com.puzheng.lejian.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.puzheng.lejian.R;
import com.puzheng.lejian.model.SPU;
import com.puzheng.lejian.store.AuthStore;

public class FavorButton extends ImageButton {
    private SPU spu;

    public FavorButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FavorButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setSPU(SPU spu) {
        this.spu = spu;
        ImageButton button = (ImageButton) findViewById(R.id.favorButton);
        if (spu.isFavored()) {
            button.setImageResource(R.drawable.ic_action_important);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), getContext().getString(R.string.favored),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AuthStore.getInstance().getUser() == null) {
                        // to login
//                        MyApp.doLoginIn(getContext());
                    } else {
                        // to favor
//                        doAddFavor();
                    }

                }
            });
        }

    }
}
