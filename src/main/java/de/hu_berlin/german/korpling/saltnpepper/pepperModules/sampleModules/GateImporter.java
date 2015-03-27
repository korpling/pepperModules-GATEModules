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
package de.hu_berlin.german.korpling.saltnpepper.pepperModules.sampleModules;

import gate.Gate;
import gate.util.GateException;

import org.eclipse.emf.common.util.URI;
import org.osgi.service.component.annotations.Component;

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperImporter;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperMapper;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModule;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.exceptions.PepperModuleException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperImporterImpl;
//import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
//import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SElementId;
//import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperties;

@Component(name = "GATEImporterComponent", factory = "PepperImporterComponentFactory")
public class GateImporter extends PepperImporterImpl implements PepperImporter
{
	public static final String MODULE_NAME="GateImporter";
	public static final String GATE_HOME_PATH="GATE_HOME";
	public GateImporter()
	{
		super();
		this.setName(MODULE_NAME);
		this.setProperties(new GATEImporterProperties());
		this.addSupportedFormat("GateDocument", "2.0", null);
		this.addSupportedFormat("GateDocument", "3.0", null);
		this.getSDocumentEndings().add(PepperModule.ENDING_XML);
	}
	
	/**
	 * Initializes GATE api and calls super's {@link #start()}.
	 */
	@Override
	public void start() throws PepperModuleException {
		try {
			URI gateHome= getResources().appendSegment("GATE_HOME");
			System.setProperty("gate.home", gateHome.toFileString());
			Gate.init();
		} catch (GateException e1) {
			throw new PepperModuleException(this, "Cannot load the GATE api because of a nested exception.",e1);
		}
		
		super.start();
	}
	
	@Override
	public PepperMapper createPepperMapper(SElementId sElementId)
	{
		GateMapper mapper = new GateMapper();
		mapper.setResourceURI(getSElementId2ResourceTable().get(sElementId));
		return (mapper);
	}
}
