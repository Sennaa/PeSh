/*
 * Decompiled with CFR 0_118.
 */
package org.sqlite.core;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.sqlite.core.Codes;
import org.sqlite.core.CoreStatement;
import org.sqlite.core.DB;

public abstract class CoreResultSet
implements Codes {
    protected final CoreStatement stmt;
    protected final DB db;
    public boolean open = false;
    public int maxRows;
    public String[] cols = null;
    public String[] colsMeta = null;
    protected boolean[][] meta = null;
    protected int limitRows;
    protected int row = 0;
    protected int lastCol;
    public boolean closeStmt;
    protected Map<String, Integer> columnNameToIndex = null;

    protected CoreResultSet(CoreStatement stmt) {
        this.stmt = stmt;
        this.db = stmt.db;
    }

    public boolean isOpen() {
        return this.open;
    }

    protected void checkOpen() throws SQLException {
        if (!this.open) {
            throw new SQLException("ResultSet closed");
        }
    }

    public int checkCol(int col) throws SQLException {
        if (this.colsMeta == null) {
            throw new IllegalStateException("SQLite JDBC: inconsistent internal state");
        }
        if (col < 1 || col > this.colsMeta.length) {
            throw new SQLException("column " + col + " out of bounds [1," + this.colsMeta.length + "]");
        }
        return --col;
    }

    protected int markCol(int col) throws SQLException {
        this.checkOpen();
        this.checkCol(col);
        this.lastCol = col--;
        return col;
    }

    public void checkMeta() throws SQLException {
        this.checkCol(1);
        if (this.meta == null) {
            this.meta = this.db.column_metadata(this.stmt.pointer);
        }
    }

    public void close() throws SQLException {
        this.cols = null;
        this.colsMeta = null;
        this.meta = null;
        this.open = false;
        this.limitRows = 0;
        this.row = 0;
        this.lastCol = -1;
        this.columnNameToIndex = null;
        if (this.stmt == null) {
            return;
        }
        if (this.stmt != null && this.stmt.pointer != 0) {
            this.db.reset(this.stmt.pointer);
            if (this.closeStmt) {
                this.closeStmt = false;
                ((Statement)((Object)this.stmt)).close();
            }
        }
    }

    protected Integer findColumnIndexInCache(String col) {
        if (this.columnNameToIndex == null) {
            return null;
        }
        return this.columnNameToIndex.get(col);
    }

    protected int addColumnIndexInCache(String col, int index) {
        if (this.columnNameToIndex == null) {
            this.columnNameToIndex = new HashMap<String, Integer>(this.cols.length);
        }
        this.columnNameToIndex.put(col, index);
        return index;
    }
}

