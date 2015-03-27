package de.hu_berlin.german.korpling.saltnpepper.pepperModules.sampleModules.tests;

import gate.Gate;
import gate.util.GateException;

import java.io.File;

import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.exceptions.PepperModuleTestException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.testFramework.PepperModuleTest;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.sampleModules.GATEImporterProperties;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.sampleModules.GateImporter;
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
	public void setUp() throws GateException{
		setFixture(new GateMapper());
		getFixture().setProperties(new GATEImporterProperties());
		getFixture().setSDocument(SaltFactory.eINSTANCE.createSDocument());
		URI gateHome=  URI.createFileURI("./src/main/resources/").appendSegment(GateImporter.GATE_HOME_PATH);
		System.setProperty("gate.home", gateHome.toFileString());
		Gate.init();
	}
	
	private static final String SAMPLE_TEXT=	"The exhumation of Richard III of England from his burial place within the site of the former Greyfriars Friary Church in the city of Leicester , England, took place in September 2012. The last king of the Plantagenet dynasty , Richard III was killed in the Battle of Bosworth on 22 August 1485, during the Wars of the Roses . His body was taken to Greyfriars Friary in Leicester, where it was buried in a crude grave in the friary church. Following the friary's dissolution in 1538 and its subsequent demolition, Richard's tomb was lost. An account arose that Richard's bones had been thrown into the River Soar at the nearby Bow Bridge.\n"+
												"A search for Richard's body began in August 2012, initiated by the Looking for Richard project with the support of the Richard III Society . The archaeological excavation was led by the University of Leicester Archaeological Services , working in partnership with Leicester City Council . On the first day of the excavation a human skeleton belonging to a man in his thirties was uncovered. It showed signs of severe injuries and had several unusual physical features, most notably a severe curvature of the back . It was exhumed to allow scientific analysis, which found that the man had probably been killed either by a blow from a large bladed weapon, probably a halberd , which cut off the back of his skull and exposed the brain, or by a sword thrust that penetrated all the way through the brain. There were signs of other wounds on the skeleton that had probably occurred after death as \"humiliation injuries\", inflicted as a form of posthumous revenge.\n"+
												"The age of the bones at death matched that of Richard when he was killed; they were dated to about the period of his death and were mostly consistent with physical descriptions of the king. Preliminary DNA analysis also showed that mitochondrial DNA extracted from the bones matched that of two matrilineal descendants, one 17th-generation and the other 19th-generation, of Richard's sister Anne of York . Taking these findings into account along with other historical, scientific and archaeological evidence, the University of Leicester announced on 4 February 2013 that it had concluded beyond reasonable doubt that the skeleton was that of Richard III.\n"+
												"As a condition of being allowed to disinter the skeleton, the excavators agreed that, if Richard were found, his remains would be reburied in Leicester Cathedral . A controversy arose over whether an alternative reburial site, such as York Minster or Westminster Abbey , would be more suitable. A legal challenge confirmed that there were no public law grounds for the courts to be involved in that decision. The reinterment is scheduled to take place in Leicester on 26 March 2015, during a televised memorial service held in the presence of the Archbishop of Canterbury and senior members of other Christian denominations.\n";
	
	@Test
	public void testOdt(){
		URI testFile= URI.createFileURI(new File(PepperModuleTest.getTestResources()+"/odt/richardIII.odt").getAbsolutePath());
		getFixture().setResourceURI(testFile);
		getFixture().mapSDocument();
		
		assertEquals(SAMPLE_TEXT, getFixture().getSDocument().getSDocumentGraph().getSTextualDSs().get(0).getSText());
	}
	
//	@Test
//	public void testDoc(){
//		URI testFile= URI.createFileURI(new File(PepperModuleTest.getTestResources()+"/odt/richardIII.doc").getAbsolutePath());
//		getFixture().setResourceURI(testFile);
//		getFixture().mapSDocument();
//		
//		assertEquals(SAMPLE_TEXT, getFixture().getSDocument().getSDocumentGraph().getSTextualDSs().get(0).getSText());
//	}
//	
//	@Test
//	public void testDocx(){
//		URI testFile= URI.createFileURI(new File(PepperModuleTest.getTestResources()+"/odt/richardIII.docx").getAbsolutePath());
//		getFixture().setResourceURI(testFile);
//		getFixture().mapSDocument();
//		
//		assertEquals(SAMPLE_TEXT, getFixture().getSDocument().getSDocumentGraph().getSTextualDSs().get(0).getSText());
//	}
	
	@Test
	public void testEntireDocument(){
		URI testFile= URI.createFileURI(new File(PepperModuleTest.getTestResources()+"/entireDocuments/wikiText.xml").getAbsolutePath());
		getFixture().getProperties().setPropertyValue(GATEImporterProperties.PROP_USE_AS_TOKEN, "Token");
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
