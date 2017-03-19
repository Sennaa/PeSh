/*
 * Decompiled with CFR 0_118.
 */
package org.sqlite;

import java.sql.Connection;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.sqlite.JDBC;
import org.sqlite.SQLiteOpenMode;

public class SQLiteConfig {
    private final Properties pragmaTable;
    private int openModeFlag = 0;
    private TransactionMode transactionMode;
    public final int busyTimeout;
    public static final String DEFAULT_DATE_STRING_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public DateClass dateClass;
    public DatePrecision datePrecision;
    public long dateMultiplier;
    public String dateStringFormat;
    private static final String[] OnOff = new String[]{"true", "false"};

    public SQLiteConfig() {
        this(new Properties());
    }

    public SQLiteConfig(Properties prop) {
        this.pragmaTable = prop;
        String openMode = this.pragmaTable.getProperty(Pragma.OPEN_MODE.pragmaName);
        if (openMode != null) {
            this.openModeFlag = Integer.parseInt(openMode);
        } else {
            this.setOpenMode(SQLiteOpenMode.READWRITE);
            this.setOpenMode(SQLiteOpenMode.CREATE);
        }
        openMode = this.pragmaTable.getProperty(Pragma.SHARED_CACHE.pragmaName);
        this.setOpenMode(SQLiteOpenMode.OPEN_URI);
        this.transactionMode = TransactionMode.getMode(this.pragmaTable.getProperty(Pragma.TRANSACTION_MODE.pragmaName, TransactionMode.DEFFERED.name()));
        this.dateClass = DateClass.getDateClass(this.pragmaTable.getProperty(Pragma.DATE_CLASS.pragmaName, DateClass.INTEGER.name()));
        this.datePrecision = DatePrecision.getPrecision(this.pragmaTable.getProperty(Pragma.DATE_PRECISION.pragmaName, DatePrecision.MILLISECONDS.name()));
        this.dateMultiplier = this.datePrecision == DatePrecision.MILLISECONDS ? 1 : 1000;
        this.dateStringFormat = this.pragmaTable.getProperty(Pragma.DATE_STRING_FORMAT.pragmaName, "yyyy-MM-dd HH:mm:ss.SSS");
        this.busyTimeout = Integer.parseInt(this.pragmaTable.getProperty(Pragma.BUSY_TIMEOUT.pragmaName, "3000"));
    }

