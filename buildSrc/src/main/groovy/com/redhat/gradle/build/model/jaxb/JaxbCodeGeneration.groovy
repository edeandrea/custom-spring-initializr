package com.redhat.gradle.build.model.jaxb

import javax.xml.validation.Schema

import groovy.transform.ToString

/**
 * Extension class to be used for storing JAXB code generation information for a project
 *
 * @author Eric Deandrea November 2016
 */
@ToString(includeNames=true, includeFields=true)
class JaxbCodeGeneration {
	/**
	 * The list of {@link Schema}s to generate
	 */
	List<Schema> schemas = []
}
