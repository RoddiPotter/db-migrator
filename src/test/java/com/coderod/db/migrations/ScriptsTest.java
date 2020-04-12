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
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.coderod.db.migrations.api.Script;
import com.coderod.db.migrations.api.ScriptsDir;

public class ScriptsTest {

	@Test
	public void testHighestVersion() {

		ScriptsDir scripts = new ScriptsDirImpl("./src/test/java/com/coderod/db/migrations/");
		Assert.assertEquals(new File("./src/test/java/com/coderod/db/migrations/"), scripts.dir());

		Assert.assertEquals(6000, scripts.highestVersion());

	}

	@Test
	public void testList() {
		
		ScriptsDir scripts = new ScriptsDirImpl("./src/test/java/com/coderod/db/migrations/");
		Script script1 = new ScriptImpl(new File("./src/test/java/com/coderod/db/migrations/1_Change.sql"));
		Script script2 = new ScriptImpl(new File("./src/test/java/com/coderod/db/migrations/2_Change.sql"));
		Script script3 = new ScriptImpl(new File("./src/test/java/com/coderod/db/migrations/10_Change.sql"));
		Script script4 = new ScriptImpl(new File("./src/test/java/com/coderod/db/migrations/5076_12345_mychange.sql"));
		Script script5 = new ScriptImpl(new File("./src/test/java/com/coderod/db/migrations/5077-12345-mychange.sql"));
		Script script6 = new ScriptImpl(new File("./src/test/java/com/coderod/db/migrations/6000_result_returned_when_none_expected.sql"));
		Assert.assertEquals(Arrays.asList(script1, script2, script3, script4, script5, script6), scripts.listScripts());
		
	}
}
