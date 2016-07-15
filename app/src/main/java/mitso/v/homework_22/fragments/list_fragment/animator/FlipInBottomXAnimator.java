package mitso.v.homework_22.fragments.list_fragment.animator;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;

public final class FlipInBottomXAnimator extends BaseItemAnimator {

    @Override protected void animateRemoveImpl(final RecyclerView.ViewHolder _holder) {
        ViewCompat.animate(_holder.itemView)
                .rotationX(-90)
                .setDuration(getRemoveDuration())
                .setInterpolator(mInterpolator)
                .setListener(new DefaultRemoveVpaListener(_holder))
                .setStartDelay(getRemoveDelay(_holder))
                .start();
    }

    @Override protected void preAnimateAddImpl(RecyclerView.ViewHolder _holder) {
        ViewCompat.setRotationX(_holder.itemView, -90);
    }

    @Override protected void animateAddImpl(final RecyclerView.ViewHolder _holder) {
        ViewCompat.animate(_holder.itemView)
                .rotationX(0)
                .setDuration(getAddDuration())
                .setInterpolator(mInterpolator)
                .setListener(new DefaultAddVpaListener(_holder))
                .setStartDelay(getAddDelay(_holder))
                .start();
    }
}
