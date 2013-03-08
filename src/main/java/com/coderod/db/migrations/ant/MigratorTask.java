/*
 * Copyright 2010 Roddi Potter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.coderod.db.migrations.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.coderod.db.migrations.Migrator;

public class MigratorTask extends Task {

	private String user;
	private String password;
	private String url;
	private String driver;
	private String scriptsLocation;
	private String action;

	public static final String UPGRADE_ALL = "upgrade-all";
	public static final String UPGRADE = "upgrade";
	public static final String ROLLBACK = "rollback";
	public static final String INIT = "init";

	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public void setScriptsLocation(String scriptsLocation) {
		this.scriptsLocation = scriptsLocation;
	}

	@Override
	public void execute() throws BuildException {
		
		Migrator migrator = new Migrator(url, driver, user, password, scriptsLocation);
		
		log(migrator.printBeforeDiagnostics());
		
		if (action != null) {

			if (action.equals(UPGRADE)) {
				log(UPGRADE + " action started");
				migrator.migrateUp();
				log(UPGRADE + " action ended");
			} else if (action.equals(UPGRADE_ALL)) {
				log(UPGRADE_ALL + " action started");
				migrator.migrateUpAll();
				log(UPGRADE_ALL + " action ended");
			} else if (action.equals(ROLLBACK)) {
				log(ROLLBACK + " action started");
				migrator.migrateDown();
				log(ROLLBACK + " action ended");
			} else if (action.equals(INIT)) {
				log(INIT + " action started");
				migrator.initDb();
				log(INIT + " action ended");
			} else {
				throw new BuildException("don't kow what " + action
						+ " is.  Must be one of:" + UPGRADE_ALL + " " + UPGRADE + " "
						+ ROLLBACK + " " + INIT);
			}

			log(migrator.printAfterDiagnostics());

		} else {
			throw new BuildException("action is required");
		}
	}

	public void setAction(String action) {
		this.action = action;
	}

}
