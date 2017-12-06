package com.neotys.action.sp;
/**
*
* @author vijesh
*/
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



public final class PLSQLExecuteAction implements Action{
	private static final String BUNDLE_NAME = "com.neotys.action.sp.PLSQLbundle";
	private static final String DISPLAY_NAME = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString("displayName");
	private static final String DISPLAY_PATH = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString("displayPath");
	private static final ImageIcon LOGO_ICON = new ImageIcon(PLSQLExecuteAction.class.getResource("plsql.png"));
	
	
	/**
	 * Enum for custom action parameters
	 *
	 * @author anouvel
	 *
	 */
	@VisibleForTesting
	static enum SqlParameter {
		
		CONNECTION_NAME("connectionName", "the name of the connection to map with other advanced actions", true, true),
		PLSQLFile_path("PLSQL file name ", "PLSQL file name with path of saved location", true, true),
		contentFile_parse("contentFile_parse", "Whether to parse the file to replace variables. Possible values are Y/N , Default value= N", true, true),
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
		return "PLSQLExecute";
	}

	@Override
	public List<ActionParameter> getDefaultActionParameters() {
		List<ActionParameter> parameters = new ArrayList<ActionParameter>();
		//parameters.add(new ActionParameter(SqlParameter.TYPE.getName(), SqlCommand.QUERY.toString()));
		parameters.add(new ActionParameter(SqlParameter.CONNECTION_NAME.getName(), "myConnection"));
		parameters.add(new ActionParameter(SqlParameter.PLSQLFile_path.getName(), "PL/SQL File path"));
		parameters.add(new ActionParameter(SqlParameter.contentFile_parse.getName(), "N"));
		
		return parameters;
	}
	

	@Override
	public Class<? extends ActionEngine> getEngineClass() {
		//return SPExecuteActionEngine.class;
		return PLSQLExecuteActionEngine.class;
	}

	@Override
	public Icon getIcon() {
		// TODO Add an icon
		//new ImageIcon(new ImageIcon("tonImage.jpg").getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT));
		return LOGO_ICON;
		//return null;
	}

	@Override
	public String getDescription() {
		final StringBuilder description = new StringBuilder();
		description.append("This action executes PLSQL\n").append("Possible parameters are:\n");

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
