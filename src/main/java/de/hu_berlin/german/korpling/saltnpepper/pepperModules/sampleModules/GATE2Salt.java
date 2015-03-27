package de.hu_berlin.german.korpling.saltnpepper.pepperModules.sampleModules;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.annotation.AnnotationSetImpl;
import gate.util.InvalidOffsetException;

import java.util.TreeSet;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	public boolean isIgnoreWhitespaceTokens() {
		return ignoreWhitespaceTokens;
	}

	public void setIgnoreWhitespaceTokens(boolean ignoreWhitespaceTokens) {
		this.ignoreWhitespaceTokens = ignoreWhitespaceTokens;
	}

	/**
	 * Determines whether the annotations contained in the default annotation set 
	 * (the annotation set without a name) should be mapped.
	 */
	private boolean mapDefaultAnnotationSet= true;
	
	public boolean isMapDefaultAnnotationSet() {
		return mapDefaultAnnotationSet;
	}

	public void setMapDefaultAnnotationSet(boolean mapDefaultAnnotationSet) {
		this.mapDefaultAnnotationSet = mapDefaultAnnotationSet;
	}

	/**
	 * Determines an inclusive list of all annotation set names to be mapped. If this array is null, all
	 * annotations are mapped.
	 */
	private String[] mapAnnotationSetNames= null;
	
	public String[] getMapAnnotationSetNames() {
		return mapAnnotationSetNames;
	}

	public void setMapAnnotationSetNames(String[] mapAnnotationSetNames) {
		this.mapAnnotationSetNames = mapAnnotationSetNames;
	}

	/**
	 * Determines whether the type of an annotation is used as a prefix for each feature. For instance a
	 * feature 'pos=VVFin' comming from annotation with type 'myTok' is mapped to 'myTok_pos=VVFin' when 
	 * this value is set to true.
	 */
	private boolean typeAsPrefix= true; 
	
	public boolean isTypeAsPrefix() {
		return typeAsPrefix;
	}

	public void setTypeAsPrefix(boolean typeAsPrefix) {
		this.typeAsPrefix = typeAsPrefix;
	}

	/**
	 * Determines a type and an annotation name to be used for creating the tokenization.
	 * The syntax is:<code>ANNOS_SET_NAME:TYPE</code>
	 */
	private String useAsToken=null;
	
	public String getUseAsToken() {
		return useAsToken;
	}

	public void setUseAsToken(String useAsToken) {
		this.useAsToken = useAsToken;
	}

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
			
		String primText= getGateDocument().getContent().toString();
		if (	(primText!= null)&&
				(!primText.isEmpty())){
			//map primary text
			STextualDS sText= getsDocument().getSDocumentGraph().createSTextualDS(primText);
			boolean mapDefault= true;
			if (	(useAsToken!= null)&&
					(!useAsToken.isEmpty())){
				String[] parts= useAsToken.split(":");
				if (parts.length== 2){
					mapTokenization(sText, parts[0], parts[1]);
					mapDefault= false;
				}else if (parts.length== 1){ 
					mapTokenization(sText, null, useAsToken);
					mapDefault= false;
				}else{
					logger.warn("There is an syntax error in passed value to detect the token annotation '"+useAsToken+"'. It must match 'ANNOS_SET_NAME:TYPE'.");
				}
			}
			if (mapDefault){
				mapTokenizationDefault(sText);
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
	
	private void mapTokenization(STextualDS sText, String annoSetName, String type){
		AnnotationSet annoSet= null;
		if (	(annoSetName== null)||
				(annoSetName.isEmpty())){
			annoSet= getGateDocument().getAnnotations();
		}else{
			annoSet= getGateDocument().getAnnotations(annoSetName);
		}
		for (Annotation anno: annoSet){
			if (type.equals(anno.getType())){
				//annotation for token found
				
				getsDocument().getSDocumentGraph().createSToken(sText, anno.getStartNode().getOffset().intValue(), anno.getEndNode().getOffset().intValue());
			}
		}
	}
	
	private void mapTokenizationDefault(STextualDS sText){
		// create an annotation set containing all annotations (named annotation sets and default annotation set)
		AnnotationSet allAnnos= new AnnotationSetImpl(getGateDocument().getAnnotations()); 
		for (String annoName: getGateDocument().getAnnotationSetNames()){
			allAnnos.addAll(getGateDocument().getAnnotations(annoName));
		}
		
		// create all tokens
		TreeSet<Long> allOffsets= new TreeSet<Long>();
		// iterate through all default annotations 
		for (Annotation anno: allAnnos){
			allOffsets.add(anno.getStartNode().getOffset());
			allOffsets.add(anno.getEndNode().getOffset());
		}
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
//			sLayer.getSNodes().addAll(spans);
		}
	}
}