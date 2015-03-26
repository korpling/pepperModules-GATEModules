package de.hu_berlin.german.korpling.saltnpepper.pepperModules.sampleModules;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.annotation.AnnotationSetImpl;
import gate.creole.ResourceInstantiationException;
import gate.util.InvalidOffsetException;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.exceptions.PepperModuleException;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDataSourceSequence;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpanningRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SLayer;

public class GATE2Salt {
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
	
	private SDocument sDocument= null;

	public SDocument getsDocument() {
		return sDocument;
	}

	public void setsDocument(SDocument sDocument) {
		this.sDocument = sDocument;
	}
	/**
	 * Determines whether tokens should be created for a text interval, which only includes whitespaces
	 * and has no (exact) annotation above.
	 */
	private boolean ignoreWhitespaceTokens= true;
	
	/**
	 * Determines whether the annotations contained in the default annotation set 
	 * (the annotation set without a name) should be mapped.
	 */
	private boolean mapDefaultAnnotationSet= true;
	
	/**
	 * Determines an inclusive list of all annotation set names to be mapped. If this array is null, all
	 * annotations are mapped.
	 */
	private String[] mapAnnotationSetNames= null;
	
	/**
	 * Determines whether the type of an annotation is used as a prefix for each feature. For instance a
	 * feature 'pos=VVFin' comming from annotation with type 'myTok' is mapped to 'myTok_pos=VVFin' when 
	 * this value is set to true.
	 */
	private boolean typeAsPrefix= true; 
	
	
	public SDocument map(){
		if (getsDocument()== null){
			setsDocument(SaltFactory.eINSTANCE.createSDocument());
		}
		if (getsDocument().getSDocumentGraph()== null){
			getsDocument().setSDocumentGraph(SaltFactory.eINSTANCE.createSDocumentGraph());
		}
		
		// map all document features to document meta data
		for (Object key: getGateDocument().getFeatures().keySet()){
			Object value= getGateDocument().getFeatures().get(key);
			if (value!= null){
				getsDocument().createSMetaAnnotation(null, key.toString(), value.toString());
			}
		}
		
		// create an annotation set containing all annotations (named annotation sets and default annotation set)
		AnnotationSet allAnnos= new AnnotationSetImpl(getGateDocument().getAnnotations()); 
		for (String annoName: getGateDocument().getAnnotationSetNames()){
			allAnnos.addAll(getGateDocument().getAnnotations(annoName));
		}
		
		String primText= getGateDocument().getContent().toString();
		if (	(primText!= null)&&
				(!primText.isEmpty())){
			//map primary text
			STextualDS sText= getsDocument().getSDocumentGraph().createSTextualDS(primText);
			
			TreeSet<Long> allOffsets= new TreeSet<Long>();
			
			// iterate through all default annotations 
			for (Annotation anno: allAnnos){
				allOffsets.add(anno.getStartNode().getOffset());
				allOffsets.add(anno.getEndNode().getOffset());
			}
			
			// create all tokens
			Long lastOffset= null;
			for (Long offset: allOffsets){
				if (lastOffset!= null){
					if (!ignoreWhitespaceTokens){
						// create token for each interval, even if the contained text is a whitespace
						
						getsDocument().getSDocumentGraph().createSToken(sText, lastOffset.intValue(), offset.intValue());
					}else{
						// only create tokens for non empty texts and annotated empty texts
						
						getsDocument().getSDocumentGraph().createSToken(sText, lastOffset.intValue(), offset.intValue());
						String text;
						try {
							text = getGateDocument().getContent().getContent(lastOffset, offset).toString();
							if (text.trim().isEmpty()){
								if (!allAnnos.getContained(lastOffset, offset).isEmpty()){
									//create empty token
									getsDocument().getSDocumentGraph().createSToken(sText, lastOffset.intValue(), offset.intValue());
								};
							}
						} catch (InvalidOffsetException e) {
							//do nothing ;
						}

					}
					
				}
				lastOffset= offset;
			}
			
			if (mapDefaultAnnotationSet){
				mapAnnotationSet(sText, getGateDocument().getAnnotations(), null);
			}
			if (mapAnnotationSetNames== null){
				mapAnnotationSetNames= getGateDocument().getAnnotationSetNames().toArray(new String[getGateDocument().getAnnotationSetNames().size()]);
			}
			for (String annoName: mapAnnotationSetNames){
				mapAnnotationSet(sText, getGateDocument().getAnnotations(annoName), annoName);
			}	
		}
		return(getsDocument());
	}
	
	private void mapAnnotationSet(STextualDS sText, AnnotationSet annoSet, String annoSetName){
		if (	(annoSet!= null)&&
				(annoSet.size() > 0)){
			SLayer sLayer= SaltFactory.eINSTANCE.createSLayer();
			sLayer.setSName(annoSetName);
			EList<SSpan> spans= new BasicEList<SSpan>();
			for (Annotation anno: annoSet){
				String type_= anno.getType()+"_";
				String type= anno.getType();
				Long start= anno.getStartNode().getOffset();
				Long end= anno.getEndNode().getOffset();
				SDataSourceSequence seq= SaltFactory.eINSTANCE.createSDataSourceSequence();
				seq.setSStart(start.intValue());
				seq.setSEnd(end.intValue());
				seq.setSSequentialDS(sText);
				
				EList<SToken> tokens= getsDocument().getSDocumentGraph().getSTokensBySequence(seq);
				
				// adding span manually because of setting its name
				SSpan sSpan= SaltFactory.eINSTANCE.createSSpan();
				sSpan.setSName(type_+anno.getId().toString());
				getsDocument().getSDocumentGraph().addSNode(sSpan);
				spans.add(sSpan);
//				sSpan.getSLayers().add(sLayer);
//				sLayer.getSNodes().add(sSpan);
				if (tokens== null){
					logger.warn("Cannot create span '"+anno.getId()+"' for tokens. ");
				}else{
					for (SToken tok: tokens){
						SSpanningRelation spanRel= SaltFactory.eINSTANCE.createSSpanningRelation();
						spanRel.setSource(sSpan);
						spanRel.setTarget(tok);
						getsDocument().getSDocumentGraph().addSRelation(spanRel);
//						sLayer.getSRelations().add(spanRel);
//						spanRel.getSLayers().add(sLayer);
					}
				}
				
				if (!anno.getFeatures().isEmpty()){
					
					for (Object annoName: anno.getFeatures().keySet()){
						String annoVal= anno.getFeatures().get(annoName).toString();
						String annoNameStr; 
						if (typeAsPrefix){
							annoNameStr= type_ + annoName.toString();
						}else{
							annoNameStr= annoName.toString();
						}
						sSpan.createSAnnotation(annoSetName, annoNameStr, annoVal);
					}
				}else{
					if (typeAsPrefix){
						sSpan.createSAnnotation(annoSetName, type_+"type", type);
					}else{
						sSpan.createSAnnotation(annoSetName, "type", type);
					}
				}
			}
			sLayer.getSNodes().addAll(spans);
		}
	}
}