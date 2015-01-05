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
package de.hu_berlin.german.korpling.saltnpepper.pepperModules.sampleModules;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.DOCUMENT_STATUS;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperMapperImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SPointingRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STYPE_NAME;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class is a GATE2Salt Mapper. It maps the GATE-XML Version 2 or 3 from GATE 7 and 8 to Salt.
 * 
 * @author Paul Burzlaff
 *
 */

public class GateMapper extends PepperMapperImpl
{
	// this is a logger, for recording messages during program process, like debug messages
	private static final Logger logger = LoggerFactory.getLogger(GateImporter.class);
	protected String text;
	STextualDS sText= null; //Salttext
	Map<Integer,SToken> tokenIDs = new HashMap<Integer,SToken>(); //GATE ID, correspond to the position in the text
	List<Integer> nodeIDs=new ArrayList<Integer>(); //ID of the GATE Nodes, corresponding to the Start/EndNotes of the Annotations
	
	public static final String TextWithNodes_TAG = "TextWithNodes";
	public static final String AnnotationSet_TAG = "AnnotationSet";
	public static final String Annotation_TAG = "Annotation";
	public static final String Name_TAG = "Name";
	public static final String Type_TAG = "Type";
	public static final String StartNode_TAG = "StartNode";
	public static final String EndNode_TAG = "EndNode";
	public static final String GateDocument_TAG = "GateDocument";
	public static final String Node_TAG = "Node";
	public static final String Value_TAG = "Value";
	public static final String GateDocumentFeatures_TAG = "GateDocumentFeatures";
	public static final String Feature_TAG = "Feature";

	/**
	 * TODO Does GATE provide Corpus information?
	 */
	@Override
	public DOCUMENT_STATUS mapSCorpus()
	{
		// getScorpus() returns the current corpus object.
		//getSCorpus().createSMetaAnnotation(null, "date", "1989-12-17");

		return (DOCUMENT_STATUS.COMPLETED);
	}
	
	public InputSource getInputSource(Reader reader,String encoding)
	{
		InputSource is = new InputSource(reader);
  	    is.setEncoding(encoding);
		return is;
	}
	
	public InputSource getInputSource(String fttext,String encoding)
	{
		InputSource is = new InputSource(new StringReader (fttext));
  	    is.setEncoding(encoding);
		return is;
	}
	
