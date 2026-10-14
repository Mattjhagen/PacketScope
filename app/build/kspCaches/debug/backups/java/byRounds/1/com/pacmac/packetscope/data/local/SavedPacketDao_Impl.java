package com.pacmac.packetscope.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SavedPacketDao_Impl implements SavedPacketDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SavedPacket> __insertionAdapterOfSavedPacket;

  private final SharedSQLiteStatement __preparedStmtOfUpdateThreatStatus;

  private final SharedSQLiteStatement __preparedStmtOfUpdateMineStatus;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public SavedPacketDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSavedPacket = new EntityInsertionAdapter<SavedPacket>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `saved_packets` (`id`,`sessionId`,`protocol`,`name`,`identifier`,`rssi`,`timestamp`,`rawBytesHex`,`isThreat`,`isMine`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SavedPacket entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getSessionId());
        statement.bindString(3, entity.getProtocol());
        statement.bindString(4, entity.getName());
        statement.bindString(5, entity.getIdentifier());
        statement.bindLong(6, entity.getRssi());
        statement.bindLong(7, entity.getTimestamp());
        statement.bindString(8, entity.getRawBytesHex());
        final int _tmp = entity.isThreat() ? 1 : 0;
        statement.bindLong(9, _tmp);
        final int _tmp_1 = entity.isMine() ? 1 : 0;
        statement.bindLong(10, _tmp_1);
      }
    };
    this.__preparedStmtOfUpdateThreatStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE saved_packets SET isThreat = ? WHERE identifier = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateMineStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE saved_packets SET isMine = ? WHERE identifier = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM saved_packets";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final SavedPacket packet, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSavedPacket.insert(packet);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<SavedPacket> packets,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSavedPacket.insert(packets);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateThreatStatus(final String identifier, final boolean isThreat,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateThreatStatus.acquire();
        int _argIndex = 1;
        final int _tmp = isThreat ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, identifier);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateThreatStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateMineStatus(final String identifier, final boolean isMine,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateMineStatus.acquire();
        int _argIndex = 1;
        final int _tmp = isMine ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, identifier);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateMineStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SavedPacket>> getPacketsForSession(final long sessionId) {
    final String _sql = "SELECT * FROM saved_packets WHERE sessionId = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, sessionId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"saved_packets"}, new Callable<List<SavedPacket>>() {
      @Override
      @NonNull
      public List<SavedPacket> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfProtocol = CursorUtil.getColumnIndexOrThrow(_cursor, "protocol");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIdentifier = CursorUtil.getColumnIndexOrThrow(_cursor, "identifier");
          final int _cursorIndexOfRssi = CursorUtil.getColumnIndexOrThrow(_cursor, "rssi");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfRawBytesHex = CursorUtil.getColumnIndexOrThrow(_cursor, "rawBytesHex");
          final int _cursorIndexOfIsThreat = CursorUtil.getColumnIndexOrThrow(_cursor, "isThreat");
          final int _cursorIndexOfIsMine = CursorUtil.getColumnIndexOrThrow(_cursor, "isMine");
          final List<SavedPacket> _result = new ArrayList<SavedPacket>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SavedPacket _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpSessionId;
            _tmpSessionId = _cursor.getLong(_cursorIndexOfSessionId);
            final String _tmpProtocol;
            _tmpProtocol = _cursor.getString(_cursorIndexOfProtocol);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpIdentifier;
            _tmpIdentifier = _cursor.getString(_cursorIndexOfIdentifier);
            final int _tmpRssi;
            _tmpRssi = _cursor.getInt(_cursorIndexOfRssi);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpRawBytesHex;
            _tmpRawBytesHex = _cursor.getString(_cursorIndexOfRawBytesHex);
            final boolean _tmpIsThreat;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsThreat);
            _tmpIsThreat = _tmp != 0;
            final boolean _tmpIsMine;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsMine);
            _tmpIsMine = _tmp_1 != 0;
            _item = new SavedPacket(_tmpId,_tmpSessionId,_tmpProtocol,_tmpName,_tmpIdentifier,_tmpRssi,_tmpTimestamp,_tmpRawBytesHex,_tmpIsThreat,_tmpIsMine);
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
  public Flow<List<SavedPacket>> getAllThreats() {
    final String _sql = "SELECT * FROM saved_packets WHERE isThreat = 1 ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"saved_packets"}, new Callable<List<SavedPacket>>() {
      @Override
      @NonNull
      public List<SavedPacket> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfProtocol = CursorUtil.getColumnIndexOrThrow(_cursor, "protocol");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIdentifier = CursorUtil.getColumnIndexOrThrow(_cursor, "identifier");
          final int _cursorIndexOfRssi = CursorUtil.getColumnIndexOrThrow(_cursor, "rssi");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfRawBytesHex = CursorUtil.getColumnIndexOrThrow(_cursor, "rawBytesHex");
          final int _cursorIndexOfIsThreat = CursorUtil.getColumnIndexOrThrow(_cursor, "isThreat");
          final int _cursorIndexOfIsMine = CursorUtil.getColumnIndexOrThrow(_cursor, "isMine");
          final List<SavedPacket> _result = new ArrayList<SavedPacket>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SavedPacket _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpSessionId;
            _tmpSessionId = _cursor.getLong(_cursorIndexOfSessionId);
            final String _tmpProtocol;
            _tmpProtocol = _cursor.getString(_cursorIndexOfProtocol);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpIdentifier;
            _tmpIdentifier = _cursor.getString(_cursorIndexOfIdentifier);
            final int _tmpRssi;
            _tmpRssi = _cursor.getInt(_cursorIndexOfRssi);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpRawBytesHex;
            _tmpRawBytesHex = _cursor.getString(_cursorIndexOfRawBytesHex);
            final boolean _tmpIsThreat;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsThreat);
            _tmpIsThreat = _tmp != 0;
            final boolean _tmpIsMine;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsMine);
            _tmpIsMine = _tmp_1 != 0;
            _item = new SavedPacket(_tmpId,_tmpSessionId,_tmpProtocol,_tmpName,_tmpIdentifier,_tmpRssi,_tmpTimestamp,_tmpRawBytesHex,_tmpIsThreat,_tmpIsMine);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
