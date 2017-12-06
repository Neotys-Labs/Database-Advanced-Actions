package com.neotys.action.sp;


/*
 * Copyright (c) 2014, Neotys
 * All rights reserved.
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

/**
 * Action capable of closing a connection from a SQL database.
 *
 * @author srichert
 *
 */
public final class SPDisconnectAction implements Action {

	private static final String BUNDLE_NAME = "com.neotys.action.sp.disconnectbundle";
	private static final String DISPLAY_NAME = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString("displayName");
	private static final String DISPLAY_PATH = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString("displayPath");

	//private static final ImageIcon LOGO_ICON = new ImageIcon(SqlDisconnectAction.class.getResource("sqldisconnectionaction.png"));

	/**
	 * Enum for custom action parameters
	 *
	 * @author anouvel
	 *
	 */
	@VisibleForTesting
	static enum SqlParameter {
		CONNECTION_NAME("connectionName", "the name of the connection to disconnect.", true);

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
		return "SP Disconnection";
	}

	@Override
	public List<ActionParameter> getDefaultActionParameters() {
		final List<ActionParameter> parameters = new ArrayList<>();
		parameters.add(new ActionParameter(SqlParameter.CONNECTION_NAME.getName(), "myConnection"));
		return parameters;
	}

	@Override
	public Class<? extends ActionEngine> getEngineClass() {
		return SPDisconnectActionEngine.class;
	}

	@Override
	public Icon getIcon() {
		//return LOGO_ICON;
		return null;
	}

	@Override
	public String getDescription() {
		final StringBuilder description = new StringBuilder();
		description.append("This action disconnects from a SQL database.\n").append("Possible parameters are:\n");

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
