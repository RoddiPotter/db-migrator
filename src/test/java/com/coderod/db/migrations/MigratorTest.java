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

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.coderod.db.migrations.Migrator;

public class MigratorTest {

	// used for sql in this test
	private Connection con1;

	@Before
	public void newConnection() throws Exception {

		Class.forName("org.h2.Driver");
		con1 = DriverManager.getConnection(
					"jdbc:h2:mem:dbMigratorTest2;AUTOCOMMIT=OFF;DB_CLOSE_DELAY=-1", "sa",
					"");
	}

	@After
	public void cleanup() {
		try {
			if (con1 != null) {
				con1.close();
			}
		} catch (SQLException e) {
			// not much to do
		}
	}

	// ugly test, but it goes through the entire lifecycle.
	@Test
	public void test() throws Exception {

		try {
			ResultSet rs = con1.createStatement()
					.executeQuery("select * from db_version");
			rs.next();
			System.out.println(rs.getInt(1));
			Assert.fail("db_version should not exist yet.");
		} catch (SQLException e) {
		}

		Migrator m = new Migrator("jdbc:h2:mem:dbMigratorTest2;AUTOCOMMIT=OFF;DB_CLOSE_DELAY=-1","org.h2.Driver", "sa", "", "./src/test/java/com/coderod/db/migrations/");

		// init db
		m.initDb();
		ResultSet rs = con1.createStatement().executeQuery("select * from db_version");
		rs.next();
		Assert.assertEquals(0, rs.getInt(1));

		// migrate to version 1
		m.migrateUp();
		rs = con1.createStatement().executeQuery("select * from db_version");
		rs.next();
		Assert.assertEquals(1, rs.getInt(1));

		// migrate to version 2, 2_Change.sql has a table in it, we'll test for
		// that too
		m.migrateUp();
		rs = con1.createStatement().executeQuery("select * from db_version");
		rs.next();
		Assert.assertEquals(2, rs.getInt(1));
		rs = con1.createStatement().executeQuery("select * from test");
		rs.next();
		Assert.assertEquals("woohoo", rs.getString(1));

		// this should grab next file - 10_Change.sql
		m.migrateUp();
		rs = con1.createStatement().executeQuery("select * from db_version");
		rs.next();
		Assert.assertEquals(10, rs.getInt(1));

		// now lets migrate down
		m.migrateDown();
		rs = con1.createStatement().executeQuery("select * from db_version");
		rs.next();
		Assert.assertEquals(2, rs.getInt(1));

		// test table shoudl still exist because we went from 10 to 2
		rs = con1.createStatement().executeQuery("select * from test");
		rs.next();
		Assert.assertEquals("woohoo", rs.getString(1));

		// this action should get rid of test table.
		m.migrateDown();
		rs = con1.createStatement().executeQuery("select * from db_version");
		rs.next();
		Assert.assertEquals(1, rs.getInt(1));
		try {
			rs = con1.createStatement().executeQuery("select * from test");
			Assert.fail("test table should have been deleted.");
		} catch (SQLException e) {
			// yes, test table no longer exists
		}

		// bring us home baby
		m.migrateDown();
		rs = con1.createStatement().executeQuery("select * from db_version");
		rs.next();
		Assert.assertEquals(0, rs.getInt(1));

		// and this should do nothing.
		m.migrateDown();
		rs = con1.createStatement().executeQuery("select * from db_version");
		rs.next();
		Assert.assertEquals(0, rs.getInt(1));

	}

}
