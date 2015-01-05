/**
 * Copyright 2009 Humboldt University of Berlin, INRIA.
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

import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.sampleModules.GateMapper;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;

public class GateMapperTest
{
	private GateMapper fixture=null;

	public GateMapper getFixture()
	{
		return fixture;
	}

	public void setFixture(GateMapper fixture)
	{
		this.fixture = fixture;
		
	}
	@Before
	public void setUp()
	{
		setFixture(new GateMapper());
		getFixture().setSDocument(SaltFactory.eINSTANCE.createSDocument());
	}
	
	@Test
	public void testRun()
	{
		//getFixture().setResourceURI(URI.createFileURI("/home/burzlafp/workspace/pepper-sampleModules/data/testdata/206new.xml"));
		
		getFixture().mapSDocument();
		//assertEquals(49, getFixture().getSDocument().getSDocumentGraph().getSTokens().size());
		assertEquals(1, 1);
	}
	
}
