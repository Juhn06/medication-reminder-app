package com.example.medication_reminder_app.data.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.medication_reminder_app.data.entity.Schedule;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class ScheduleDao_Impl implements ScheduleDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Schedule> __insertionAdapterOfSchedule;

  private final EntityDeletionOrUpdateAdapter<Schedule> __deletionAdapterOfSchedule;

  private final EntityDeletionOrUpdateAdapter<Schedule> __updateAdapterOfSchedule;

  private final SharedSQLiteStatement __preparedStmtOfSetActive;

  private final SharedSQLiteStatement __preparedStmtOfDeactivateByMedicine;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByMedicineId;

  public ScheduleDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSchedule = new EntityInsertionAdapter<Schedule>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `schedules` (`id`,`medicine_id`,`time_hour`,`time_minute`,`is_active`,`created_at`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Schedule entity) {
        statement.bindLong(1, entity.id);
        statement.bindLong(2, entity.medicineId);
        statement.bindLong(3, entity.timeHour);
        statement.bindLong(4, entity.timeMinute);
        final int _tmp = entity.isActive ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.createdAt);
      }
    };
    this.__deletionAdapterOfSchedule = new EntityDeletionOrUpdateAdapter<Schedule>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `schedules` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Schedule entity) {
        statement.bindLong(1, entity.id);
      }
    };
    this.__updateAdapterOfSchedule = new EntityDeletionOrUpdateAdapter<Schedule>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `schedules` SET `id` = ?,`medicine_id` = ?,`time_hour` = ?,`time_minute` = ?,`is_active` = ?,`created_at` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Schedule entity) {
        statement.bindLong(1, entity.id);
        statement.bindLong(2, entity.medicineId);
        statement.bindLong(3, entity.timeHour);
        statement.bindLong(4, entity.timeMinute);
        final int _tmp = entity.isActive ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.createdAt);
        statement.bindLong(7, entity.id);
      }
    };
    this.__preparedStmtOfSetActive = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE schedules SET is_active = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeactivateByMedicine = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE schedules SET is_active = 0 WHERE medicine_id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM schedules WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteByMedicineId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM schedules WHERE medicine_id = ?";
        return _query;
      }
    };
  }

  @Override
  public long insert(final Schedule schedule) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfSchedule.insertAndReturnId(schedule);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<Long> insertAll(final List<Schedule> schedules) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final List<Long> _result = __insertionAdapterOfSchedule.insertAndReturnIdsList(schedules);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final Schedule schedule) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfSchedule.handle(schedule);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final Schedule schedule) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfSchedule.handle(schedule);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void setActive(final int scheduleId, final boolean isActive) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfSetActive.acquire();
    int _argIndex = 1;
    final int _tmp = isActive ? 1 : 0;
    _stmt.bindLong(_argIndex, _tmp);
    _argIndex = 2;
    _stmt.bindLong(_argIndex, scheduleId);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfSetActive.release(_stmt);
    }
  }

  @Override
  public void deactivateByMedicine(final int medicineId) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeactivateByMedicine.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, medicineId);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeactivateByMedicine.release(_stmt);
    }
  }

  @Override
  public void deleteById(final int scheduleId) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, scheduleId);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteById.release(_stmt);
    }
  }

  @Override
  public void deleteByMedicineId(final int medicineId) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByMedicineId.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, medicineId);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteByMedicineId.release(_stmt);
    }
  }

  @Override
  public LiveData<List<Schedule>> getByMedicineId(final int medicineId) {
    final String _sql = "SELECT * FROM schedules WHERE medicine_id = ? ORDER BY time_hour, time_minute";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, medicineId);
    return __db.getInvalidationTracker().createLiveData(new String[] {"schedules"}, false, new Callable<List<Schedule>>() {
      @Override
      @Nullable
      public List<Schedule> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMedicineId = CursorUtil.getColumnIndexOrThrow(_cursor, "medicine_id");
          final int _cursorIndexOfTimeHour = CursorUtil.getColumnIndexOrThrow(_cursor, "time_hour");
          final int _cursorIndexOfTimeMinute = CursorUtil.getColumnIndexOrThrow(_cursor, "time_minute");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "is_active");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final List<Schedule> _result = new ArrayList<Schedule>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Schedule _item;
            final int _tmpMedicineId;
            _tmpMedicineId = _cursor.getInt(_cursorIndexOfMedicineId);
            final int _tmpTimeHour;
            _tmpTimeHour = _cursor.getInt(_cursorIndexOfTimeHour);
            final int _tmpTimeMinute;
            _tmpTimeMinute = _cursor.getInt(_cursorIndexOfTimeMinute);
            _item = new Schedule(_tmpMedicineId,_tmpTimeHour,_tmpTimeMinute);
            _item.id = _cursor.getInt(_cursorIndexOfId);
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _item.isActive = _tmp != 0;
            _item.createdAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<List<Schedule>> getAllActive() {
    final String _sql = "SELECT * FROM schedules WHERE is_active = 1 ORDER BY time_hour, time_minute";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"schedules"}, false, new Callable<List<Schedule>>() {
      @Override
      @Nullable
      public List<Schedule> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMedicineId = CursorUtil.getColumnIndexOrThrow(_cursor, "medicine_id");
          final int _cursorIndexOfTimeHour = CursorUtil.getColumnIndexOrThrow(_cursor, "time_hour");
          final int _cursorIndexOfTimeMinute = CursorUtil.getColumnIndexOrThrow(_cursor, "time_minute");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "is_active");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final List<Schedule> _result = new ArrayList<Schedule>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Schedule _item;
            final int _tmpMedicineId;
            _tmpMedicineId = _cursor.getInt(_cursorIndexOfMedicineId);
            final int _tmpTimeHour;
            _tmpTimeHour = _cursor.getInt(_cursorIndexOfTimeHour);
            final int _tmpTimeMinute;
            _tmpTimeMinute = _cursor.getInt(_cursorIndexOfTimeMinute);
            _item = new Schedule(_tmpMedicineId,_tmpTimeHour,_tmpTimeMinute);
            _item.id = _cursor.getInt(_cursorIndexOfId);
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _item.isActive = _tmp != 0;
            _item.createdAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public List<Schedule> getByMedicineIdSync(final int medicineId) {
    final String _sql = "SELECT * FROM schedules WHERE medicine_id = ? AND is_active = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, medicineId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfMedicineId = CursorUtil.getColumnIndexOrThrow(_cursor, "medicine_id");
      final int _cursorIndexOfTimeHour = CursorUtil.getColumnIndexOrThrow(_cursor, "time_hour");
      final int _cursorIndexOfTimeMinute = CursorUtil.getColumnIndexOrThrow(_cursor, "time_minute");
      final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "is_active");
      final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
      final List<Schedule> _result = new ArrayList<Schedule>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Schedule _item;
        final int _tmpMedicineId;
        _tmpMedicineId = _cursor.getInt(_cursorIndexOfMedicineId);
        final int _tmpTimeHour;
        _tmpTimeHour = _cursor.getInt(_cursorIndexOfTimeHour);
        final int _tmpTimeMinute;
        _tmpTimeMinute = _cursor.getInt(_cursorIndexOfTimeMinute);
        _item = new Schedule(_tmpMedicineId,_tmpTimeHour,_tmpTimeMinute);
        _item.id = _cursor.getInt(_cursorIndexOfId);
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsActive);
        _item.isActive = _tmp != 0;
        _item.createdAt = _cursor.getLong(_cursorIndexOfCreatedAt);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<Schedule> getAllActiveSync() {
    final String _sql = "SELECT * FROM schedules WHERE is_active = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfMedicineId = CursorUtil.getColumnIndexOrThrow(_cursor, "medicine_id");
      final int _cursorIndexOfTimeHour = CursorUtil.getColumnIndexOrThrow(_cursor, "time_hour");
      final int _cursorIndexOfTimeMinute = CursorUtil.getColumnIndexOrThrow(_cursor, "time_minute");
      final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "is_active");
      final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
      final List<Schedule> _result = new ArrayList<Schedule>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Schedule _item;
        final int _tmpMedicineId;
        _tmpMedicineId = _cursor.getInt(_cursorIndexOfMedicineId);
        final int _tmpTimeHour;
        _tmpTimeHour = _cursor.getInt(_cursorIndexOfTimeHour);
        final int _tmpTimeMinute;
        _tmpTimeMinute = _cursor.getInt(_cursorIndexOfTimeMinute);
        _item = new Schedule(_tmpMedicineId,_tmpTimeHour,_tmpTimeMinute);
        _item.id = _cursor.getInt(_cursorIndexOfId);
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsActive);
        _item.isActive = _tmp != 0;
        _item.createdAt = _cursor.getLong(_cursorIndexOfCreatedAt);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Schedule getByIdSync(final int scheduleId) {
    final String _sql = "SELECT * FROM schedules WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, scheduleId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfMedicineId = CursorUtil.getColumnIndexOrThrow(_cursor, "medicine_id");
      final int _cursorIndexOfTimeHour = CursorUtil.getColumnIndexOrThrow(_cursor, "time_hour");
      final int _cursorIndexOfTimeMinute = CursorUtil.getColumnIndexOrThrow(_cursor, "time_minute");
      final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "is_active");
      final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
      final Schedule _result;
      if (_cursor.moveToFirst()) {
        final int _tmpMedicineId;
        _tmpMedicineId = _cursor.getInt(_cursorIndexOfMedicineId);
        final int _tmpTimeHour;
        _tmpTimeHour = _cursor.getInt(_cursorIndexOfTimeHour);
        final int _tmpTimeMinute;
        _tmpTimeMinute = _cursor.getInt(_cursorIndexOfTimeMinute);
        _result = new Schedule(_tmpMedicineId,_tmpTimeHour,_tmpTimeMinute);
        _result.id = _cursor.getInt(_cursorIndexOfId);
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsActive);
        _result.isActive = _tmp != 0;
        _result.createdAt = _cursor.getLong(_cursorIndexOfCreatedAt);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public int countByMedicine(final int medicineId) {
    final String _sql = "SELECT COUNT(*) FROM schedules WHERE medicine_id = ? AND is_active = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, medicineId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _result;
      if (_cursor.moveToFirst()) {
        _result = _cursor.getInt(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
