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
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.coderod.db.migrations.api.Script;
import com.coderod.db.migrations.api.ScriptsDir;

public class ScriptsDirImpl implements ScriptsDir {

	private final File dir;
	
	public ScriptsDirImpl(String pathToScripts) {
		this.dir = new File(pathToScripts);
		if(!dir.exists()) {
			throw new RuntimeException(pathToScripts + " does not exist");
		}
	}
	
	@Override
	public File dir() {
		return dir;
	}

	@Override
	public int highestVersion() {
		List<Script> scripts = listScripts();
		if(!scripts.isEmpty()) {
			return scripts.get(scripts.size() - 1).version();
		} else {
			return 0;
		}
	}

	@Override
	public List<Script> listScripts() {
		File[] scriptFiles = dir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				if(name.matches("^\\d+.*\\.sql")) {
					return true;
				} else {
					return false;
				}
				
			}
		});
		List<Script> scripts = new ArrayList<Script>();
		for(File scriptFile : scriptFiles) {
			scripts.add(new ScriptImpl(scriptFile));
		}
		Collections.sort(scripts);
		
		return scripts;
	}

	@Override
	public Script next(int previousVersion) {
		for(Script script : listScripts()) {
			if(script.version() > previousVersion) {
				return script;
			}
		}
		return null;
	}

	@Override
	public Script get(int currentVersion) {
		for(Script script : listScripts()) {
			if(script.version() == currentVersion) {
				return script;
			}
		}
		return null;
	}

	@Override
	public Script previous(int currentVersion) {
		List<Script> scripts = listScripts();
		Collections.reverse(scripts);
		for(Script script : scripts) {
			if(script.version() < currentVersion) {
				return script;
			}
		}
		return null;
	}

}
