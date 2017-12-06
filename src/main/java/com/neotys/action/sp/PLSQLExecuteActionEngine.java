package com.neotys.action.sp;
/**
*
* @author vijesh
*/
//import static com.neotys.action.sp.SqlConnectAction.SqlParameter.CONNECTION_URL;

import java.util.List;




import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


import com.neotys.action.sp.PLSQLExecuteAction.SqlParameter;
import com.neotys.action.sp.SqlCommand;
import com.neotys.action.sp.PLSQLengine;
import com.neotys.action.sp.parser;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.Logger;
import com.neotys.extensions.action.engine.SampleResult;

public final class PLSQLExecuteActionEngine implements ActionEngine {

	@VisibleForTesting
	static final int DEFAULT_BATCH_SIZE = 500;

	private static final String STATUS_CODE_INVALID_PARAMETER = "NL-SQL-ACTION-01";
	private static final String STATUS_CODE_INVALID_TYPE = "NL-SQL-ACTION-02";
	private static final String STATUS_CODE_ERROR_CONNECTION_CREATION = "NL-SQL-ACTION-03";

	private static final String PREFIX_CONNECTION_PROPERTIES = "connection.";

	private static boolean driversLoaded = false;

	/**
	 * Load common driver class
	 * @param context
	 */
	private static synchronized void loadDefaultDriverClass(final Logger logger) {
		if (!driversLoaded) {
			loadDriverClass(logger, "com.mysql.jdbc.Driver");
			loadDriverClass(logger, "oracle.jdbc.driver.OracleDriver");
			loadDriverClass(logger, "org.postgresql.Driver");
			loadDriverClass(logger, "com.microsoft.sqlserver.jdbc.SQLServerDriver");
			driversLoaded = true;
		}
	}

	/**
	 * Load driver class, that will register himself to DriverManager.
	 * @param context
	 * @param driverClassName
	 */
	private static void loadDriverClass(final Logger logger, final String driverClassName) {
		try {
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			logger.warn("Driver class not found : " + driverClassName);
		}
	}

	/**
	 * Initialize the map of parameters.
	 * @param parameters
	 * @return
	 */
	private Map<String, String> initParametersMap(final List<ActionParameter> parameters) {
		final Map<String, String> parametersMap = new HashMap<>();
		for (final ActionParameter parameter : parameters) {
			if (parameter.getName() != null) {
				parametersMap.put(parameter.getName(),
						parameter.getValue());
			}
		}
		// make sure there is no null values
		for (final SqlParameter parameter : SqlParameter.values()) {
			if (!parametersMap.containsKey(parameter.getName()) && parameter.getName() != null) {
				parametersMap.put(parameter.getName(), "");
			}
		}
		return parametersMap;
	}

	/**
	 * Initialize the connection properties.
	 * @param parameters
	 * @return
	 */
	private Properties initConnectionProperties(final List<ActionParameter> parameters) {
		final Properties connectionsProperties = new Properties();
		for (final ActionParameter parameter : parameters) {
			if (parameter.getName() != null && parameter.getName().startsWith(PREFIX_CONNECTION_PROPERTIES)) {
				connectionsProperties.put(parameter.getName().substring(PREFIX_CONNECTION_PROPERTIES.length()),
						parameter.getValue());
			}
		}
		return connectionsProperties;
	}

