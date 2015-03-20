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
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MigrateAllTest {

	private Connection con;

	// need a different db connection here.
	
	@Before
	public void newConnection() throws Exception {

		Class.forName("org.h2.Driver");
		con = DriverManager.getConnection(
					"jdbc:h2:mem:dbMigratorTest3;AUTOCOMMIT=OFF;DB_CLOSE_DELAY=-1", "sa",
					"");
	}

	// ugly test, but it goes through the entire lifecycle.
	@Test
	public void test() throws Exception {

		try {
			ResultSet rs = con.createStatement().executeQuery("select * from db_version");
			rs.next();
			System.out.println(rs.getInt(1));
			Assert.fail("db_version should not exist yet.");
		} catch (SQLException e) {
		}

		Migrator m = new Migrator(
				"jdbc:h2:mem:dbMigratorTest3;AUTOCOMMIT=OFF;DB_CLOSE_DELAY=-1",
				"org.h2.Driver", "sa", ""
				, "./src/test/java/com/coderod/db/migrations/");

		// init db
		m.initDb();
		ResultSet rs = con.createStatement().executeQuery("select * from db_version");
		rs.next();
		Assert.assertEquals(0, rs.getInt(1));

		// migrate all versions
		m.migrateUpAll();
		rs = con.createStatement().executeQuery("select * from db_version");
		rs.next();
		Assert.assertEquals(5077, rs.getInt(1));

	}
	
}
