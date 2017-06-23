package com.sqsong.androidstudysamples.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 青松 on 2017/5/11.
 */

public class PhoneAreaCodeData implements Parcelable, Comparable<PhoneAreaCodeData> {

    private int code;
    private boolean isHot;
    private String cnName;
    private String enName;
    private String countrySimpleCode;
    private String chinesePinyin;

    public PhoneAreaCodeData() {}

    protected PhoneAreaCodeData(Parcel in) {
        code = in.readInt();
        isHot = in.readByte() != 0;
        cnName = in.readString();
        enName = in.readString();
        countrySimpleCode = in.readString();
        chinesePinyin = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
        dest.writeByte((byte) (isHot ? 1 : 0));
        dest.writeString(cnName);
        dest.writeString(enName);
        dest.writeString(countrySimpleCode);
        dest.writeString(chinesePinyin);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PhoneAreaCodeData> CREATOR = new Creator<PhoneAreaCodeData>() {
        @Override
        public PhoneAreaCodeData createFromParcel(Parcel in) {
            return new PhoneAreaCodeData(in);
        }

        @Override
        public PhoneAreaCodeData[] newArray(int size) {
            return new PhoneAreaCodeData[size];
        }
    };

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isHot() {
        return isHot;
    }

    public void setHot(boolean hot) {
        isHot = hot;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getCountrySimpleCode() {
        return countrySimpleCode;
    }

    public void setCountrySimpleCode(String countrySimpleCode) {
        this.countrySimpleCode = countrySimpleCode;
    }

    public String getChinesePinyin() {
        return chinesePinyin;
    }

    public void setChinesePinyin(String chinesePinyin) {
        this.chinesePinyin = chinesePinyin;
    }

    @Override
    public int compareTo(PhoneAreaCodeData data) {
//        boolean flag;
//        if ((flag = getChinesePinyin().startsWith("#")) ^ data.getChinesePinyin().startsWith("#")) {
//            return flag ? -1 : 1;
//        }
        return getChinesePinyin().compareTo(data.getChinesePinyin());
    }
}
