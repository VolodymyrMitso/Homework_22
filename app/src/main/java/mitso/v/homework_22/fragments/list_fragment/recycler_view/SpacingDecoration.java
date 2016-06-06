package mitso.v.homework_22.fragments.list_fragment.recycler_view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpacingDecoration extends RecyclerView.ItemDecoration {

    private int     mSpace;

    public SpacingDecoration(int _space) {
        this.mSpace = _space;
    }

    @Override
    public void getItemOffsets(Rect _outRect, View _view, RecyclerView _parent, RecyclerView.State _state) {

        _outRect.left = mSpace;
        _outRect.right = mSpace;
        _outRect.bottom = mSpace;

        if (_parent.getChildLayoutPosition(_view) == 0)
            _outRect.top = mSpace;
        else
            _outRect.top = 0;
    }
}