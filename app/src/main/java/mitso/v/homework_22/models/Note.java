package mitso.v.homework_22.models;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Note implements Serializable, Comparable<Note> {

    private long        mId;
    private String      mBody;
    private Date        mDate;
    private String      mFormattedDate;

    public Note(String _body, Date _date) {
        this.mId = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(_date));
        this.mBody = _body;
        this.mDate = _date;
        this.mFormattedDate = new SimpleDateFormat("dd MMMM yyyy").format(_date);
    }

    public Note(long _id, String _body) {
        this.mId = _id;
        this.mBody = _body;
        try {
            this.mDate = new SimpleDateFormat("yyyyMMddHHmmssSSS").parse(String.valueOf(_id));
            this.mFormattedDate = new SimpleDateFormat("dd MMMM yyyy").format(mDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int compareTo(@NonNull Note _another) {
        if (this.getId() < _another.getId())
            return 1;
        else
            return -1;
    }

    @Override
    public boolean equals(Object _o) {
        if (this == _o) return true;
        if (!(_o instanceof Note)) return false;

        Note note = (Note) _o;

        return mId == note.mId;
    }

    @Override
    public int hashCode() {
        return (int) (mId ^ (mId >>> 32));
    }

    @Override
    public String toString() {
        return "Note{" +
                "mId=" + mId +
                ", mBody='" + mBody +
                ", mDate=" + mDate +
                ", mFormattedDate='" + mFormattedDate +
                '}';
    }

    public long getId() {
        return mId;
    }

    public String getBody() {
        return mBody;
    }

    public String getFormattedDate() {
        return mFormattedDate;
    }

    public String getFormattedTime() {
        return new SimpleDateFormat("HH:mm").format(mDate);
    }

}
