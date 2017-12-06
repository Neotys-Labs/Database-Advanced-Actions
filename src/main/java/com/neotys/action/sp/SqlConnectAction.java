/*
 * Copyright (c) 2014, Neotys
 * All rights reserved.
 */
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
import com.neotys.extensions.action.ActionParameter.Type;
import com.neotys.extensions.action.engine.ActionEngine;

/**
 * Action capable of creating a connection to a SQL database.
 *
 * @author srichert
 *
 */
public final class SqlConnectAction implements Action {

	private static final String BUNDLE_NAME = "com.neotys.action.sp.connectbundle";
	private static final String DISPLAY_NAME = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString("displayName");
	private static final String DISPLAY_PATH = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString("displayPath");

//	private static final ImageIcon LOGO_ICON = new ImageIcon(SqlConnectAction.class.getResource("SPExecuteaction.png"));

	/**
	 * Enum for custom action parameters
	 *
	 * @author anouvel
	 *
	 */
	@VisibleForTesting
	static enum SqlParameter {
		CONNECTION_NAME("connectionName", "the name of the connection to map with other advanced actions", true),
		CONNECTION_URL("connectionURL", "the JDBC connection URL (jdbc:mysql://localhost:3306/)", true),
		CONNECTION_USER("connection.user", "the user name to connect the DB", false),
		CONNECTION_PASSWORD("connection.password", "the password of the user", false),
		CONNECTION_PROPERTY("connection.<propertyName>", "a connection property", false),
		DRIVER_CLASS_NAME("driverClassName", "fully qualified name of a custom driver class. NeoLoad embeds drivers for Oracle, MS SQL Server, MySQL and PostgreSQL", false);

		private final String name;
		private final String description;
		private final boolean required;

		SqlParameter(String name, String description, boolean required) {
			this.name = name;
			this.description = description;
			this.required = required;
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

	}

	@Override
	public String getType() {
		return "SP Connection";
	}

	@Override
	public List<ActionParameter> getDefaultActionParameters() {
		final List<ActionParameter> parameters = new ArrayList<>();
		parameters.add(new ActionParameter(SqlParameter.CONNECTION_NAME.getName(), "myConnection"));
		parameters.add(new ActionParameter(SqlParameter.CONNECTION_URL.getName(), ""));
		parameters.add(new ActionParameter(SqlParameter.CONNECTION_USER.getName(), ""));
		parameters.add(new ActionParameter(SqlParameter.CONNECTION_PASSWORD.getName(), "", Type.PASSWORD));
		return parameters;
	}

	@Override
	public Class<? extends ActionEngine> getEngineClass() {
		return SqlConnectActionEngine.class;
	}

	@Override
	public Icon getIcon() {
		//return LOGO_ICON;
		return null;
	}

	@Override
	
	public String getDescription() {
		final StringBuilder description = new StringBuilder();
		description.append("This action connects to a SQL database.\n").append("Possible parameters are:\n");

		for (final SqlParameter parameter : SqlParameter.values()) {
			description.append(parameter.getFullDescription()).append("\n");
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
