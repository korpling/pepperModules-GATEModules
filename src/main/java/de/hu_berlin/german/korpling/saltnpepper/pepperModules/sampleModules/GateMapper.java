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
 * This class is a GATE2Salt Mapper. It maps the GATE-XML Version 3 from GATE 7 and 8 to Salt.
 * 
 * @author Paul Burzlaff
 *
 */

public class GateMapper extends PepperMapperImpl
{
	// this is a logger, for recording messages during program process, like
	// debug messages
	private static final Logger logger = LoggerFactory.getLogger(GateImporter.class);
	protected String text;
	STextualDS sText= null;
	Map<Integer,SToken> tokenIDs = new HashMap<Integer,SToken>();
	// TODO tokenranges entf wenn nich gebraucht
	//Map<String,int[]> tokenRanges = new HashMap<String,int[]>(); 
	List<Integer> nodeIDs=new ArrayList<Integer>();

	/**
	 * Does GATE provide Corpus information?
	 */
	@Override
	public DOCUMENT_STATUS mapSCorpus()
	{
		// getScorpus() returns the current corpus object.
		getSCorpus().createSMetaAnnotation(null, "date", "1989-12-17");

		return (DOCUMENT_STATUS.COMPLETED);
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
		// getResources(), make sure, it was set in createMapper()
		URI resource = getResourceURI();
		System.out.println(resource);
		// we record, which file currently is imported to the debug stream, in
		// this dummy implementation the resource is null
		logger.debug("Importing the file {}.", resource);
		
		//TODO set progress
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
					System.out.println("Start Element: " + qName);

					if (qName.equalsIgnoreCase("TextWithNodes"))
					{
						btext = true;
					}
					else if (qName.equalsIgnoreCase("AnnotationSet")) 
					{
						bas=true;
						if(attributes.getLength()>0)
						{
							if (attributes.getQName(0).equalsIgnoreCase("Name"))
							{
								as_name=attributes.getValue(0);
							}
						}
					}
					else if (qName.equalsIgnoreCase("Annotation")) 
					{
						banno=true;

						for(short i=0;i<attributes.getLength();i++)
						{
							if(attributes.getQName(i).equalsIgnoreCase("Type"))
							{
								a_name=attributes.getValue(i);
							}
							else if (attributes.getQName(i).equalsIgnoreCase("StartNode"))
							{
								try
								{
									a_start=Integer.parseInt(attributes.getValue(i).trim());
								} catch (NumberFormatException e)
								{
									logger.error("NumberFormatException in Annotation "+a_name+" at: "+attributes.getValue(i));
									e.printStackTrace();  //TODO Flo fragen wie sich pst verhaelt
								}
							} 
							else if (attributes.getQName(i).equalsIgnoreCase("EndNode"))
							{
								try
								{
									a_end=Integer.parseInt(attributes.getValue(i).trim());
								} catch (NumberFormatException e)
								{
									logger.error("NumberFormatException in Annotation "+a_name+" at: "+attributes.getValue(i));
									e.printStackTrace();
								}
							} 
							else if (attributes.getQName(i).equalsIgnoreCase("Id"))
							{
								//TODO id gebraucht?
								//id=attributes.getValue(i);
							}
						}
						//generate spans
//						EList<SToken> token_set = new BasicEList<SToken>();
//						for(Integer ele : nodeIDs)
//						{
//							if(ele >= a_start & ele<=a_end)
//							{
//								token_set.add(tokenIDs.get(ele));
//							}
//							if(ele>a_end){break;}
//						}
//						
//						SSpan topic = getSDocument().getSDocumentGraph().createSSpan(token_set);
//						topic.createSAnnotation(null, a_name, "wert aus features?");

					}
					else if (qName.equalsIgnoreCase("GateDocument"))
					{
						System.out.println(attributes.getQName(0));
						System.out.println(attributes.getValue(0));
						if (attributes.getQName(0).equalsIgnoreCase("version"))
						{
							if (!attributes.getValue(0).equalsIgnoreCase("3"))
							{
								logger.warn("This Module works for GATE_Document Version 3. Anyway still trying...");
							}
						}
						addProgress(0.05);
					}
					else if (qName.equalsIgnoreCase("Node"))
					{
						if(attributes.getLength()>0){nodeID = attributes.getValue(0).trim();}
						else{logger.error("Node has no attribute");	}
						
						try
						{
							nodeIDs.add(Integer.parseInt(nodeID));
						} catch (NumberFormatException e)
						{
							logger.error("NumberFormatException at Node: "+attributes.getValue(0));
							e.printStackTrace();
						}
						System.out.println(nodeID);
					}
					else if (qName.equalsIgnoreCase("Name"))
					{
						//System.out.println("inName");
						if(banno | bgatedocfeat)
						{
							//System.out.println("inNamebas");
							bname=true;
						}
					}
					else if (qName.equalsIgnoreCase("Value"))
					{
						if(banno | bgatedocfeat)
						{
							bvalue=true;
						}
					}
					else if (qName.equalsIgnoreCase("GateDocumentFeatures"))
					{
						bgatedocfeat=true;
					}
				}

