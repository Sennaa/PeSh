/*
 * Decompiled with CFR 0_118.
 */
package org.sqlite;

import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Executor;
import org.sqlite.jdbc4.JDBC4Connection;

public class SQLiteConnection
extends JDBC4Connection {
    public SQLiteConnection(String url, String fileName) throws SQLException {
        this(url, fileName, new Properties());
    }

    public SQLiteConnection(String url, String fileName, Properties prop) throws SQLException {
        super(url, fileName, prop);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
    }

    @Override
    public String getSchema() throws SQLException {
        return null;
    }

    @Override
    public void abort(Executor executor) throws SQLException {
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return 0;
    }
}

