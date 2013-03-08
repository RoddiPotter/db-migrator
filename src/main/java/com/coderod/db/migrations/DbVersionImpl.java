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
import java.sql.ResultSet;
import java.sql.SQLException;

import com.coderod.db.migrations.api.DbVersion;

public class DbVersionImpl extends DatabaseImpl implements DbVersion {

	public DbVersionImpl(String user, String password, String url, String driver) {
		super(user, password, url, driver);
	}

	@Override
	public int currentVersion() {
		int currentVersion = 0;
		if (exists()) {
			Connection conn = null;
			try {
				conn = getConnection();
				conn.setAutoCommit(false);
				ResultSet rs = conn.createStatement().executeQuery(
						"select version from db_version");
				rs.next();
				currentVersion = rs.getInt(1);
				conn.commit();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				throw new RuntimeException("can't read from db_version table", e);
			} finally {
				closeConnection(conn);
			}
		}
		return currentVersion;
	}

	@Override
	public boolean exists() {
		boolean exists = false;
		Connection conn = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			conn.createStatement().executeQuery("select version from db_version");
			exists = true;
			conn.commit();
		} catch (SQLException e) {
			exists = false;
		} finally {
			closeConnection(conn);
		}

		return exists;
	}

	@Override
	public void init() {

		if (!exists()) {
			Connection conn = null;
			try {
				conn = getConnection();
				conn.setAutoCommit(false);
				conn.createStatement().executeUpdate(
						"create table db_version (version integer not null)");
				conn.createStatement().executeUpdate(
						"insert into db_version (version) values (0)");
				conn.commit();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				throw new RuntimeException(
						"Could not create db_version table, or set initial value to 0", e);
			} finally {
				closeConnection(conn);
			}

		}

	}

	@Override
	public void update(int version) {

		if (exists()) {

			if (version >= 0) {
				Connection conn = null;
				try {
					conn = getConnection();
					conn.setAutoCommit(false);
					conn.createStatement().executeUpdate(
							"update db_version set version = " + version);
					conn.commit();
				} catch (SQLException e) {
					throw new RuntimeException("Could not update db_version to "
							+ version, e);
				} finally {
					closeConnection(conn);
				}

			}

		} else {
			throw new RuntimeException("db_version table not initialized yet");
		}

	}

}
