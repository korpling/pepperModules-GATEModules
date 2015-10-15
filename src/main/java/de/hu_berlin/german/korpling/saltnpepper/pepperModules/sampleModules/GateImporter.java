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

import org.corpus_tools.pepper.common.PepperConfiguration;
import org.corpus_tools.pepper.impl.PepperImporterImpl;
import org.corpus_tools.pepper.modules.PepperImporter;
import org.corpus_tools.pepper.modules.PepperMapper;
import org.corpus_tools.pepper.modules.PepperModule;
import org.corpus_tools.pepper.modules.exceptions.PepperModuleNotReadyException;
import org.corpus_tools.salt.graph.Identifier;
import org.eclipse.emf.common.util.URI;
import org.osgi.service.component.annotations.Component;


@Component(name = "GATEImporterComponent", factory = "PepperImporterComponentFactory")
public class GateImporter extends PepperImporterImpl implements PepperImporter {
	public GateImporter() {
		super();
		setName("GateImporter");
		setSupplierContact(URI.createURI(PepperConfiguration.EMAIL));
		setSupplierHomepage(URI.createURI("https://github.com/korpling/pepperModules-GATEModules"));
		setDesc("This importer transforms data in the GATE format to a Salt model. ");
		addSupportedFormat("GateDocument", "2.0", null);
		addSupportedFormat("GateDocument", "3.0", null);
		getDocumentEndings().add(PepperModule.ENDING_XML);
	}

	@Override
	public PepperMapper createPepperMapper(Identifier Identifier) {
		GateMapper mapper = new GateMapper();
		mapper.setResourceURI(getIdentifier2ResourceTable().get(Identifier));
		return (mapper);
	}

	@Override
	public Double isImportable(URI corpusPath) {
		// some code to analyze the given corpus-structure
		return (null);
	}

	@Override
	public boolean isReadyToStart() throws PepperModuleNotReadyException {
		// make some initializations if necessary
		return (super.isReadyToStart());
	}
}
