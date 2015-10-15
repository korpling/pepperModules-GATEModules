/**
 * Copyright 2009 Humboldt-Universität zu Berlin, INRIA.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
package de.hu_berlin.german.korpling.saltnpepper.pepperModules.sampleModules.tests;

import java.io.File;

import org.corpus_tools.salt.SaltFactory;
import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;

import de.hu_berlin.german.korpling.saltnpepper.pepperModules.sampleModules.GateMapper;

public class GateMapperTest {
	private GateMapper fixture = null;

	public GateMapper getFixture() {
		return fixture;
	}

	public void setFixture(GateMapper fixture) {
		this.fixture = fixture;

	}

	@Before
	public void setUp() {
		setFixture(new GateMapper());
		getFixture().setDocument(SaltFactory.createSDocument());
	}

	@Test
	public void testRun() {
		getFixture().setResourceURI(URI.createFileURI(new File("").getAbsolutePath() + "/src/test/resources/sample1/gate/myCorpus/subCorpus/Sudebnik1497.xml".toString()));
		getFixture().mapSDocument();
	}

}
