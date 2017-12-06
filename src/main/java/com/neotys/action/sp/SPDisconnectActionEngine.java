package com.neotys.action.sp;




/*
 * Copyright (c) 2014, Neotys
 * All rights reserved.
 */


import static com.neotys.action.sp.SPDisconnectAction.SqlParameter.CONNECTION_NAME;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.neotys.action.sp.SPDisconnectAction.SqlParameter;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.SampleResult;


/**
 * Engine for the action capable of closing a connection from a SQL database.
 *
 * @author srichert
 *
 */
public class SPDisconnectActionEngine implements ActionEngine {

	private static final String STATUS_CODE_INVALID_PARAMETER = "NL-SQL-DISCONNECT-ACTION-01";
	private static final String STATUS_CODE_ERROR_DISCONNECTION = "NL-SQL-DISCONNECT-ACTION-02";

	/**
	 * Initialize the map of parameters.
	 * @param parameters
	 * @return
	 */
	private static Map<String, String> initParametersMap(final List<ActionParameter> parameters) {
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



	@Override
	public SampleResult execute(final Context context, final List<ActionParameter> parameters) {
		final Map<String, String> parametersMap = initParametersMap(parameters);

		// check required parameters				
		final String connectionName = parametersMap.get(CONNECTION_NAME.getName());
		if (Strings.isNullOrEmpty(connectionName)) {
			return SPDisconnectResultFactory.newErrorResult(context, STATUS_CODE_INVALID_PARAMETER,
					"Invalid argument: Missing parameter "
							+ CONNECTION_NAME.getName(), null);
		}

		// Get Connction from Current VU map: 
		final Object connectionObject = context.getCurrentVirtualUser().remove(connectionName);
		if (connectionObject == null) {
			return SPDisconnectResultFactory.newErrorResult(context, STATUS_CODE_INVALID_PARAMETER,
					"Invalid argument: Connection name not found. Argument: " + 
							connectionName + ", object: " + connectionObject, null);
			
		} else if (!(connectionObject instanceof Connection)) {
			return SPDisconnectResultFactory.newErrorResult(context, STATUS_CODE_INVALID_PARAMETER,
					"Invalid argument: Connection name is not the right type. Argument: " + 
							connectionName + ", object: " + connectionObject, null);
		}

		//added by vijesh
		final SampleResult sampleResult = new SampleResult();
		final StringBuilder requestBuilder = new StringBuilder();
		final StringBuilder responseBuilder = new StringBuilder();
		
		sampleResult.sampleStart();
		
		
		// Disconnection
		try {
			final Connection connection = (Connection) connectionObject;
			connection.close();
			//added by vijesh
			appendLineToStringBuilder(responseBuilder,"disconnected database: " );
			
		} catch (final SQLException e) {
			return SPDisconnectResultFactory.newErrorResult(context, STATUS_CODE_ERROR_DISCONNECTION,
					"An error occurred while closing the connection: " + e.toString(), e);
		}

		//added by vijesh
				sampleResult.sampleEnd();
				sampleResult.setResponseContent(responseBuilder.toString());
				
				return sampleResult;
		//return SPDisconnectResultFactory.newDisconnectionResult(context);  commented by vijesh
	}

	
	
	//added by vijesh
	private void appendLineToStringBuilder(final StringBuilder sb, final String line){
		sb.append(line).append("\n");
	}
	@Override
	public void stopExecute() {
	}

}
