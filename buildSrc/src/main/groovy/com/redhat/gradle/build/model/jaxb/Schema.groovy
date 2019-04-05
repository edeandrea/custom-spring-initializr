package com.redhat.gradle.build.model.jaxb

import groovy.transform.ToString

/**
 * Class for holding schema information
 *
 * @author Eric Deandrea November 2016
 */
@ToString(includeNames=true, includeFields=true)
class Schema {
	/**
	 * The description for the task. If not specified a default will be generated.
	 */
	String taskDescription

	/**
	 * The name of the task to create. If not specified a default will be generated.
	 */
	String taskName

	/**
	 * The name of the schema file
	 */
	String schemaFile

	/**
	 * The java package name
	 */
	String javaPackageName

	/**
	 * The {@link com.redhat.gradle.build.types.Xjc.Scope Scope} to use, specified as a string
	 */
	String scope

	/**
	 * The directory containing the schema file
	 */
	File schemaDir

	/**
	 * The xjc binding file to use
	 */
	File xjcBindingFile

	/**
	 * The directory where generated sources will be placed
	 */
	File schemaGenDir
}
