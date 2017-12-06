package com.neotys.action.sp;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.neotys.extensions.action.Action;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;
import com.neotys.action.sp.SqlCommand;



public final class SPExecuteAction implements Action{
	private static final String BUNDLE_NAME = "com.neotys.action.sp.bundle";
	private static final String DISPLAY_NAME = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString("displayName");
	private static final String DISPLAY_PATH = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString("displayPath");
	private static final ImageIcon LOGO_ICON = new ImageIcon(SPExecuteAction.class.getResource("sp.png"));

	
	/**
	 * Enum for custom action parameters
	 *
	 * @author anouvel
	 *
	 */
	@VisibleForTesting
	static enum SqlParameter {
		TYPE("type", "the type of SQL statement, possible values are " + SqlCommand.QUERY + " and "
				+ SqlCommand.UPDATE, true, true),
		CONNECTION_URL("connectionURL", "the JDBC connection URL (jdbc:mysql://localhost:3306/)", true, false),
		CONNECTION_NAME("connectionName", "the name of the connection to map with other advanced actions", true, true),
		CONNECTION_USER("connection.user", "the user name to connect the DB", false, false),
		CONNECTION_PASSWORD("connection.password", "the password of the user", false, false),
		CONNECTION_PROPERTY("connection.<propertyName>", "a connection property", false, false),
		SQL_STATEMENT("StoredProc Name", "the Stored procedure to execute", true, true),
		PARAMETER_VALUES("parameter values", "(required). A comma separated string of  IN,OUT & INOUT parameter value(s) -. For OUT need to pass ‘?’ as value. ex:- Martin,1234,?,ab34g", true, true),
		PARAMETER_SIGNATURE("parameter Signature", "IN,OUT & INOUT along with datatype -comma separated.VARCHAR-IN,INT-INOUT,NUMERIC-OUT,VARCHAR-IN", true, true),
		VARIABLE_NAME("variableName", "the NeoLoad variable to put the value(s) in", false, false),
		DRIVER_CLASS_NAME("driverClassName", "fully qualified name of a custom driver class. Drivers for Oracle, MS SQL Server, MySQL and PostgreSQL are embedded", false, false),
		INCLUDE_QUERY_RESULTS("includeQueryResults", "if set at false, the results are not included in the XML response. Default value is true. Should be \"false\" to optimize resource consumption when executing a query that returns multiple rows and retrieving the values from the variables", false, false),
		BATCH_SIZE("batchSize", "the batch number to keep in memory before performing a batch update. Default value is 500", false, false);

		private final String name;
		private final String description;
		private final boolean required;
		/** Whether or not the parameter is included in the description (whether the user can see it or not). */
		private final boolean visible;

		/**
		 * @param name
		 * @param description
		 * @param required
		 * @param visible Whether or not the parameter is included in the description (whether the user can see it or not). 
		 */
		SqlParameter(String name, String description, boolean required, final boolean visible) {
			this.name = name;
			this.description = description;
			this.required = required;
			this.visible = visible;
		}

		private String getRequiredOptionalString() {
			return required ? "(required) " : "(optional) ";
		}

		public String getFullDescription() {
			return "- " + name + ": " + getRequiredOptionalString() + description + ".";
		}

		public String getName() {
			return name;
		}
		
		public boolean isVisible() {
			return visible;
		}

	}

	@Override
	public String getType() {
		return "SPExecute";
	}

	@Override
	public List<ActionParameter> getDefaultActionParameters() {
		List<ActionParameter> parameters = new ArrayList<ActionParameter>();
		//parameters.add(new ActionParameter(SqlParameter.TYPE.getName(), SqlCommand.QUERY.toString()));
		parameters.add(new ActionParameter(SqlParameter.CONNECTION_NAME.getName(), "myConnection"));
		parameters.add(new ActionParameter(SqlParameter.SQL_STATEMENT.getName(), "SP Name"));
		parameters.add(new ActionParameter(SqlParameter.PARAMETER_VALUES.getName(), "val1,val2,?,val4,...."));
		parameters.add(new ActionParameter(SqlParameter.PARAMETER_SIGNATURE.getName(), "VARCHAR-IN,INT-INOUT,NUMERIC-OUT,VARCHAR-IN,...."));
		//parameters.add(new ActionParameter(SqlParameter.VARIABLE_NAME.getName(), ""));
		return parameters;
	}
	

	@Override
	public Class<? extends ActionEngine> getEngineClass() {
		//return SPExecuteActionEngine.class;
		return SPExecuteActionEngineforgenericsp.class;
	}

	@Override
	public Icon getIcon() {
		// TODO Add an icon
		return LOGO_ICON;
		//return null;
	}

	@Override
	public String getDescription() {
		final StringBuilder description = new StringBuilder();
		description.append("This action executes Storeproc\n").append("Possible parameters are:\n");

		for (final SqlParameter parameter : SqlParameter.values()) {
			if (parameter.isVisible()) {
				description.append(parameter.getFullDescription()).append("\n");
			}
		}

		return description.toString();
	}

	@Override
	public String getDisplayName() {
		return DISPLAY_NAME;
	}

	@Override
	public String getDisplayPath() {
		return DISPLAY_PATH;
	}

	@Override
	public Optional<String> getMinimumNeoLoadVersion() {
		return Optional.absent();
	}

	@Override
	public Optional<String> getMaximumNeoLoadVersion() {
		return Optional.absent();
	}
}
