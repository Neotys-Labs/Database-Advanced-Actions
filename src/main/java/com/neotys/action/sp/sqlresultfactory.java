package com.neotys.action.sp;

/*
 * Copyright (c) 2014, Neotys
 * All rights reserved.
 */

import static com.neotys.action.sp.XmlResultWriter.generateXMLOutput;

import javax.xml.transform.TransformerException;

import com.google.common.base.Optional;
import com.google.common.collect.Multimap;
import com.neotys.action.sp.XmlResultWriter.State;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.SampleResult;

/**
 * Factory of <code>SampleResult</code>
 * @author anouvel
 *
 */
final class SqlResultFactory {
	private static final String STATUS_CODE_OK = "OK";

	/** Utilities classes are not intended to be instantiated. */
	private SqlResultFactory() {
		throw new IllegalAccessError();
	}

	/**
	 * Create error SampleResult in XML format.
	 * @param context
	 * @param statusCode
	 * @param statusMessage
	 * @param e
	 * @return
	 */
	static SampleResult newErrorResult(final Context context, final String statusCode,
			final String statusMessage, final Exception e) {
		context.getLogger().error(statusMessage, e);
		return newResult(context, true, statusCode, statusMessage, null, null, false, Optional.<Long>absent());
	}

	/**
	 * Create SampleResult in XML format from an update.
	 * @param context
	 * @param rowAffected
	 * @return
	 */
	static SampleResult newUpdateResult(final Context context, final int rowAffected, final long duration) {
		// build status message
		final StringBuilder statusMessageBuilder = new StringBuilder();
		statusMessageBuilder.append("The SQL update was executed successfully");
		if (rowAffected > 0) {
			statusMessageBuilder.append(" (" + rowAffected + " rows affected)");
		}
		statusMessageBuilder.append(".");
		return newResult(context, false, STATUS_CODE_OK, statusMessageBuilder.toString(), null, null, false, Optional.of(duration));
	}

	/**
	 * Create SampleResult in XML format from a query.
	 * @param context
	 * @param columnsForEachSqlStatement
	 * @param valuesForEachColumn
	 * @param includeQueryResults
	 * @return
	 */
	static SampleResult newQueryResult(final Context context,
			final Multimap<String, String> columnsForEachSqlStatement,
			final Multimap<String, String> valuesForEachColumn, final boolean includeQueryResults, final long duration) {
		String statusMessage = "The SQL query was executed successfully ("
				+ SqlActionUtils.getTotalRowCountAsString(columnsForEachSqlStatement, valuesForEachColumn)
				+ " rows retrieved).";
		return newResult(context, false, STATUS_CODE_OK, statusMessage, columnsForEachSqlStatement,
				valuesForEachColumn,
				includeQueryResults, Optional.of(duration));
	}

	/**
	 * Create SampleResult in XML format.
	 * @param context
	 * @param isError
	 * @param statusCode
	 * @param statusMessage
	 * @param columnsForEachSqlStatement
	 * @param valuesForEachColumn
	 * @param includeQueryResults
	 * @return
	 */
	private static SampleResult newResult(final Context context, final boolean isError, final String statusCode,
			final String statusMessage, final Multimap<String, String> columnsForEachSqlStatement,
			final Multimap<String, String> valuesForEachColumn, boolean includeQueryResults, final Optional<Long> duration) {
		final SampleResult result = new SampleResult();
		result.setStatusCode(statusCode);
		result.setError(isError);
		if(duration.isPresent())
		result.setDuration(duration.get());
		context.getLogger().debug("SQL Action execution finished with status code: " + statusCode + " (" + statusMessage + ")");

		final State state = isError ? State.ERROR : State.OK;
		try {
			result.setResponseContent(generateXMLOutput(state, statusMessage, columnsForEachSqlStatement,
					valuesForEachColumn, includeQueryResults));
		} catch (TransformerException e) {
			result.setError(true);
			context.getLogger().error("An error occurred while creating XML output: " + e.toString());
		}
		return result;
	}
}
