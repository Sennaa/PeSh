/*
 * Decompiled with CFR 0_118.
 */
package org.sqlite.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteConnection;
import org.sqlite.core.CoreDatabaseMetaData;
import org.sqlite.core.DB;
import org.sqlite.core.NativeDB;
import org.sqlite.date.FastDateFormat;

public abstract class CoreConnection {
    private static final String RESOURCE_NAME_PREFIX = ":resource:";
    private final String url;
    private String fileName;
    protected DB db = null;
    protected CoreDatabaseMetaData meta = null;
    protected boolean autoCommit = true;
    protected int transactionIsolation = 8;
    private int busyTimeout = 0;
    protected final int openModeFlags;
    protected SQLiteConfig.TransactionMode transactionMode = SQLiteConfig.TransactionMode.DEFFERED;
    protected static final Map<SQLiteConfig.TransactionMode, String> beginCommandMap = new HashMap<SQLiteConfig.TransactionMode, String>();
    private static final Set<String> pragmaSet = new TreeSet<String>();
    public final SQLiteConfig.DateClass dateClass;
    public final SQLiteConfig.DatePrecision datePrecision;
    public final long dateMultiplier;
    public final FastDateFormat dateFormat;
    public final String dateStringFormat;

    protected CoreConnection(String url, String fileName, Properties prop) throws SQLException {
        this.url = url;
        this.fileName = this.extractPragmasFromFilename(fileName, prop);
        SQLiteConfig config = new SQLiteConfig(prop);
        this.dateClass = config.dateClass;
        this.dateMultiplier = config.dateMultiplier;
        this.dateFormat = FastDateFormat.getInstance(config.dateStringFormat);
        this.dateStringFormat = config.dateStringFormat;
        this.datePrecision = config.datePrecision;
        this.transactionMode = config.getTransactionMode();
        this.openModeFlags = config.getOpenModeFlags();
        this.open(this.openModeFlags, config.busyTimeout);
        if (fileName.startsWith("file:") && !fileName.contains("cache=")) {
            this.db.shared_cache(config.isEnabledSharedCache());
        }
        this.db.enable_load_extension(config.isEnabledLoadExtension());
        config.apply((Connection)((Object)this));
    }

