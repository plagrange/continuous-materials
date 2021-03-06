package fr.soleil.lib.ci.jenkinsjobgenerator.service;

import fr.soleil.lib.ci.jenkinsjobgenerator.domain.mustache.JenkinsSvnConfig;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Writer;

/**
 * Created by ABEILLE on 09/04/2014.
 */
public class JenkinsGitConfigGeneratorService extends JenkinsConfigGeneratorService {

    private Logger logger = LoggerFactory.getLogger(JenkinsGitConfigGeneratorService.class);

    public JenkinsGitConfigGeneratorService() {
        super("mustache-template-config-java-git.xml");
    }

    @Override
    public void load(Writer writer, ProjectDocument projectDocument) {
        logger.debug("loading project {}", projectDocument);
        String gitURL = projectDocument.getScmConnection();
        JenkinsSvnConfig config = new JenkinsSvnConfig(projectDocument.getDescription(), JobUtilities.getEmails(projectDocument), gitURL);
        logger.debug("jenkins config is {}", config);
        // create jenkins job
        compile(writer, config);
    }
}
