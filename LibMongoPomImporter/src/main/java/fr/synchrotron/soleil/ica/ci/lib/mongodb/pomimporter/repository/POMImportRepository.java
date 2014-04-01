package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.repository;

import com.google.gson.Gson;
import com.mongodb.DB;
import com.mongodb.WriteConcern;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.ArtifactDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.ProjectDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBDataSource;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

/**
 * @author Gregory Boissinot
 */
public class POMImportRepository {

    private MongoDBDataSource mongoDBDataSource;

    public POMImportRepository(MongoDBDataSource mongoDBDataSource) {
        this.mongoDBDataSource = mongoDBDataSource;
    }

    /*
     *   Artifact Document
     */

    public boolean isArtifactDocumentAlreadyExists(ArtifactDocument artifactDocument) {
        DB mongoDB = mongoDBDataSource.getMongoDB();
        Jongo jongo = new Jongo(mongoDB);
        MongoCollection artifacts = jongo.getCollection("artifacts");
//        String criteria = "{\"org\":\"" + artifactDocument.getOrganisation()
//                + "\", \"name\":\"" + artifactDocument.getName()
//                + "\", \"status\":\"" + artifactDocument.getStatus()
//                + "\", \"version\":\"" + artifactDocument.getVersion()
//                + "\"}";
        Gson gson = new Gson();
        return artifacts.count(gson.toJson(artifactDocument)) != 0;
    }

    public void updateArtifactDocument(ArtifactDocument artifactDocument) {
        DB mongoDB = mongoDBDataSource.getMongoDB();
        Jongo jongo = new Jongo(mongoDB);
        MongoCollection artifacts = jongo.getCollection("artifacts");
        artifacts.withWriteConcern(WriteConcern.SAFE);
//        String criteria = "{\"org\":\"" + artifactDocument.getOrganisation()
//                + "\", \"name\":\"" + artifactDocument.getName()
//                + "\", \"status\":\"" + artifactDocument.getStatus()
//                + "\", \"version\":\"" + artifactDocument.getVersion()
//                + "\"}";
        Gson gson = new Gson();
        artifacts.update(gson.toJson(artifactDocument)).with(artifactDocument);
    }

    public void insertArtifactDocument(ArtifactDocument artifactDocument) {
        DB mongoDB = mongoDBDataSource.getMongoDB();
        Jongo jongo = new Jongo(mongoDB);
        MongoCollection artifacts = jongo.getCollection("artifacts");
        artifacts.insert(artifactDocument);
    }

    /*
     *   Project Document
     */

    public boolean isProjectDocumentAlreadyExists(ProjectDocument projectDocument) {
        DB mongoDB = mongoDBDataSource.getMongoDB();
        Jongo jongo = new Jongo(mongoDB);
        MongoCollection projects = jongo.getCollection("projects");
        //String criteria = "{\"org\":\"" + projectDocument.getOrg() + "\", \"name\":\"" + projectDocument.getName() + "\"}";
        Gson gson = new Gson();
        return projects.count(gson.toJson(projectDocument)) != 0;
    }

    public void updateProjectDocument(ProjectDocument projectDocument) {
        DB mongoDB = mongoDBDataSource.getMongoDB();
        Jongo jongo = new Jongo(mongoDB);
        MongoCollection projects = jongo.getCollection("projects");
        projects.withWriteConcern(WriteConcern.SAFE);
        //String criteria = "{\"org\":\"" + projectDocument.getOrg() + "\", \"name\":\"" + projectDocument.getName() + "\"}";
        Gson gson = new Gson();
        projects.update(gson.toJson(projectDocument)).with(projectDocument);
    }

    public void insertProjectDocument(ProjectDocument projectDocument) {
        DB mongoDB = mongoDBDataSource.getMongoDB();
        Jongo jongo = new Jongo(mongoDB);
        MongoCollection projects = jongo.getCollection("projects");
        projects.insert(projectDocument);
    }
}
