package com.neotys.action.sp;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.SampleResult;

import java.sql.*;
import java.util.Iterator;

import static com.google.common.collect.LinkedListMultimap.create;
import static com.neotys.action.sp.SqlActionUtils.createVariables;
import static com.neotys.action.sp.SqlResultFactory.*;

/**
 * Enum for the type of SQL statement. QUERY for a SELECT and UPDATE for an INSERT, CREATE, DROP ...
 *
 * @author anouvel
 *
 */
enum SqlCommand {
	QUERY {
		/**
		 * Execute a select query.
		 * @param context
		 * @param connection
		 * @param queries
		 * @param variableName
		 * @param includeQueryResults
		 * @param batchSize
		 * @return a sample result in XML format
		 */
		@Override
		SampleResult execute(final Context context, final Connection connection, final String queries,
				final String variableName, final boolean includeQueryResults, final int batchSize) {
			final Multimap<String, String> columnsForEachSqlStatement = create();
			final Multimap<String, String> valuesForEachColumn = create();

			final long startTime = System.currentTimeMillis();
			long endTime = startTime;
			// only calculate the query results if they will be included.
			try (final Statement stmt = connection.createStatement()) {
				int queryCount = 1;
				for (final String query : STATEMENT_SPLITTER.split(queries)) {
					if (query.trim().length() == 0) {
						// empty or whitespace statement
						continue;
					}
					final String queryKey = QUERY_KEY + queryCount;
					try (final ResultSet rs = stmt.executeQuery(query)) {
						if (rs.next()) {
							// store results only if there is at least one row in results
							if (includeQueryResults) {
								storeInMapsQueryResults(rs, queryKey, columnsForEachSqlStatement, valuesForEachColumn);
							}
						} else {
							// no results
							columnsForEachSqlStatement.put(queryKey, "");
						}
					} catch (final SQLException e) {
						return newErrorResult(context, STATUS_CODE_ERROR_EXECUTING, "An error:\n" + e.toString()
								+ "This occurred while executing query:\n" + query, e);
					}
					queryCount++;
				}
				endTime = System.currentTimeMillis();
			} catch (final SQLException e) {
				return newErrorResult(context, STATUS_CODE_ERROR_EXECUTING, "An error:\n" + e.toString()
						+ "This occurred while creating statement from connection.", e);
			}
			
			if (!Strings.isNullOrEmpty(variableName) && !valuesForEachColumn.isEmpty()) {
				createVariables(context.getVariableManager(), valuesForEachColumn, variableName);
			}
			return newQueryResult(context, columnsForEachSqlStatement, valuesForEachColumn, includeQueryResults, (endTime-startTime));
		}

		/**
		 * Fill maps with ResultSet content
		 * @param rs
		 * @param queryKey
		 * @param columnsForEachSqlStatement
		 * @param valuesForEachColumn
		 * @throws SQLException
		 */
		private void storeInMapsQueryResults(final ResultSet rs, final String queryKey, final Multimap<String, String> columnsForEachSqlStatement, final Multimap<String, String> valuesForEachColumn) throws SQLException {
			// associate columns to queries
			final ResultSetMetaData metadata = rs.getMetaData();
			final int columnCount = metadata.getColumnCount();
			for (int i = 1 ; i <= columnCount ; i++) {
				String uniqueColumnLabel = SqlActionUtils.uniqueColumnName(columnsForEachSqlStatement, metadata.getColumnLabel(i));
				columnsForEachSqlStatement.put(queryKey, uniqueColumnLabel);
			}
			// ResultSet is already pointing to first row
			do {
				final Iterator<String> columnIterator = columnsForEachSqlStatement.get(queryKey).iterator();
				for (int i = 1 ; i <= columnCount ; i++) {
					// associate values to columns
					final String rowValueAsString = rs.getString(i);
					valuesForEachColumn.put(columnIterator.next(), rowValueAsString != null ? rowValueAsString : "");
				}
			} while (rs.next());
		}
	},
	UPDATE {
		/**
		 * Execute updates : CREATE, INSERT, DROP ...
		 * @param context
		 * @param connection
		 * @param sqlStatements
		 * @param variableName
		 * @param includeQueryResults
		 * @param batchSize
		 * @return a sample result in XML format
		 */
		@Override
		SampleResult execute(final Context context, final Connection connection, final String sqlStatements,
				final String variableName, final boolean includeQueryResults, final int batchSize) {
			int rowAffected = 0, countBatch = 0;
			final long startTime = System.currentTimeMillis();
			long endTime = startTime;
			try (Statement stmt = connection.createStatement()) {
				for (String statement : STATEMENT_SPLITTER.split(sqlStatements)) {
					if (statement.trim().length() == 0) {
						// empty or whitespace statement
						continue;
					}
					stmt.addBatch(statement);
					if (++countBatch % batchSize == 0) {
						rowAffected += executeBatch(stmt);
					}
				}
				rowAffected += executeBatch(stmt);
				connection.commit();
				endTime = System.currentTimeMillis();
			} catch (SQLException e) {
				try {
					if (connection != null) {
						connection.rollback();
					}
				} catch (SQLException e2) {
					context.getLogger().error("Error while rolling back", e2);
				}
				return newErrorResult(context, STATUS_CODE_ERROR_EXECUTING, "An error:\n" + e.toString()
						+ "This occurred while executing update:\n" + sqlStatements, e);
			}
			return newUpdateResult(context, rowAffected, (endTime-startTime));
		}

		/**
		 * Execute all batches and get the number of row affected.
		 * @param stmt
		 * @return
		 * @throws SQLException
		 */
		private int executeBatch(final Statement stmt) throws SQLException {
			int rowAffected = 0;
			for (int affected : stmt.executeBatch()) {
				rowAffected += affected > 0 ? affected : 0;
			}
			return rowAffected;
		}
	};

	private static final String QUERY_KEY = "QUERY";
	private static final Splitter STATEMENT_SPLITTER = Splitter.on(';');

	private static final String STATUS_CODE_ERROR_EXECUTING = "NL-SQL-ACTION-04";

	/**
	 * Execute sqlStatement according to SqlCommand
	 * @param context
	 * @param connection
	 * @param sqlStatement
	 * @param variableName
	 * @param includeQueryResults
	 * @param batchSize
	 * @return
	 */
	abstract SampleResult execute(final Context context, final Connection connection, final String sqlStatement,
			final String variableName, final boolean includeQueryResults, final int batchSize);
}