	@Override
	public SampleResult execute(final Context context, final List<ActionParameter> parameters) {
		
		String spresult="";
		
		//added by vijesh
		final SampleResult sampleResult = new SampleResult();
		final StringBuilder requestBuilder = new StringBuilder();
		final StringBuilder responseBuilder = new StringBuilder();
	
		//code for specific  sp	
		// Initialisation
				String con=((ActionParameter) parameters.get(0)).getValue();
				//String aSQLScriptFilePath = "D:\\Neoloadsupport\\storedproc\\sql1.sql";
				String aSQLScriptFilePath=((ActionParameter) parameters.get(1)).getValue();
				String contentFile_parse=((ActionParameter) parameters.get(2)).getValue();
				//String params=((ActionParameter) parameters.get(2)).getValue();
				
				
				sampleResult.sampleStart();
		//parse comma seperates string to array
	//	appendLineToStringBuilder(requestBuilder, "parsing comma spepearted string to arary");
	//	String[] B = params.split(",");
		//context.getLogger().debug("debugging info parameter vaues..." +B[0]+ ","+ B[1]+ "...");
		//call stored procedure method
	
				
		try{		

		  
		  PLSQLengine  scriptRunner = new com.neotys.action.sp.PLSQLengine(context, con,false,true);
				
		
		
		
		switch(contentFile_parse) {
		
		
			
		
		case "Y":
			 spresult=scriptRunner.runScript(parser.parse(context,aSQLScriptFilePath));
			 break;
			
		default: 	spresult=scriptRunner.runScript(new FileReader(aSQLScriptFilePath));
		            break;
			
			       
		}
		
		  appendLineToStringBuilder(responseBuilder, spresult);
		
			// Optional Part...
			List<Table> tableList; // Used to store result of 'SELECT' Query execution
			List<String> sqlOutput; // Used to store result of any quires except 'SELECT' quires

			tableList = scriptRunner.getTableList();
			sqlOutput = scriptRunner.getSqlOutput();
			
			  appendLineToStringBuilder(responseBuilder, sqlOutput.toString());
			  appendLineToStringBuilder(responseBuilder, tableList.toString());
			
		  
		} 
		catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//system.out.println(e.getMessage());
			appendLineToStringBuilder(responseBuilder, e.getMessage());
			return getErrorResult(context, sampleResult, e.getMessage(), e);
		}
		
		context.getLogger().debug("completed PL/SQL execution");
		//system.out.println("completed storeproc execution");
			
				


		sampleResult.sampleEnd();

		sampleResult.setRequestContent(requestBuilder.toString());
		sampleResult.setResponseContent(responseBuilder.toString());
		return sampleResult;
	}

	
	//added by vijesh
	private void appendLineToStringBuilder(final StringBuilder sb, final String line){
		sb.append(line).append("\n");
	}
	
	private static SampleResult getErrorResult(final Context context, final SampleResult result, final String errorMessage, final Exception exception) {
		result.setError(true);
		result.setStatusCode("NL-PLSQL-ERROR");
		result.setResponseContent(errorMessage);
		if(exception != null){
			context.getLogger().error(errorMessage, exception);
		} else{
			context.getLogger().error(errorMessage);
		}
		return result;
	}
	@VisibleForTesting
	boolean getIncludeQueryResults(String includeQueryResultsAsString) {
		if (!Strings.isNullOrEmpty(includeQueryResultsAsString)) {
			if ("true".equalsIgnoreCase(includeQueryResultsAsString.trim())) {
				return true;
			} else if ("false".equalsIgnoreCase(includeQueryResultsAsString.trim())) {
				return false;
			} else {
				throw new IllegalArgumentException("Invalid parameter " + SqlParameter.INCLUDE_QUERY_RESULTS.getName() + 
						" should be true or false but it's: " + includeQueryResultsAsString);
			}
		}
		return true; // true is default
	}

	/**
	 * Return batch size
	 * @param batchSizeAsString
	 * @return
	 */
	@VisibleForTesting
	int getBatchSize(final String batchSizeAsString) {
		int batchSize = DEFAULT_BATCH_SIZE;
		if (!Strings.isNullOrEmpty(batchSizeAsString)) {
			try {
				batchSize = Integer.valueOf(batchSizeAsString);
			} catch (final NumberFormatException nfe) {
				throw new IllegalArgumentException("Invalid " + SqlParameter.BATCH_SIZE.getName() + ": " + batchSizeAsString);
			}
			if (batchSize <= 0) {
				throw new IllegalArgumentException("Invalid " + SqlParameter.BATCH_SIZE.getName() + ": " + batchSizeAsString);
			}
		}
		return batchSize;
	}

	/**
	 * Get a connection from URL. Initialized with <code>connectionsProperties</code>. Set autoCommit to false in case of UPDATE.
	 * @param context
	 * @param connectionURL
	 * @param connectionsProperties
	 * @param autoCommit
	 * @return
	 * @throws SQLException
	 */
	@VisibleForTesting
	Connection newConnection(final Context context, final String connectionURL, final Properties connectionsProperties,
			boolean autoCommit)
			throws SQLException {
		final Connection conn = DriverManager.getConnection(connectionURL, connectionsProperties);
		conn.setAutoCommit(autoCommit);
		context.getLogger().info("Connected to database: " + connectionURL);
		return conn;
	}

	@Override
	public void stopExecute() {
	}

}