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
	public void testBla()
	{
		
		getFixture().setResourceURI(URI.createFileURI("/home/burzlafp/workspace/pepper-sampleModules/data/testdata/206new.xml"));
		
		getFixture().mapSDocument();
		assertEquals(49, getFixture().getSDocument().getSDocumentGraph().getSTokens().size());
		assertEquals(1, 1);
	}
	
}
