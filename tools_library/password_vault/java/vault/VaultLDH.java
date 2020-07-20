/*
 *  Copyright (c) 2018 Gabriele Corso
 *
 *  Distributed under the MIT software license, see the accompanying
 *  file LICENSE or http://www.opensource.org/licenses/mit-license.php.
 */

package com.gcorso.cyberacademy.vault;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class VaultLDH {

    private static final String xform = "DES/ECB/PKCS5Padding";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "vault.db";
    private static final String TABLE_NAME_NOTES = "note";

    private static final String NOTE_COLUMN_ID = "_id";
    private static final String NOTE_COLUMN_TITLE = "title";
    private static final String NOTE_COLUMN_DATE = "date";
    private static final String NOTE_COLUMN_DATA = "data";

    private NotesDBOpenHelper openHelper;
    private SQLiteDatabase database;

    public VaultLDH(Context context){
        openHelper = new NotesDBOpenHelper(context);
        database = openHelper.getWritableDatabase();
    }

    public NotesDBOpenHelper getOpenHelper (Context context){
        openHelper = new NotesDBOpenHelper(context);
        return openHelper;
    }

    public SQLiteDatabase getWritableDatabase (Context context){
        openHelper = new NotesDBOpenHelper(context);
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db;
    }

    public List<Note> getNotes(String password){
        String sql = "SELECT _id, title, data FROM note";
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        List<Note> notes = new ArrayList<>();

        while(!cursor.isAfterLast()){

            Note note = new Note(cursor.getInt(0), cursor.getString(1), cursor.getBlob(2));

            TEA tea = new TEA(password.getBytes());
            String cleartext = new String(tea.decrypt(note.getData()));

            note.setText(cleartext);

            notes.add(note);
            cursor.moveToNext();
        }

        cursor.close();
        return notes;
    }

    public void addNote(String password, String title, String text){

        TEA tea = new TEA(password.getBytes());
        byte[] ciphertext = tea.encrypt(text.getBytes());
        ContentValues contentValues= new ContentValues();
        contentValues.put(NOTE_COLUMN_TITLE, title);
        contentValues.put(NOTE_COLUMN_DATA, ciphertext);
        database.insert(TABLE_NAME_NOTES, null, contentValues);
    }

    public void deleteNote(int id){
        String sql = "DELETE FROM note WHERE _id = " + Integer.toString(id);
        database.execSQL(sql);
    }



    // class that interfaces the connection between the LDH and the database
    private class NotesDBOpenHelper extends SQLiteOpenHelper {

        public NotesDBOpenHelper(Context context) {
          super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String SQL_CREATE_TABLE_VAULT = "CREATE TABLE `note` (\n" +
                    "\t`_id`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                    "\t`title`\tTEXT,\n" +
                    "\t`data`\tBLOB\n" +
                    ");";
            db.execSQL(SQL_CREATE_TABLE_VAULT);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_NOTES);
            onCreate(db);
        }
    }
}
