package helloworldnepal.com.ehrdashboards.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "link")
public class Link {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "display_value")
    private String displayValue;

    @ColumnInfo(name = "url")
    private String url;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return displayValue;
    }
}