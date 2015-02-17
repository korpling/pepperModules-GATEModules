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

import org.junit.Before;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.FormatDesc;
import de.hu_berlin.german.korpling.saltnpepper.pepper.testFramework.PepperImporterTest;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.sampleModules.GateImporter;

/**
 * This is a dummy implementation of a JUnit test for testing the {@link GateImporter} class.
 * Feel free to adapt and enhance this test class for real tests to check the work of your importer.
 * If you are not confirm with JUnit, please have a look at <a href="http://www.vogella.com/tutorials/JUnit/article.html">
 * http://www.vogella.com/tutorials/JUnit/article.html</a>.
 * <br/>
 * Please note, that the test class is derived from {@link PepperImporterTest}. The usage of this class
 * should simplfy your work and allows you to test only your single importer in the Pepper environment.
 * @author Florian Zipser
 *
 */
public class GateImporterTest extends PepperImporterTest{
	/**
	 * This method is called by the JUnit environment each time before a test case starts. 
	 * So each time a method annotated with @Test is called. This enables, that each method 
	 * could run in its own environment being not influenced by before or after running test 
	 * cases. 
	 */
	@Before
	public void setUp(){
		setFixture(new GateImporter());
		
		FormatDesc formatDef= new FormatDesc();
		formatDef.setFormatName("GateDocument");
		formatDef.setFormatVersion("3.0");
		this.supportedFormatsCheck.add(formatDef);
		
		formatDef= new FormatDesc();
		formatDef.setFormatName("GateDocument");
		formatDef.setFormatVersion("2.0");
		this.supportedFormatsCheck.add(formatDef);
	}

	//TODO add further tests for any test cases you can think of and which are necessary
}
