package com.neotys.action.sp;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.apache.xerces.util.XMLChar;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Multimap;
import com.neotys.extensions.action.engine.VariableManager;

/**
 * Utilities class for Custom Action SQL
 * @author anouvel
 *
 */
final class SqlActionUtils {

	@VisibleForTesting
	static final String DEFAULT_COLUMN_NAME = "column";
	@VisibleForTesting
	static final String RAND_NAME = "rand";
	@VisibleForTesting
	static final String COUNT_NAME = "count";
	@VisibleForTesting
	static final String MATCH_NR_NAME = "matchNr";

	private static final Random RANDOM = new Random();

	static final String SEP_VAR = "_";

	/** Utility classes are not intended to be instantiated. */
	private SqlActionUtils() {
		throw new IllegalAccessError();
	}

	/**
	 * Get a unique column label for variables and remove invalid XML characters.
	 * @param multimap
	 * @param label
	 * @return unique label
	 */
	static String uniqueColumnName(Multimap<String, String> multimap, String label) {
		final String columnLabel = getValidXmlName(label);
		if (!multimap.values().contains(columnLabel)) {
			return columnLabel;
		}
		int index = columnLabel.length() - 1;
		while (index >= 0 && Character.isDigit(columnLabel.charAt(index))) {
			index--;
		}
		String base = columnLabel;
		if (index >= 0 && columnLabel.charAt(index) == '_') {
			base = columnLabel.substring(0, index);
		}

		int j = 1;
		String newLabel = base + "_" + j;
		while (multimap.values().contains(newLabel)) {
			j++;
			newLabel = base + "_" + j;
		}
		return newLabel;
	}

	/**
	 * Remove invalid XML characters.
	 * @param label
	 * @return
	 */
	private static String getValidXmlName(String label) {
		final StringBuilder validNameBuilder = new StringBuilder();
		for(int index=0;index<label.length();index++){
			char c = label.charAt(index);
			if ((index == 0 && XMLChar.isNameStart(c)) || (index != 0 && XMLChar.isName(c))) {
				validNameBuilder.append(c);
			}
		}
		final String validName = validNameBuilder.toString();
		if(validName.isEmpty()){
			return DEFAULT_COLUMN_NAME;
		}
		return validName;
	}

	/**
	 * Create one multi-valued variable per column : variableName_[columnName]_[row].
	 * @param variableManager
	 * @param valuesForEachColumn
	 * @param variableName
	 */
	static void createVariables(VariableManager variableManager, Multimap<String, String> valuesForEachColumn,
			String variableName) {
		final Set<String> columnNames = valuesForEachColumn.keySet();
		int randomRow = -1;
		for (final String columnName : columnNames) {
			final Iterator<String> valuesIt = valuesForEachColumn.get(columnName).iterator();
			int row = 1;
			int rowCount = valuesForEachColumn.get(columnName).size();
			while (valuesIt.hasNext()) {
				final String value = valuesIt.next();
				if (row == 1) {
					//first row, we set values count
					variableManager.setValue(variableName + SEP_VAR + columnName, value);
					variableManager.setValue(variableName + SEP_VAR + columnName + SEP_VAR + COUNT_NAME,
							Integer.toString(rowCount));
					variableManager.setValue(variableName + SEP_VAR + columnName + SEP_VAR + MATCH_NR_NAME,
							Integer.toString(rowCount));
				}
				if (row == 1 && randomRow == -1) {
					//first row and first column
					variableManager.setValue(variableName, value);
					//create a random value between 1 and rowCount
					randomRow = RANDOM.nextInt(rowCount) + 1;
				}
				variableManager.setValue(variableName + SEP_VAR + columnName + SEP_VAR + (row++), value);
			}
			variableManager.setValue(variableName + SEP_VAR + columnName + SEP_VAR + RAND_NAME,
					variableManager.getValue(variableName + SEP_VAR + columnName + SEP_VAR + randomRow));
		}

	}

	/**
	 * Get the total of rows retrieved by all queries executed.
	 * @param columnsForEachSqlStatement
	 * @param valuesForEachColumn
	 * @return
	 */
	static String getTotalRowCountAsString(final Multimap<String, String> columnsForEachSqlStatement,
			final Multimap<String, String> valuesForEachColumn) {
		int count = 0;
		if (columnsForEachSqlStatement == null || valuesForEachColumn == null) {
			return Integer.toString(count);
		}
		for (final String result : columnsForEachSqlStatement.keySet()) {
			final String firstColumn = columnsForEachSqlStatement.get(result).iterator().next();
			if (valuesForEachColumn.containsKey(firstColumn)) {
				count += valuesForEachColumn.get(firstColumn).size();
			}
		}
		return Integer.toString(count);
	}
}
