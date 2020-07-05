package com.ishaangupta.inventory

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ishaangupta.inventory.DTO.ListItem
import com.ishaangupta.inventory.DTO.MainList
import java.util.jar.Manifest

class DBHandler(val context : Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createMainListTable = "CREATE TABLE $TABLE_MAIN_LIST (" +
                "$COL_ID integer PRIMARY KEY AUTOINCREMENT," +
                "$COL_NAME varchar," +
                "$COL_CREATED_AT datetime DEFAULT CURRENT_TIMESTAMP);"

        val createListItemTable = "CREATE TABLE $TABLE_LIST_ITEM (" +
                "$COL_ID integer PRIMARY KEY AUTOINCREMENT," +
                "$COL_MAIN_ID integer," +
                "$COL_ITEM_NAME varchar," +
                "$COL_QTY integer," +
                "$COL_EXPIRY_MONTH varchar," +
                "$COL_CREATED_AT datetime DEFAULT CURRENT_TIMESTAMP);"

        db.execSQL(createMainListTable)
        db.execSQL(createListItemTable)


    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
    // MAIN LIST
    fun addMainList(mainList : MainList):Boolean
    {
        val  db :SQLiteDatabase = writableDatabase
        val cv = ContentValues()
        cv.put(COL_NAME, mainList.name)
        val result = db.insert(TABLE_MAIN_LIST, null, cv)
        return result != (-1).toLong()
    }
    fun getMainLists() : MutableList<MainList>
    {
        val result : MutableList<MainList> = ArrayList()
        val db:SQLiteDatabase = readableDatabase
        val queryResult : Cursor = db.rawQuery("SELECT * FROM $TABLE_MAIN_LIST", null)
        if (queryResult.moveToFirst())
        {
            do {
                val mainList = MainList()
                mainList.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
                mainList.name = queryResult.getString(queryResult.getColumnIndex(COL_NAME))
                result.add(mainList)
            }while(queryResult.moveToNext())
        }

        queryResult.close()
        return result
    }
    fun updateMainList(mainList : MainList)
    {
        val  db :SQLiteDatabase = writableDatabase
        val cv = ContentValues()
        cv.put(COL_NAME, mainList.name)
        db.update(TABLE_MAIN_LIST, cv, "$COL_ID=?", arrayOf(mainList.id.toString()))

    }
    fun deleteMainList(mainListId : Long)
    {
        val  db :SQLiteDatabase = writableDatabase
        db.delete(TABLE_LIST_ITEM, "$COL_MAIN_ID=?", arrayOf(mainListId.toString()))
        db.delete(TABLE_MAIN_LIST, "$COL_ID=?", arrayOf(mainListId.toString()))
    }
    /// LIST ITEM
    fun addListItem(item : ListItem):Boolean
    {
        val  db :SQLiteDatabase = writableDatabase
        val cv = ContentValues()
        cv.put(COL_ITEM_NAME, item.itemName)
        cv.put(COL_MAIN_ID, item.main_id)
        cv.put(COL_QTY, item.qty)
        cv.put(COL_EXPIRY_MONTH, item.expiry_month)

        val result = db.insert(TABLE_LIST_ITEM, null, cv)
        return result != (-1).toLong()

    }
    fun deleteListItem(itemListId : Long)
    {
        val  db :SQLiteDatabase = writableDatabase
        db.delete(TABLE_LIST_ITEM, "$COL_ID=?", arrayOf(itemListId.toString()))
    }

    fun updateListItem(item : ListItem)
    {
        val db :SQLiteDatabase = writableDatabase
        val cv = ContentValues()

        cv.put(COL_ITEM_NAME, item.itemName)
        cv.put(COL_MAIN_ID, item.main_id)
        cv.put(COL_QTY, item.qty)
        cv.put(COL_EXPIRY_MONTH, item.expiry_month)

        db.update(TABLE_LIST_ITEM, cv, "$COL_ID = ?", arrayOf(item.id.toString()))


    }
    fun getListItem(mainId : Long) : MutableList<ListItem>
    {
        val result : MutableList<ListItem> = ArrayList()

        val db:SQLiteDatabase = readableDatabase
        val queryResult : Cursor = db.rawQuery("SELECT * FROM $TABLE_LIST_ITEM WHERE $COL_MAIN_ID = $mainId ", null)
        if (queryResult.moveToFirst())
        {
            do {
                val listItem = ListItem()
                listItem.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
                listItem.main_id = queryResult.getLong(queryResult.getColumnIndex(COL_MAIN_ID))
                listItem.itemName = queryResult.getString(queryResult.getColumnIndex(COL_ITEM_NAME))
                listItem.qty = queryResult.getLong(queryResult.getColumnIndex(COL_QTY))
                listItem.expiry_month = queryResult.getString(queryResult.getColumnIndex(COL_EXPIRY_MONTH))
                result.add(listItem)
            }while(queryResult.moveToNext())
        }

        queryResult.close()
        return result
    }

    fun updateListItem()
    {

    }

}