package mitso.v.homework_22.fragments.list_fragment.recycler_view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mitso.v.homework_22.R;
import mitso.v.homework_22.constants.Constants;
import mitso.v.homework_22.databinding.CardNoteBinding;
import mitso.v.homework_22.models.Note;

public class NoteAdapter extends SelectableAdapter<NoteViewHolder> {

    private String          LOG_TAG = Constants.NOTE_ADAPTER_LOG_TAG;

    private List<Note>      mNoteList;
    private INoteHandler    mNoteHandler;
    private Context         mContext;

    public NoteAdapter(Context _context, List<Note> _noteList) {
        this.mContext = _context;
        this.mNoteList = new ArrayList<>(_noteList);
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup _parent, int _viewType) {
        return new NoteViewHolder(CardNoteBinding.inflate(LayoutInflater.from(_parent.getContext()), _parent, false).getRoot());
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

        final Drawable selectedShape = mContext.getResources().getDrawable(R.drawable.shape_card_selected);
        final Drawable defaultShape = mContext.getResources().getDrawable(R.drawable.shape_card_default);
        _holder.getBinding().cardNote.setBackgroundDrawable(isSelected(_position) ? selectedShape : defaultShape);

    }

    @Override
    public int getItemCount() {
        return mNoteList.size();
    }

    public void setNoteHandler(INoteHandler _noteHandler) {
        if (mNoteHandler == null) {
            this.mNoteHandler = _noteHandler;
            Log.i(LOG_TAG, "HANDLER IS SET.");
        }
    }

    public void releaseNoteHandler() {
        if (mNoteHandler != null) {
            this.mNoteHandler = null;
            Log.i(LOG_TAG, "HANDLER IS NULL.");
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void addNote(int _position, Note _note) {
        mNoteList.add(_position, _note);
        notifyItemInserted(_position);

        Log.i(LOG_TAG, "NOTE IS ADDED TO LIST.");
    }

    public void removeNote(int _position) {
        mNoteList.remove(_position);
        notifyItemRemoved(_position);

        Log.i(LOG_TAG, "NOTE IS DELETED FROM LIST.");
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void addItem(int _position, Note _note) {
        mNoteList.add(_position, _note);
        notifyItemInserted(_position);
    }

    private void removeItem(int _position) {
        mNoteList.remove(_position);
        notifyItemRemoved(_position);
    }

    private void moveItem(int _fromPosition, int _toPosition) {
        final Note note = mNoteList.remove(_fromPosition);
        mNoteList.add(_toPosition, note);
        notifyItemMoved(_fromPosition, _toPosition);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void removeNotes(List<Integer> _positions) {
        Collections.sort(_positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer _lhs, Integer _rhs) {
                return _rhs - _lhs;
            }
        });

        while (!_positions.isEmpty()) {

            if (_positions.size() == 1) {

                removeItem(_positions.get(0));
                _positions.remove(0);

            } else {

                int count = 1;

                while (_positions.size() > count && _positions.get(count).equals(_positions.get(count - 1) - 1))
                    ++count;

                if (count == 1)
                    removeItem(_positions.get(0));
                else
                    removeRange(_positions.get(count - 1), count);

                for (int i = 0; i < count; ++i)
                    _positions.remove(0);
            }
        }

        Log.i(LOG_TAG, "SELECTED NOTES ARE DELETED FROM LIST.");
    }

    private void removeRange(int _positionStart, int _itemCount) {
        for (int i = 0; i < _itemCount; ++i)
            mNoteList.remove(_positionStart);

        notifyItemRangeRemoved(_positionStart, _itemCount);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void animateTo(List<Note> _noteList) {
        applyAndAnimateRemovals(_noteList);
        applyAndAnimateAdditions(_noteList);
        applyAndAnimateMovedItems(_noteList);
    }

    private void applyAndAnimateRemovals(List<Note> _newNotes) {
        for (int i = mNoteList.size() - 1; i >= 0; i--) {
            final Note note = mNoteList.get(i);
            if (!_newNotes.contains(note))
                removeItem(i);
        }
    }

    private void applyAndAnimateAdditions(List<Note> _newNotes) {
        for (int i = 0, count = _newNotes.size(); i < count; i++) {
            final Note note = _newNotes.get(i);
            if (!mNoteList.contains(note))
                addItem(i, note);
        }
    }

    private void applyAndAnimateMovedItems(List<Note> _newNotes) {
        for (int toPosition = _newNotes.size() - 1; toPosition >= 0; toPosition--) {
            final Note note = _newNotes.get(toPosition);
            final int fromPosition = mNoteList.indexOf(note);
            if (fromPosition >= 0 && fromPosition != toPosition)
                moveItem(fromPosition, toPosition);
        }
    }
}
