/*
 * Decompiled with CFR 0_118.
 */
package org.sqlite.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.sqlite.SQLiteConnection;
import org.sqlite.core.Codes;
import org.sqlite.core.CoreDatabaseMetaData;
import org.sqlite.core.CoreResultSet;
import org.sqlite.core.DB;
import org.sqlite.jdbc4.JDBC4ResultSet;

public abstract class CoreStatement
implements Codes {
    public final SQLiteConnection conn;
    protected final DB db;
    protected final CoreResultSet rs;
    protected CoreDatabaseMetaData metadata;
    public long pointer;
    protected String sql = null;
    protected int batchPos;
    protected Object[] batch = null;
    protected boolean resultsWaiting = false;

    protected CoreStatement(SQLiteConnection c) {
        this.conn = c;
        this.db = this.conn.db();
        this.rs = new JDBC4ResultSet(this);
    }

    protected final void checkOpen() throws SQLException {
        if (this.pointer == 0) {
            throw new SQLException("statement is not executing");
        }
    }

    boolean isOpen() throws SQLException {
        return this.pointer != 0;
    }

    protected boolean exec() throws SQLException {
        if (this.sql == null) {
            throw new SQLException("SQLiteJDBC internal error: sql==null");
        }
        if (this.rs.isOpen()) {
            throw new SQLException("SQLite JDBC internal error: rs.isOpen() on exec.");
        }
        boolean rc = false;
        try {
            rc = this.db.execute(this, null);
        }
        finally {
            this.resultsWaiting = rc;
        }
        return this.db.column_count(this.pointer) != 0;
    }

    protected boolean exec(String sql) throws SQLException {
        if (sql == null) {
            throw new SQLException("SQLiteJDBC internal error: sql==null");
        }
        if (this.rs.isOpen()) {
            throw new SQLException("SQLite JDBC internal error: rs.isOpen() on exec.");
        }
        boolean rc = false;
        try {
            rc = this.db.execute(sql);
        }
        finally {
            this.resultsWaiting = rc;
        }
        return this.db.column_count(this.pointer) != 0;
    }

    protected void internalClose() throws SQLException {
        if (this.db.conn.isClosed()) {
            throw DB.newSQLException(1, "Connection is closed");
        }
        if (this.pointer == 0) {
            return;
        }
        this.rs.close();
        this.batch = null;
        this.batchPos = 0;
        int resp = this.db.finalize(this);
        if (resp != 0 && resp != 21) {
            this.db.throwex();
        }
    }

    public abstract ResultSet executeQuery(String var1, boolean var2) throws SQLException;
}

