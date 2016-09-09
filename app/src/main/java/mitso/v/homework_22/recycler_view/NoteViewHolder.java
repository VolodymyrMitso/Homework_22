package mitso.v.homework_22.recycler_view;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import mitso.v.homework_22.databinding.CardNoteBinding;

public class NoteViewHolder extends RecyclerView.ViewHolder {

    private CardNoteBinding mBinding;

    public NoteViewHolder(View _itemView) {
        super(_itemView);

        mBinding = DataBindingUtil.bind(_itemView);
    }

    public CardNoteBinding getBinding() {

        return mBinding;
    }
}
