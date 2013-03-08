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

import junit.framework.Assert;

import org.junit.Test;

import com.coderod.db.migrations.DbVersionImpl;
import com.coderod.db.migrations.api.DbVersion;

public class DbVersionTest {

	@Test
	public void test() {

		DbVersion dbVersion = new DbVersionImpl("sa", "",
				"jdbc:h2:mem:dbMigratorTest;AUTOCOMMIT=OFF;DB_CLOSE_DELAY=-1", "org.h2.Driver");

		Assert.assertEquals(0, dbVersion.currentVersion());

		dbVersion.init();

		Assert.assertEquals(0, dbVersion.currentVersion());

		dbVersion.update(1);

		Assert.assertEquals(1, dbVersion.currentVersion());

		dbVersion.update(2);

		Assert.assertEquals(2, dbVersion.currentVersion());

		dbVersion.update(1);

		Assert.assertEquals(1, dbVersion.currentVersion());

		dbVersion.update(0);

		Assert.assertEquals(0, dbVersion.currentVersion());

		// zero is a hard limit for decrementing
		dbVersion.update(0);

		Assert.assertEquals(0, dbVersion.currentVersion());

	}

}
