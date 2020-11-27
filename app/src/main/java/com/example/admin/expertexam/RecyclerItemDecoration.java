package com.example.admin.expertexam;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

class RecyclerItemDecoration extends RecyclerView.ItemDecoration {

    private int headerOffset;
    boolean isSticky;
    SectionCallback sectionCallback;
    View headerView;
    TextView tvQuestionTitle;
    Context context;

    public RecyclerItemDecoration(Context context, int headerHeight, boolean isSticky, SectionCallback callback) {
        this.headerOffset = headerHeight;
        this.isSticky = isSticky;
        this.sectionCallback = callback;
        this.context = context;
    }
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildAdapterPosition(view);
        if (sectionCallback.isSectionHeader(pos)) {
            outRect.top = headerOffset;
        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        if (headerView == null) {
            headerView = inflateHeader(parent);
            tvQuestionTitle = (TextView) headerView.findViewById(R.id.tv_question_number);
            fixLayoutSize(headerView, parent);
        }
        String prevTitle = "";
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            int childPos = parent.getChildAdapterPosition(child);
            String title = sectionCallback.getSectionHeaderName(childPos);
            tvQuestionTitle.setText(title);
            if (!prevTitle.equalsIgnoreCase(title) || sectionCallback.isSectionHeader(childPos)) {
                drawHeader(c, child, headerView);
                prevTitle = title;
            }
        }
    }

    private void drawHeader(Canvas c, View child, View headerView) {
        c.save();
        if (isSticky) {
            c.translate(0, Math.max(0, child.getTop() - headerView.getHeight()));
        }
        else {
            c.translate(0, child.getTop() - headerView.getHeight());
        }
        headerView.draw(c);
        c.restore();
    }

    public void fixLayoutSize(View view, ViewGroup viewGroup) {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(viewGroup.getWidth(),View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(viewGroup.getHeight(),View.MeasureSpec.UNSPECIFIED);
        int childWidth = ViewGroup.getChildMeasureSpec(widthSpec,viewGroup.getPaddingLeft()+viewGroup.getPaddingRight(),view.getLayoutParams().width);
        int childHeight = ViewGroup.getChildMeasureSpec(heightSpec,viewGroup.getPaddingTop()+viewGroup.getPaddingBottom(),view.getLayoutParams().height);

        view.measure(childWidth,childHeight);
        view.layout(0,0,view.getMeasuredWidth(),view.getMeasuredHeight());
    }

    private View inflateHeader(RecyclerView recyclerView) {
        View view = LayoutInflater.from(context).inflate(R.layout.result_section_header, recyclerView, false);
        return view;
    }

    public interface SectionCallback {
        public boolean isSectionHeader(int pos);
        public String getSectionHeaderName(int pos);
    }

}
