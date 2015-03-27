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

import java.util.ArrayList;
import java.util.List;

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperties;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperty;

/**
 * Defines the properties to be used for the {@link EXMARaLDAImporter}. 
 * @author Florian Zipser
 *
 */
public class GATEImporterProperties extends PepperModuleProperties 
{
	/**
	 * Determines whether tokens should be created for a text interval, which only includes whitespaces
	 * and has no (exact) annotation above.
	 */
	public static final String PROP_IGNORE_WHITESPACE_TOKENS="ignoreWhitespaceTokens";
	
	/**
	 * Determines whether the annotations contained in the default annotation set 
	 * (the annotation set without a name) should be mapped.
	 */
	public static final String PROP_MAP_DEFAULT_ANNO_SET="mapDefaultAnnotationSet";
	
	/**
	 * Determines an inclusive list of all annotation set names to be mapped. If this array is null, all
	 * annotations are mapped. The list is comma separated.
	 */
	public static final String PROP_MAP_ANNOTATION_SET_NAMES="mapAnnotationSetNames";
	
	/**
	 * Determines whether the type of an annotation is used as a prefix for each feature. For instance a
	 * feature 'pos=VVFin' comming from annotation with type 'myTok' is mapped to 'myTok_pos=VVFin' when 
	 * this value is set to true.
	 */
	public static final String PROP_TYPE_AS_PREFIX="typeAsPrefix";
	
	/**
	 * Determines a type and an annotation name to be used for creating the tokenization.
	 * The syntax is:<code>ANNOS_SET_NAME:TYPE</code>
	 */
	public static final String PROP_USE_AS_TOKEN="useAsToken";
	
	public GATEImporterProperties(){
		this.addProperty(new PepperModuleProperty<Boolean>(PROP_IGNORE_WHITESPACE_TOKENS, Boolean.class, "Determines whether tokens should be created for a text interval, which only includes whitespaces and has no (exact) annotation above.", true, false));
		this.addProperty(new PepperModuleProperty<Boolean>(PROP_MAP_DEFAULT_ANNO_SET, Boolean.class, "Determines whether the annotations contained in the default annotation set (the annotation set without a name) should be mapped.", true, true));
		this.addProperty(new PepperModuleProperty<String>(PROP_MAP_ANNOTATION_SET_NAMES, String.class, "Determines an inclusive list of all annotation set names to be mapped. If this array is null, all annotations are mapped. The list is comma separated.", null, true));
		this.addProperty(new PepperModuleProperty<Boolean>(PROP_TYPE_AS_PREFIX, Boolean.class, "Determines whether the type of an annotation is used as a prefix for each feature. For instance a feature 'pos=VVFin' comming from annotation with type 'myTok' is mapped to 'myTok_pos=VVFin' when this value is set to true.", true, true));
		this.addProperty(new PepperModuleProperty<String>(PROP_USE_AS_TOKEN, String.class, "Determines a type and an annotation name to be used for creating the tokenization. The syntax is:<code>ANNOS_SET_NAME:TYPE</code>", null, true));
	}
	
	public Boolean getIgnoreWhitespaces(){
		return((Boolean)this.getProperty(PROP_IGNORE_WHITESPACE_TOKENS).getValue());
	}
	public Boolean getMapDefaultAnnoSet(){
		return((Boolean)this.getProperty(PROP_MAP_DEFAULT_ANNO_SET).getValue());
	}
	public Boolean getTypeAsPrefix(){
		return((Boolean)this.getProperty(PROP_TYPE_AS_PREFIX).getValue());
	}
	public String[] getMapAnnotationSetNames(){
		String[] retVal= null;
		
		String map= (String)this.getProperty(PROP_MAP_ANNOTATION_SET_NAMES).getValue();
		if (map!= null){
			String[] parts= map.split(",");
			if (parts.length> 0){
				retVal= new String[parts.length];
				int i=0;
				for (String annoSetName: parts){
					retVal[i]= annoSetName.trim();
					i++;
				}
			}
			}
		return(retVal);
	}
	public String getUseAsToken(){
		return((String)this.getProperty(PROP_USE_AS_TOKEN).getValue());
	}
	
}
