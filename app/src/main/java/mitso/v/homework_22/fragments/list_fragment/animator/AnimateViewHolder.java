package mitso.v.homework_22.fragments.list_fragment.animator;

import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class AnimateViewHolder extends RecyclerView.ViewHolder {

    public AnimateViewHolder(View _itemView) {
        super(_itemView);
    }

    public void preAnimateAddImpl() {
    }

    public void preAnimateRemoveImpl() {
    }

    public abstract void animateAddImpl(ViewPropertyAnimatorListener _listener);

    public abstract void animateRemoveImpl(ViewPropertyAnimatorListener _listener);
}
