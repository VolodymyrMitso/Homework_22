package mitso.v.homework_22.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Note implements Serializable {

    private String      mBody;
    private Date        mDate;
    private long        mId;
    private boolean     isSelected;
    private String      mFormattedDate;
    private String      mFormattedTime;

    public Note(String _body, Date _date) {
        this.mBody = _body;
        this.mDate = _date;
        this.mId = Long.parseLong(new SimpleDateFormat("ddMMyyyyHHmmssSSS").format(_date));
        this.mFormattedDate = new SimpleDateFormat("dd/MMMM/yyyy").format(_date);
        this.mFormattedTime = new SimpleDateFormat("HH:mm:ss").format(_date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Note)) return false;

        Note note = (Note) o;

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

    public boolean isSelected() {
        return isSelected;
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
