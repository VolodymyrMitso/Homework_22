package mitso.v.homework_22.fragments.list_fragment.recycler_view;

import mitso.v.homework_22.models.Note;

public interface INoteHandler {

    void onClick(Note _note, int _position);

    void onLongClick(Note _note, int _position);
}
