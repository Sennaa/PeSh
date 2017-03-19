/*
 * Decompiled with CFR 0_118.
 */
package org.sqlite.jdbc4;

import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.util.Properties;
import org.sqlite.SQLiteConnection;
import org.sqlite.core.CoreDatabaseMetaData;
import org.sqlite.core.DB;
import org.sqlite.jdbc3.JDBC3Connection;
import org.sqlite.jdbc4.JDBC4DatabaseMetaData;
import org.sqlite.jdbc4.JDBC4PreparedStatement;
import org.sqlite.jdbc4.JDBC4Statement;

public abstract class JDBC4Connection
extends JDBC3Connection
implements Connection {
    public JDBC4Connection(String url, String fileName, Properties prop) throws SQLException {
        super(url, fileName, prop);
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        this.checkOpen();
        if (this.meta == null) {
            this.meta = new JDBC4DatabaseMetaData((SQLiteConnection)this);
        }
        return (DatabaseMetaData)((Object)this.meta);
    }

    @Override
    public Statement createStatement(int rst, int rsc, int rsh) throws SQLException {
        this.checkOpen();
        this.checkCursor(rst, rsc, rsh);
        return new JDBC4Statement((SQLiteConnection)this);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int rst, int rsc, int rsh) throws SQLException {
        this.checkOpen();
        this.checkCursor(rst, rsc, rsh);
        return new JDBC4PreparedStatement((SQLiteConnection)this, sql);
    }

    @Override
    public boolean isClosed() throws SQLException {
        return this.db == null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public Clob createClob() throws SQLException {
        return null;
    }

    @Override
    public Blob createBlob() throws SQLException {
        return null;
    }

    @Override
    public NClob createNClob() throws SQLException {
        return null;
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isValid(int timeout) throws SQLException {
        if (this.db == null) {
            return false;
        }
        Statement statement = this.createStatement();
        try {
            boolean bl = statement.execute("select 1");
            return bl;
        }
        finally {
            statement.close();
        }
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return null;
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return null;
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return null;
    }
}

