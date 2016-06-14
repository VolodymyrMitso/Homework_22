package mitso.v.homework_22.models;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Note implements Serializable {

    private long        mId;
    private String      mBody;
    private Date        mDate;
    private String      mFormattedDate;
    private String      mFormattedTime;

    public Note(String _body, Date _date) {
        this.mId = Long.parseLong(new SimpleDateFormat("ddMMyyyyHHmmssSSS").format(_date));
        this.mBody = _body;
        this.mDate = _date;
        this.mFormattedDate = new SimpleDateFormat("dd/MMMM/yyyy").format(_date);
        this.mFormattedTime = new SimpleDateFormat("HH:mm:ss").format(_date);
    }

    public Note(long _id, String _body) {
        this.mId = _id;
        this.mBody = _body;
        try {
            this.mDate = new SimpleDateFormat("ddMMyyyyHHmmssSSS").parse(String.valueOf(_id));
            this.mFormattedDate = new SimpleDateFormat("dd/MMMM/yyyy").format(mDate);
            this.mFormattedTime = new SimpleDateFormat("HH:mm:ss").format(mDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
                ", mDate=" + mDate +
                ", mBody='" + mBody + '\'' +
                '}';
    }

    public String getBody() {
        return mBody;
    }

    public void setBody(String _body) {
        this.mBody = _body;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date _date) {
        this.mDate = _date;
    }

    public String getFormattedDate() {
        return mFormattedDate;
    }

    public void setFormattedDate(String _formattedDate) {
        this.mFormattedDate = _formattedDate;
    }

    public String getFormattedTime() {
        return mFormattedTime;
    }

    public void setFormattedTime(String _formattedTime) {
        this.mFormattedTime = _formattedTime;
    }

    public long getId() {
        return mId;
    }
}