    private String extractPragmasFromFilename(String filename, Properties prop) throws SQLException {
        int parameterDelimiter = filename.indexOf(63);
        if (parameterDelimiter == -1) {
            return filename;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(filename.substring(0, parameterDelimiter));
        int nonPragmaCount = 0;
        String[] parameters = filename.substring(parameterDelimiter + 1).split("&");
        for (int i = 0; i < parameters.length; ++i) {
            String parameter = parameters[parameters.length - 1 - i].trim();
            if (parameter.isEmpty()) continue;
            String[] kvp = parameter.split("=");
            String key = kvp[0].trim().toLowerCase();
            if (pragmaSet.contains(key)) {
                if (kvp.length == 1) {
                    throw new SQLException(String.format("Please specify a value for PRAGMA %s in URL %s", key, this.url));
                }
                String value = kvp[1].trim();
                if (value.isEmpty() || prop.containsKey(key)) continue;
                prop.setProperty(key, value);
                continue;
            }
            sb.append(nonPragmaCount == 0 ? '?' : '&');
            sb.append(parameter);
            ++nonPragmaCount;
        }
        String newFilename = sb.toString();
        return newFilename;
    }

    private void open(int openModeFlags, int busyTimeout) throws SQLException {
        if (!(":memory:".equals(this.fileName) || this.fileName.startsWith("file:") || this.fileName.contains("mode=memory"))) {
            if (this.fileName.startsWith(":resource:")) {
                String resourceName = this.fileName.substring(":resource:".length());
                ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
                URL resourceAddr = contextCL.getResource(resourceName);
                if (resourceAddr == null) {
                    try {
                        resourceAddr = new URL(resourceName);
                    }
                    catch (MalformedURLException e) {
                        throw new SQLException(String.format("resource %s not found: %s", resourceName, e));
                    }
                }
                try {
                    this.fileName = this.extractResource(resourceAddr).getAbsolutePath();
                }
                catch (IOException e) {
                    throw new SQLException(String.format("failed to load %s: %s", resourceName, e));
                }
            }
            File file = new File(this.fileName).getAbsoluteFile();
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                for (File up = parent; up != null && !up.exists(); up = up.getParentFile()) {
                    parent = up;
                }
                throw new SQLException("path to '" + this.fileName + "': '" + parent + "' does not exist");
            }
            try {
                if (!file.exists() && file.createNewFile()) {
                    file.delete();
                }
            }
            catch (Exception e) {
                throw new SQLException("opening db: '" + this.fileName + "': " + e.getMessage());
            }
            this.fileName = file.getAbsolutePath();
        }
        try {
            NativeDB.load();
            this.db = new NativeDB();
        }
        catch (Exception e) {
            SQLException err = new SQLException("Error opening connection");
            err.initCause(e);
            throw err;
        }
        this.db.open((SQLiteConnection)this, this.fileName, openModeFlags);
        this.setBusyTimeout(busyTimeout);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private File extractResource(URL resourceAddr) throws IOException {
        String dbFileName;
        if (resourceAddr.getProtocol().equals("file")) {
            try {
                return new File(resourceAddr.toURI());
            }
            catch (URISyntaxException e) {
                throw new IOException(e.getMessage());
            }
        }
        String tempFolder = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath();
        File dbFile = new File(tempFolder, dbFileName = String.format("sqlite-jdbc-tmp-%d.db", resourceAddr.hashCode()));
        if (dbFile.exists()) {
            long tmpFileLastModified;
            long resourceLastModified = resourceAddr.openConnection().getLastModified();
            if (resourceLastModified < (tmpFileLastModified = dbFile.lastModified())) {
                return dbFile;
            }
            boolean deletionSucceeded = dbFile.delete();
            if (!deletionSucceeded) {
                throw new IOException("failed to remove existing DB file: " + dbFile.getAbsolutePath());
            }
        }
        byte[] buffer = new byte[8192];
        FileOutputStream writer = new FileOutputStream(dbFile);
        InputStream reader = resourceAddr.openStream();
        try {
            int bytesRead = 0;
            while ((bytesRead = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, bytesRead);
            }
            File deletionSucceeded = dbFile;
            return deletionSucceeded;
        }
        finally {
            writer.close();
            reader.close();
        }
    }

    public int getBusyTimeout() {
        return this.busyTimeout;
    }

    public void setBusyTimeout(int milliseconds) throws SQLException {
        this.busyTimeout = milliseconds;
        this.db.busy_timeout(this.busyTimeout);
    }

    public String url() {
        return this.url;
    }

    public String libversion() throws SQLException {
        this.checkOpen();
        return this.db.libversion();
    }

    public DB db() {
        return this.db;
    }

    protected void checkOpen() throws SQLException {
        if (this.db == null) {
            throw new SQLException("database connection closed");
        }
    }

    protected void checkCursor(int rst, int rsc, int rsh) throws SQLException {
        if (rst != 1003) {
            throw new SQLException("SQLite only supports TYPE_FORWARD_ONLY cursors");
        }
        if (rsc != 1007) {
            throw new SQLException("SQLite only supports CONCUR_READ_ONLY cursors");
        }
        if (rsh != 2) {
            throw new SQLException("SQLite only supports closing cursors at commit");
        }
    }

    protected void setTransactionMode(SQLiteConfig.TransactionMode mode) {
        this.transactionMode = mode;
    }

    public String getDriverVersion() {
        return this.db != null ? "native" : "unloaded";
    }

    public void finalize() throws SQLException {
        this.close();
    }

    public void close() throws SQLException {
        if (this.db == null) {
            return;
        }
        if (this.meta != null) {
            this.meta.close();
        }
        this.db.close();
        this.db = null;
    }

    static {
        beginCommandMap.put(SQLiteConfig.TransactionMode.DEFFERED, "begin;");
        beginCommandMap.put(SQLiteConfig.TransactionMode.IMMEDIATE, "begin immediate;");
        beginCommandMap.put(SQLiteConfig.TransactionMode.EXCLUSIVE, "begin exclusive;");
        for (SQLiteConfig.Pragma pragma : SQLiteConfig.Pragma.values()) {
            pragmaSet.add(pragma.pragmaName);
        }
    }
}

