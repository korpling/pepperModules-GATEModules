package de.hu_berlin.german.korpling.saltnpepper.pepperModules.sampleModules.tests;

import java.io.File;

import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;

import de.hu_berlin.german.korpling.saltnpepper.pepper.testFramework.PepperModuleTest;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.sampleModules.GateMapper;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;

public class GATEMapperTest{

	private  GateMapper fixture= null;

	public GateMapper getFixture() {
		return fixture;
	}

	public void setFixture(GateMapper fixture) {
		this.fixture = fixture;
	}
	
	@Before
	public void setUp(){
		setFixture(new GateMapper());
		getFixture().setSDocument(SaltFactory.eINSTANCE.createSDocument());
	}
	
	@Test
	public void testPrimData_odt(){
		URI testFile= URI.createFileURI(new File(PepperModuleTest.getTestResources()+"/primData_odt/Alme 1630.xml").getAbsolutePath());
		getFixture().setResourceURI(testFile);
		getFixture().mapSDocument();
		SaltProject project= SaltFactory.eINSTANCE.createSaltProject();
		SCorpusGraph graph= SaltFactory.eINSTANCE.createSCorpusGraph();
		project.getSCorpusGraphs().add(graph);
		SCorpus corp= graph.createSCorpus(URI.createFileURI("gate")).get(0);
		SDocument sDoc= graph.createSDocument(corp, "myDOc");
		sDoc.setSDocumentGraph(getFixture().getSDocument().getSDocumentGraph());
		project.saveSaltProject(URI.createFileURI("/home/florian/work/SaltNPepper/workspace/pepperModules/pepperModules-GATEModules/src/test/resources/primData_odt/salt/"));
	}
}
