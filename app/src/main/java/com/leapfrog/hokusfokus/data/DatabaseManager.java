package com.leapfrog.hokusfokus.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.leapfrog.hokusfokus.model.Task;
import com.leapfrog.hokusfokus.utils.AppLog;
import com.leapfrog.hokusfokus.utils.ResourceUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * helps to add,insert,update,delete data in database table.
 *
 * @author rajesh.
 * @date 4/22/15.
 */
public class DatabaseManager {

    private final SQLiteDatabase sqLiteDatabase;
    private final DatabaseHelper helper;
    private static DatabaseManager instance;

    private String TAG = DatabaseManager.class.getSimpleName();

    private DatabaseManager(Context context) {
        helper = new DatabaseHelper(context);
        sqLiteDatabase = helper.getWritableDatabase();
    }

    public static synchronized DatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context);
        }
        return instance;
    }

    /**
     * insert task to Databse
     *
     * @param task {@link Task} to be inserted to database
     * @return true if data inserted successfully else false
     */
    public boolean insertTask(Task task) {
        long id;
        ContentValues contentValues;
        contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_TASK, task.task);
        contentValues.put(DatabaseHelper.COLUMN_STATUS, task.taskStatus);
        contentValues.put(DatabaseHelper.COLUMN_TASK_ADDED_MONTH, task.taskMonth);
        contentValues.put(DatabaseHelper.COLUMN_TIME_STAMP, task.timeStamp);
        contentValues.put(DatabaseHelper.COLUMN_TASK_MONTH_AND_DATE, task.taskMonthDate);

        AppLog.showLog(TAG, " Add task " + contentValues.toString());

        id = sqLiteDatabase.insert(DatabaseHelper.TABLE_FOCUS, null, contentValues);
        return id != -1;
    }

    /**
     * get all the {@link Task} according to the date
     *
     * @param timeStamp - {@link java.util.Date}
     * @return {@link List} containing tasks from passed date
     */
    public List<Task> getAllTask(String timeStamp) {
        AppLog.showLog(TAG, " task timeStamp before" + timeStamp);

        Task task;
        Cursor cursor = null;
        String query = "Select * from " + DatabaseHelper.TABLE_FOCUS + " WHERE " + DatabaseHelper.COLUMN_TIME_STAMP + "='" + timeStamp + "' " +
                " ORDER BY " + DatabaseHelper.COLUMN_TASK_ORDER;

        cursor = sqLiteDatabase.rawQuery(query, null);
        ArrayList<Task> tasks = new ArrayList<>();
        while (cursor.moveToNext()) {
            task = new Task();
            task.id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
            task.task = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TASK));
            task.taskStatus = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS));
            task.timeStamp = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TIME_STAMP));
            task.taskMonth = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_ADDED_MONTH));
            task.taskMonthDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_MONTH_AND_DATE));

            AppLog.showLog(TAG, "timestamp after" + task.timeStamp);
            tasks.add(task);
        }
        cursor.close();

        return tasks;
    }

    /**
     * get all the {@link Task} according to the date
     *
     * @param date - {@link java.util.Date}
     * @return {@link HashMap} containing tasks from passed date
     */

    public HashMap<Integer, ArrayList<Task>> getTaskByDate(ArrayList<String> date) {
        HashMap<Integer, ArrayList<Task>> hashMap = new HashMap<>();
        ArrayList<Task> tasks;
        Task task;

        for (int i = 0; i < date.size(); i++) {
            tasks = new ArrayList<>();
            String query = "SELECT *  FROM " + DatabaseHelper.TABLE_FOCUS + " where " + DatabaseHelper.COLUMN_TIME_STAMP + "='" + date.get(i) + "'";
            Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            while (cursor.moveToNext()) {
                task = new Task();
                task.task = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TASK));
                task.taskStatus = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS));
                task.timeStamp = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TIME_STAMP));
                task.id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                task.taskMonth = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_ADDED_MONTH));
                task.taskMonthDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_MONTH_AND_DATE));
                tasks.add(task);
            }
            hashMap.put(i, tasks);
            cursor.close();
        }

        return hashMap;
    }

    /**
     * get all the months entered in the database
     *
     * @return ArrayList containing sorted months
     */
    public ArrayList<String> getAllMonths() {
        Set<String> unsortedMonths = new HashSet<>();
        String query = "SELECT DISTINCT " + DatabaseHelper.COLUMN_TASK_ADDED_MONTH + " FROM " + DatabaseHelper.TABLE_FOCUS;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        while (cursor.moveToNext()) {
            unsortedMonths.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_ADDED_MONTH)));
        }
        cursor.close();
        return ResourceUtils.sortMonth(unsortedMonths);
    }

    /**
     * deletes the task
     *
     * @param task Task which need to be deleted
     */
    public void deleteTaskById(String task) {

        String sql = "DELETE FROM " + DatabaseHelper.TABLE_FOCUS + "  WHERE " + DatabaseHelper.COLUMN_TASK + "='" + task + "'";
        sqLiteDatabase.execSQL(sql);
    }

    public Task getTaskObject(String task) {
        Cursor cursor;
        Task objTask = new Task();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_FOCUS + " WHERE " + DatabaseHelper.COLUMN_TASK + "='" + task + "'";
        cursor = sqLiteDatabase.rawQuery(query, null);
        while (cursor.moveToNext()) {
            objTask.task = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TASK));
            objTask.taskStatus = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS));
            objTask.timeStamp = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TIME_STAMP));
            objTask.id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
            objTask.taskMonth = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_ADDED_MONTH));
            objTask.taskMonthDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_MONTH_AND_DATE));
        }
        return objTask;
    }

    /**
     * get task month wise
     *
     * @param month month of which task needs to be listed
     * @return ArrayList containing task of th related month
     */
    public ArrayList<String> getTaskDateAddedInMonth(String month) {
        ArrayList<String> date = new ArrayList<>();
        String query = "SELECT DISTINCT " + DatabaseHelper.COLUMN_TIME_STAMP +
                " FROM " + DatabaseHelper.TABLE_FOCUS + " where " +
                DatabaseHelper.COLUMN_TASK_ADDED_MONTH + "='" + month + "'" + " ORDER BY " + DatabaseHelper.COLUMN_TIME_STAMP;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        while (cursor.moveToNext()) {
            date.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TIME_STAMP)));
        }
        cursor.close();
        return date;
    }

    /**
     * updates task in the databse
     *
     * @param taskId      ID of the task that is to be updated
     * @param taskContent content that is to be inserted to the database
     */
    public void updateTask(int taskId, String taskContent) {
        String query = "UPDATE " + DatabaseHelper.TABLE_FOCUS + " SET  " + DatabaseHelper.COLUMN_TASK + "='" + taskContent + "' WHERE " + DatabaseHelper.COLUMN_ID + "=" + taskId;
        sqLiteDatabase.execSQL(query);
    }

    /**
     * updates task status in the databse
     *
     * @param taskId     ID of the task that is to be updated
     * @param taskStatus status of the task
     */
    public void updateTaskStatus(int taskId, String taskStatus) {
        String query = "UPDATE " + DatabaseHelper.TABLE_FOCUS + " SET  " + DatabaseHelper.COLUMN_STATUS + "='" + taskStatus + "' WHERE " + DatabaseHelper.COLUMN_ID + "=" + taskId;
        sqLiteDatabase.execSQL(query);
    }

    /**
     * check if the daat exists or not
     *
     * @return true if exists else false
     */
    public boolean isSingleRowExist() {
        String query = "Select * from " + DatabaseHelper.TABLE_FOCUS;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        return cursor.getCount() > 0 ? true : false;
    }

    public void updateTaskPriority(int taskId, int priority) {
        String query = "UPDATE " + DatabaseHelper.TABLE_FOCUS + " SET  " + DatabaseHelper.COLUMN_TASK_ORDER + "=" + priority + " WHERE " + DatabaseHelper.COLUMN_ID + "=" + taskId;
        sqLiteDatabase.execSQL(query);
    }

    public void closeDatabase() {
        sqLiteDatabase.close();
    }

}
