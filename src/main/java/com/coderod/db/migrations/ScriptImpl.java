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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.coderod.db.migrations.api.Script;

public class ScriptImpl implements Script {

	private final File scriptFile;
	private final int version;

	private String upSql;
	private String downSql;

	public ScriptImpl(File scriptFile) {
		this.scriptFile = scriptFile;

		if (!scriptFile.exists()) {
			throw new RuntimeException("file now found: " + scriptFile);
		}

		// extract the version from the filename
		Matcher m = Pattern.compile("^\\d+").matcher(scriptFile.getName());
		if (m.find()) {
			String versionPart = m.group();
			versionPart = versionPart.replaceAll("\\D+", "");
			version = Integer.parseInt(versionPart);
		} else {
			throw new IllegalArgumentException("Can't find version in filename: "
					+ scriptFile.getName() + ". The filename must start with a digit.");
		}

		// extract the up / down sql
		parseSql();

	}

	private void parseSql() {

		BufferedReader br = null;
		try {

			br = new BufferedReader(new FileReader(scriptFile));
			StringBuffer buffy = new StringBuffer();
			String line = br.readLine();
			while (line != null) {
				buffy.append(line);
				line = br.readLine();
				if (line != null) {
					buffy.append(Migrator.new_line);
				}
			}

			String[] split = buffy.toString().split("@UP");
			if (split.length > 1) {
				upSql = split[1].trim();
			}
			split = buffy.toString().split("@DOWN");
			if (split.length > 1) {
				downSql = split[1].trim();
			}

		} catch (FileNotFoundException e) {
			throw new RuntimeException("can't read file " + scriptFile, e);
		} catch (IOException e) {
			throw new RuntimeException("can't read file " + scriptFile, e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// bah
				}
			}

		}

	}

	@Override
	public File scriptFile() {
		return scriptFile;
	}

	@Override
	public int version() {
		return version;
	}

	@Override
	public int compareTo(Script o) {
		return version - o.version();
	}

	@Override
	public boolean equals(Object obj) {
		Script other = (Script) obj;
		return scriptFile.equals(other.scriptFile());
	}

	@Override
	public int hashCode() {
		return scriptFile.hashCode();
	}

	@Override
	public String toString() {
		return scriptFile.getName();
	}

	@Override
	public String getUpSql() {
		return upSql;
	}

	@Override
	public String getDownSql() {
		return downSql;
	}
}
