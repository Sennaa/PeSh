/*
 * Decompiled with CFR 0_118.
 */
package org.sqlite.jdbc3;

import java.sql.CallableStatement;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteOpenMode;
import org.sqlite.core.CoreConnection;
import org.sqlite.core.DB;
import org.sqlite.jdbc3.JDBC3Savepoint;

public abstract class JDBC3Connection
extends CoreConnection {
    private final AtomicInteger savePoint = new AtomicInteger(0);

    protected JDBC3Connection(String url, String fileName, Properties prop) throws SQLException {
        super(url, fileName, prop);
    }

    public String getCatalog() throws SQLException {
        this.checkOpen();
        return null;
    }

    public void setCatalog(String catalog) throws SQLException {
        this.checkOpen();
    }

    public int getHoldability() throws SQLException {
        this.checkOpen();
        return 2;
    }

    public void setHoldability(int h) throws SQLException {
        this.checkOpen();
        if (h != 2) {
            throw new SQLException("SQLite only supports CLOSE_CURSORS_AT_COMMIT");
        }
    }

    public int getTransactionIsolation() {
        return this.transactionIsolation;
    }

    public void setTransactionIsolation(int level) throws SQLException {
        this.checkOpen();
        switch (level) {
            case 8: {
                this.db.exec("PRAGMA read_uncommitted = false;");
                break;
            }
            case 1: {
                this.db.exec("PRAGMA read_uncommitted = true;");
                break;
            }
            default: {
                throw new SQLException("SQLite supports only TRANSACTION_SERIALIZABLE and TRANSACTION_READ_UNCOMMITTED.");
            }
        }
        this.transactionIsolation = level;
    }

    public Map<String, Class<?>> getTypeMap() throws SQLException {
        throw new SQLException("not yet implemented");
    }

    public void setTypeMap(Map map) throws SQLException {
        throw new SQLException("not yet implemented");
    }

    public boolean isReadOnly() throws SQLException {
        return (this.openModeFlags & SQLiteOpenMode.READONLY.flag) != 0;
    }

    public void setReadOnly(boolean ro) throws SQLException {
        if (ro != this.isReadOnly()) {
            throw new SQLException("Cannot change read-only flag after establishing a connection. Use SQLiteConfig#setReadOnly and SQLiteConfig.createConnection().");
        }
    }

    public abstract DatabaseMetaData getMetaData() throws SQLException;

    public String nativeSQL(String sql) {
        return sql;
    }

    public void clearWarnings() throws SQLException {
    }

    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    public boolean getAutoCommit() throws SQLException {
        this.checkOpen();
        return this.autoCommit;
    }

    public void setAutoCommit(boolean ac) throws SQLException {
        this.checkOpen();
        if (this.autoCommit == ac) {
            return;
        }
        this.autoCommit = ac;
        this.db.exec(this.autoCommit ? "commit;" : (String)beginCommandMap.get(this.transactionMode));
    }

    public void commit() throws SQLException {
        this.checkOpen();
        if (this.autoCommit) {
            throw new SQLException("database in auto-commit mode");
        }
        this.db.exec("commit;");
        this.db.exec((String)beginCommandMap.get(this.transactionMode));
    }

    public void rollback() throws SQLException {
        this.checkOpen();
        if (this.autoCommit) {
            throw new SQLException("database in auto-commit mode");
        }
        this.db.exec("rollback;");
        this.db.exec((String)beginCommandMap.get(this.transactionMode));
    }

    public Statement createStatement() throws SQLException {
        return this.createStatement(1003, 1007, 2);
    }

    public Statement createStatement(int rsType, int rsConcurr) throws SQLException {
        return this.createStatement(rsType, rsConcurr, 2);
    }

    public abstract Statement createStatement(int var1, int var2, int var3) throws SQLException;

    public CallableStatement prepareCall(String sql) throws SQLException {
        return this.prepareCall(sql, 1003, 1007, 2);
    }

    public CallableStatement prepareCall(String sql, int rst, int rsc) throws SQLException {
        return this.prepareCall(sql, rst, rsc, 2);
    }

    public CallableStatement prepareCall(String sql, int rst, int rsc, int rsh) throws SQLException {
        throw new SQLException("SQLite does not support Stored Procedures");
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return this.prepareStatement(sql, 1003, 1007);
    }

    public PreparedStatement prepareStatement(String sql, int autoC) throws SQLException {
        return this.prepareStatement(sql);
    }

    public PreparedStatement prepareStatement(String sql, int[] colInds) throws SQLException {
        return this.prepareStatement(sql);
    }

    public PreparedStatement prepareStatement(String sql, String[] colNames) throws SQLException {
        return this.prepareStatement(sql);
    }

    public PreparedStatement prepareStatement(String sql, int rst, int rsc) throws SQLException {
        return this.prepareStatement(sql, rst, rsc, 2);
    }

    public abstract PreparedStatement prepareStatement(String var1, int var2, int var3, int var4) throws SQLException;

    public Savepoint setSavepoint() throws SQLException {
        this.checkOpen();
        if (this.autoCommit) {
            this.autoCommit = false;
        }
        JDBC3Savepoint sp = new JDBC3Savepoint(this.savePoint.incrementAndGet());
        this.db.exec(String.format("SAVEPOINT %s", sp.getSavepointName()));
        return sp;
    }

    public Savepoint setSavepoint(String name) throws SQLException {
        this.checkOpen();
        if (this.autoCommit) {
            this.autoCommit = false;
        }
        JDBC3Savepoint sp = new JDBC3Savepoint(this.savePoint.incrementAndGet(), name);
        this.db.exec(String.format("SAVEPOINT %s", sp.getSavepointName()));
        return sp;
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        this.checkOpen();
        if (this.autoCommit) {
            throw new SQLException("database in auto-commit mode");
        }
        this.db.exec(String.format("RELEASE SAVEPOINT %s", savepoint.getSavepointName()));
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        this.checkOpen();
        if (this.autoCommit) {
            throw new SQLException("database in auto-commit mode");
        }
        this.db.exec(String.format("ROLLBACK TO SAVEPOINT %s", savepoint.getSavepointName()));
    }

    public Struct createStruct(String t, Object[] attr) throws SQLException {
        throw new SQLException("unsupported by SQLite");
    }
}

