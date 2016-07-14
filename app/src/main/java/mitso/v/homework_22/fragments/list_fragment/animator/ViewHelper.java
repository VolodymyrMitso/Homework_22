package mitso.v.homework_22.fragments.list_fragment.animator;

import android.support.v4.view.ViewCompat;
import android.view.View;

public final class ViewHelper {

    public static void clear(View _view) {

        ViewCompat.setAlpha(_view, 1);
        ViewCompat.setScaleY(_view, 1);
        ViewCompat.setScaleX(_view, 1);
        ViewCompat.setTranslationY(_view, 0);
        ViewCompat.setTranslationX(_view, 0);
        ViewCompat.setRotation(_view, 0);
        ViewCompat.setRotationY(_view, 0);
        ViewCompat.setRotationX(_view, 0);
        ViewCompat.setPivotY(_view, _view.getMeasuredHeight() / 2);
        ViewCompat.setPivotX(_view, _view.getMeasuredWidth() / 2);
        ViewCompat.animate(_view).setInterpolator(null).setStartDelay(0);
    }
}
