package com.redhat.gradle.build.types

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.JavaVersion
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Task for doing XJC class generation from a schema
 *
 * @author Eric Deandrea August 2012
 */
class Xjc extends DefaultTask {
	/**
	 * The name of the schema file to generate from
	 */
	@Input
	String schemaFile

	/**
	 * The xjc binding file to use
	 */
	@InputFile
	File xjcBindingFile

	/**
	 * The directory containing the schema file
	 */
	@InputDirectory
	File schemaDir

	/**
	 * The directory where generated sources will be placed
	 */
	@OutputDirectory
	File schemaGenDir

	/**
	 * The name of the java package to use for the generated sources
	 */
	@Input
	String javaPackageName = ''

	/**
	 * The scope where the generated sources will be used
	 */
	@Input
	Scope scope

	private xjcTaskAlreadyCreated = false

	private static final String ALLOW_ANY_EXTERNAL_SCHEMA = "all"
	private static final String ALLOW_ANY_EXTERNAL_DTD = "all"
	private static final String ALLOW_ANY_EXTERNAL_STYLE_SHEET = "all"

	/**
	 * An enum for defining the scope
	 */
	static enum Scope {
		/**
		 * Libraries are on the compile classpath
		 */
		COMPILE,

		/**
		 * Libraries are on the testCompile classpath
		 */
		TEST_COMPILE,

		/**
		 * Libraries are on the integTestCompile classpath
		 */
		INTEGRATION_TEST_COMPILE
	}

	Xjc() {
		description = "Generates Java source files using the XJC compiler for the project '${project.name}'"
		group = 'Build'
	}

	private void validateProperties() {
		if (!this.schemaGenDir) {
			throw new GradleException("Property schemaGenDir not set on task $name")
		}

		if (!this.xjcBindingFile) {
			throw new GradleException("Property xjcBindingFile not set on task $name")
		}

		if (!this.schemaDir) {
			throw new GradleException("Property schemaDir not set on task $name")
		}

		if (!this.schemaFile) {
			throw new GradleException("Property schemaFile not set on task $name")
		}
	}

	@TaskAction
	def generateSources() {
		validateProperties()

		def schema = getSchema()
		logger.lifecycle "Running XJC compiler for schema $schema"

		if (JavaVersion.current().isJava8Compatible()) {
			System.setProperty 'javax.xml.accessExternalSchema', ALLOW_ANY_EXTERNAL_SCHEMA
			System.setProperty 'javax.xml.accessExternalDTD', ALLOW_ANY_EXTERNAL_DTD
			System.setProperty 'javax.xml.accessExternalStylesheet', ALLOW_ANY_EXTERNAL_STYLE_SHEET
		}

		if (!xjcTaskAlreadyCreated) {
			ant.taskdef(name: 'xjc', classname: 'com.sun.tools.xjc.XJCTask', classpath: project.configurations.xjc.asPath)
			xjcTaskAlreadyCreated = true
		}

		def language = schema.name.endsWith('wsdl') ? 'WSDL' : 'XMLSCHEMA'

		if (this.javaPackageName) {
			ant.xjc(
				destdir: schemaGenDir.absolutePath,
				binding: xjcBindingFile,
				package: javaPackageName,
				extension: true,
				schema: schema,
				language: language)
		}
		else {
			ant.xjc(
				destdir: schemaGenDir.absolutePath,
				binding: xjcBindingFile,
				extension: true,
				schema: schema,
				language: language)
		}
	}

	@InputFile
	File getSchema() {
		new File(this.schemaDir, this.schemaFile)
	}
}
