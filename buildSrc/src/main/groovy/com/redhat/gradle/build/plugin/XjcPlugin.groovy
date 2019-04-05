package com.redhat.gradle.build.plugin

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.GroovyBasePlugin

import com.redhat.gradle.build.model.jaxb.JaxbCodeGeneration
import com.redhat.gradle.build.model.jaxb.Schema
import com.redhat.gradle.build.types.Xjc
import com.redhat.gradle.build.types.Xjc.Scope

/**
 * Plugin for applying defaults for {@link Xjc} tasks
 *
 * @author Eric Deandrea November 2016
 */
final class XjcPlugin implements Plugin<Project> {
	private static final String XJC_GENERATION_TASK_NAME = 'xjcGeneration'

	@Override
	final void apply(Project project) {
		project.configurations {
			xjc
		}

		// Add the jaxb code generation extension
		project.extensions.create(XJC_GENERATION_TASK_NAME, JaxbCodeGeneration)

		// This adds the ability in the build.gradle file to do something like
		// schema()
		// It adds a schema() method to the Project object which takes a Map
		project.extensions[XJC_GENERATION_TASK_NAME].convention.plugins.utilities = new SchemaPluginConvention(project)

		// Want to add the task to run all of the xjc tasks
		def xjcGenerationTask = project.task(XJC_GENERATION_TASK_NAME, group: 'Build', dependsOn: project.tasks.withType(Xjc), description: 'Run all XJC tasks')

		project.afterEvaluate { proj ->
			// 1st capture anything in the jaxbGeneration extension and create tasks for them
			createTasksFromSchemas(proj)

			proj.tasks.withType(Xjc) { xjcTask ->
				if (xjcTask.scope == null) {
					xjcTask.scope = Scope.COMPILE
				}

				if (xjcTask.xjcBindingFile == null) {
					xjcTask.xjcBindingFile = proj.file 'src/main/schema/bindings/xjc.xjb.xml'
				}

				if (xjcTask.schemaDir == null) {
					xjcTask.schemaDir = proj.file 'src/main/schema/schemas'
				}

				def xjcSchemaGenDirEmpty = xjcTask.schemaGenDir == null

				switch (xjcTask.scope) {
					case Scope.COMPILE:
						if (xjcSchemaGenDirEmpty) {
							xjcTask.schemaGenDir = proj.file "$proj.buildDir/generated-sources/jaxb/main"
						}

						proj.tasks.compileJava.dependsOn xjcTask
						proj.sourceSets.main.java.srcDir xjcTask.schemaGenDir

						proj.plugins.withType(GroovyBasePlugin) {
							proj.sourceSets.main.groovy.srcDir xjcTask.schemaGenDir
						}
						break

					case Scope.TEST_COMPILE:
						if (xjcSchemaGenDirEmpty) {
							xjcTask.schemaGenDir = proj.file "$proj.buildDir/generated-sources/jaxb/test"
						}

						proj.tasks.compileTestJava.dependsOn xjcTask
						proj.sourceSets.test.java.srcDir xjcTask.schemaGenDir

						proj.plugins.withType(GroovyBasePlugin) {
							proj.sourceSets.test.groovy.srcDir xjcTask.schemaGenDir
						}
						break
				}
			}
		}
	}

	private void createTasksFromSchemas(final Project project) {
		def schemas = project?.xjcGeneration?.schemas

		if (schemas && !schemas.isEmpty()) {
			schemas.each { schema ->
				def xsdFile = schema?.schemaFile
				def javaPackage = schema?.javaPackageName

				if (!xsdFile) {
					throw new GradleException("Required property schemaFile not set in xjcGeneration schema element")
				}

				def taskName = schema.taskName ?: "schemaGen_${javaPackage ? javaPackage.replaceAll('\\.', '-') : xsdFile.replaceAll(' ', '-')}"
				def taskDesc = schema.taskDescription ?: "Generate sources for the schema file $xsdFile"

				project.task(taskName, type: Xjc, description: taskDesc) {
					schemaFile = xsdFile
					javaPackageName = javaPackage

					if (schema.scope) {
						scope = Scope.valueOf(schema?.scope)
					}

					if (schema.schemaDir) {
						schemaDir = schema.schemaDir
					}

					if (schema.xjcBindingFile) {
						xjcBindingFile = schema.xjcBindingFile
					}

					if (schema.schemaGenDir) {
						schemaGenDir = schema.schemaGenDir
					}
				}
			}
		}
	}

	class SchemaPluginConvention {
		private Project project

		SchemaPluginConvention(Project project) {
			this.project = project
		}

		def schema(Map<String, Object> map) { new Schema(map) }
	}
}
