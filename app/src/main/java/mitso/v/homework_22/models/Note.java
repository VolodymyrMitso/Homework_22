package mitso.v.homework_22.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Note implements Serializable {

    private String      mBody;
    private Date        mDate;
    private String      mFormattedDate;
    private String      mFormattedTime;

    public Note(String _body, Date _date) {
        this.mBody = _body;
        this.mDate = _date;
        this.mFormattedDate = new SimpleDateFormat("dd/MMMM/yyyy").format(_date);
        this.mFormattedTime = new SimpleDateFormat("HH:mm:ss").format(_date);
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
}
