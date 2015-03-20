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

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;

import com.coderod.db.migrations.api.DbVersion;
import com.coderod.db.migrations.api.Script;
import com.coderod.db.migrations.api.ScriptsDir;

public class Migrator extends DatabaseImpl {

	public static final String new_line = System.getProperty("line.separator");

	private final ScriptsDir scripts;
	private final DbVersion dbVersion;

	public Migrator(String url, String driver, String user, String password,
			String scriptsDir) {

		super(user, password, url, driver);

		this.scripts = new ScriptsDirImpl(scriptsDir);
		this.dbVersion = new DbVersionImpl(user, password, url, driver);
	}

	public void initDb() {

		if (!dbVersion.exists()) {
			dbVersion.init();
		}
	}

	public void migrateUpAll() {
		while (scripts.highestVersion() > dbVersion.currentVersion()) {
			boolean success = migrateUp();
			if (!success) {
				break;
			}
		}
	}

	public boolean migrateUp() {

		if (dbVersion.exists()) {

			Script script = scripts.next(dbVersion.currentVersion());
			String upSql = script.getUpSql();

			if (script != null) {

				String[] statments = null;

				if (upSql != null) {
					statments = upSql.split(";");
				} else {
					statments = new String[0];
				}

				Connection conn = null;
				try {

					conn = getConnection();

					conn.setAutoCommit(false);
					if (statments != null && statments.length > 0) {
						System.out.println("upgrading dbversion from "
								+ dbVersion.currentVersion() + " to "
								+ script.version() + ":");

						System.out
								.println("----------------------@UP----------------------------");
						for (String statement : statments) {

							statement = statement.replaceAll("$\n", "");
							System.out.print("" + statement + ";");
							conn.createStatement().executeUpdate(statement);
							System.out.println(" \u2714");
						}
						System.out
								.println("----------------------@UP----------------------------");

					}
					dbVersion.update(script.version());
					conn.commit();
					System.out.println("succeeded, now at " + script.version());
					return true;

				} catch (SQLException e) {
					System.out.print(" \u2718 \u2775 ");
					System.out.println(e.getMessage());
					try {
						conn.rollback();
					} catch (SQLException e1) {
						// not much to do
					}
				} finally {
					closeConnection(conn);
				}
			}

		}
		return false;

	}

	public boolean migrateDown() {

		if (dbVersion.exists()) {

			Script script = scripts.get(dbVersion.currentVersion());

			if (script != null) {

				Connection conn = null;
				try {

					conn = getConnection();
					conn.setAutoCommit(false);
					String allDownSql = script.getDownSql();

					String[] statements;

					if (allDownSql != null) {
						statements = allDownSql.split(";");
					} else {
						statements = new String[0];
					}

					if (statements.length > 0) {
						System.out.println("downgrading dbversion from "
								+ dbVersion.currentVersion() + " to "
								+ script.version());

						System.out
								.println("---------------------@DOWN-----------------------------");
						for (String statement : statements) {
							System.out.print(statement + ";");
							conn.createStatement().executeUpdate(statement);
							System.out.println(" \u2714");
						}
						System.out
								.println("---------------------@DOWN----------------------------------");

					}
					// now roll back to previous script number, checking to make
					// sure we're not at the first script
					Script previousScript = scripts.previous(dbVersion
							.currentVersion());
					if (previousScript != null) {
						dbVersion.update(previousScript.version());
					} else {
						dbVersion.update(0);
					}
					conn.commit();
					System.out.println("succeeded, now at " + script.version());
					return true;

				} catch (SQLException e) {
					System.out.print(" \u2718 \u2775 ");
					System.out.println(e.getMessage());
					try {
						conn.rollback();
					} catch (SQLException e1) {
						// not much to do
					}
				} finally {
					closeConnection(conn);
				}

			}

		}
		return false;
	}

	public String printBeforeDiagnostics() {
		StringWriter writer = new StringWriter();
		writer.write("Script dir: " + this.scripts.dir().getAbsolutePath()
				+ new_line);
		for (Script script : this.scripts.listScripts()) {
			writer.write("Found script: " + script.scriptFile().getName()
					+ "(version: " + script.version() + ")" + new_line);
		}
		if (dbVersion.exists()) {
			writer.write("Data versioning is initialized." + new_line);
			writer.write("Database version is at " + dbVersion.currentVersion()
					+ new_line);
		} else {
			writer.write("Data versioning is not initialized" + new_line);
		}
		return writer.toString();
	}

	public String printAfterDiagnostics() {
		StringWriter writer = new StringWriter();
		String new_line = System.getProperty("line.separator");
		if (dbVersion.exists()) {
			writer.write("Database version is "
					+ dbVersion.currentVersion() + new_line);
		} else {
			writer.write("Data versioning is not yet initialized" + new_line);
		}
		return writer.toString();

	}

}
