/*
 * Decompiled with CFR 0_118.
 */
package org.sqlite.core;

import java.sql.SQLException;
import java.util.Date;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteConnection;
import org.sqlite.core.CoreResultSet;
import org.sqlite.core.CoreStatement;
import org.sqlite.core.DB;
import org.sqlite.date.FastDateFormat;
import org.sqlite.jdbc4.JDBC4Statement;

public abstract class CorePreparedStatement
extends JDBC4Statement {
    protected int columnCount;
    protected int paramCount;

    protected CorePreparedStatement(SQLiteConnection conn, String sql) throws SQLException {
        super(conn);
        this.sql = sql;
        this.db.prepare(this);
        this.rs.colsMeta = this.db.column_names(this.pointer);
        this.columnCount = this.db.column_count(this.pointer);
        this.paramCount = this.db.bind_parameter_count(this.pointer);
        this.batch = null;
        this.batchPos = 0;
    }

    @Override
    protected void finalize() throws SQLException {
        this.close();
    }

    protected void checkParameters() throws SQLException {
        if (this.batch == null && this.paramCount > 0) {
            throw new SQLException("Values not bound to statement");
        }
    }

    @Override
    public int[] executeBatch() throws SQLException {
        if (this.batchPos == 0) {
            return new int[0];
        }
        this.checkParameters();
        try {
            int[] arrn = this.db.executeBatch(this.pointer, this.batchPos / this.paramCount, this.batch);
            return arrn;
        }
        finally {
            this.clearBatch();
        }
    }

    @Override
    public int getUpdateCount() throws SQLException {
        if (this.pointer == 0 || this.resultsWaiting || this.rs.isOpen()) {
            return -1;
        }
        return this.db.changes();
    }

    protected void batch(int pos, Object value) throws SQLException {
        this.checkOpen();
        if (this.batch == null) {
            this.batch = new Object[this.paramCount];
        }
        this.batch[this.batchPos + pos - 1] = value;
    }

    protected void setDateByMilliseconds(int pos, Long value) throws SQLException {
        switch (this.conn.dateClass) {
            case TEXT: {
                this.batch(pos, this.conn.dateFormat.format(new java.sql.Date(value)));
                break;
            }
            case REAL: {
                this.batch(pos, new Double((double)value.longValue() / 8.64E7 + 2440587.5));
                break;
            }
            default: {
                this.batch(pos, new Long(value / this.conn.dateMultiplier));
            }
        }
    }

}

