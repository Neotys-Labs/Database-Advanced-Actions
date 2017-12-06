/*
 * Copyright (c) 2014, Neotys
 * All rights reserved.
 */
package com.neotys.action.sp;

import static com.neotys.action.sp.SqlConnectAction.SqlParameter.CONNECTION_NAME;
import static com.neotys.action.sp.SqlConnectAction.SqlParameter.CONNECTION_URL;
import static com.neotys.action.sp.SqlConnectAction.SqlParameter.DRIVER_CLASS_NAME;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.neotys.action.sp.SqlConnectAction.SqlParameter;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.Logger;
import com.neotys.extensions.action.engine.SampleResult;


/**
 * Engine for the action capable of creating a connection to a SQL database.
 *
 * @author srichert
 *
 */
public class SqlConnectActionEngine implements ActionEngine {

	private static final String STATUS_CODE_INVALID_PARAMETER = "NL-SQL-CONNECT-ACTION-01";
	private static final String STATUS_CODE_ERROR_CONNECTION_CREATION = "NL-SQL-CONNECT-ACTION-02";

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
	private static Map<String, String> initParametersMap(final List<ActionParameter> parameters) {
		final Map<String, String> parametersMap = new HashMap<>();
		for (final ActionParameter parameter : parameters) {
			if (parameter.getName() != null && !parameter.getName().startsWith(PREFIX_CONNECTION_PROPERTIES)) {
				parametersMap.put(parameter.getName(),
						parameter.getValue());
			}
		}
		// make sure there is no null values
		for (final SqlParameter parameter : SqlParameter.values()) {
			if (!parametersMap.containsKey(parameter.getName()) && parameter.getName() != null
					&& !parameter.getName().startsWith(PREFIX_CONNECTION_PROPERTIES)) {
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
	private static Properties initConnectionProperties(final List<ActionParameter> parameters) {
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
		loadDefaultDriverClass(context.getLogger());
		final Map<String, String> parametersMap = initParametersMap(parameters);
		final Properties connectionsProperties = initConnectionProperties(parameters);

	
		// check required parameters		
		final String connectionURL = parametersMap.get(CONNECTION_URL.getName());
		if (Strings.isNullOrEmpty(connectionURL)) {
			return SqlConnectResultFactory.newErrorResult(context, STATUS_CODE_INVALID_PARAMETER,
					"Invalid argument: Missing parameter "
							+ CONNECTION_URL.getName(), null);
		}
		final String connectionName = parametersMap.get(CONNECTION_NAME.getName());
		if (Strings.isNullOrEmpty(connectionName)) {
			return SqlConnectResultFactory.newErrorResult(context, STATUS_CODE_INVALID_PARAMETER,
					"Invalid argument: Missing parameter "
							+ CONNECTION_NAME.getName(), null);
		}

		// load Driver
		final String driverClassName = parametersMap.get(DRIVER_CLASS_NAME.getName());
		if (!Strings.isNullOrEmpty(driverClassName)) {
			loadDriverClass(context.getLogger(), driverClassName);
		}

		// Connection
		//added by vijesh
		final SampleResult sampleResult = new SampleResult();
		final StringBuilder requestBuilder = new StringBuilder();
		final StringBuilder responseBuilder = new StringBuilder();
		
		sampleResult.sampleStart();
		
		try {
			// if an open connection with this name already exists then we warn the user and close the connection.
			final Object connectionObject = context.getCurrentVirtualUser().get(connectionName);
			if (connectionObject instanceof Connection) {
				try {
					final Connection tempConnection = (Connection) connectionObject;
					if (!tempConnection.isClosed()) {
						context.getLogger().error("An open connection was not closed properly. Name: " + 
								connectionName + ", object: " + tempConnection.getMetaData().getURL());
						tempConnection.close();
					}
				} catch (final Exception e) {
					// ignored. we don't care because this test is only here to provide more debug info to the user.
				}
			}
			
			final Connection connection = newConnection(context, connectionURL, connectionsProperties, false);

			// Put Connection in the context of the VU, so it could be retrieved further
			if (context.getLogger().isDebugEnabled()) {
				context.getLogger().debug("Put Connection in the context of the VU");
				appendLineToStringBuilder(responseBuilder,"Connected to database: " + connectionURL);
			}
			
			context.getCurrentVirtualUser().put(connectionName, connection);
			//context.getCurrentVirtualUser().put("myConnection", connection);  ///aded by vijesh to use in SP1
		} catch (final SQLException e) {
			return SqlConnectResultFactory.newErrorResult(context, STATUS_CODE_ERROR_CONNECTION_CREATION,
					"An error occurred while creating the connection: " + e.toString(), e);
		}
		
		//return SqlConnectResultFactory.newConnectionResult(context);    //commented by vijesh
		
		//added by vijesh
		sampleResult.sampleEnd();
		sampleResult.setResponseContent(responseBuilder.toString());
		
		return sampleResult;
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

	//added by vijesh
	private void appendLineToStringBuilder(final StringBuilder sb, final String line){
		sb.append(line).append("\n");
	}
	@Override
	public void stopExecute() {
	}

}
