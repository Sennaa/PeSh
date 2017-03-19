/*
 * Decompiled with CFR 0_118.
 */
package org.sqlite.jdbc4;

import javax.sql.PooledConnection;
import javax.sql.StatementEventListener;

public abstract class JDBC4PooledConnection
implements PooledConnection {
    @Override
    public void addStatementEventListener(StatementEventListener listener) {
    }

    @Override
    public void removeStatementEventListener(StatementEventListener listener) {
    }
}

