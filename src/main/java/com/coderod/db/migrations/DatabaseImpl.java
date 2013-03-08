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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.coderod.db.migrations.api.Database;

public class DatabaseImpl implements Database {

	private final String user;
	private final String password;
	private final String url;

	public DatabaseImpl(String user, String password, String url, String driver) {

		this.user = user;
		this.password = password;
		this.url = url;

		try {

			Class.forName(driver);

		} catch (ClassNotFoundException e) {
			throw new RuntimeException(
					"Invalid driver name, or driver class not on classpath", e);
		}

	}

	@Override
	public Connection getConnection() {
		try {
			// the connection wasn't given to us at construction time, get a
			// fresh new one.
			Connection connection = DriverManager.getConnection(url, user, password);
			if (connection == null) {
				throw new RuntimeException("No database available at " + url);
			}
			return connection;
		} catch (SQLException e) {
			throw new RuntimeException("Can't connect to database with params: user="
						+ user + ", password=" + password + ", url=" + url, e);
		}
	}

	@Override
	public void closeConnection(Connection conn) {
		if (conn != null) {
			try {
				if (!conn.isClosed()) {
					try {
						conn.close();
					} catch (SQLException e) {
						// nothing todo
					}
				}
			} catch (SQLException e) {
				// nothing to do
			}
		}
	}

}
