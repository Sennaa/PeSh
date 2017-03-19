/*
 * Decompiled with CFR 0_118.
 */
package org.sqlite.jdbc3;

import java.io.PrintStream;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import org.sqlite.ExtendedCommand;
import org.sqlite.SQLiteConnection;
import org.sqlite.core.CoreDatabaseMetaData;
import org.sqlite.core.CoreResultSet;
import org.sqlite.core.CoreStatement;
import org.sqlite.core.DB;

public abstract class JDBC3Statement
extends CoreStatement {
    protected JDBC3Statement(SQLiteConnection conn) {
        super(conn);
    }

    public void close() throws SQLException {
        if (this.metadata != null) {
            --this.metadata.refCount;
            this.metadata.close();
            this.metadata = null;
        }
        this.internalClose();
    }

    protected void finalize() throws SQLException {
        this.close();
    }

    public boolean execute(String sql) throws SQLException {
        this.internalClose();
        ExtendedCommand.SQLExtension ext = ExtendedCommand.parse(sql);
        if (ext != null) {
            ext.execute(this.db);
            return false;
        }
        this.sql = sql;
        this.db.prepare(this);
        return this.exec();
    }

    @Override
    public ResultSet executeQuery(String sql, boolean closeStmt) throws SQLException {
        this.rs.closeStmt = closeStmt;
        return this.executeQuery(sql);
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        this.internalClose();
        this.sql = sql;
        this.db.prepare(this);
        if (!this.exec()) {
            this.internalClose();
            throw new SQLException("query does not return ResultSet", "SQLITE_DONE", 101);
        }
        return this.getResultSet();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int executeUpdate(String sql) throws SQLException {
        this.internalClose();
        this.sql = sql;
        int changes = 0;
        ExtendedCommand.SQLExtension ext = ExtendedCommand.parse(sql);
        if (ext != null) {
            ext.execute(this.db);
        } else {
            try {
                changes = this.db.total_changes();
                int statusCode = this.db._exec(sql);
                if (statusCode != 0) {
                    throw DB.newSQLException(statusCode, "");
                }
                changes = this.db.total_changes() - changes;
            }
            finally {
                this.internalClose();
            }
        }
        return changes;
    }

    public ResultSet getResultSet() throws SQLException {
        this.checkOpen();
        if (this.rs.isOpen()) {
            throw new SQLException("ResultSet already requested");
        }
        if (this.db.column_count(this.pointer) == 0) {
            return null;
        }
        if (this.rs.colsMeta == null) {
            this.rs.colsMeta = this.db.column_names(this.pointer);
        }
        this.rs.cols = this.rs.colsMeta;
        this.rs.open = this.resultsWaiting;
        this.resultsWaiting = false;
        return (ResultSet)((Object)this.rs);
    }

    public int getUpdateCount() throws SQLException {
        if (this.pointer != 0 && !this.rs.isOpen() && !this.resultsWaiting && this.db.column_count(this.pointer) == 0) {
            return this.db.changes();
        }
        return -1;
    }

    public void addBatch(String sql) throws SQLException {
        this.internalClose();
        if (this.batch == null || this.batchPos + 1 >= this.batch.length) {
            Object[] nb = new Object[java.lang.Math.max(10, this.batchPos * 2)];
            if (this.batch != null) {
                System.arraycopy(this.batch, 0, nb, 0, this.batch.length);
            }
            this.batch = nb;
        }
        this.batch[this.batchPos++] = sql;
    }

    public void clearBatch() throws SQLException {
        this.batchPos = 0;
        if (this.batch != null) {
            for (int i = 0; i < this.batch.length; ++i) {
                this.batch[i] = null;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int[] executeBatch() throws SQLException {
        this.internalClose();
        if (this.batch == null || this.batchPos == 0) {
            return new int[0];
        }
        int[] changes = new int[this.batchPos];
        DB dB = this.db;
        synchronized (dB) {
            try {
                for (int i = 0; i < changes.length; ++i) {
                    try {
                        this.sql = (String)this.batch[i];
                        this.db.prepare(this);
                        changes[i] = this.db.executeUpdate(this, null);
                    }
                    catch (SQLException e) {
                        throw new BatchUpdateException("batch entry " + i + ": " + e.getMessage(), changes);
                    }
                    finally {
                        this.db.finalize(this);
                    }
                }
            }
            finally {
                this.clearBatch();
            }
        }
        return changes;
    }

    public void setCursorName(String name) {
    }

    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    public void clearWarnings() throws SQLException {
    }

    public Connection getConnection() throws SQLException {
        return this.conn;
    }

    public void cancel() throws SQLException {
        this.db.interrupt();
    }

    public int getQueryTimeout() throws SQLException {
        return this.conn.getBusyTimeout();
    }

    public void setQueryTimeout(int seconds) throws SQLException {
        if (seconds < 0) {
            throw new SQLException("query timeout must be >= 0");
        }
        this.conn.setBusyTimeout(1000 * seconds);
    }

    public int getMaxRows() throws SQLException {
        return this.rs.maxRows;
    }

    public void setMaxRows(int max) throws SQLException {
        if (max < 0) {
            throw new SQLException("max row count must be >= 0");
        }
        this.rs.maxRows = max;
    }

    public int getMaxFieldSize() throws SQLException {
        return 0;
    }

    public void setMaxFieldSize(int max) throws SQLException {
        if (max < 0) {
            throw new SQLException("max field size " + max + " cannot be negative");
        }
    }

    public int getFetchSize() throws SQLException {
        return ((ResultSet)((Object)this.rs)).getFetchSize();
    }

    public void setFetchSize(int r) throws SQLException {
        ((ResultSet)((Object)this.rs)).setFetchSize(r);
    }

    public int getFetchDirection() throws SQLException {
        return ((ResultSet)((Object)this.rs)).getFetchDirection();
    }

    public void setFetchDirection(int d) throws SQLException {
        ((ResultSet)((Object)this.rs)).setFetchDirection(d);
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        if (this.metadata == null) {
            this.metadata = (CoreDatabaseMetaData)((Object)this.conn.getMetaData());
            ++this.metadata.refCount;
        }
        return this.metadata.getGeneratedKeys();
    }

    public boolean getMoreResults() throws SQLException {
        return this.getMoreResults(0);
    }

    public boolean getMoreResults(int c) throws SQLException {
        this.checkOpen();
        this.internalClose();
        return false;
    }

    public int getResultSetConcurrency() throws SQLException {
        return 1007;
    }

    public int getResultSetHoldability() throws SQLException {
        return 2;
    }

    public int getResultSetType() throws SQLException {
        return 1003;
    }

    public void setEscapeProcessing(boolean enable) throws SQLException {
        if (enable) {
            throw this.unused();
        }
    }

    protected SQLException unused() {
        return new SQLException("not implemented by SQLite JDBC driver");
    }

    public boolean execute(String sql, int[] colinds) throws SQLException {
        throw this.unused();
    }

    public boolean execute(String sql, String[] colnames) throws SQLException {
        throw this.unused();
    }

    public int executeUpdate(String sql, int autoKeys) throws SQLException {
        throw this.unused();
    }

    public int executeUpdate(String sql, int[] colinds) throws SQLException {
        throw this.unused();
    }

    public int executeUpdate(String sql, String[] cols) throws SQLException {
        throw this.unused();
    }

    public boolean execute(String sql, int autokeys) throws SQLException {
        throw this.unused();
    }

    static class BackupObserver
    implements DB.ProgressObserver {
        BackupObserver() {
        }

        @Override
        public void progress(int remaining, int pageCount) {
            System.out.println(String.format("remaining:%d, page count:%d", remaining, pageCount));
        }
    }

}

