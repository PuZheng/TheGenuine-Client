package com.puzheng.lejian;

import android.content.Intent;
import android.support.v4.app.ListFragment;
import android.util.Pair;
import android.view.View;
import android.widget.ListView;

import com.puzheng.deferred.Deferrable;
import com.puzheng.deferred.DoneHandler;
import com.puzheng.lejian.adapter.SPUListAdapter;
import com.puzheng.lejian.model.SPU;

import java.util.List;

public class SPUListFragment extends ListFragment {
    private Deferrable<List<SPU>, Pair<String, String>> deferrable;
    private boolean inited;

    public SPUListFragment() {

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        SPU spu = (SPU) getListAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), SPUActivity.class);
        intent.putExtra(Const.TAG_SPU, spu);
        getActivity().startActivity(intent);
    }

    public SPUListFragment setDeferrable(Deferrable<List<SPU>, Pair<String, String>> deferrable) {
        this.deferrable = deferrable;

        return this;
    }

    public void init() {
        if (!inited) {
            deferrable.done(new DoneHandler<List<SPU>>() {
                @Override
                public void done(List<SPU> spus) {
                    setListAdapter(new SPUListAdapter(getActivity(), spus));
                }
            });
            inited = true;
        }
    }

    static class Builder {
        private Deferrable<List<SPU>, Pair<String, String>> deferrable;

        public Builder() {

        }

        public Builder deferred(Deferrable<List<SPU>, Pair<String, String>> deferrable) {
            this.deferrable = deferrable;
            return this;
        }

        public SPUListFragment build() {
            return new SPUListFragment().setDeferrable(deferrable);
        }
    }
}