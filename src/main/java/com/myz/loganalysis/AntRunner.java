package com.myz.loganalysis;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

public class AntRunner {

    public static HashMap<String, Project> antProjectMap = new HashMap<String, Project>();

    public static void runAntScript(String buildfilePath, String target, Properties props) {
        Project project = antProjectMap.get(buildfilePath);
        if (project == null) {
            synchronized (antProjectMap) {
                project = antProjectMap.get(buildfilePath);
                if (project == null) {
                    project = new Project();

                    File buildFile = new File(buildfilePath);

                    ClassLoader cl = AntRunner.class.getClassLoader();

                    project.setCoreLoader(cl);
                    BuildLogger buildLog = new DefaultLogger();
                    buildLog.setMessageOutputLevel(Project.MSG_INFO);
                    buildLog.setOutputPrintStream(System.out);
                    buildLog.setErrorPrintStream(System.err);
                    buildLog.setEmacsMode(false);
                    project.addBuildListener(buildLog);

                    project.init();
                    project.setUserProperty("ant.file", buildfilePath);
                    // set user-define properties
                    Enumeration e = props.keys();
                    while (e.hasMoreElements()) {
                        String arg = (String) e.nextElement();
                        String value = props.getProperty(arg);
                        project.setUserProperty(arg, value);
                    }
                    ProjectHelper.configureProject(project, buildFile);
                }
            }
        }

        project.executeTarget(target);
    }
}
