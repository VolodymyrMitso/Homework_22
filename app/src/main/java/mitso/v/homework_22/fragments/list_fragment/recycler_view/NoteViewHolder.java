package mitso.v.homework_22.fragments.list_fragment.recycler_view;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import mitso.v.homework_22.databinding.NoteCardBinding;

public class NoteViewHolder extends RecyclerView.ViewHolder {

    private NoteCardBinding     mBinding;

    public NoteViewHolder(View _itemView) {
        super(_itemView);

        mBinding = DataBindingUtil.bind(_itemView);
    }

    public NoteCardBinding getBinding() {
        return mBinding;
    }
}
