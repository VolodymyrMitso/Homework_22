package mitso.v.homework_22.fragments.list_fragment.recycler_view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mitso.v.homework_22.R;
import mitso.v.homework_22.databinding.NoteCardBinding;
import mitso.v.homework_22.models.Note;

public class NoteAdapter extends SelectableAdapter<NoteViewHolder> {

    private List<Note>      mNoteList;
    private INoteHandler    mNoteHandler;
    private Context         mContext;

    public NoteAdapter(Context _context, List<Note> _noteList) {
        this.mContext = _context;

        /** !!! */
        this.mNoteList = new ArrayList<>(_noteList);
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup _parent, int _viewType) {
        final NoteCardBinding binding = NoteCardBinding.inflate(LayoutInflater.from(_parent.getContext()), _parent, false);
        return new NoteViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(final NoteViewHolder _holder, final int _position) {

        final Note note = mNoteList.get(_position);

        _holder.getBinding().setNote(note);

        _holder.getBinding().setClickerOpenNote(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNoteHandler.onClick(note, _holder.getAdapterPosition());
            }
        });

        _holder.getBinding().setClickerSelectNote(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                mNoteHandler.onLongClick(note, _holder.getAdapterPosition());

                return true;
            }
        });

        /** highlight selected item: */
        final Drawable selectedShape = mContext.getResources().getDrawable(R.drawable.shape_card_selected);
        final Drawable unselectedShape = mContext.getResources().getDrawable(R.drawable.shape_card_unselected);
        _holder.getBinding().cardNote.setBackgroundDrawable(isSelected(_position) ? selectedShape : unselectedShape);
    }

    public void removeNotes(List<Integer> _positions) {
        // Reverse-sort the list
        Collections.sort(_positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });

        // Split the list in ranges
        while (!_positions.isEmpty()) {

            if (_positions.size() == 1) {

                removeNote(_positions.get(0));
                _positions.remove(0);

            } else {

                int count = 1;
                while (_positions.size() > count && _positions.get(count).equals(_positions.get(count - 1) - 1))
                    ++count;

                if (count == 1)
                    removeNote(_positions.get(0));
                else
                    removeRange(_positions.get(count - 1), count);

                for (int i = 0; i < count; ++i)
                    _positions.remove(0);
            }
        }
    }

    private void removeNote(int _position) {
        mNoteList.remove(_position);
        notifyItemRemoved(_position);
    }

    private void removeRange(int _positionStart, int _itemCount) {
        for (int i = 0; i < _itemCount; ++i)
            mNoteList.remove(_positionStart);

        notifyItemRangeRemoved(_positionStart, _itemCount);
    }

    @Override
    public int getItemCount() {
        return mNoteList.size();
    }

    public void setNoteHandler(INoteHandler _noteHandler) {
        this.mNoteHandler = _noteHandler;
    }

    public void releaseNoteHandler() {
        this.mNoteHandler = null;
    }

    /** !!! */
    public List<Note> getNoteList() {
        return mNoteList;
    }
}
