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

import org.osgi.service.component.annotations.Component;

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperImporter;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperMapper;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModule;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperImporterImpl;
//import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
//import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SElementId;
//import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperties;

@Component(name = "GATEImporterComponent", factory = "PepperImporterComponentFactory")
public class GateImporter extends PepperImporterImpl implements PepperImporter
{
	public static final String MODULE_NAME="GateImporter";
	public GateImporter()
	{
		super();
		this.setName(MODULE_NAME);
		this.addSupportedFormat("GateDocument", "2.0", null);
		this.addSupportedFormat("GateDocument", "3.0", null);
		this.getSDocumentEndings().add(PepperModule.ENDING_XML);
	}
	
	@Override
	public PepperMapper createPepperMapper(SElementId sElementId)
	{
		GateMapper mapper = new GateMapper();
		mapper.setResourceURI(getSElementId2ResourceTable().get(sElementId));
		return (mapper);
	}
}
