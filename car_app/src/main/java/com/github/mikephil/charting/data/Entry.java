package com.github.mikephil.charting.data;

import android.os.Parcel;
import android.os.ParcelFormatException;
import android.os.Parcelable;
import com.github.mikephil.charting.utils.Utils;

public class Entry extends BaseEntry implements Parcelable {
    public static final Parcelable.Creator<Entry> CREATOR = new Parcelable.Creator<Entry>() {
        public Entry createFromParcel(Parcel source) {
            return new Entry(source);
        }

        public Entry[] newArray(int size) {
            return new Entry[size];
        }
    };
    private float x = 0.0f;

    public Entry() {
    }

    public Entry(float x2, float y) {
        super(y);
        this.x = x2;
    }

    public Entry(float x2, float y, Object data) {
        super(y, data);
        this.x = x2;
    }

    public float getX() {
        return this.x;
    }

    public void setX(float x2) {
        this.x = x2;
    }

    public Entry copy() {
        return new Entry(this.x, getY(), getData());
    }

    public boolean equalTo(Entry e2) {
        if (e2 != null && e2.getData() == getData() && Math.abs(e2.x - this.x) <= Utils.FLOAT_EPSILON && Math.abs(e2.getY() - getY()) <= Utils.FLOAT_EPSILON) {
            return true;
        }
        return false;
    }

    public String toString() {
        return "Entry, x: " + this.x + " y: " + getY();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.x);
        dest.writeFloat(getY());
        if (getData() == null) {
            dest.writeInt(0);
        } else if (getData() instanceof Parcelable) {
            dest.writeInt(1);
            dest.writeParcelable((Parcelable) getData(), flags);
        } else {
            throw new ParcelFormatException("Cannot parcel an Entry with non-parcelable data");
        }
    }

    protected Entry(Parcel in) {
        this.x = in.readFloat();
        setY(in.readFloat());
        if (in.readInt() == 1) {
            setData(in.readParcelable(Object.class.getClassLoader()));
        }
    }
}