	/**
	 * Parses GATE XML-Document V3 with SAX-Parser
	 * time O(n)
	 * space k+2l+m+n O(k+l+n) k=text l=annotations m=metadata n=salt representation
	 * lazy space complex definition:
	 * annotations are divided into nodes and spans with informations
	 * nodes can't be greater than the text+1 and are stored (2l)
	 * spans and informations are only stored (l)
	 * 
	 * salt representation constructed successive and includes all above given information (k+l+m)
	 */
	@Override
	public DOCUMENT_STATUS mapSDocument()
	{

		// the method getSDocument() returns the current document for creating
		// the document-structure
		getSDocument().setSDocumentGraph(SaltFactory.eINSTANCE.createSDocumentGraph());
		// to get the exact resource, which be processed now, call
		// getResources()
		URI resource = getResourceURI();
		System.out.println(resource);
		// we record, which file currently is imported to the debug stream
		logger.debug("Importing the file {}.", resource);
		
		try
		{	
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			
			DefaultHandler handler = new DefaultHandler()
			{
				boolean btext = false;
				boolean bas = false;
				boolean bname=false,bvalue = false;
				boolean bgatedocfeat = false;
				boolean banno = false;
				// TODO wo den AS namen unterbringen?
				String as_name="";
				String nodeID="";

				int a_start=-1,a_end=-1;
				String a_name="";
				String name="",value="";
				List<String> featurepairs=new ArrayList<String>();

				public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
				{
					if (TextWithNodes_TAG.equals(qName))
					{
						btext = true;
					}
					else if (AnnotationSet_TAG.equals(qName)) 
					{
						bas=true;
						if(attributes.getLength()>0)
						{
							if (Name_TAG.equals(attributes.getQName(0)))
							{
								as_name=attributes.getValue(0);
							}
						}
						else
						{
							as_name="Default";
						}
					}
					else if (Annotation_TAG.equals(qName)) 
					{
						banno=true;

						for(short i=0;i<attributes.getLength();i++)
						{
							if(Type_TAG.equals(attributes.getQName(i)))
							{
								a_name=attributes.getValue(i);
							}
							else if (StartNode_TAG.equals(attributes.getQName(i)))
							{
								try
								{
									a_start=Integer.parseInt(attributes.getValue(i).trim());
								} catch (NumberFormatException e)
								{
									logger.error("NumberFormatException in Annotation "+a_name+" at: "+attributes.getValue(i));
								}
							} 
							else if (EndNode_TAG.equals(attributes.getQName(i)))
							{
								try
								{
									a_end=Integer.parseInt(attributes.getValue(i).trim());
								} catch (NumberFormatException e)
								{
									logger.error("NumberFormatException in Annotation "+a_name+" at: "+attributes.getValue(i));
								}
							} 
						}
					}
					else if (GateDocument_TAG.equals(qName))
					{
						if ("version".equals(attributes.getQName(0)))
						{
							if (!"3".equals(attributes.getValue(0)) | !"2".equals(attributes.getValue(0)))
							{
								logger.warn("This Module works for GATE_Document Version 2 or 3. Anyway still trying...");
							}
						}
						addProgress(0.05);
					}
					else if (Node_TAG.equals(qName))
					{
						if(attributes.getLength()>0){nodeID = attributes.getValue(0).trim();}
						else{logger.error("Node has no attribute");	}
						
						try	{nodeIDs.add(Integer.parseInt(nodeID));}
						catch (NumberFormatException e)	
						{logger.error("NumberFormatException at Node: "+attributes.getValue(0));}
					}
					else if (Name_TAG.equals(qName))
					{
						if(banno | bgatedocfeat){bname=true;}
					}
					else if (Value_TAG.equals(qName))
					{
						if(banno | bgatedocfeat){bvalue=true;}
					}
					else if (GateDocumentFeatures_TAG.equals(qName))
					{
						bgatedocfeat=true;
					}
				}

				public void endElement(String uri, String localName, String qName) throws SAXException
				{

					if (TextWithNodes_TAG.equals(qName))
					{
						btext = false;
						//generate Salttext
						sText= getSDocument().getSDocumentGraph().createSTextualDS(text);
						text=null; //saving memory
						System.out.println(text);
						//generate Salttokens
						int pos=-1;
						for(Integer nodeID : nodeIDs)
						{
							int act_val=nodeID;
							if(pos>-1)
							{
								SToken token = getSDocument().getSDocumentGraph().createSToken(sText, pos, act_val);
								tokenIDs.put(pos, token);
							}
							pos=act_val;
						}
						addProgress(0.4);
					}
					else if (AnnotationSet_TAG.equals(qName)) 
					{
						bas=false;
						addProgress(0.05); //can have infinite amount of AS but better to give some feedback to the user
					}
					else if (Annotation_TAG.equals(qName)) 
					{
						//generate Spans with features as bar name
						EList<SToken> token_set = new BasicEList<SToken>();
						for(Integer ele : nodeIDs)
						{
							if(ele >= a_start & ele<=a_end)
							{
								token_set.add(tokenIDs.get(ele));
							}
							if(ele>a_end){break;}
						}
						String afeatures="";
						if(featurepairs.isEmpty())
						{
							afeatures=a_name;
						}
						else
						{
							for(String ele : featurepairs)
							{
								afeatures+=ele+",";
							}
							afeatures=afeatures.substring(0, afeatures.length()-1);
						}
						SSpan topic = getSDocument().getSDocumentGraph().createSSpan(token_set);
						topic.createSAnnotation(null, a_name, afeatures);

						name="";value="";
						featurepairs.clear();
						banno=false;
					}
					else if (Feature_TAG.equals(qName)) 
					{
						if(name!="" & value!="")
						{
							if(bgatedocfeat)
							{
								getSDocument().createSMetaAnnotation(null, name, value);
							}
							else
							{
								featurepairs.add(name+":"+value); //GATE Features from Annotation for the text in the bar
							}
							name="";
							value="";
						}
					}
					else if (GateDocumentFeatures_TAG.equals(qName)) 
					{
						bgatedocfeat=false;
					}
				}

				public void characters(char ch[], int start, int length) throws SAXException
				{
					if (btext)
					{		
						text += new String(ch, start, length);					
					}
					else if(bas|bgatedocfeat)
					{
						if(bname)
						{
							bname=false;
							name=new String(ch, start, length);
						}
						else if(bvalue)
						{
							bvalue=false;
							value=new String(ch, start, length);
						}
					}
				}
			};
			
	  	//TODO: encoding nicht hard codieren
		String encoding = "UTF-8";
		File file = new File(resource.toFileString());
  	    InputStream inputStream= new FileInputStream(file);
  	    Reader reader = new InputStreamReader(inputStream,encoding);	
  	    InputSource is = getInputSource(reader,encoding);
  	    saxParser.parse(is, handler);
		
		} catch (Exception e){logger.error("XML-Parser Error: "+ e);}
		
		setProgress(1.0);
		
		return (DOCUMENT_STATUS.COMPLETED);
	}
}
