package fr.soleil.lib.ci.jenkinsjobgenerator.service;

import fr.soleil.lib.ci.jenkinsjobgenerator.domain.mustache.ScmType;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.repository.ProjectRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.BasicMongoDBDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * TODO manage GIT SCM
 *
 * @author ABEILLE
 */
public class JenkinsJobGeneratorService {

    private static String MONGODB_HOSTNAME = System.getProperty("fr.soleil.ci.mongodb.hostname");
    private static String MONGODB_PORT = System.getProperty("fr.soleil.ci.mongodb.port");
    private static String JENKINS_URL = System.getProperty("fr.soleil.ci.jenkins.url");
    private static String JENKINS_USER = System.getProperty("fr.soleil.ci.jenkins.user");
    private static String JENKINS_PWD = System.getProperty("fr.soleil.ci.jenkins.pwd");

    private Logger logger = LoggerFactory.getLogger(JenkinsJobGeneratorService.class);

    private JenkinsCvsConfigGeneratorService configGeneratorCVS;
    private JenkinsSvnConfigGeneratorService configGeneratorSVN;
    private JenkinsGitConfigGeneratorService configGeneratorGIT;

    public JenkinsJobGeneratorService() throws IOException {
        configGeneratorCVS = new JenkinsCvsConfigGeneratorService();
        configGeneratorSVN = new JenkinsSvnConfigGeneratorService();
        configGeneratorGIT = new JenkinsGitConfigGeneratorService();
    }

    /**
     * Load all projects from mongodb and create their jenkins jobs
     */
    public void createAllJobs() throws IOException {

        int mongoPort = Integer.parseInt(MONGODB_PORT);
        ProjectRepository projectRepository = new ProjectRepository(new BasicMongoDBDataSource(MONGODB_HOSTNAME, mongoPort));
        final List<ProjectDocument> allProjectDocument = projectRepository.getAllProjectDocument();
        for (ProjectDocument projectDocument : allProjectDocument) {
            processJob(projectDocument);
        }

    }

    private void processJob(ProjectDocument projectDocument) {
        String jenkinsJobName = JobUtilities.getJobName(projectDocument);
        logger.debug("processing job {}", jenkinsJobName);
        int rep = 0;
        try {
            boolean createJob = !isJenkinsJobExists(jenkinsJobName);
            logger.debug("is job exists {}", createJob);
            rep = createJob(ScmType.getScmType(projectDocument), projectDocument, createJob);
        } catch (IOException e) {
            logger.error("error for job {}", jenkinsJobName);
            logger.error("", e);
        } catch (JAXBException e) {
            logger.error("error for job {}", jenkinsJobName);
            logger.error("", e);
        }
        logger.debug("{} result is {}", jenkinsJobName, rep);
    }


    private int createJob(ScmType scmType, ProjectDocument projectDocument, boolean createJob)
            throws IOException, JAXBException {
        int responseCode = 00;
        URL url;
        String jobName = JobUtilities.getJobName(projectDocument);
        if (createJob) {
            url = new URL(JENKINS_URL + "/createItem?name=" + jobName);
        } else {
            url = new URL(JENKINS_URL + "/job/" + jobName + "/config.xml");
        }
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {

            JobUtilities.addAuth(conn, JENKINS_USER, JENKINS_USER);
            conn.setRequestMethod("POST");
            OutputStream os = conn.getOutputStream();
            Writer writer = new OutputStreamWriter(os);
            switch (scmType) {
                case CVS:
                    configGeneratorCVS.load(writer, projectDocument);
                    break;
                case SVN:
                    configGeneratorSVN.load(writer, projectDocument);
                    break;
                case GIT:
                    configGeneratorGIT.load(writer, projectDocument);
                    break;
                default:
                    break;
            }
            os.flush();
            responseCode = conn.getResponseCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            logger.debug("server output is:");
            while ((output = br.readLine()) != null) {
                logger.debug(output);
            }
        } finally {
            conn.disconnect();
        }
        return responseCode;
    }

    private boolean isJenkinsJobExists(String jobName) throws IOException,
            JAXBException {
        URL jobURL = new URL(JENKINS_URL + "/job/" + jobName + "/config.xml");
        HttpURLConnection conn = (HttpURLConnection) jobURL.openConnection();
        boolean isJenkinsJobExists = false;
        try {
            JobUtilities.addAuth(conn, JENKINS_USER, JENKINS_PWD);
            conn.setRequestMethod("GET");
            conn.getInputStream();
            isJenkinsJobExists = true;
        } catch (FileNotFoundException e) {
            // job does not exist
            isJenkinsJobExists = false;
        } finally {
            conn.disconnect();
        }
        return isJenkinsJobExists;
    }

}
