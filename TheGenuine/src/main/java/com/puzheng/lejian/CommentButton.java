package com.puzheng.lejian;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.puzheng.humanize.Humanize;
import com.puzheng.lejian.model.SPU;

public class CommentButton extends Button {
    private SPU spu;

    public CommentButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommentButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setSPU(SPU spu) {
        this.spu = spu;
        setText(getContext().getString(R.string.comment_number,
                Humanize.with(getContext()).num(spu.getCommentCnt())));
        setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CommentsActivity.class);
                intent.putExtra(Const.TAG_SPU_ID, CommentButton.this.spu.getId());
                getContext().startActivity(intent);
            }
        });
    }
}
