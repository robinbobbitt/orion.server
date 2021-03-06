/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation and others 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.orion.server.cf.manifest.v2.utils;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.orion.server.cf.manifest.v2.*;

/**
 * Inputs global settings to manifest applications
 */
public class ManifestTransformator implements Analyzer {

	private static final String[] RESERVED_PROPERTIES = {//
	"env", // //$NON-NLS-1$
			"inherit", // //$NON-NLS-1$
			"applications" // //$NON-NLS-1$
	};

	private boolean isReserved(ManifestParseTree node) {
		String value = node.getLabel();
		for (String property : RESERVED_PROPERTIES)
			if (property.equals(value))
				return true;

		return false;
	}

	/* populate without overriding */
	private void populate(ManifestParseTree application, List<ManifestParseTree> globals) {
		for (ManifestParseTree property : globals)
			if (!application.has(property.getLabel()))
				application.getChildren().add(property);
	}

	@Override
	public void apply(ManifestParseTree node) throws AnalyzerException {

		ManifestParseTree applications = node.getOpt(ManifestConstants.MANIFEST_APPLICATIONS);
		if (applications == null)
			/* nothing to do */
			return;

		/* find global properties */
		List<ManifestParseTree> globals = new LinkedList<ManifestParseTree>();
		for (ManifestParseTree property : node.getChildren())
			if (!isReserved(property))
				globals.add(property);

		if (globals.isEmpty())
			/* nothing to do */
			return;

		/* populate properties per application */
		for (ManifestParseTree application : applications.getChildren())
			populate(application, globals);
	}
}