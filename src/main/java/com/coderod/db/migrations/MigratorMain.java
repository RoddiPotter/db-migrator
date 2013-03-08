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
package com.coderod.db.migrations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class MigratorMain {

	public static void main(String[] args) {

		String action = args[0];

		Properties props = new Properties();
		try {
			props.load(new FileInputStream(new File("migrator.properties")));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(
					"No migrator.properties file found.  Make sure it's in the same dir as the jar file.",
					e);
		} catch (IOException e) {
			throw new RuntimeException(
					"Can't read migrator.properties file.  Make sure it's readable.", e);
		}

		String driver = props.getProperty("db.driver");
		String url = props.getProperty("db.url");
		String user = props.getProperty("db.username");
		String password = props.getProperty("db.password");
		String scripts = props.getProperty("migration.scripts.are.in");

		Migrator migrator = new Migrator(url, driver, user, password, scripts);

		if (action != null) {

			if (action.equals("upgrade")) {
				System.out.println("upgrade action started");
				migrator.migrateUp();
				System.out.println("upgrade action ended");
			} else if (action.equals("upgrade-all")) {
				System.out.println("upgrade-all action started");
				migrator.migrateUpAll();
				System.out.println("upgrade-all action ended");
			} else if (action.equals("rollback")) {
				System.out.println("rollback action started");
				migrator.migrateDown();
				System.out.println("rollback action ended");
			} else if (action.equals("init")) {
				System.out.println("init action started");
				migrator.initDb();
				System.out.println("init action ended");
			} else {
				throw new IllegalArgumentException("don't kow what " + action
						+ " is.  Must be one of: upgrade-all upgrade rollback init");
			}

		}
	}
}
