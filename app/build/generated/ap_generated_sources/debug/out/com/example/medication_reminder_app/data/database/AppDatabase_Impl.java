package com.example.medication_reminder_app.data.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.example.medication_reminder_app.data.dao.HistoryDao;
import com.example.medication_reminder_app.data.dao.HistoryDao_Impl;
import com.example.medication_reminder_app.data.dao.MedicineDao;
import com.example.medication_reminder_app.data.dao.MedicineDao_Impl;
import com.example.medication_reminder_app.data.dao.RelativeDao;
import com.example.medication_reminder_app.data.dao.RelativeDao_Impl;
import com.example.medication_reminder_app.data.dao.ScheduleDao;
import com.example.medication_reminder_app.data.dao.ScheduleDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile MedicineDao _medicineDao;

  private volatile ScheduleDao _scheduleDao;

  private volatile HistoryDao _historyDao;

  private volatile RelativeDao _relativeDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `medicines` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT, `dosage` TEXT, `times_per_day` INTEGER NOT NULL, `notes` TEXT, `image_path` TEXT, `created_at` INTEGER NOT NULL, `is_active` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `schedules` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `medicine_id` INTEGER NOT NULL, `time_hour` INTEGER NOT NULL, `time_minute` INTEGER NOT NULL, `is_active` INTEGER NOT NULL, `created_at` INTEGER NOT NULL, FOREIGN KEY(`medicine_id`) REFERENCES `medicines`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_schedules_medicine_id` ON `schedules` (`medicine_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `history_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `medicine_id` INTEGER, `medicine_name` TEXT, `medicine_dosage` TEXT, `schedule_id` INTEGER NOT NULL, `scheduled_time` INTEGER NOT NULL, `scheduled_date` TEXT, `taken_time` INTEGER NOT NULL, `status` TEXT, `note` TEXT, `snooze_first_time` INTEGER NOT NULL DEFAULT 0, FOREIGN KEY(`medicine_id`) REFERENCES `medicines`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_history_logs_medicine_id` ON `history_logs` (`medicine_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_history_logs_scheduled_date` ON `history_logs` (`scheduled_date`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `relatives` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT, `phone` TEXT, `fcm_token` TEXT, `created_at` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '222311dedf4d4b460ac3ab1431743cf9')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `medicines`");
        db.execSQL("DROP TABLE IF EXISTS `schedules`");
        db.execSQL("DROP TABLE IF EXISTS `history_logs`");
        db.execSQL("DROP TABLE IF EXISTS `relatives`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsMedicines = new HashMap<String, TableInfo.Column>(8);
        _columnsMedicines.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMedicines.put("name", new TableInfo.Column("name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMedicines.put("dosage", new TableInfo.Column("dosage", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMedicines.put("times_per_day", new TableInfo.Column("times_per_day", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMedicines.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMedicines.put("image_path", new TableInfo.Column("image_path", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMedicines.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMedicines.put("is_active", new TableInfo.Column("is_active", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMedicines = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMedicines = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMedicines = new TableInfo("medicines", _columnsMedicines, _foreignKeysMedicines, _indicesMedicines);
        final TableInfo _existingMedicines = TableInfo.read(db, "medicines");
        if (!_infoMedicines.equals(_existingMedicines)) {
          return new RoomOpenHelper.ValidationResult(false, "medicines(com.example.medication_reminder_app.data.entity.Medicine).\n"
                  + " Expected:\n" + _infoMedicines + "\n"
                  + " Found:\n" + _existingMedicines);
        }
        final HashMap<String, TableInfo.Column> _columnsSchedules = new HashMap<String, TableInfo.Column>(6);
        _columnsSchedules.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchedules.put("medicine_id", new TableInfo.Column("medicine_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchedules.put("time_hour", new TableInfo.Column("time_hour", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchedules.put("time_minute", new TableInfo.Column("time_minute", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchedules.put("is_active", new TableInfo.Column("is_active", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchedules.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSchedules = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysSchedules.add(new TableInfo.ForeignKey("medicines", "CASCADE", "NO ACTION", Arrays.asList("medicine_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesSchedules = new HashSet<TableInfo.Index>(1);
        _indicesSchedules.add(new TableInfo.Index("index_schedules_medicine_id", false, Arrays.asList("medicine_id"), Arrays.asList("ASC")));
        final TableInfo _infoSchedules = new TableInfo("schedules", _columnsSchedules, _foreignKeysSchedules, _indicesSchedules);
        final TableInfo _existingSchedules = TableInfo.read(db, "schedules");
        if (!_infoSchedules.equals(_existingSchedules)) {
          return new RoomOpenHelper.ValidationResult(false, "schedules(com.example.medication_reminder_app.data.entity.Schedule).\n"
                  + " Expected:\n" + _infoSchedules + "\n"
                  + " Found:\n" + _existingSchedules);
        }
        final HashMap<String, TableInfo.Column> _columnsHistoryLogs = new HashMap<String, TableInfo.Column>(11);
        _columnsHistoryLogs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHistoryLogs.put("medicine_id", new TableInfo.Column("medicine_id", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHistoryLogs.put("medicine_name", new TableInfo.Column("medicine_name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHistoryLogs.put("medicine_dosage", new TableInfo.Column("medicine_dosage", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHistoryLogs.put("schedule_id", new TableInfo.Column("schedule_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHistoryLogs.put("scheduled_time", new TableInfo.Column("scheduled_time", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHistoryLogs.put("scheduled_date", new TableInfo.Column("scheduled_date", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHistoryLogs.put("taken_time", new TableInfo.Column("taken_time", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHistoryLogs.put("status", new TableInfo.Column("status", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHistoryLogs.put("note", new TableInfo.Column("note", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHistoryLogs.put("snooze_first_time", new TableInfo.Column("snooze_first_time", "INTEGER", true, 0, "0", TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysHistoryLogs = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysHistoryLogs.add(new TableInfo.ForeignKey("medicines", "SET NULL", "NO ACTION", Arrays.asList("medicine_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesHistoryLogs = new HashSet<TableInfo.Index>(2);
        _indicesHistoryLogs.add(new TableInfo.Index("index_history_logs_medicine_id", false, Arrays.asList("medicine_id"), Arrays.asList("ASC")));
        _indicesHistoryLogs.add(new TableInfo.Index("index_history_logs_scheduled_date", false, Arrays.asList("scheduled_date"), Arrays.asList("ASC")));
        final TableInfo _infoHistoryLogs = new TableInfo("history_logs", _columnsHistoryLogs, _foreignKeysHistoryLogs, _indicesHistoryLogs);
        final TableInfo _existingHistoryLogs = TableInfo.read(db, "history_logs");
        if (!_infoHistoryLogs.equals(_existingHistoryLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "history_logs(com.example.medication_reminder_app.data.entity.HistoryLog).\n"
                  + " Expected:\n" + _infoHistoryLogs + "\n"
                  + " Found:\n" + _existingHistoryLogs);
        }
        final HashMap<String, TableInfo.Column> _columnsRelatives = new HashMap<String, TableInfo.Column>(5);
        _columnsRelatives.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRelatives.put("name", new TableInfo.Column("name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRelatives.put("phone", new TableInfo.Column("phone", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRelatives.put("fcm_token", new TableInfo.Column("fcm_token", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRelatives.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRelatives = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRelatives = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRelatives = new TableInfo("relatives", _columnsRelatives, _foreignKeysRelatives, _indicesRelatives);
        final TableInfo _existingRelatives = TableInfo.read(db, "relatives");
        if (!_infoRelatives.equals(_existingRelatives)) {
          return new RoomOpenHelper.ValidationResult(false, "relatives(com.example.medication_reminder_app.data.entity.Relative).\n"
                  + " Expected:\n" + _infoRelatives + "\n"
                  + " Found:\n" + _existingRelatives);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "222311dedf4d4b460ac3ab1431743cf9", "c250dccb418234195c3c811d5a95ddad");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "medicines","schedules","history_logs","relatives");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `medicines`");
      _db.execSQL("DELETE FROM `schedules`");
      _db.execSQL("DELETE FROM `history_logs`");
      _db.execSQL("DELETE FROM `relatives`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(MedicineDao.class, MedicineDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ScheduleDao.class, ScheduleDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(HistoryDao.class, HistoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RelativeDao.class, RelativeDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public MedicineDao medicineDao() {
    if (_medicineDao != null) {
      return _medicineDao;
    } else {
      synchronized(this) {
        if(_medicineDao == null) {
          _medicineDao = new MedicineDao_Impl(this);
        }
        return _medicineDao;
      }
    }
  }

  @Override
  public ScheduleDao scheduleDao() {
    if (_scheduleDao != null) {
      return _scheduleDao;
    } else {
      synchronized(this) {
        if(_scheduleDao == null) {
          _scheduleDao = new ScheduleDao_Impl(this);
        }
        return _scheduleDao;
      }
    }
  }

  @Override
  public HistoryDao historyDao() {
    if (_historyDao != null) {
      return _historyDao;
    } else {
      synchronized(this) {
        if(_historyDao == null) {
          _historyDao = new HistoryDao_Impl(this);
        }
        return _historyDao;
      }
    }
  }

  @Override
  public RelativeDao relativeDao() {
    if (_relativeDao != null) {
      return _relativeDao;
    } else {
      synchronized(this) {
        if(_relativeDao == null) {
          _relativeDao = new RelativeDao_Impl(this);
        }
        return _relativeDao;
      }
    }
  }
}
