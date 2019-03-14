package com.nonexistenware.igor.pocketmemo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import com.nonexistenware.igor.pocketmemo.model.Memo;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "secured_memo_db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Memo.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Memo.TABLE_NAME);
        onCreate(db);
    }

    public long insertNote(String memo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Memo.COLUMN_MEMO, memo);
        long id = db.insert(Memo.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public Memo getMemo(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(Memo.TABLE_NAME, new String[] {Memo.COLUMN_ID, Memo.COLUMN_MEMO, Memo.COLUMN_TIMESTAMP},
                Memo.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

            Memo memo = new Memo(
                    cursor.getInt(cursor.getColumnIndex(Memo.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(Memo.COLUMN_MEMO)),
                    cursor.getString(cursor.getColumnIndex(Memo.COLUMN_TIMESTAMP)));
            cursor.close();

            return memo;
        }

        public List<Memo> getAllMemo() {
        List<Memo> memos = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Memo.TABLE_NAME + " ORDER BY " +
                Memo.COLUMN_TIMESTAMP + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Memo memo = new Memo();
                memo.setId(cursor.getInt(cursor.getColumnIndex(Memo.COLUMN_ID)));
                memo.setMemo(cursor.getString(cursor.getColumnIndex(Memo.COLUMN_MEMO)));
                memo.setTimestamp(cursor.getString(cursor.getColumnIndex(Memo.COLUMN_TIMESTAMP)));

                memos.add(memo);
            } while (cursor.moveToNext());
        }

        db.close();

        return memos;
    }

    public int getMemosCount() {
        String countQuery = "SELECT * FROM " + Memo.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int updateMemo(Memo memo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Memo.COLUMN_MEMO, memo.getId());

        return db.update(Memo.TABLE_NAME, values, Memo.COLUMN_ID + " = ? ",
                new String[]{String.valueOf(memo.getId())});
    }

    public void deleteMemo(Memo memo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Memo.TABLE_NAME, Memo.COLUMN_ID + " = ?",
                new String[]{String.valueOf(memo.getId())});
        db.close();
    }

}
