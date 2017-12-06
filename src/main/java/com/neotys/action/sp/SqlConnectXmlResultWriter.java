/*
 * Copyright (c) 2014, Neotys
 * All rights reserved.
 */
package com.neotys.action.sp;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Multimap;

/**
 * Write XML format in <code>String</code> for SQL Action response.
 *
 * Example of XML generated:
 * <pre>
 * {@code
 * 	<Output>
 *   	<Status state="OK">The SQL query was executed successfully (1 rows retrieved).</Status>
 *   	<Results>
 *      	<Result rowCount="1">
 *           	<Row>
 *              	<count>16</count>
 *           	</Row>
 *       	</Result>
 *   	</Results>
 *	</Output>
 *}
 * </pre>
 *
 * @author anouvel
 */
final class SqlConnectXmlResultWriter {

	@VisibleForTesting
	static final String OUTPUT_ELEMENT_NAME = "Output";
	@VisibleForTesting
	static final String STATUS_ELEMENT_NAME = "Status";
	@VisibleForTesting
	static final String RESULTS_ELEMENT_NAME = "Results";
	@VisibleForTesting
	static final String RESULT_ELEMENT_NAME = "Result";
	@VisibleForTesting
	static final String ROW_ELEMENT_NAME = "Row";

	@VisibleForTesting
	static final String STATE_ATTRIBUT_NAME = "state";
	@VisibleForTesting
	static final String ROW_COUNT_ATTRIBUT_NAME = "rowCount";

	/**
	 * Enum for the state of the SQL action result
	 * @author anouvel
	 *
	 */
	static enum State {
		OK,
		ERROR
	}

	/** Utilities classes are not intended to be instantiated. */
	private SqlConnectXmlResultWriter() {
		throw new IllegalAccessError();
	}

	/**
	 * Get row count for on sql statement
	 * @param columnsForEachSqlStatement
	 * @param valuesForEachColumn
	 * @param result
	 * @return
	 */
	private static String getRowCountAsString(final Multimap<String, String> columnsForEachSqlStatement,
			final Multimap<String, String> valuesForEachColumn, final String result) {
		String column = columnsForEachSqlStatement.get(result).iterator().next();
		return Integer.toString(valuesForEachColumn.get(column).size());
	}

	/**
	 * Generate output in XML as <code>String</code>
	 * @param state
	 * @param statusMessage
	 * @param columnsForEachSqlStatement
	 * @param valuesForEachColumn
	 * @param includeQueryResults
	 * @return
	 * @throws TransformerException
	 * @throws ParserConfigurationException
	 */
	static String generateXMLOutput(final State state, final String statusMessage,
			final Multimap<String, String> columnsForEachSqlStatement,
			final Multimap<String, String> valuesForEachColumn, final boolean includeQueryResults)
			throws TransformerException, ParserConfigurationException {
		final DocumentBuilderFactory dFact = DocumentBuilderFactory.newInstance();
		final DocumentBuilder build = dFact.newDocumentBuilder();
		final Document doc = build.newDocument();

		// create output element
		final Element outputElement = doc.createElement(OUTPUT_ELEMENT_NAME);
		doc.appendChild(outputElement);

		// create status element
		final Element statusElement = doc.createElement(STATUS_ELEMENT_NAME);
		final Attr stateAttribute = doc.createAttribute(STATE_ATTRIBUT_NAME);
		stateAttribute.setValue(state.name());
		statusElement.setAttributeNode(stateAttribute);
		statusElement.appendChild(doc.createTextNode(statusMessage));
		outputElement.appendChild(statusElement);

		// add results if there are any
		if (includeQueryResults && (columnsForEachSqlStatement != null && !columnsForEachSqlStatement.isEmpty())) {
			// create Results element
			final Element resultsElement = doc.createElement(RESULTS_ELEMENT_NAME);
			outputElement.appendChild(resultsElement);
			for (final String result : columnsForEachSqlStatement.keySet()) {
				// create Result element
				final Element resultElement = doc.createElement(RESULT_ELEMENT_NAME);
				Attr rowCountAttribute = doc.createAttribute(ROW_COUNT_ATTRIBUT_NAME);
				rowCountAttribute.setValue(getRowCountAsString(columnsForEachSqlStatement, valuesForEachColumn, result));
				resultElement.setAttributeNode(rowCountAttribute);
				resultsElement.appendChild(resultElement);

				final List<Element> rowList = new ArrayList<>();
				for (String columnName : columnsForEachSqlStatement.get(result)) {
					int index = 0;
					for (final String value : valuesForEachColumn.get(columnName)) {
						Element rowElement = index < rowList.size() ? rowList.get(index) : null;
						if (rowElement == null) {
							// create Row element
							rowElement = doc.createElement(ROW_ELEMENT_NAME);
							resultElement.appendChild(rowElement);
							rowList.add(rowElement);
						}

						// create Column element
						final Element columnElement = doc.createElement(columnName);
						columnElement.appendChild(doc.createTextNode(value));
						rowElement.appendChild(columnElement);

						index++;
					}
				}
			}
		}

		// Transform XML to String
		final TransformerFactory tFact = TransformerFactory.newInstance();
		final Transformer trans = tFact.newTransformer();
		trans.setOutputProperty(OutputKeys.INDENT, "yes");
		trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		final StringWriter writer = new StringWriter();
		final StreamResult result = new StreamResult(writer);
		final DOMSource source = new DOMSource(doc);
		trans.transform(source, result);

		return writer.toString();
	}
}
