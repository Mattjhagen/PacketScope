package com.pacmac.packetscope.data.local;

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
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile ScanSessionDao _scanSessionDao;

  private volatile SavedPacketDao _savedPacketDao;

  private volatile MyDeviceDao _myDeviceDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `scan_sessions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestamp` INTEGER NOT NULL, `type` TEXT NOT NULL, `count` INTEGER NOT NULL, `locationLat` REAL, `locationLon` REAL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `saved_packets` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sessionId` INTEGER NOT NULL, `protocol` TEXT NOT NULL, `name` TEXT NOT NULL, `identifier` TEXT NOT NULL, `rssi` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `rawBytesHex` TEXT NOT NULL, `isThreat` INTEGER NOT NULL, `isMine` INTEGER NOT NULL, FOREIGN KEY(`sessionId`) REFERENCES `scan_sessions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_saved_packets_sessionId` ON `saved_packets` (`sessionId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `my_devices` (`identifier` TEXT NOT NULL, `name` TEXT NOT NULL, `type` TEXT NOT NULL, `addedTimestamp` INTEGER NOT NULL, `note` TEXT NOT NULL, PRIMARY KEY(`identifier`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '6211d1b58449eb4cb1989e4d08dd0416')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `scan_sessions`");
        db.execSQL("DROP TABLE IF EXISTS `saved_packets`");
        db.execSQL("DROP TABLE IF EXISTS `my_devices`");
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
        final HashMap<String, TableInfo.Column> _columnsScanSessions = new HashMap<String, TableInfo.Column>(6);
        _columnsScanSessions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanSessions.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanSessions.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanSessions.put("count", new TableInfo.Column("count", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanSessions.put("locationLat", new TableInfo.Column("locationLat", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanSessions.put("locationLon", new TableInfo.Column("locationLon", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysScanSessions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesScanSessions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoScanSessions = new TableInfo("scan_sessions", _columnsScanSessions, _foreignKeysScanSessions, _indicesScanSessions);
        final TableInfo _existingScanSessions = TableInfo.read(db, "scan_sessions");
        if (!_infoScanSessions.equals(_existingScanSessions)) {
          return new RoomOpenHelper.ValidationResult(false, "scan_sessions(com.pacmac.packetscope.data.local.ScanSession).\n"
                  + " Expected:\n" + _infoScanSessions + "\n"
                  + " Found:\n" + _existingScanSessions);
        }
        final HashMap<String, TableInfo.Column> _columnsSavedPackets = new HashMap<String, TableInfo.Column>(10);
        _columnsSavedPackets.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedPackets.put("sessionId", new TableInfo.Column("sessionId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedPackets.put("protocol", new TableInfo.Column("protocol", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedPackets.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedPackets.put("identifier", new TableInfo.Column("identifier", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedPackets.put("rssi", new TableInfo.Column("rssi", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedPackets.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedPackets.put("rawBytesHex", new TableInfo.Column("rawBytesHex", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedPackets.put("isThreat", new TableInfo.Column("isThreat", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedPackets.put("isMine", new TableInfo.Column("isMine", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSavedPackets = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysSavedPackets.add(new TableInfo.ForeignKey("scan_sessions", "CASCADE", "NO ACTION", Arrays.asList("sessionId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesSavedPackets = new HashSet<TableInfo.Index>(1);
        _indicesSavedPackets.add(new TableInfo.Index("index_saved_packets_sessionId", false, Arrays.asList("sessionId"), Arrays.asList("ASC")));
        final TableInfo _infoSavedPackets = new TableInfo("saved_packets", _columnsSavedPackets, _foreignKeysSavedPackets, _indicesSavedPackets);
        final TableInfo _existingSavedPackets = TableInfo.read(db, "saved_packets");
        if (!_infoSavedPackets.equals(_existingSavedPackets)) {
          return new RoomOpenHelper.ValidationResult(false, "saved_packets(com.pacmac.packetscope.data.local.SavedPacket).\n"
                  + " Expected:\n" + _infoSavedPackets + "\n"
                  + " Found:\n" + _existingSavedPackets);
        }
        final HashMap<String, TableInfo.Column> _columnsMyDevices = new HashMap<String, TableInfo.Column>(5);
        _columnsMyDevices.put("identifier", new TableInfo.Column("identifier", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMyDevices.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMyDevices.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMyDevices.put("addedTimestamp", new TableInfo.Column("addedTimestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMyDevices.put("note", new TableInfo.Column("note", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMyDevices = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMyDevices = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMyDevices = new TableInfo("my_devices", _columnsMyDevices, _foreignKeysMyDevices, _indicesMyDevices);
        final TableInfo _existingMyDevices = TableInfo.read(db, "my_devices");
        if (!_infoMyDevices.equals(_existingMyDevices)) {
          return new RoomOpenHelper.ValidationResult(false, "my_devices(com.pacmac.packetscope.data.local.MyDevice).\n"
                  + " Expected:\n" + _infoMyDevices + "\n"
                  + " Found:\n" + _existingMyDevices);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "6211d1b58449eb4cb1989e4d08dd0416", "b7375994f06a6d8235561eb86aa01902");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "scan_sessions","saved_packets","my_devices");
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
      _db.execSQL("DELETE FROM `scan_sessions`");
      _db.execSQL("DELETE FROM `saved_packets`");
      _db.execSQL("DELETE FROM `my_devices`");
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
    _typeConvertersMap.put(ScanSessionDao.class, ScanSessionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SavedPacketDao.class, SavedPacketDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MyDeviceDao.class, MyDeviceDao_Impl.getRequiredConverters());
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
  public ScanSessionDao scanSessionDao() {
    if (_scanSessionDao != null) {
      return _scanSessionDao;
    } else {
      synchronized(this) {
        if(_scanSessionDao == null) {
          _scanSessionDao = new ScanSessionDao_Impl(this);
        }
        return _scanSessionDao;
      }
    }
  }

  @Override
  public SavedPacketDao savedPacketDao() {
    if (_savedPacketDao != null) {
      return _savedPacketDao;
    } else {
      synchronized(this) {
        if(_savedPacketDao == null) {
          _savedPacketDao = new SavedPacketDao_Impl(this);
        }
        return _savedPacketDao;
      }
    }
  }

  @Override
  public MyDeviceDao myDeviceDao() {
    if (_myDeviceDao != null) {
      return _myDeviceDao;
    } else {
      synchronized(this) {
        if(_myDeviceDao == null) {
          _myDeviceDao = new MyDeviceDao_Impl(this);
        }
        return _myDeviceDao;
      }
    }
  }
}
