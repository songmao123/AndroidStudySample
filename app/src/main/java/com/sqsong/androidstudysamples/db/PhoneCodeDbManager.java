package com.sqsong.androidstudysamples.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.github.promeg.pinyinhelper.Pinyin;
import com.sqsong.androidstudysamples.BaseApplication;
import com.sqsong.androidstudysamples.R;
import com.sqsong.androidstudysamples.bean.PhoneAreaCodeData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by 青松 on 2017/5/11.
 */

public class PhoneCodeDbManager {

    private static final String TAG = PhoneCodeDbManager.class.getSimpleName();
    private static final String PHONE_AREA_CODE_TABLE_NALE = "city_mobile_code";

    private String mDbPath;
    private Context mContext;
    private static PhoneCodeDbManager sInstance;

    private PhoneCodeDbManager() {
        this.mContext = BaseApplication.getInstance();
        checkDbStatus();
    }

    public static PhoneCodeDbManager getInstance() {
        if (sInstance == null) {
            synchronized (PhoneCodeDbManager.class) {
                if (sInstance == null) {
                    sInstance = new PhoneCodeDbManager();
                }
            }
        }
        return sInstance;
    }

    private void checkDbStatus() {
        mDbPath = new StringBuffer("/data").append(Environment.getDataDirectory().getAbsolutePath())
                .append("/").append(mContext.getPackageName()).append("/phone_code.sqlite").toString();
        boolean imported = isDbFileImported(mDbPath);
        Log.i(TAG, imported ? "import db success!" : "import db failure!");
    }

    private boolean isDbFileImported(String dbPath) {
        File file = new File(dbPath);
        if (file.exists()) {
            return true;
        }
        return importDbFile(dbPath);
    }

    private boolean importDbFile(String dbPath) {
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = BaseApplication.getInstance().getResources().openRawResource(R.raw.city_mobile_code);
            outputStream = new FileOutputStream(dbPath);
            byte[] len = new byte[2048];
            int count = -1;
            while ((count = inputStream.read(len)) != -1) {
                outputStream.write(len, 0, count);
            }
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }

    private SQLiteDatabase openDataBase() {
        File dbFile = new File(mDbPath);
        if (!dbFile.exists()) {
            importDbFile(mDbPath);
        }
        SQLiteDatabase database = null;
        try {
            database = SQLiteDatabase.openDatabase(mDbPath, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return database;
    }

    /**
     * 语言类型
     * @param languageType 0-中文  1-英文
     * @return
     */
    public List<PhoneAreaCodeData> queryAreaCodeList(int languageType) {
        List<PhoneAreaCodeData> codeList = null;
        SQLiteDatabase database = openDataBase();
        if (database == null) return null;

        Cursor cursor = null;
        try {
            cursor = database.query(PHONE_AREA_CODE_TABLE_NALE, null, null, null, null, null, null);
            codeList = new ArrayList<>();
            while (cursor != null && cursor.moveToNext()) {
                int code = cursor.getInt(cursor.getColumnIndex("code"));
                int is_hot = cursor.getInt(cursor.getColumnIndex("is_hot"));
                String cs_name = cursor.getString(cursor.getColumnIndex("cs_name"));
                String en_name = cursor.getString(cursor.getColumnIndex("en_name"));
                String is_code = cursor.getString(cursor.getColumnIndex("is_code"));

                PhoneAreaCodeData data = new PhoneAreaCodeData();
                data.setCode(code);
                data.setHot(is_hot == 1 ? true : false);
                data.setCnName(cs_name);
                data.setEnName(en_name);
                data.setCountrySimpleCode(is_code);
                if (languageType == 0) {
                    data.setChinesePinyin(Pinyin.toPinyin(cs_name, " "));
                } else {
                    data.setChinesePinyin(en_name);
                }
                codeList.add(data);
            }

            Collections.sort(codeList, new Comparator<PhoneAreaCodeData>() {
                @Override
                public int compare(PhoneAreaCodeData o1, PhoneAreaCodeData o2) {
                    return o1.compareTo(o2);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            database.close();
        }
        return codeList;
    }
}
