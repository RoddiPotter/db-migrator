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

import org.junit.Assert;
import org.junit.Test;

import com.coderod.db.migrations.api.Script;

public class ScriptTest {

	private static final String new_line = System.getProperty("line.separator");

	@Test
	public void test() {

		Script script = new ScriptImpl(new File(
				"./src/test/java/com/coderod/db/migrations/1_Change.sql"));
		Assert.assertEquals(1, script.version());

		script = new ScriptImpl(new File("./src/test/java/com/coderod/db/migrations/2_Change.sql"));
		Assert.assertEquals(2, script.version());

		script = new ScriptImpl(new File(
				"./src/test/java/com/coderod/db/migrations/5076_12345_mychange.sql"));
		Assert.assertEquals(5076, script.version());

	}

	@Test(expected=IllegalArgumentException.class)
	public void testNoVersionInName() {
		new ScriptImpl(new File("./src/test/java/com/coderod/db/migrations/Change.sql"));
	}
	
	@Test
	public void testParsing() {

		Script script = new ScriptImpl(new File(
				"./src/test/java/com/coderod/db/migrations/1_Change.sql"));
		Assert.assertEquals(null, script.getUpSql());
		Assert.assertEquals(null, script.getDownSql());

		script = new ScriptImpl(new File("./src/test/java/com/coderod/db/migrations/2_Change.sql"));
		
		String expected = "-- a test table" + new_line + "CREATE table test("
				+ new_line + "	test varchar(200) not null, -- a test column \t"
				+ new_line + ");"
				+ new_line + 
				"insert into test (test) values ('woohoo');";

		Assert.assertEquals(expected, script.getUpSql());

		Assert.assertEquals("drop table test;", script.getDownSql());

	}
	
}
