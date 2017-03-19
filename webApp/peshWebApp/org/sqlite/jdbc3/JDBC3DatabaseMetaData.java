/*
 * Decompiled with CFR 0_118.
 */
package org.sqlite.jdbc3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.sqlite.SQLiteConnection;
import org.sqlite.core.CoreDatabaseMetaData;
import org.sqlite.core.CoreStatement;
import org.sqlite.util.StringUtils;

public abstract class JDBC3DatabaseMetaData
extends CoreDatabaseMetaData {
    protected static final Pattern TYPE_INTEGER = Pattern.compile(".*(INT|BOOL).*");
    protected static final Pattern TYPE_VARCHAR = Pattern.compile(".*(CHAR|CLOB|TEXT|BLOB).*");
    protected static final Pattern TYPE_FLOAT = Pattern.compile(".*(REAL|FLOA|DOUB|DEC|NUM).*");
    private static final Map<String, Integer> RULE_MAP = new HashMap<String, Integer>();
    protected static final Pattern FK_NAMED_PATTERN;
    protected static final Pattern PK_UNNAMED_PATTERN;
    protected static final Pattern PK_NAMED_PATTERN;

    protected JDBC3DatabaseMetaData(SQLiteConnection conn) {
        super(conn);
    }

    public Connection getConnection() {
        return this.conn;
    }

    public int getDatabaseMajorVersion() {
        return 3;
    }

    public int getDatabaseMinorVersion() {
        return 0;
    }

    public int getDriverMajorVersion() {
        return 1;
    }

    public int getDriverMinorVersion() {
        return 1;
    }

    public int getJDBCMajorVersion() {
        return 2;
    }

    public int getJDBCMinorVersion() {
        return 1;
    }

    public int getDefaultTransactionIsolation() {
        return 8;
    }

    public int getMaxBinaryLiteralLength() {
        return 0;
    }

    public int getMaxCatalogNameLength() {
        return 0;
    }

    public int getMaxCharLiteralLength() {
        return 0;
    }

    public int getMaxColumnNameLength() {
        return 0;
    }

    public int getMaxColumnsInGroupBy() {
        return 0;
    }

    public int getMaxColumnsInIndex() {
        return 0;
    }

    public int getMaxColumnsInOrderBy() {
        return 0;
    }

    public int getMaxColumnsInSelect() {
        return 0;
    }

    public int getMaxColumnsInTable() {
        return 0;
    }

    public int getMaxConnections() {
        return 0;
    }

    public int getMaxCursorNameLength() {
        return 0;
    }

    public int getMaxIndexLength() {
        return 0;
    }

    public int getMaxProcedureNameLength() {
        return 0;
    }

    public int getMaxRowSize() {
        return 0;
    }

    public int getMaxSchemaNameLength() {
        return 0;
    }

    public int getMaxStatementLength() {
        return 0;
    }

    public int getMaxStatements() {
        return 0;
    }

    public int getMaxTableNameLength() {
        return 0;
    }

    public int getMaxTablesInSelect() {
        return 0;
    }

    public int getMaxUserNameLength() {
        return 0;
    }

    public int getResultSetHoldability() {
        return 2;
    }

    public int getSQLStateType() {
        return 2;
    }

    public String getDatabaseProductName() {
        return "SQLite";
    }

    public String getDatabaseProductVersion() throws SQLException {
        return this.conn.libversion();
    }

    public String getDriverName() {
        return "SQLiteJDBC";
    }

    public String getDriverVersion() {
        return this.conn.getDriverVersion();
    }

    public String getExtraNameCharacters() {
        return "";
    }

    public String getCatalogSeparator() {
        return ".";
    }

    public String getCatalogTerm() {
        return "catalog";
    }

    public String getSchemaTerm() {
        return "schema";
    }

    public String getProcedureTerm() {
        return "not_implemented";
    }

    public String getSearchStringEscape() {
        return null;
    }

    public String getIdentifierQuoteString() {
        return " ";
    }

    public String getSQLKeywords() {
        return "";
    }

    public String getNumericFunctions() {
        return "";
    }

    public String getStringFunctions() {
        return "";
    }

    public String getSystemFunctions() {
        return "";
    }

    public String getTimeDateFunctions() {
        return "";
    }

    public String getURL() {
        return this.conn.url();
    }

    public String getUserName() {
        return null;
    }

    public boolean allProceduresAreCallable() {
        return false;
    }

    public boolean allTablesAreSelectable() {
        return true;
    }

    public boolean dataDefinitionCausesTransactionCommit() {
        return false;
    }

    public boolean dataDefinitionIgnoredInTransactions() {
        return false;
    }

    public boolean doesMaxRowSizeIncludeBlobs() {
        return false;
    }

    public boolean deletesAreDetected(int type) {
        return false;
    }

    public boolean insertsAreDetected(int type) {
        return false;
    }

    public boolean isCatalogAtStart() {
        return true;
    }

    public boolean locatorsUpdateCopy() {
        return false;
    }

    public boolean nullPlusNonNullIsNull() {
        return true;
    }

    public boolean nullsAreSortedAtEnd() {
        return !this.nullsAreSortedAtStart();
    }

    public boolean nullsAreSortedAtStart() {
        return true;
    }

    public boolean nullsAreSortedHigh() {
        return true;
    }

    public boolean nullsAreSortedLow() {
        return !this.nullsAreSortedHigh();
    }

    public boolean othersDeletesAreVisible(int type) {
        return false;
    }

    public boolean othersInsertsAreVisible(int type) {
        return false;
    }

    public boolean othersUpdatesAreVisible(int type) {
        return false;
    }

    public boolean ownDeletesAreVisible(int type) {
        return false;
    }

    public boolean ownInsertsAreVisible(int type) {
        return false;
    }

    public boolean ownUpdatesAreVisible(int type) {
        return false;
    }

    public boolean storesLowerCaseIdentifiers() {
        return false;
    }

    public boolean storesLowerCaseQuotedIdentifiers() {
        return false;
    }

    public boolean storesMixedCaseIdentifiers() {
        return true;
    }

    public boolean storesMixedCaseQuotedIdentifiers() {
        return false;
    }

    public boolean storesUpperCaseIdentifiers() {
        return false;
    }

    public boolean storesUpperCaseQuotedIdentifiers() {
        return false;
    }

    public boolean supportsAlterTableWithAddColumn() {
        return false;
    }

    public boolean supportsAlterTableWithDropColumn() {
        return false;
    }

    public boolean supportsANSI92EntryLevelSQL() {
        return false;
    }

    public boolean supportsANSI92FullSQL() {
        return false;
    }

    public boolean supportsANSI92IntermediateSQL() {
        return false;
    }

    public boolean supportsBatchUpdates() {
        return true;
    }

    public boolean supportsCatalogsInDataManipulation() {
        return false;
    }

    public boolean supportsCatalogsInIndexDefinitions() {
        return false;
    }

    public boolean supportsCatalogsInPrivilegeDefinitions() {
        return false;
    }

    public boolean supportsCatalogsInProcedureCalls() {
        return false;
    }

    public boolean supportsCatalogsInTableDefinitions() {
        return false;
    }

    public boolean supportsColumnAliasing() {
        return true;
    }

    public boolean supportsConvert() {
        return false;
    }

    public boolean supportsConvert(int fromType, int toType) {
        return false;
    }

    public boolean supportsCorrelatedSubqueries() {
        return false;
    }

    public boolean supportsDataDefinitionAndDataManipulationTransactions() {
        return true;
    }

    public boolean supportsDataManipulationTransactionsOnly() {
        return false;
    }

    public boolean supportsDifferentTableCorrelationNames() {
        return false;
    }

    public boolean supportsExpressionsInOrderBy() {
        return true;
    }

    public boolean supportsMinimumSQLGrammar() {
        return true;
    }

    public boolean supportsCoreSQLGrammar() {
        return true;
    }

    public boolean supportsExtendedSQLGrammar() {
        return false;
    }

    public boolean supportsLimitedOuterJoins() {
        return true;
    }

    public boolean supportsFullOuterJoins() {
        return false;
    }

    public boolean supportsGetGeneratedKeys() {
        return true;
    }

    public boolean supportsGroupBy() {
        return true;
    }

    public boolean supportsGroupByBeyondSelect() {
        return false;
    }

    public boolean supportsGroupByUnrelated() {
        return false;
    }

    public boolean supportsIntegrityEnhancementFacility() {
        return false;
    }

    public boolean supportsLikeEscapeClause() {
        return false;
    }

    public boolean supportsMixedCaseIdentifiers() {
        return true;
    }

    public boolean supportsMixedCaseQuotedIdentifiers() {
        return false;
    }

    public boolean supportsMultipleOpenResults() {
        return false;
    }

    public boolean supportsMultipleResultSets() {
        return false;
    }

    public boolean supportsMultipleTransactions() {
        return true;
    }

    public boolean supportsNamedParameters() {
        return true;
    }

    public boolean supportsNonNullableColumns() {
        return true;
    }

    public boolean supportsOpenCursorsAcrossCommit() {
        return false;
    }

    public boolean supportsOpenCursorsAcrossRollback() {
        return false;
    }

    public boolean supportsOpenStatementsAcrossCommit() {
        return false;
    }

    public boolean supportsOpenStatementsAcrossRollback() {
        return false;
    }

    public boolean supportsOrderByUnrelated() {
        return false;
    }

    public boolean supportsOuterJoins() {
        return true;
    }

    public boolean supportsPositionedDelete() {
        return false;
    }

    public boolean supportsPositionedUpdate() {
        return false;
    }

    public boolean supportsResultSetConcurrency(int t, int c) {
        return t == 1003 && c == 1007;
    }

    public boolean supportsResultSetHoldability(int h) {
        return h == 2;
    }

    public boolean supportsResultSetType(int t) {
        return t == 1003;
    }

    public boolean supportsSavepoints() {
        return false;
    }

    public boolean supportsSchemasInDataManipulation() {
        return false;
    }

    public boolean supportsSchemasInIndexDefinitions() {
        return false;
    }

    public boolean supportsSchemasInPrivilegeDefinitions() {
        return false;
    }

    public boolean supportsSchemasInProcedureCalls() {
        return false;
    }

    public boolean supportsSchemasInTableDefinitions() {
        return false;
    }

    public boolean supportsSelectForUpdate() {
        return false;
    }

    public boolean supportsStatementPooling() {
        return false;
    }

    public boolean supportsStoredProcedures() {
        return false;
    }

    public boolean supportsSubqueriesInComparisons() {
        return false;
    }

    public boolean supportsSubqueriesInExists() {
        return true;
    }

    public boolean supportsSubqueriesInIns() {
        return true;
    }

    public boolean supportsSubqueriesInQuantifieds() {
        return false;
    }

    public boolean supportsTableCorrelationNames() {
        return false;
    }

    public boolean supportsTransactionIsolationLevel(int level) {
        return level == 8;
    }

    public boolean supportsTransactions() {
        return true;
    }

    public boolean supportsUnion() {
        return true;
    }

    public boolean supportsUnionAll() {
        return true;
    }

    public boolean updatesAreDetected(int type) {
        return false;
    }

    public boolean usesLocalFilePerTable() {
        return false;
    }

    public boolean usesLocalFiles() {
        return true;
    }

    public boolean isReadOnly() throws SQLException {
        return this.conn.isReadOnly();
    }

    public ResultSet getAttributes(String c, String s, String t, String a) throws SQLException {
        if (this.getAttributes == null) {
            this.getAttributes = this.conn.prepareStatement("select null as TYPE_CAT, null as TYPE_SCHEM, null as TYPE_NAME, null as ATTR_NAME, null as DATA_TYPE, null as ATTR_TYPE_NAME, null as ATTR_SIZE, null as DECIMAL_DIGITS, null as NUM_PREC_RADIX, null as NULLABLE, null as REMARKS, null as ATTR_DEF, null as SQL_DATA_TYPE, null as SQL_DATETIME_SUB, null as CHAR_OCTET_LENGTH, null as ORDINAL_POSITION, null as IS_NULLABLE, null as SCOPE_CATALOG, null as SCOPE_SCHEMA, null as SCOPE_TABLE, null as SOURCE_DATA_TYPE limit 0;");
        }
        return this.getAttributes.executeQuery();
    }

    public ResultSet getBestRowIdentifier(String c, String s, String t, int scope, boolean n) throws SQLException {
        if (this.getBestRowIdentifier == null) {
            this.getBestRowIdentifier = this.conn.prepareStatement("select null as SCOPE, null as COLUMN_NAME, null as DATA_TYPE, null as TYPE_NAME, null as COLUMN_SIZE, null as BUFFER_LENGTH, null as DECIMAL_DIGITS, null as PSEUDO_COLUMN limit 0;");
        }
        return this.getBestRowIdentifier.executeQuery();
    }

    public ResultSet getColumnPrivileges(String c, String s, String t, String colPat) throws SQLException {
        if (this.getColumnPrivileges == null) {
            this.getColumnPrivileges = this.conn.prepareStatement("select null as TABLE_CAT, null as TABLE_SCHEM, null as TABLE_NAME, null as COLUMN_NAME, null as GRANTOR, null as GRANTEE, null as PRIVILEGE, null as IS_GRANTABLE limit 0;");
        }
        return this.getColumnPrivileges.executeQuery();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ResultSet getColumns(String c, String s, String tblNamePattern, String colNamePattern) throws SQLException {
        StringBuilder sql;
        boolean colFound;
        this.checkOpen();
        sql = new StringBuilder(700);
        sql.append("select null as TABLE_CAT, null as TABLE_SCHEM, tblname as TABLE_NAME, ").append("cn as COLUMN_NAME, ct as DATA_TYPE, tn as TYPE_NAME, 2000000000 as COLUMN_SIZE, ").append("2000000000 as BUFFER_LENGTH, 10   as DECIMAL_DIGITS, 10   as NUM_PREC_RADIX, ").append("colnullable as NULLABLE, null as REMARKS, colDefault as COLUMN_DEF, ").append("0    as SQL_DATA_TYPE, 0    as SQL_DATETIME_SUB, 2000000000 as CHAR_OCTET_LENGTH, ").append("ordpos as ORDINAL_POSITION, (case colnullable when 0 then 'NO' when 1 then 'YES' else '' end)").append("    as IS_NULLABLE, null as SCOPE_CATLOG, null as SCOPE_SCHEMA, ").append("null as SCOPE_TABLE, null as SOURCE_DATA_TYPE from (");
        colFound = false;
        ResultSet rs = null;
        try {
            String[] types = new String[]{"TABLE", "VIEW"};
            rs = this.getTables(c, s, tblNamePattern, types);
            while (rs.next()) {
                String tableName = rs.getString(3);
                Statement colstat = this.conn.createStatement();
                ResultSet rscol = null;
                try {
                    String pragmaStatement = "PRAGMA table_info('" + tableName + "')";
                    rscol = colstat.executeQuery(pragmaStatement);
                    int i = 0;
                    while (rscol.next()) {
                        String colName = rscol.getString(2);
                        String colType = rscol.getString(3);
                        String colNotNull = rscol.getString(4);
                        String colDefault = rscol.getString(5);
                        int colNullable = 2;
                        if (colNotNull != null) {
                            int n = colNullable = colNotNull.equals("0") ? 1 : 0;
                        }
                        if (colFound) {
                            sql.append(" union all ");
                        }
                        colFound = true;
                        colType = colType == null ? "TEXT" : colType.toUpperCase();
                        int colJavaType = -1;
                        colJavaType = TYPE_INTEGER.matcher(colType).find() ? 4 : (TYPE_VARCHAR.matcher(colType).find() ? 12 : (TYPE_FLOAT.matcher(colType).find() ? 6 : 12));
                        sql.append("select ").append(i).append(" as ordpos, ").append(colNullable).append(" as colnullable,").append("'").append(colJavaType).append("' as ct, ").append("'").append(tableName).append("' as tblname, ").append("'").append(this.escape(colName)).append("' as cn, ").append("'").append(this.escape(colType)).append("' as tn, ").append(JDBC3DatabaseMetaData.quote(colDefault == null ? null : this.escape(colDefault))).append(" as colDefault");
                        if (colNamePattern != null) {
                            sql.append(" where upper(cn) like upper('").append(this.escape(colNamePattern)).append("')");
                        }
                        ++i;
                    }
                    continue;
                }
                finally {
                    if (rscol != null) {
                        try {
                            rscol.close();
                        }
                        catch (SQLException pragmaStatement) {}
                    }
                    if (colstat == null) continue;
                    try {
                        colstat.close();
                        continue;
                    }
                    catch (SQLException pragmaStatement) {}
                    continue;
                }
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (colFound) {
            sql.append(") order by TABLE_SCHEM, TABLE_NAME, ORDINAL_POSITION;");
        } else {
            sql.append("select null as ordpos, null as colnullable, null as ct, null as tblname, null as cn, null as tn, null as colDefault) limit 0;");
        }
        Statement stat = this.conn.createStatement();
        return ((CoreStatement)((Object)stat)).executeQuery(sql.toString(), true);
    }

    public ResultSet getCrossReference(String pc, String ps, String pt, String fc, String fs, String ft) throws SQLException {
        if (pt == null) {
            return this.getExportedKeys(fc, fs, ft);
        }
        if (ft == null) {
            return this.getImportedKeys(pc, ps, pt);
        }
        StringBuilder query = new StringBuilder();
        query.append("select ").append(JDBC3DatabaseMetaData.quote(pc)).append(" as PKTABLE_CAT, ").append(JDBC3DatabaseMetaData.quote(ps)).append(" as PKTABLE_SCHEM, ").append(JDBC3DatabaseMetaData.quote(pt)).append(" as PKTABLE_NAME, ").append("'' as PKCOLUMN_NAME, ").append(JDBC3DatabaseMetaData.quote(fc)).append(" as FKTABLE_CAT, ").append(JDBC3DatabaseMetaData.quote(fs)).append(" as FKTABLE_SCHEM, ").append(JDBC3DatabaseMetaData.quote(ft)).append(" as FKTABLE_NAME, ").append("'' as FKCOLUMN_NAME, -1 as KEY_SEQ, 3 as UPDATE_RULE, 3 as DELETE_RULE, '' as FK_NAME, '' as PK_NAME, ").append(Integer.toString(5)).append(" as DEFERRABILITY limit 0 ");
        return ((CoreStatement)((Object)this.conn.createStatement())).executeQuery(query.toString(), true);
    }

    public ResultSet getSchemas() throws SQLException {
        if (this.getSchemas == null) {
            this.getSchemas = this.conn.prepareStatement("select null as TABLE_SCHEM, null as TABLE_CATALOG limit 0;");
        }
        return this.getSchemas.executeQuery();
    }

    public ResultSet getCatalogs() throws SQLException {
        if (this.getCatalogs == null) {
            this.getCatalogs = this.conn.prepareStatement("select null as TABLE_CAT limit 0;");
        }
        return this.getCatalogs.executeQuery();
    }

    public ResultSet getPrimaryKeys(String c, String s, String table) throws SQLException {
        PrimaryKeyFinder pkFinder = new PrimaryKeyFinder(table);
        String[] columns = pkFinder.getColumns();
        Statement stat = this.conn.createStatement();
        StringBuilder sql = new StringBuilder(512);
        sql.append("select null as TABLE_CAT, null as TABLE_SCHEM, '").append(this.escape(table)).append("' as TABLE_NAME, cn as COLUMN_NAME, ks as KEY_SEQ, pk as PK_NAME from (");
        if (columns == null) {
            sql.append("select null as cn, null as pk, 0 as ks) limit 0;");
            return ((CoreStatement)((Object)stat)).executeQuery(sql.toString(), true);
        }
        String pkName = pkFinder.getName();
        for (int i = 0; i < columns.length; ++i) {
            if (i > 0) {
                sql.append(" union ");
            }
            sql.append("select ").append(pkName).append(" as pk, '").append(this.escape(columns[i].trim())).append("' as cn, ").append(i).append(" as ks");
        }
        return ((CoreStatement)((Object)stat)).executeQuery(sql.append(") order by cn;").toString(), true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
        PrimaryKeyFinder pkFinder = new PrimaryKeyFinder(table);
        String[] pkColumns = pkFinder.getColumns();
        Statement stat = this.conn.createStatement();
        catalog = catalog != null ? JDBC3DatabaseMetaData.quote(catalog) : null;
        schema = schema != null ? JDBC3DatabaseMetaData.quote(schema) : null;
        StringBuilder exportedKeysQuery = new StringBuilder(512);
        int count = 0;
        if (pkColumns != null) {
            ResultSet rs = stat.executeQuery("select name from sqlite_master where type = 'table'");
            ArrayList<String> tableList = new ArrayList<String>();
            while (rs.next()) {
                tableList.add(rs.getString(1));
            }
            rs.close();
            ResultSet fk = null;
            String target = table.toLowerCase();
            for (String tbl : tableList) {
                try {
                    fk = stat.executeQuery("pragma foreign_key_list('" + this.escape(tbl) + "')");
                }
                catch (SQLException e) {
                    if (e.getErrorCode() == 101) continue;
                    throw e;
                }
                Statement stat2 = null;
                try {
                    stat2 = this.conn.createStatement();
                    while (fk.next()) {
                        int keySeq = fk.getInt(2) + 1;
                        String PKTabName = fk.getString(3).toLowerCase();
                        if (PKTabName == null || !PKTabName.equals(target)) continue;
                        String PKColName = fk.getString(5);
                        PKColName = PKColName == null ? pkColumns[0] : PKColName.toLowerCase();
                        exportedKeysQuery.append(count > 0 ? " union all select " : "select ").append(Integer.toString(keySeq)).append(" as ks, lower('").append(this.escape(tbl)).append("') as fkt, lower('").append(this.escape(fk.getString(4))).append("') as fcn, '").append(this.escape(PKColName)).append("' as pcn, ").append(RULE_MAP.get(fk.getString(6))).append(" as ur, ").append(RULE_MAP.get(fk.getString(7))).append(" as dr, ");
                        rs = stat2.executeQuery("select sql from sqlite_master where lower(name) = lower('" + this.escape(tbl) + "')");
                        if (rs.next()) {
                            Matcher matcher = FK_NAMED_PATTERN.matcher(rs.getString(1));
                            if (matcher.find()) {
                                exportedKeysQuery.append("'").append(this.escape(matcher.group(1).toLowerCase())).append("' as fkn");
                            } else {
                                exportedKeysQuery.append("'' as fkn");
                            }
                        }
                        rs.close();
                        ++count;
                    }
                    continue;
                }
                finally {
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (SQLException keySeq) {}
                    try {
                        if (stat2 != null) {
                            stat2.close();
                        }
                    }
                    catch (SQLException keySeq) {}
                    try {
                        if (fk == null) continue;
                        fk.close();
                        continue;
                    }
                    catch (SQLException keySeq) {}
                    continue;
                }
            }
        }
        boolean hasImportedKey = count > 0;
        StringBuilder sql = new StringBuilder(512);
        sql.append("select ").append(catalog).append(" as PKTABLE_CAT, ").append(schema).append(" as PKTABLE_SCHEM, ").append(JDBC3DatabaseMetaData.quote(table)).append(" as PKTABLE_NAME, ").append(hasImportedKey ? "pcn" : "''").append(" as PKCOLUMN_NAME, ").append(catalog).append(" as FKTABLE_CAT, ").append(schema).append(" as FKTABLE_SCHEM, ").append(hasImportedKey ? "fkt" : "''").append(" as FKTABLE_NAME, ").append(hasImportedKey ? "fcn" : "''").append(" as FKCOLUMN_NAME, ").append(hasImportedKey ? "ks" : "-1").append(" as KEY_SEQ, ").append(hasImportedKey ? "ur" : "3").append(" as UPDATE_RULE, ").append(hasImportedKey ? "dr" : "3").append(" as DELETE_RULE, ").append(hasImportedKey ? "fkn" : "''").append(" as FK_NAME, ").append(pkFinder.getName() != null ? pkFinder.getName() : "''").append(" as PK_NAME, ").append(Integer.toString(5)).append(" as DEFERRABILITY ");
        if (hasImportedKey) {
            sql.append("from (").append(exportedKeysQuery).append(") order by fkt");
        } else {
            sql.append("limit 0");
        }
        return ((CoreStatement)((Object)stat)).executeQuery(sql.toString(), true);
    }

    public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
        ResultSet rs = null;
        Statement stat = this.conn.createStatement();
        StringBuilder sql = new StringBuilder(700);
        sql.append("select ").append(JDBC3DatabaseMetaData.quote(catalog)).append(" as PKTABLE_CAT, ").append(JDBC3DatabaseMetaData.quote(schema)).append(" as PKTABLE_SCHEM, ").append("ptn as PKTABLE_NAME, pcn as PKCOLUMN_NAME, ").append(JDBC3DatabaseMetaData.quote(catalog)).append(" as FKTABLE_CAT, ").append(JDBC3DatabaseMetaData.quote(schema)).append(" as FKTABLE_SCHEM, ").append(JDBC3DatabaseMetaData.quote(table)).append(" as FKTABLE_NAME, ").append("fcn as FKCOLUMN_NAME, ks as KEY_SEQ, ur as UPDATE_RULE, dr as DELETE_RULE, '' as FK_NAME, '' as PK_NAME, ").append(Integer.toString(5)).append(" as DEFERRABILITY from (");
        try {
            rs = stat.executeQuery("pragma foreign_key_list('" + this.escape(table) + "');");
        }
        catch (SQLException e) {
            sql.append("select -1 as ks, '' as ptn, '' as fcn, '' as pcn, ").append(3).append(" as ur, ").append(3).append(" as dr) limit 0;");
            return ((CoreStatement)((Object)stat)).executeQuery(sql.toString(), true);
        }
        int i = 0;
        while (rs.next()) {
            int keySeq = rs.getInt(2) + 1;
            String PKTabName = rs.getString(3);
            String FKColName = rs.getString(4);
            String PKColName = rs.getString(5);
            if (PKColName == null) {
                PKColName = new PrimaryKeyFinder(PKTabName).getColumns()[0];
            }
            String updateRule = rs.getString(6);
            String deleteRule = rs.getString(7);
            if (i > 0) {
                sql.append(" union all ");
            }
            sql.append("select ").append(keySeq).append(" as ks,").append("'").append(this.escape(PKTabName)).append("' as ptn, '").append(this.escape(FKColName)).append("' as fcn, '").append(this.escape(PKColName)).append("' as pcn,").append("case '").append(this.escape(updateRule)).append("'").append(" when 'NO ACTION' then ").append(3).append(" when 'CASCADE' then ").append(0).append(" when 'RESTRICT' then ").append(1).append(" when 'SET NULL' then ").append(2).append(" when 'SET DEFAULT' then ").append(4).append(" end as ur, ").append("case '").append(this.escape(deleteRule)).append("'").append(" when 'NO ACTION' then ").append(3).append(" when 'CASCADE' then ").append(0).append(" when 'RESTRICT' then ").append(1).append(" when 'SET NULL' then ").append(2).append(" when 'SET DEFAULT' then ").append(4).append(" end as dr");
            ++i;
        }
        rs.close();
        return ((CoreStatement)((Object)stat)).executeQuery(sql.append(");").toString(), true);
    }

    public ResultSet getIndexInfo(String c, String s, String table, boolean u, boolean approximate) throws SQLException {
        ResultSet rs = null;
        Statement stat = this.conn.createStatement();
        StringBuilder sql = new StringBuilder(500);
        sql.append("select null as TABLE_CAT, null as TABLE_SCHEM, '").append(this.escape(table)).append("' as TABLE_NAME, un as NON_UNIQUE, null as INDEX_QUALIFIER, n as INDEX_NAME, ").append(Integer.toString(3)).append(" as TYPE, op as ORDINAL_POSITION, ").append("cn as COLUMN_NAME, null as ASC_OR_DESC, 0 as CARDINALITY, 0 as PAGES, null as FILTER_CONDITION from (");
        rs = stat.executeQuery("pragma index_list('" + this.escape(table) + "');");
        ArrayList indexList = new ArrayList();
        while (rs.next()) {
            indexList.add(new ArrayList());
            ((ArrayList)indexList.get(indexList.size() - 1)).add(rs.getString(2));
            ((ArrayList)indexList.get(indexList.size() - 1)).add(rs.getInt(3));
        }
        rs.close();
        if (indexList.size() == 0) {
            sql.append("select null as un, null as n, null as op, null as cn) limit 0;");
            return ((CoreStatement)((Object)stat)).executeQuery(sql.toString(), true);
        }
        boolean i = false;
        Iterator indexIterator = indexList.iterator();
        ArrayList<String> unionAll = new ArrayList<String>();
        while (indexIterator.hasNext()) {
            ArrayList currentIndex = (ArrayList)indexIterator.next();
            String indexName = currentIndex.get(0).toString();
            rs = stat.executeQuery("pragma index_info('" + this.escape(indexName) + "');");
            while (rs.next()) {
                StringBuilder sqlRow = new StringBuilder();
                sqlRow.append("select ").append(Integer.toString(1 - (Integer)currentIndex.get(1))).append(" as un,'").append(this.escape(indexName)).append("' as n,").append(Integer.toString(rs.getInt(1) + 1)).append(" as op,'").append(this.escape(rs.getString(3))).append("' as cn");
                unionAll.add(sqlRow.toString());
            }
            rs.close();
        }
        String sqlBlock = StringUtils.join(unionAll, " union all ");
        return ((CoreStatement)((Object)stat)).executeQuery(sql.append(sqlBlock).append(");").toString(), true);
    }

    public ResultSet getProcedureColumns(String c, String s, String p, String colPat) throws SQLException {
        if (this.getProcedures == null) {
            this.getProcedureColumns = this.conn.prepareStatement("select null as PROCEDURE_CAT, null as PROCEDURE_SCHEM, null as PROCEDURE_NAME, null as COLUMN_NAME, null as COLUMN_TYPE, null as DATA_TYPE, null as TYPE_NAME, null as PRECISION, null as LENGTH, null as SCALE, null as RADIX, null as NULLABLE, null as REMARKS limit 0;");
        }
        return this.getProcedureColumns.executeQuery();
    }

    public ResultSet getProcedures(String c, String s, String p) throws SQLException {
        if (this.getProcedures == null) {
            this.getProcedures = this.conn.prepareStatement("select null as PROCEDURE_CAT, null as PROCEDURE_SCHEM, null as PROCEDURE_NAME, null as UNDEF1, null as UNDEF2, null as UNDEF3, null as REMARKS, null as PROCEDURE_TYPE limit 0;");
        }
        return this.getProcedures.executeQuery();
    }

    public ResultSet getSuperTables(String c, String s, String t) throws SQLException {
        if (this.getSuperTables == null) {
            this.getSuperTables = this.conn.prepareStatement("select null as TABLE_CAT, null as TABLE_SCHEM, null as TABLE_NAME, null as SUPERTABLE_NAME limit 0;");
        }
        return this.getSuperTables.executeQuery();
    }

    public ResultSet getSuperTypes(String c, String s, String t) throws SQLException {
        if (this.getSuperTypes == null) {
            this.getSuperTypes = this.conn.prepareStatement("select null as TYPE_CAT, null as TYPE_SCHEM, null as TYPE_NAME, null as SUPERTYPE_CAT, null as SUPERTYPE_SCHEM, null as SUPERTYPE_NAME limit 0;");
        }
        return this.getSuperTypes.executeQuery();
    }

    public ResultSet getTablePrivileges(String c, String s, String t) throws SQLException {
        if (this.getTablePrivileges == null) {
            this.getTablePrivileges = this.conn.prepareStatement("select  null as TABLE_CAT, null as TABLE_SCHEM, null as TABLE_NAME, null as GRANTOR, null GRANTEE,  null as PRIVILEGE, null as IS_GRANTABLE limit 0;");
        }
        return this.getTablePrivileges.executeQuery();
    }

    public synchronized ResultSet getTables(String c, String s, String tblNamePattern, String[] types) throws SQLException {
        this.checkOpen();
        tblNamePattern = tblNamePattern == null || "".equals(tblNamePattern) ? "%" : this.escape(tblNamePattern);
        StringBuilder sql = new StringBuilder();
        sql.append("select null as TABLE_CAT, null as TABLE_SCHEM, name as TABLE_NAME,").append(" upper(type) as TABLE_TYPE, null as REMARKS, null as TYPE_CAT, null as TYPE_SCHEM,").append(" null as TYPE_NAME, null as SELF_REFERENCING_COL_NAME, null as REF_GENERATION").append(" from (select name, type from sqlite_master union all select name, type from sqlite_temp_master)").append(" where TABLE_NAME like '").append(tblNamePattern).append("' and TABLE_TYPE in (");
        if (types == null || types.length == 0) {
            sql.append("'TABLE','VIEW'");
        } else {
            sql.append("'").append(types[0].toUpperCase()).append("'");
            for (int i = 1; i < types.length; ++i) {
                sql.append(",'").append(types[i].toUpperCase()).append("'");
            }
        }
        sql.append(") order by TABLE_TYPE, TABLE_NAME;");
        return ((CoreStatement)((Object)this.conn.createStatement())).executeQuery(sql.toString(), true);
    }

    public ResultSet getTableTypes() throws SQLException {
        this.checkOpen();
        if (this.getTableTypes == null) {
            this.getTableTypes = this.conn.prepareStatement("select 'TABLE' as TABLE_TYPE union select 'VIEW' as TABLE_TYPE;");
        }
        this.getTableTypes.clearParameters();
        return this.getTableTypes.executeQuery();
    }

    public ResultSet getTypeInfo() throws SQLException {
        if (this.getTypeInfo == null) {
            this.getTypeInfo = this.conn.prepareStatement("select tn as TYPE_NAME, dt as DATA_TYPE, 0 as PRECISION, null as LITERAL_PREFIX, null as LITERAL_SUFFIX, null as CREATE_PARAMS, 1 as NULLABLE, 1 as CASE_SENSITIVE, 3 as SEARCHABLE, 0 as UNSIGNED_ATTRIBUTE, 0 as FIXED_PREC_SCALE, 0 as AUTO_INCREMENT, null as LOCAL_TYPE_NAME, 0 as MINIMUM_SCALE, 0 as MAXIMUM_SCALE, 0 as SQL_DATA_TYPE, 0 as SQL_DATETIME_SUB, 10 as NUM_PREC_RADIX from (    select 'BLOB' as tn, 2004 as dt union    select 'NULL' as tn, 0 as dt union    select 'REAL' as tn, 7 as dt union    select 'TEXT' as tn, 12 as dt union    select 'INTEGER' as tn, 4 as dt) order by TYPE_NAME;");
        }
        this.getTypeInfo.clearParameters();
        return this.getTypeInfo.executeQuery();
    }

    public ResultSet getUDTs(String c, String s, String t, int[] types) throws SQLException {
        if (this.getUDTs == null) {
            this.getUDTs = this.conn.prepareStatement("select  null as TYPE_CAT, null as TYPE_SCHEM, null as TYPE_NAME,  null as CLASS_NAME,  null as DATA_TYPE, null as REMARKS, null as BASE_TYPE limit 0;");
        }
        this.getUDTs.clearParameters();
        return this.getUDTs.executeQuery();
    }

    public ResultSet getVersionColumns(String c, String s, String t) throws SQLException {
        if (this.getVersionColumns == null) {
            this.getVersionColumns = this.conn.prepareStatement("select null as SCOPE, null as COLUMN_NAME, null as DATA_TYPE, null as TYPE_NAME, null as COLUMN_SIZE, null as BUFFER_LENGTH, null as DECIMAL_DIGITS, null as PSEUDO_COLUMN limit 0;");
        }
        return this.getVersionColumns.executeQuery();
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        if (this.getGeneratedKeys == null) {
            this.getGeneratedKeys = this.conn.prepareStatement("select last_insert_rowid();");
        }
        return this.getGeneratedKeys.executeQuery();
    }

    public Struct createStruct(String t, Object[] attr) throws SQLException {
        throw new SQLException("Not yet implemented by SQLite JDBC driver");
    }

    public ResultSet getFunctionColumns(String a, String b, String c, String d) throws SQLException {
        throw new SQLException("Not yet implemented by SQLite JDBC driver");
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
    }

    static {
        RULE_MAP.put("NO ACTION", 3);
        RULE_MAP.put("CASCADE", 0);
        RULE_MAP.put("RESTRICT", 1);
        RULE_MAP.put("SET NULL", 2);
        RULE_MAP.put("SET DEFAULT", 4);
        FK_NAMED_PATTERN = Pattern.compile(".*\\sCONSTRAINT\\s+(.*?)\\s*FOREIGN\\s+KEY\\s*\\((.*?)\\).*", 34);
        PK_UNNAMED_PATTERN = Pattern.compile(".*\\sPRIMARY\\s+KEY\\s+\\((.*?,+.*?)\\).*", 34);
        PK_NAMED_PATTERN = Pattern.compile(".*\\sCONSTRAINT\\s+(.*?)\\s+PRIMARY\\s+KEY\\s+\\((.*?)\\).*", 34);
    }

    class PrimaryKeyFinder {
        String table;
        String pkName;
        String[] pkColumns;

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public PrimaryKeyFinder(String table) throws SQLException {
            this.pkName = null;
            this.pkColumns = null;
            this.table = table;
            if (table == null || table.trim().length() == 0) {
                throw new SQLException("Invalid table name: '" + this.table + "'");
            }
            Statement stat = null;
            ResultSet rs = null;
            try {
                stat = JDBC3DatabaseMetaData.this.conn.createStatement();
                rs = stat.executeQuery("select sql from sqlite_master where lower(name) = lower('" + JDBC3DatabaseMetaData.this.escape(table) + "') and type = 'table'");
                if (!rs.next()) {
                    throw new SQLException("Table not found: '" + table + "'");
                }
                Matcher matcher = JDBC3DatabaseMetaData.PK_NAMED_PATTERN.matcher(rs.getString(1));
                if (matcher.find()) {
                    this.pkName = "" + '\'' + JDBC3DatabaseMetaData.this.escape(matcher.group(1).toLowerCase()) + '\'';
                    this.pkColumns = matcher.group(2).split(",");
                } else {
                    matcher = JDBC3DatabaseMetaData.PK_UNNAMED_PATTERN.matcher(rs.getString(1));
                    if (matcher.find()) {
                        this.pkColumns = matcher.group(1).split(",");
                    }
                }
                if (this.pkColumns == null) {
                    rs = stat.executeQuery("pragma table_info('" + JDBC3DatabaseMetaData.this.escape(table) + "');");
                    while (rs.next()) {
                        if (!rs.getBoolean(6)) continue;
                        this.pkColumns = new String[]{rs.getString(2)};
                    }
                }
                if (this.pkColumns != null) {
                    for (int i = 0; i < this.pkColumns.length; ++i) {
                        this.pkColumns[i] = this.pkColumns[i].toLowerCase().trim();
                    }
                }
            }
            finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (Exception matcher) {}
                try {
                    if (stat != null) {
                        stat.close();
                    }
                }
                catch (Exception matcher) {}
            }
        }

        public String getName() {
            return this.pkName;
        }

        public String[] getColumns() {
            return this.pkColumns;
        }
    }

}

