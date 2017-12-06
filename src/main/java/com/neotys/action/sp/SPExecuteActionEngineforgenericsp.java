package com.neotys.action.sp;

import java.util.List;



import static com.neotys.action.sp.SPExecuteAction.SqlParameter.BATCH_SIZE;
import static com.neotys.action.sp.SPExecuteAction.SqlParameter.CONNECTION_URL;
import static com.neotys.action.sp.SPExecuteAction.SqlParameter.DRIVER_CLASS_NAME;
import static com.neotys.action.sp.SPExecuteAction.SqlParameter.INCLUDE_QUERY_RESULTS;
import static com.neotys.action.sp.SPExecuteAction.SqlParameter.SQL_STATEMENT;
import static com.neotys.action.sp.SPExecuteAction.SqlParameter.TYPE;
import static com.neotys.action.sp.SPExecuteAction.SqlParameter.VARIABLE_NAME;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.neotys.action.sp.SqlCommand;
//import org.apache.commons.net.ftp.FTPClient;



import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.neotys.action.sp.SPExecuteAction.SqlParameter;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.Logger;
import com.neotys.extensions.action.engine.SampleResult;

public final class SPExecuteActionEngineforgenericsp implements ActionEngine {

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
		
		// Initialisation
				/*String con=((ActionParameter) parameters.get(0)).getValue();
				String storeprocname=((ActionParameter) parameters.get(1)).getValue();
				String params=((ActionParameter) parameters.get(2)).getValue();
				
				
				sampleResult.sampleStart();
		//parse comma seperates string to array
		appendLineToStringBuilder(requestBuilder, "parsing comma spepearted string to arary");
		String[] B = params.split(",");
		context.getLogger().debug("debugging info parameter vaues..." +B[0]+ ","+ B[1]+ "...");
		//call stored procedure method
	  try {
		
		
		
		switch(storeprocname) {
		
		case "fcrsit116_1.ap_ol_neft_process_fj_sdmc":
			
			//SP1 sp=new SP1();
		
		   // spresult=SP1.callOracleStoredFunctOUTParameter(context,con, B);
			  spresult=SP2.callOracleStoredFunctOUTParameter(context,con, B);
			  
			break;
		case "SP2":
			break;
			       }

		appendLineToStringBuilder(responseBuilder, "Store proc execution result start:"+ spresult+ ": End");
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
			appendLineToStringBuilder(responseBuilder, e.getMessage());
		}
		
		context.getLogger().debug("completed storeproc execution");
		System.out.println("completed storeproc execution");
			
				*/
				//NEw CODE TO HANDLE ANY STORE PROC WITH IN OUT & INOUT PARAMETERS DYNAMICALLY
				
				String con=((ActionParameter) parameters.get(0)).getValue();
				String storeprocname=((ActionParameter) parameters.get(1)).getValue();
				String params=((ActionParameter) parameters.get(2)).getValue();
				String paramTypes=((ActionParameter) parameters.get(3)).getValue();
				
				
				
//End of new program
				
				
				
		sampleResult.sampleStart();
		//parse comma seperates string to array
		appendLineToStringBuilder(requestBuilder, "parsing comma spepearted string to arary");
		//String[] B = params.split(",");
		//changed to exclamation for dell
		String[] B = params.split(",");
		
		String[] A =paramTypes.split(",");
		
		if (A.length!= B.length){
			//
			appendLineToStringBuilder(responseBuilder, "Error numeber of parameter values:"+ B.length+ " not match with number of parameter types :"+ A.length);
			sampleResult.setResponseContent(responseBuilder.toString());
			return sampleResult;
		}
								
		appendLineToStringBuilder(responseBuilder, "Stored procedure executing is :"+storeprocname);
		//context.getLogger().debug("debugging info parameter vaues..." +B[0]+ ","+ B[1]+ "...");
		context.getLogger().debug("debugging info parameter vaues..." +B[0]);
		//call stored procedure method
	  try {
		
		
	/*	switch(storeprocname) {
		
		case "fcrsit116_1.ap_ol_neft_process_fj_sdmc":
			
			//SP1 sp=new SP1();
		
		   // spresult=SP1.callOracleStoredFunctOUTParameter(context,con, B);
			  spresult=SP2.callOracleStoredFunctOUTParameter(context,con, B);
			  
			break;
		case "SP2":
			break;
		*/
		  spresult=SPconstructExecutelocal.callOracleStoredFunctOUTParameter(context,con,storeprocname,A,B);
		 
		                        

		appendLineToStringBuilder(responseBuilder, "Store proc execution result start:"+ spresult+ ": End");
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
			appendLineToStringBuilder(responseBuilder, e.getMessage());
			return getErrorResult(context, sampleResult, e.getMessage(), e);
		}
		
		context.getLogger().debug("completed storeproc execution");
		System.out.println("completed storeproc execution");
			
		//remove connection object added by vijesh
		//context.getCurrentVirtualUser().remove("myConnection");
		appendLineToStringBuilder(requestBuilder, "SqlConnectAction request.");
	
		// TODO perform execution.

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
		result.setStatusCode("NL-SP-ERROR");
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