    public Connection createConnection(String url) throws SQLException {
        return JDBC.createConnection(url, this.toProperties());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void apply(Connection conn) throws SQLException {
        HashSet<String> pragmaParams = new HashSet<String>();
        for (Pragma each : Pragma.values()) {
            pragmaParams.add(each.pragmaName);
        }
        pragmaParams.remove(Pragma.OPEN_MODE.pragmaName);
        pragmaParams.remove(Pragma.SHARED_CACHE.pragmaName);
        pragmaParams.remove(Pragma.LOAD_EXTENSION.pragmaName);
        pragmaParams.remove(Pragma.DATE_PRECISION.pragmaName);
        pragmaParams.remove(Pragma.DATE_CLASS.pragmaName);
        pragmaParams.remove(Pragma.DATE_STRING_FORMAT.pragmaName);
        Statement stat = conn.createStatement();
        try {
            for (Object each2 : this.pragmaTable.keySet()) {
                String value;
                String key = each2.toString();
                if (!pragmaParams.contains(key) || (value = this.pragmaTable.getProperty(key)) == null) continue;
                stat.execute(String.format("pragma %s=%s", key, value));
            }
        }
        finally {
            if (stat != null) {
                stat.close();
            }
        }
    }

    private void set(Pragma pragma, boolean flag) {
        this.setPragma(pragma, Boolean.toString(flag));
    }

    private void set(Pragma pragma, int num) {
        this.setPragma(pragma, Integer.toString(num));
    }

    private boolean getBoolean(Pragma pragma, String defaultValue) {
        return Boolean.parseBoolean(this.pragmaTable.getProperty(pragma.pragmaName, defaultValue));
    }

    public boolean isEnabledSharedCache() {
        return this.getBoolean(Pragma.SHARED_CACHE, "false");
    }

    public boolean isEnabledLoadExtension() {
        return this.getBoolean(Pragma.LOAD_EXTENSION, "false");
    }

    public int getOpenModeFlags() {
        return this.openModeFlag;
    }

    public void setPragma(Pragma pragma, String value) {
        this.pragmaTable.put(pragma.pragmaName, value);
    }

    public Properties toProperties() {
        this.pragmaTable.setProperty(Pragma.OPEN_MODE.pragmaName, Integer.toString(this.openModeFlag));
        this.pragmaTable.setProperty(Pragma.TRANSACTION_MODE.pragmaName, this.transactionMode.getValue());
        this.pragmaTable.setProperty(Pragma.DATE_CLASS.pragmaName, this.dateClass.getValue());
        this.pragmaTable.setProperty(Pragma.DATE_PRECISION.pragmaName, this.datePrecision.getValue());
        this.pragmaTable.setProperty(Pragma.DATE_STRING_FORMAT.pragmaName, this.dateStringFormat);
        return this.pragmaTable;
    }

    static DriverPropertyInfo[] getDriverPropertyInfo() {
        Pragma[] pragma = Pragma.values();
        DriverPropertyInfo[] result = new DriverPropertyInfo[pragma.length];
        int index = 0;
        for (Pragma p : Pragma.values()) {
            DriverPropertyInfo di = new DriverPropertyInfo(p.pragmaName, null);
            di.choices = p.choices;
            di.description = p.description;
            di.required = false;
            result[index++] = di;
        }
        return result;
    }

    public void setOpenMode(SQLiteOpenMode mode) {
        this.openModeFlag |= mode.flag;
    }

    public void resetOpenMode(SQLiteOpenMode mode) {
        this.openModeFlag &= ~ mode.flag;
    }

    public void setSharedCache(boolean enable) {
        this.set(Pragma.SHARED_CACHE, enable);
    }

    public void enableLoadExtension(boolean enable) {
        this.set(Pragma.LOAD_EXTENSION, enable);
    }

    public void setReadOnly(boolean readOnly) {
        if (readOnly) {
            this.setOpenMode(SQLiteOpenMode.READONLY);
            this.resetOpenMode(SQLiteOpenMode.CREATE);
            this.resetOpenMode(SQLiteOpenMode.READWRITE);
        } else {
            this.setOpenMode(SQLiteOpenMode.READWRITE);
            this.setOpenMode(SQLiteOpenMode.CREATE);
            this.resetOpenMode(SQLiteOpenMode.READONLY);
        }
    }

    public void setCacheSize(int numberOfPages) {
        this.set(Pragma.CACHE_SIZE, numberOfPages);
    }

    public void enableCaseSensitiveLike(boolean enable) {
        this.set(Pragma.CASE_SENSITIVE_LIKE, enable);
    }

    public void enableCountChanges(boolean enable) {
        this.set(Pragma.COUNT_CHANGES, enable);
    }

    public void setDefaultCacheSize(int numberOfPages) {
        this.set(Pragma.DEFAULT_CACHE_SIZE, numberOfPages);
    }

    public void enableEmptyResultCallBacks(boolean enable) {
        this.set(Pragma.EMPTY_RESULT_CALLBACKS, enable);
    }

    private static String[] toStringArray(PragmaValue[] list) {
        String[] result = new String[list.length];
        for (int i = 0; i < list.length; ++i) {
            result[i] = list[i].getValue();
        }
        return result;
    }

    public void setEncoding(Encoding encoding) {
        this.setPragma(Pragma.ENCODING, encoding.typeName);
    }

    public void enforceForeignKeys(boolean enforce) {
        this.set(Pragma.FOREIGN_KEYS, enforce);
    }

    public void enableFullColumnNames(boolean enable) {
        this.set(Pragma.FULL_COLUMN_NAMES, enable);
    }

    public void enableFullSync(boolean enable) {
        this.set(Pragma.FULL_SYNC, enable);
    }

    public void incrementalVacuum(int numberOfPagesToBeRemoved) {
        this.set(Pragma.INCREMENTAL_VACUUM, numberOfPagesToBeRemoved);
    }

    public void setJournalMode(JournalMode mode) {
        this.setPragma(Pragma.JOURNAL_MODE, mode.name());
    }

    public void setJounalSizeLimit(int limit) {
        this.set(Pragma.JOURNAL_SIZE_LIMIT, limit);
    }

    public void useLegacyFileFormat(boolean use) {
        this.set(Pragma.LEGACY_FILE_FORMAT, use);
    }

    public void setLockingMode(LockingMode mode) {
        this.setPragma(Pragma.LOCKING_MODE, mode.name());
    }

    public void setPageSize(int numBytes) {
        this.set(Pragma.PAGE_SIZE, numBytes);
    }

    public void setMaxPageCount(int numPages) {
        this.set(Pragma.MAX_PAGE_COUNT, numPages);
    }

    public void setReadUncommited(boolean useReadUncommitedIsolationMode) {
        this.set(Pragma.READ_UNCOMMITED, useReadUncommitedIsolationMode);
    }

    public void enableRecursiveTriggers(boolean enable) {
        this.set(Pragma.RECURSIVE_TRIGGERS, enable);
    }

    public void enableReverseUnorderedSelects(boolean enable) {
        this.set(Pragma.REVERSE_UNORDERED_SELECTS, enable);
    }

    public void enableShortColumnNames(boolean enable) {
        this.set(Pragma.SHORT_COLUMN_NAMES, enable);
    }

    public void setSynchronous(SynchronousMode mode) {
        this.setPragma(Pragma.SYNCHRONOUS, mode.name());
    }

    public void setTempStore(TempStore storeType) {
        this.setPragma(Pragma.TEMP_STORE, storeType.name());
    }

    public void setTempStoreDirectory(String directoryName) {
        this.setPragma(Pragma.TEMP_STORE_DIRECTORY, String.format("'%s'", directoryName));
    }

    public void setUserVersion(int version) {
        this.set(Pragma.USER_VERSION, version);
    }

    public void setTransactionMode(TransactionMode transactionMode) {
        this.transactionMode = transactionMode;
    }

    public void setTransactionMode(String transactionMode) {
        this.setTransactionMode(TransactionMode.getMode(transactionMode));
    }

    public TransactionMode getTransactionMode() {
        return this.transactionMode;
    }

    public void setDatePrecision(String datePrecision) throws SQLException {
        this.datePrecision = DatePrecision.getPrecision(datePrecision);
    }

    public void setDateClass(String dateClass) {
        this.dateClass = DateClass.getDateClass(dateClass);
    }

    public void setDateStringFormat(String dateStringFormat) {
        this.dateStringFormat = dateStringFormat;
    }

    public void setBusyTimeout(String milliseconds) {
        this.setPragma(Pragma.BUSY_TIMEOUT, milliseconds);
    }

    static /* synthetic */ String[] access$000() {
        return OnOff;
    }

    static /* synthetic */ String[] access$100(PragmaValue[] x0) {
        return SQLiteConfig.toStringArray(x0);
    }

    public static enum DateClass implements PragmaValue
    {
        INTEGER,
        TEXT,
        REAL;
        

        private DateClass() {
        }

        @Override
        public String getValue() {
            return this.name();
        }

        public static DateClass getDateClass(String dateClass) {
            return DateClass.valueOf(dateClass.toUpperCase());
        }
    }

    public static enum DatePrecision implements PragmaValue
    {
        SECONDS,
        MILLISECONDS;
        

        private DatePrecision() {
        }

        @Override
        public String getValue() {
            return this.name();
        }

        public static DatePrecision getPrecision(String precision) {
            return DatePrecision.valueOf(precision.toUpperCase());
        }
    }

    public static enum TransactionMode implements PragmaValue
    {
        DEFFERED,
        IMMEDIATE,
        EXCLUSIVE;
        

        private TransactionMode() {
        }

        @Override
        public String getValue() {
            return this.name();
        }

        public static TransactionMode getMode(String mode) {
            return TransactionMode.valueOf(mode.toUpperCase());
        }
    }

    public static enum TempStore implements PragmaValue
    {
        DEFAULT,
        FILE,
        MEMORY;
        

        private TempStore() {
        }

        @Override
        public String getValue() {
            return this.name();
        }
    }

    public static enum SynchronousMode implements PragmaValue
    {
        OFF,
        NORMAL,
        FULL;
        

        private SynchronousMode() {
        }

        @Override
        public String getValue() {
            return this.name();
        }
    }

    public static enum LockingMode implements PragmaValue
    {
        NORMAL,
        EXCLUSIVE;
        

        private LockingMode() {
        }

        @Override
        public String getValue() {
            return this.name();
        }
    }

    public static enum JournalMode implements PragmaValue
    {
        DELETE,
        TRUNCATE,
        PERSIST,
        MEMORY,
        WAL,
        OFF;
        

        private JournalMode() {
        }

        @Override
        public String getValue() {
            return this.name();
        }
    }

    public static enum Encoding implements PragmaValue
    {
        UTF8("'UTF-8'"),
        UTF16("'UTF-16'"),
        UTF16_LITTLE_ENDIAN("'UTF-16le'"),
        UTF16_BIG_ENDIAN("'UTF-16be'"),
        UTF_8(UTF8),
        UTF_16(UTF16),
        UTF_16LE(UTF16_LITTLE_ENDIAN),
        UTF_16BE(UTF16_BIG_ENDIAN);
        
        public final String typeName;

        private Encoding(String typeName) {
            this.typeName = typeName;
        }

        private Encoding(Encoding encoding) {
            this.typeName = encoding.getValue();
        }

        @Override
        public String getValue() {
            return this.typeName;
        }

        public static Encoding getEncoding(String value) {
            return Encoding.valueOf(value.replaceAll("-", "_").toUpperCase());
        }
    }

    private static interface PragmaValue {
        public String getValue();
    }

    public static enum Pragma {
        OPEN_MODE("open_mode", "Database open-mode flag", null),
        SHARED_CACHE("shared_cache", "Enable SQLite Shared-Cache mode, native driver only", SQLiteConfig.access$000()),
        LOAD_EXTENSION("enable_load_extension", "Enable SQLite load_extention() function, native driver only", SQLiteConfig.access$000()),
        CACHE_SIZE("cache_size"),
        CASE_SENSITIVE_LIKE("case_sensitive_like", SQLiteConfig.access$000()),
        COUNT_CHANGES("count_changes", SQLiteConfig.access$000()),
        DEFAULT_CACHE_SIZE("default_cache_size"),
        EMPTY_RESULT_CALLBACKS("empty_result_callback", SQLiteConfig.access$000()),
        ENCODING("encoding", SQLiteConfig.access$100(Encoding.values())),
        FOREIGN_KEYS("foreign_keys", SQLiteConfig.access$000()),
        FULL_COLUMN_NAMES("full_column_names", SQLiteConfig.access$000()),
        FULL_SYNC("fullsync", SQLiteConfig.access$000()),
        INCREMENTAL_VACUUM("incremental_vacuum"),
        JOURNAL_MODE("journal_mode", SQLiteConfig.access$100(JournalMode.values())),
        JOURNAL_SIZE_LIMIT("journal_size_limit"),
        LEGACY_FILE_FORMAT("legacy_file_format", SQLiteConfig.access$000()),
        LOCKING_MODE("locking_mode", SQLiteConfig.access$100(LockingMode.values())),
        PAGE_SIZE("page_size"),
        MAX_PAGE_COUNT("max_page_count"),
        READ_UNCOMMITED("read_uncommited", SQLiteConfig.access$000()),
        RECURSIVE_TRIGGERS("recursive_triggers", SQLiteConfig.access$000()),
        REVERSE_UNORDERED_SELECTS("reverse_unordered_selects", SQLiteConfig.access$000()),
        SHORT_COLUMN_NAMES("short_column_names", SQLiteConfig.access$000()),
        SYNCHRONOUS("synchronous", SQLiteConfig.access$100(SynchronousMode.values())),
        TEMP_STORE("temp_store", SQLiteConfig.access$100(TempStore.values())),
        TEMP_STORE_DIRECTORY("temp_store_directory"),
        USER_VERSION("user_version"),
        TRANSACTION_MODE("transaction_mode", SQLiteConfig.access$100(TransactionMode.values())),
        DATE_PRECISION("date_precision", "\"seconds\": Read and store integer dates as seconds from the Unix Epoch (SQLite standard).\n\"milliseconds\": (DEFAULT) Read and store integer dates as milliseconds from the Unix Epoch (Java standard).", SQLiteConfig.access$100(DatePrecision.values())),
        DATE_CLASS("date_class", "\"integer\": (Default) store dates as number of seconds or milliseconds from the Unix Epoch\n\"text\": store dates as a string of text\n\"real\": store dates as Julian Dates", SQLiteConfig.access$100(DateClass.values())),
        DATE_STRING_FORMAT("date_string_format", "Format to store and retrieve dates stored as text. Defaults to \"yyyy-MM-dd HH:mm:ss.SSS\"", null),
        BUSY_TIMEOUT("busy_timeout", null);
        
        public final String pragmaName;
        public final String[] choices;
        public final String description;

        private Pragma(String pragmaName) {
            this(pragmaName, null);
        }

        private Pragma(String pragmaName, String[] choices) {
            this(pragmaName, null, choices);
        }

        private Pragma(String pragmaName, String description, String[] choices) {
            this.pragmaName = pragmaName;
            this.description = description;
            this.choices = choices;
        }

        public final String getPragmaName() {
            return this.pragmaName;
        }
    }

}

