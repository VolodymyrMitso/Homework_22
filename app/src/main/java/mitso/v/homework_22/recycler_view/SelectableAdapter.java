package mitso.v.homework_22.recycler_view;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.List;

import mitso.v.homework_22.models.Note;

public abstract class SelectableAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private SparseBooleanArray          mSelectedItems;

    public SelectableAdapter() {

        this.mSelectedItems = new SparseBooleanArray();
    }

    public void makeSelection(int _position) {

        if (mSelectedItems.get(_position, false))
            mSelectedItems.delete(_position);
        else
            mSelectedItems.put(_position, true);

        notifyItemChanged(_position);
    }

    public void clearSelection() {

        final List<Integer> selection = getSelectedItems();
        mSelectedItems.clear();
        for (Integer i : selection)
            notifyItemChanged(i);
    }

    public void selectAllItems(List<Note> _noteList) {

        clearSelection();

        for (int i = 0; i < _noteList.size(); i++) {
            mSelectedItems.put(i, true);
            notifyItemChanged(i);
        }
    }

    public List<Integer> getSelectedItems() {

        final List <Integer> items = new ArrayList<>(mSelectedItems.size());
        for (int i = 0; i < mSelectedItems.size(); i++)
            items.add(mSelectedItems.keyAt(i));

        return items;
    }

    public int getSelectedItemCount() {

        return mSelectedItems.size();
    }

    public boolean isSelected(int _position) {

        return getSelectedItems().contains(_position);
    }
}