package com.kandi.dell.nscarlauncher.base.adapter;

import android.view.View;
import android.view.ViewGroup;



/**
 * Created by hui on 2018/4/14.
 */

public abstract class MultipleSourcesRvAdapter extends BaseMkRvAdapter {
    int curSelect = 0;

    public MultipleSourcesRvAdapter() {
    }
    public int getSectionsCount() {
        return 1;
    }

    public abstract int getRowsCountInSection(int var1);

    public abstract View onCreateView(ViewGroup var1, int var2);

    public abstract void onBindViewHolder(AutoViewHolder holder, IndexPath indexPath);
    public AutoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        Log.d("tag", "[MkMultipleSourcesRvAdapter.onCreateViewHolder]:" + viewType);
        View view = this.onCreateView(parent, viewType);
        return new AutoViewHolder(view);
    }
    public int getItemViewType(int position) {
        int sectionCount = this.getSectionsCount();
        int curSectionMax = 0;

        for(int i = 0; i < sectionCount; ++i) {
            int rowCount = this.getRowsCountInSection(i);
            curSectionMax += rowCount;
            if(position < curSectionMax) {
                return i;
            }
        }

        return 0;
    }

    public void onBindViewHolder(AutoViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        long startTime = System.nanoTime();
        IndexPath indexPath = this.getIndexPathWithPostion(position);
        long endTime = System.nanoTime();
//        Log.d("tag", "getIndexPath(" + (endTime - startTime) + "ns)");
        this.onBindViewHolder(holder, indexPath);
    }

    public int getItemCount() {
        int count = 0;
        int sectionCount = this.getSectionsCount();

        for(int i = 0; i < sectionCount; ++i) {
            int rowCount = this.getRowsCountInSection(i);
            count += rowCount;
        }

        return count;
    }

    public IndexPath getIndexPathWithPostion(int position) {
        int sectionCount = this.getSectionsCount();
        int curSectionMin = 0;
        int curSectionMax = 0;

        for(int i = 0; i < sectionCount; ++i) {
            int rowCount = this.getRowsCountInSection(i);
            curSectionMax += rowCount;
            if(position >= curSectionMin && position < curSectionMax) {
                int offset = position - curSectionMin;
                this.curSelect = i;
                return new IndexPath(i, offset);
            }

            curSectionMin = curSectionMax;
        }

        return null;
    }
    public class IndexPath {
        public int section;
        public int row;

        public IndexPath(int section, int row) {
            this.section = section;
            this.row = row;
        }

        public int getSection() {
            return this.section;
        }

        public int getRow() {
            return this.row;
        }
    }
}