				public void endElement(String uri, String localName, String qName) throws SAXException
				{

					System.out.println("End Element: " + qName);
					if (qName.equalsIgnoreCase("TextWithNodes"))
					{
						btext = false;
						//generate Salttext
						sText= getSDocument().getSDocumentGraph().createSTextualDS(text);
						//TODO text=null speicher opt
						//System.out.println(text);

						//generate Salttokens
						int pos=-1;
						for(Integer nodeID : nodeIDs)
						{
							int act_val=nodeID;
							if(pos>-1)
							{
								//no long in SToken
								SToken token = getSDocument().getSDocumentGraph().createSToken(sText, pos, act_val);
								tokenIDs.put(pos, token);
							}
							pos=act_val;
						}
						addProgress(0.4);
//						for(Map.Entry<String, int[]> ele : tokenRanges.entrySet())
//						{
//							int[] fromto = ele.getValue();
//							SToken token = getSDocument().getSDocumentGraph().createSToken(sText, fromto[0], fromto[1]);
//							tokenIDs.put(ele.getKey(), token);
//						}
					}
					else if (qName.equalsIgnoreCase("AnnotationSet")) 
					{
						bas=false;
						addProgress(0.05); //can have infinite amount of AS but better to give some feedback to the user
					}
					else if (qName.equalsIgnoreCase("Annotation")) 
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
						System.out.println("afeatures: "+afeatures);
						name="";value="";
						featurepairs.clear();
						banno=false;
					}
					else if (qName.equalsIgnoreCase("Feature")) 
					{
						if(name!="" & value!="")
						{
							if(bgatedocfeat)
							{
								System.out.println("Metadata: "+name+":"+value);
								getSDocument().createSMetaAnnotation(null, name, value);
							}
							else
							{
								featurepairs.add(name+":"+value); //GATE Features from Annotation for text in bar
							}
							name="";
							value="";
						}
					}
					else if (qName.equalsIgnoreCase("GateDocumentFeatures")) 
					{
						bgatedocfeat=false;
					}
				}

				public void characters(char ch[], int start, int length) throws SAXException
				{

					if (btext)
					{
						//System.out.println("text : " + new String(ch, start, length));
						//System.out.println(nodeID);		
						text += new String(ch, start, length);					
					}
					else if(bas|bgatedocfeat)
					{
						//System.out.println("inAS");
						if(bname)
						{
							//System.out.println("inname");
							bname=false;
							name=new String(ch, start, length);
							System.out.println("name:"+name);
						}
						else if(bvalue)
						{
							bvalue=false;
							value=new String(ch, start, length);
							System.out.println("value:"+value);
						}
					}
				}

			};

		File file = new File(resource.toFileString());
  	    InputStream inputStream= new FileInputStream(file);
  	    Reader reader = new InputStreamReader(inputStream,"UTF-8");	
		InputSource is = new InputSource(reader);
  	    is.setEncoding("UTF-8");
		saxParser.parse(is, handler);
		
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		setProgress(1.0);
		
		System.out.println(text);
		
		
		//TODO aufraeumen
		/**
		 * STEP 1: we create the primary data and hold a reference on the
		 * primary data object
		 */
		STextualDS primaryText = getSDocument().getSDocumentGraph().createSTextualDS("Is this example more complicated than it appears to be?");
		
		// we add a progress to notify the user about the process status (this
		// is very helpful, especially for longer taking processes)
		

		/**
		 * STEP 2: we create a tokenization over the primary data
		 */
		SToken tok_is = getSDocument().getSDocumentGraph().createSToken(primaryText, 0, 2); // Is
		SToken tok_thi = getSDocument().getSDocumentGraph().createSToken(primaryText, 3, 7); // this
		SToken tok_exa = getSDocument().getSDocumentGraph().createSToken(primaryText, 8, 15); // example
		SToken tok_mor = getSDocument().getSDocumentGraph().createSToken(primaryText, 16, 20); // more
		SToken tok_com = getSDocument().getSDocumentGraph().createSToken(primaryText, 21, 32); // complicated
		SToken tok_tha = getSDocument().getSDocumentGraph().createSToken(primaryText, 33, 37); // than
		SToken tok_it = getSDocument().getSDocumentGraph().createSToken(primaryText, 38, 40); // it
		SToken tok_app = getSDocument().getSDocumentGraph().createSToken(primaryText, 41, 48); // appears
		SToken tok_to = getSDocument().getSDocumentGraph().createSToken(primaryText, 49, 51); // to
		SToken tok_be = getSDocument().getSDocumentGraph().createSToken(primaryText, 52, 54); // be
		SToken tok_PUN = getSDocument().getSDocumentGraph().createSToken(primaryText, 54, 55); // ?

		// we add a progress to notify the user about the process status (this
		// is very helpful, especially for longer taking processes)


		/**
		 * STEP 3: we create a part-of-speech and a lemma annotation for tokens
		 */
		// we create part-of-speech annotations
		tok_is.createSAnnotation(null, "pos", "VBZ");
		tok_thi.createSAnnotation(null, "pos", "DT");
		tok_exa.createSAnnotation(null, "pos", "NN");
		tok_mor.createSAnnotation(null, "pos", "RBR");
		tok_com.createSAnnotation(null, "pos", "JJ");
		tok_tha.createSAnnotation(null, "pos", "IN");
		tok_it.createSAnnotation(null, "pos", "PRP");
		tok_app.createSAnnotation(null, "pos", "VBZ");
		tok_to.createSAnnotation(null, "pos", "TO");
		tok_be.createSAnnotation(null, "pos", "VB");
		tok_PUN.createSAnnotation(null, "pos", ".");

		// we create lemma annotations
		tok_is.createSAnnotation(null, "lemma", "be");
		tok_thi.createSAnnotation(null, "lemma", "this");
		tok_exa.createSAnnotation(null, "lemma", "example");
		tok_mor.createSAnnotation(null, "lemma", "more");
		tok_com.createSAnnotation(null, "lemma", "complicated");
		tok_tha.createSAnnotation(null, "lemma", "than");
		tok_it.createSAnnotation(null, "lemma", "it");
		tok_app.createSAnnotation(null, "lemma", "appear");
		tok_to.createSAnnotation(null, "lemma", "to");
		tok_be.createSAnnotation(null, "lemma", "be");
		tok_PUN.createSAnnotation(null, "lemma", ".");

		// we add a progress to notify the user about the process status (this
		// is very helpful, especially for longer taking processes)

		/**
		 * STEP 4: we create some information structure annotations via spans,
		 * spans can be used, to group tokens to a set, which can be annotated
		 * <table border="1">
		 * <tr>
		 * <td>contrast-focus</td>
		 * <td colspan="9">topic</td>
		 * </tr>
		 * <tr>
		 * <td>Is</td>
		 * <td>this</td>
		 * <td>example</td>
		 * <td>more</td>
		 * <td>complicated</td>
		 * <td>than</td>
		 * <td>it</td>
		 * <td>appears</td>
		 * <td>to</td>
		 * <td>be</td>
		 * </tr>
		 * </table>
		 */
		SSpan contrastFocus = getSDocument().getSDocumentGraph().createSSpan(tok_is);
		contrastFocus.createSAnnotation(null, "Inf-Struct", "contrast-focus");
		EList<SToken> topic_set = new BasicEList<SToken>();
		topic_set.add(tok_thi);
		topic_set.add(tok_exa);
		topic_set.add(tok_mor);
		topic_set.add(tok_com);
		topic_set.add(tok_tha);
		topic_set.add(tok_it);
		topic_set.add(tok_app);
		topic_set.add(tok_to);
		topic_set.add(tok_be);
		SSpan topic = getSDocument().getSDocumentGraph().createSSpan(topic_set);
		topic.createSAnnotation(null, "Inf-Struct", "topic");

		// we add a progress to notify the user about the process status (this
		// is very helpful, especially for longer taking processes)


		/**
		 * STEP 5: we create anaphoric relation between 'it' and 'this example',
		 * therefore 'this example' must be added to a span. This makes use of
		 * the graph based model of Salt. First we create a relation, than we
		 * set its source and its target node and last we add the relation to
		 * the graph.
		 */
		EList<SToken> target_set = new BasicEList<SToken>();
		target_set.add(tok_thi);
		target_set.add(tok_exa);
		SSpan target = getSDocument().getSDocumentGraph().createSSpan(target_set);
		SPointingRelation anaphoricRel = SaltFactory.eINSTANCE.createSPointingRelation();
		anaphoricRel.setSStructuredSource(tok_is);
		anaphoricRel.setSStructuredTarget(target);
		anaphoricRel.addSType("anaphoric");
		// we add the created relation to the graph
		getSDocument().getSDocumentGraph().addSRelation(anaphoricRel);

		// we add a progress to notify the user about the process status (this
		// is very helpful, especially for longer taking processes)


		/**
		 * STEP 6: We create a syntax tree following the Tiger scheme
		 */
		SStructure root = SaltFactory.eINSTANCE.createSStructure();
		SStructure sq = SaltFactory.eINSTANCE.createSStructure();
		SStructure np1 = SaltFactory.eINSTANCE.createSStructure();
		SStructure adjp1 = SaltFactory.eINSTANCE.createSStructure();
		SStructure adjp2 = SaltFactory.eINSTANCE.createSStructure();
		SStructure sbar = SaltFactory.eINSTANCE.createSStructure();
		SStructure s1 = SaltFactory.eINSTANCE.createSStructure();
		SStructure np2 = SaltFactory.eINSTANCE.createSStructure();
		SStructure vp1 = SaltFactory.eINSTANCE.createSStructure();
		SStructure s2 = SaltFactory.eINSTANCE.createSStructure();
		SStructure vp2 = SaltFactory.eINSTANCE.createSStructure();
		SStructure vp3 = SaltFactory.eINSTANCE.createSStructure();

		// we add annotations to each SStructure node
		root.createSAnnotation(null, "cat", "ROOT");
		sq.createSAnnotation(null, "cat", "SQ");
		np1.createSAnnotation(null, "cat", "NP");
		adjp1.createSAnnotation(null, "cat", "ADJP");
		adjp2.createSAnnotation(null, "cat", "ADJP");
		sbar.createSAnnotation(null, "cat", "SBAR");
		s1.createSAnnotation(null, "cat", "S");
		np2.createSAnnotation(null, "cat", "NP");
		vp1.createSAnnotation(null, "cat", "VP");
		s2.createSAnnotation(null, "cat", "S");
		vp2.createSAnnotation(null, "cat", "VP");
		vp3.createSAnnotation(null, "cat", "VP");

		// we add the root node first
		getSDocument().getSDocumentGraph().addSNode(root);
		STYPE_NAME domRel = STYPE_NAME.SDOMINANCE_RELATION;
		// than we add the rest and connect them to each other
		getSDocument().getSDocumentGraph().addSNode(root, sq, domRel);
		getSDocument().getSDocumentGraph().addSNode(sq, tok_is, domRel); // "Is"
		getSDocument().getSDocumentGraph().addSNode(sq, np1, domRel);
		getSDocument().getSDocumentGraph().addSNode(np1, tok_thi, domRel); // "this"
		getSDocument().getSDocumentGraph().addSNode(np1, tok_exa, domRel); // "example"
		getSDocument().getSDocumentGraph().addSNode(sq, adjp1, domRel);
		getSDocument().getSDocumentGraph().addSNode(adjp1, adjp2, domRel);
		getSDocument().getSDocumentGraph().addSNode(adjp2, tok_mor, domRel); // "more"
		getSDocument().getSDocumentGraph().addSNode(adjp2, tok_com, domRel); // "complicated"
		getSDocument().getSDocumentGraph().addSNode(adjp1, sbar, domRel);
		getSDocument().getSDocumentGraph().addSNode(sbar, tok_tha, domRel); // "than"
		getSDocument().getSDocumentGraph().addSNode(sbar, s1, domRel);
		getSDocument().getSDocumentGraph().addSNode(s1, np2, domRel);
		getSDocument().getSDocumentGraph().addSNode(np2, tok_it, domRel); // "it"
		getSDocument().getSDocumentGraph().addSNode(s1, vp1, domRel);
		getSDocument().getSDocumentGraph().addSNode(vp1, tok_app, domRel); // "appears"
		getSDocument().getSDocumentGraph().addSNode(vp1, s2, domRel);
		getSDocument().getSDocumentGraph().addSNode(s2, vp2, domRel);
		getSDocument().getSDocumentGraph().addSNode(vp2, tok_to, domRel); // "to"
		getSDocument().getSDocumentGraph().addSNode(vp2, vp3, domRel);
		getSDocument().getSDocumentGraph().addSNode(vp3, tok_be, domRel); // "be"
		getSDocument().getSDocumentGraph().addSNode(root, tok_PUN, domRel); // "?"

		// we set progress to 'done' to notify the user about the process status
		// (this is very helpful, especially for longer taking processes)

		//System.out.println(getSDocument().getSDocumentGraph().getSTokens().size());
		// now we are done and return the status that everything was successful
		return (DOCUMENT_STATUS.COMPLETED);
	}
	
	

}
