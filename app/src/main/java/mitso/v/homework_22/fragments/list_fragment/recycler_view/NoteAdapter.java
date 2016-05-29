package mitso.v.homework_22.fragments.list_fragment.recycler_view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import mitso.v.homework_22.databinding.NoteCardBinding;
import mitso.v.homework_22.models.Note;

public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder> {

    private List<Note>      mNoteList;
    private INoteHandler    mNoteHandler;

    public NoteAdapter(List<Note> _noteList) {
        this.mNoteList = new ArrayList<>(_noteList);
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup _parent, int _viewType) {
        final NoteCardBinding binding = NoteCardBinding.inflate(LayoutInflater.from(_parent.getContext()), _parent, false);
        return new NoteViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(NoteViewHolder _holder, int _position) {

        final Note note = mNoteList.get(_position);

        _holder.getBinding().setNote(note);

        _holder.getBinding().setClickerOpenNote(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNoteHandler.openNote(note);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNoteList.size();
    }

    public void setNoteHandler(INoteHandler _noteHandler) {
        this.mNoteHandler = _noteHandler;
    }

    public void releaseBankHandler() {
        this.mNoteHandler = null;
    }
}
