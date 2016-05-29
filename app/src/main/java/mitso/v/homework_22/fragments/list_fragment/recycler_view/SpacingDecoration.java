package mitso.v.homework_22.fragments.list_fragment.recycler_view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpacingDecoration extends RecyclerView.ItemDecoration {

    private int     mHalfSpace;

    public SpacingDecoration(int _space) {
        this.mHalfSpace = _space / 2;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        if (parent.getPaddingLeft() != mHalfSpace) {
            parent.setPadding(mHalfSpace, mHalfSpace, mHalfSpace, mHalfSpace);
            parent.setClipToPadding(false);
        }

        outRect.top = mHalfSpace;
        outRect.bottom = mHalfSpace;
        outRect.left = mHalfSpace;
        outRect.right = mHalfSpace;
    }
}