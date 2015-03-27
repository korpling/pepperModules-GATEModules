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

import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.corpora.DocumentImpl;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.DOCUMENT_STATUS;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.exceptions.PepperModuleException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperMapperImpl;

/**
 * This class is a GATE2Salt Mapper. It maps the GATE-XML Version 2 or 3 from GATE 7 and 8 to Salt.
 * 
 * @author Paul Burzlaff
 *
 */

public class GateMapper extends PepperMapperImpl
{
	// this is a logger, for recording messages during program process, like debug messages
	private static final Logger logger = LoggerFactory.getLogger(GateImporter.MODULE_NAME);
	
	/** The GATE document to be mapped **/
	private Document gateDocument= null;
	
	public Document getGateDocument() {
		return gateDocument;
	}

	public void setGateDocument(Document gateDocument) {
		this.gateDocument = gateDocument;
	}


	/**
	 *
	 */
	@Override
	public DOCUMENT_STATUS mapSDocument()
	{
		try {
			URL u = new URL(getResourceURI().toString()); 
			FeatureMap params = Factory.newFeatureMap();
			params.put("sourceUrl", u); 
			setGateDocument((Document) Factory.createResource(DocumentImpl.class.getName(), params));
		} catch (ResourceInstantiationException | MalformedURLException e) {
			throw new PepperModuleException(this, "Cannot map document '"+getSDocument().getId()+"' because of a nested exception. ", e);
		} 
		GATE2Salt mapper= new GATE2Salt();
		
		mapper.setIgnoreWhitespaceTokens(((GATEImporterProperties)getProperties()).getIgnoreWhitespaces());
		mapper.setMapDefaultAnnotationSet(((GATEImporterProperties)getProperties()).getMapDefaultAnnoSet());
		mapper.setMapAnnotationSetNames(((GATEImporterProperties)getProperties()).getMapAnnotationSetNames());
		mapper.setTypeAsPrefix(((GATEImporterProperties)getProperties()).getTypeAsPrefix());
		mapper.setUseAsToken(((GATEImporterProperties)getProperties()).getUseAsToken());
		
		mapper.setGateDocument(getGateDocument());
		mapper.setsDocument(getSDocument());
		mapper.map();
		
		return (DOCUMENT_STATUS.COMPLETED);
	}
}
