/*
 * Copyright (c) 2016 Rocana.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rocana.gradle

import org.apache.avro.Schema
import org.apache.avro.SchemaParseException
import org.apache.avro.compiler.specific.SpecificCompiler
import org.apache.avro.generic.GenericData
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

class AvroPlugin implements Plugin<Project> {

  @Override
  void apply(Project project) {
    project.pluginManager.apply('java')

    NamedDomainObjectContainer<AvroContainer> avroConfigs = project.container(AvroContainer)
    project.extensions.avro = avroConfigs

    project.afterEvaluate {
      project.avro.each { AvroContainer container ->
        String compileTaskName;

        // The compile task for the main source set has a special name.
        if (container.name == 'main') {
          compileTaskName = 'compileJava'
        } else {
          compileTaskName = "compile${container.name.capitalize()}Java"
        }

        File sourceDirectory = container.sourceDirectory ?: new File(project.projectDir, "src/${container.name}/avro")
        File generatedSourceDirectory = container.generatedSourceDirectory ?: new File(project.buildDir, "generated-avro-${container.name}")

        // Create an Avro task for each config specified.
        Task taskCompile = project.tasks.create("compile${container.name.capitalize()}Avro", AvroCompileTask.class) { task ->
          task.source = sourceDirectory
          task.generatedSourceDirectory = generatedSourceDirectory
          task.fieldVisibility = container.fieldVisibility

          if (container.templateDirectory != null) {
            task.templateDirectory = container.templateDirectory.absolutePath
          }
        }

        /*
         * TODO: This will throw an exception if the user creates an Avro config
         * for a source set that doesn't exist. We should throw a nicer error.
         */

        // Make the corresponding Java compilation task depend on the created Avro compile task.
        project.tasks.getByName(compileTaskName).dependsOn(taskCompile)
        project.sourceSets[container.name].java.srcDirs(taskCompile.generatedSourceDirectory)
      }
    }
  }

  def tasks = [AvroCompileTask.class]
}

class AvroContainer {

  final String name

  boolean createSetters
  String fieldVisibility
  String stringType
  File templateDirectory
  File sourceDirectory
  File generatedSourceDirectory

  AvroContainer(String name) {
    this.name = name
  }

  @Override
  String toString() {
    return "{ name:$name, createSetters:$createSetters, fieldVisibility:$fieldVisibility, stringType:$stringType, templateDir:$templateDirectory, generatedSourceDirectory:$generatedSourceDirectory }"
  }

}

class AvroCompileTask extends SourceTask {

  @OutputDirectory
  File generatedSourceDirectory

  boolean generateSetters
  String stringType
  String fieldVisibility
  String templateDirectory

  AvroCompileTask() {
    stringType = "String"
    generateSetters = true

    setDescription("Compile Avro schema files to Java source code")
  }

  @TaskAction
  def compile() {
    logger.info("Compiling {} files to {}", source.size(), generatedSourceDirectory)

    Deque<File> toProcess = new ArrayDeque<>(source.getFiles())
    Map<String, Schema> types = new HashMap<>()
    Map<File, Schema> toCompile = new HashMap<>()

    stringType = GenericData.StringType.valueOf(stringType)

    while (!toProcess.isEmpty()) {
      File file = toProcess.pop()

      logger.info("Loading Avro schema file: {}. Known types: {}", file, types.keySet())

      def parser = new Schema.Parser()
      parser.addTypes(types)

      Schema schema

      try {
        schema = parser.parse(file)

        types.put(schema.name, schema)
        toCompile.put(file, schema)
      } catch (SchemaParseException e) {
        if (e.getMessage().matches("unknown type")) {
          logger.debug("Unknown type {} found - requeuing for parsing later.", e.getMessage())
          toProcess.push(file)
        } else {
          throw e
        }
      }
    }

    toCompile.each { Map.Entry<File, Schema> entry ->
      logger.info("Compiling Avro file {}", entry.getKey())

      SpecificCompiler compiler = new SpecificCompiler(entry.getValue())

      compiler.setCreateSetters(generateSetters)
      compiler.setStringType(GenericData.StringType.valueOf(stringType))

      if (fieldVisibility != null) {
        compiler.setFieldVisibility(SpecificCompiler.FieldVisibility.valueOf(fieldVisibility))
      }

      if (templateDirectory != null) {
        compiler.setTemplateDir(templateDirectory)
      }

      compiler.compileToDestination(entry.getKey(), generatedSourceDirectory)
    }
  }

}
