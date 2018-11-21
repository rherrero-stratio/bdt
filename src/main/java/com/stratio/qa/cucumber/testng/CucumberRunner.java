/*
 * Copyright (C) 2014 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stratio.qa.cucumber.testng;

import cucumber.api.CucumberOptions;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.CucumberException;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.RuntimeOptionsFactory;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CucumberRunner {

    private final cucumber.runtime.Runtime runtime;

    private ClassLoader classLoader;

    private RuntimeOptions runtimeOptions;

    private final Logger logger = LoggerFactory.getLogger(this.getClass()
            .getCanonicalName());

    /**
     * Default constructor for cucumber Runner.
     *
     * @param clazz class
     * @param feature feature to execute
     * @throws IOException exception
     * @throws ClassNotFoundException exception
     * @throws InstantiationException exception
     * @throws IllegalAccessException exception
     * @throws NoSuchMethodException exception
     * @throws InvocationTargetException exception
     */
    @SuppressWarnings("unused")
    public CucumberRunner(Class<?> clazz, String... feature) throws IOException, ClassNotFoundException,
            InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        classLoader = clazz.getClassLoader();
        ResourceLoader resourceLoader = new MultiLoader(classLoader);

        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(clazz,
                new Class[]{CucumberOptions.class});
        runtimeOptions = runtimeOptionsFactory.create();
        String testSuffix = System.getProperty("TESTSUFFIX");
        String targetExecutionsPath = "target/executions/";
        if (testSuffix != null) {
            targetExecutionsPath = targetExecutionsPath + testSuffix + "/";
        }
        boolean aux = new File(targetExecutionsPath).mkdirs();
        CucumberReporter reporterTestNG;

        if ((feature.length == 0)) {
            reporterTestNG = new CucumberReporter(targetExecutionsPath, clazz.getCanonicalName(), "");
        } else {
            List<String> features = new ArrayList<String>();
            String fPath = "src/test/resources/features/" + feature[0] + ".feature";
            features.add(fPath);
            runtimeOptions.getFeaturePaths().addAll(features);
            reporterTestNG = new CucumberReporter(targetExecutionsPath, clazz.getCanonicalName(), feature[0]);
        }

        List<String> uniqueGlue = new ArrayList<String>();
        uniqueGlue.add("classpath:com/stratio/cct/testsAT/specs");
        uniqueGlue.add("classpath:com/stratio/qa/specs");
        uniqueGlue.add("classpath:com/stratio/sparta/testsAT/specs");
        uniqueGlue.add("classpath:com/stratio/gosecsso/testsAT/specs");
        uniqueGlue.add("classpath:com/stratio/dcos/crossdata/testsAT/specs");
        uniqueGlue.add("classpath:com/stratio/cct/configuration/api/specs");
        uniqueGlue.add("classpath:com/stratio/crossdata/testsAT/specs");
        uniqueGlue.add("classpath:com/stratio/streaming/testsAT/specs");
        uniqueGlue.add("classpath:com/stratio/ingestion/testsAT/specs");
        uniqueGlue.add("classpath:com/stratio/datavis/testsAT/specs");
        uniqueGlue.add("classpath:com/stratio/connectors/testsAT/specs");
        uniqueGlue.add("classpath:com/stratio/admin/testsAT/specs");
        uniqueGlue.add("classpath:com/stratio/explorer/testsAT/specs");
        uniqueGlue.add("classpath:com/stratio/manager/testsAT/specs");
        uniqueGlue.add("classpath:com/stratio/viewer/testsAT/specs");
        uniqueGlue.add("classpath:com/stratio/decision/testsAT/specs");
        uniqueGlue.add("classpath:com/stratio/paas/testsAT/specs");
        uniqueGlue.add("classpath:com/stratio/cassandra/lucene/testsAT/specs");
        uniqueGlue.add("classpath:com/stratio/analytic/testsAT/specs");
        uniqueGlue.add("classpath:com/stratio/exhibitor/testsAT/specs");
        uniqueGlue.add("classpath:com/stratio/intelligence/testsAT/specs");
        uniqueGlue.add("classpath:com/stratio/postgresbd/testsAT/specs");
        uniqueGlue.add("classpath:com/stratio/postgresql/testsAT/specs");
        uniqueGlue.add("classpath:com/stratio/zookeeper/testsAT/specs");
        uniqueGlue.add("classpath:com/stratio/universe/testsAT/specs");
        uniqueGlue.add("classpath:com/stratio/paas/dgDatadictionaryAT/specs");
        uniqueGlue.add("classpath:com/stratio/paas/dgtests/specs");
        uniqueGlue.add("classpath:com/stratio/elastic/specs");
        uniqueGlue.add("classpath:com/stratio/kafka/specs");
        uniqueGlue.add("classpath:com/stratio/hdfs/specs");
        uniqueGlue.add("classpath:com/stratio/kibana/specs");
        uniqueGlue.add("classpath:com/stratio/cassandra/specs");
        uniqueGlue.add("classpath:com/stratio/schema_registry/specs");
        uniqueGlue.add("classpath:com/stratio/rest_proxy/specs");
        uniqueGlue.add("classpath:com/stratio/spark/tests/specs");
        uniqueGlue.add("classpath:com/stratio/schema/discovery/specs");
        uniqueGlue.add("classpath:com/stratio/pgbouncer/specs");

        runtimeOptions.getGlue().clear();
        runtimeOptions.getGlue().addAll(uniqueGlue);

        runtimeOptions.addFormatter(reporterTestNG);
        Set<Class<? extends ICucumberFormatter>> implementers = new Reflections("com.stratio.qa.utils")
                .getSubTypesOf(ICucumberFormatter.class);

        for (Class<? extends ICucumberFormatter> implementerClazz : implementers) {
            Constructor<?> ctor = implementerClazz.getConstructor();
            ctor.setAccessible(true);
            runtimeOptions.addFormatter((ICucumberFormatter) ctor.newInstance());
        }

        ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
        runtime = new cucumber.runtime.Runtime(resourceLoader, classFinder, classLoader, runtimeOptions);
    }

    /**
     * Run the testclases(Features).
     *
     * @throws IOException exception
     * @throws NoSuchMethodException exception
     * @throws InvocationTargetException exception
     * @throws IllegalAccessException exception
     */
    public void runCukes() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        runtime.run();

        if (!runtime.getErrors().isEmpty()) {
            Iterator<Throwable> iterator = runtime.getErrors().iterator();
            while (iterator.hasNext()) {
                Throwable value = iterator.next();
                if (value.getMessage().contains("TESTS EXECUTION ABORTED!")) {
                    iterator.remove();
                }
            }
            logger.error ("Got {} exceptions", runtime.getErrors());
            throw new CucumberException(runtime.getErrors().get(0));
        }
    }
}
