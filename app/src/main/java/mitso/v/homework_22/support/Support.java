package mitso.v.homework_22.support;

import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import mitso.v.homework_22.R;
import mitso.v.homework_22.fragments.list_fragment.recycler_view.NoteAdapter;
import mitso.v.homework_22.models.Note;

public class Support {

    public void shareNote(Context _context, EditText _editText) {

        if (!_editText.getText().toString().isEmpty()) {

            final Intent shareNoteIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareNoteIntent.setType("text/plain");
            shareNoteIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, _context.getResources().getString(R.string.s_share_note));
            shareNoteIntent.putExtra(android.content.Intent.EXTRA_TEXT, _editText.getText().toString());

            if (shareNoteIntent.resolveActivity(_context.getPackageManager()) != null)
                _context.startActivity(Intent.createChooser(shareNoteIntent, _context.getResources().getString(R.string.s_share_note)));
            else
                Toast.makeText(_context, _context.getResources().getString(R.string.s_no_program), Toast.LENGTH_LONG).show();

        } else
            Toast.makeText(_context, _context.getResources().getString(R.string.s_empty_note), Toast.LENGTH_LONG).show();
    }

    public void shareNotes(Context _context, NoteAdapter _noteAdapter, List<Note> _noteList) {

        final StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < _noteAdapter.getSelectedItems().size(); i++) {

            final int index = _noteAdapter.getSelectedItems().get(i);

            stringBuilder.append(_noteList.get(index).getFormattedDate());
            stringBuilder.append(" - ");
            stringBuilder.append(_noteList.get(index).getFormattedTime());
            stringBuilder.append("\n");
            stringBuilder.append(_noteList.get(index).getBody());

            if (i != _noteAdapter.getSelectedItems().size() - 1)
                stringBuilder.append("\n\n**********\n\n");
        }

        final Intent shareNoteIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareNoteIntent.setType("text/plain");
        shareNoteIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, _context.getResources().getString(R.string.s_share_notes));
        shareNoteIntent.putExtra(android.content.Intent.EXTRA_TEXT, stringBuilder.toString());

        if (shareNoteIntent.resolveActivity(_context.getPackageManager()) != null)
            _context.startActivity(Intent.createChooser(shareNoteIntent, _context.getResources().getString(R.string.s_share_notes)));
        else
            Toast.makeText(_context, _context.getResources().getString(R.string.s_no_program), Toast.LENGTH_LONG).show();
    }

    public void filterList(List<Note> _noteList, String _query, List<Note> _filteredList) {
        _query = _query.toLowerCase();

        _filteredList.clear();
        for (Note note : _noteList) {
            final String noteBody = note.getBody().toLowerCase();
            final String noteDate = note.getFormattedDate().toLowerCase();
            if (noteBody.contains(_query) || noteDate.contains(_query))
                _filteredList.add(note);
        }
    }
}
