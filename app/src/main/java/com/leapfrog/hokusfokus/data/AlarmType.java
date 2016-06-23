package com.leapfrog.hokusfokus.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class to handle type of Alarm
 * <li>Reminder Alarm</li>
 * <li>Focus Hour Start Alarm</li>
 * <li>Focus Hour End Alarm</li>
 *
 */
public class AlarmType implements Parcelable {
    public static final int ALARM_START = 0x1;
    public static final int ALARM_END = 0x2;
    public static final int ALARM_REMINDER = 0x3;

    public int alarmType = ALARM_START;

    public AlarmType(int alarmType) {
        this.alarmType = alarmType;
    }


    protected AlarmType(Parcel in) {
        alarmType = in.readInt();
    }

    public static final Creator<AlarmType> CREATOR = new Creator<AlarmType>() {
        @Override
        public AlarmType createFromParcel(Parcel in) {
            return new AlarmType(in);
        }

        @Override
        public AlarmType[] newArray(int size) {
            return new AlarmType[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(alarmType);
    }
}
