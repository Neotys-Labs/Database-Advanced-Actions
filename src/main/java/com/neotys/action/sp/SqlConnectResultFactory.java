/*
 * Copyright (c) 2014, Neotys
 * All rights reserved.
 */
package com.neotys.action.sp;

import static com.neotys.action.sp.SqlConnectXmlResultWriter.generateXMLOutput;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.google.common.collect.Multimap;
import com.neotys.action.sp.SqlConnectXmlResultWriter.State;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.SampleResult;

/**
 * Factory of <code>SampleResult</code>
 * @author anouvel
 *
 */
final class SqlConnectResultFactory {
	private static final String STATUS_CODE_OK = "OK";

	/** Utilities classes are not intended to be instantiated. */
	private SqlConnectResultFactory() {
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
		return newResult(context, true, statusCode, statusMessage, null, null, false);
	}

	/**
	 * Create SampleResult in XML format from an update.
	 * @param context
	 * @param statusMessage
	 * @return
	 */
	static SampleResult newConnectionResult(final Context context) {
		return newResult(context, false, STATUS_CODE_OK, "The SQL connection was executed successfully.", null, null,
				false);
	}

	/**
	 * Create SampleResult in XML format.
	 * @param context
	 * @param error
	 * @param statusCode
	 * @param statusMessage
	 * @param columnsForEachSqlStatement
	 * @param valuesForEachColumn
	 * @param includeQueryResults
	 * @return
	 */
	private static SampleResult newResult(final Context context, final boolean isError, final String statusCode,
			final String statusMessage, final Multimap<String, String> columnsForEachSqlStatement,
			final Multimap<String, String> valuesForEachColumn, boolean includeQueryResults) {
		final SampleResult result = new SampleResult();
		result.setStatusCode(statusCode);
		result.setError(isError);
		context.getLogger().debug("SQL connect action execution finished with status code: " + statusCode + 
				" (" + statusMessage + ")");

		final State state = isError ? State.ERROR : State.OK;
		try {
			result.setResponseContent(generateXMLOutput(state, statusMessage, columnsForEachSqlStatement,
					valuesForEachColumn, includeQueryResults));
		} catch (TransformerException | ParserConfigurationException e) {
			result.setError(true);
			context.getLogger().error("An error occurred while creating XML output: " + e.toString());
		}
		return result;
	}
}
