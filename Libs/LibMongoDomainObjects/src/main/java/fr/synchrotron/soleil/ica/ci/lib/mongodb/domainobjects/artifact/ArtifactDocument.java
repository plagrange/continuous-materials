package fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact;


import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.traceability.BuildContext;

import java.util.Date;
import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class ArtifactDocument {

    private String org;

    private String name;

    private String version;

    private String status;

    private String type;

    private boolean isThirdParty;

    private Date creationDate;

    private Date publicationDate;

    private String sha1;

    private String md5;

    private String description;

    private String fileExtension;

    private long fileSize;

    private boolean isForce;

    private ArtifactDocumentForJava javaLanguage;

    private ArtifactDocumentForC cLanguage;

    private List<ArtifactDependency> dependencies;

    private BuildContext buildContext;

    @Override
    public String toString() {
        return "ArtifactDocument{" +
                "organisation='" + org + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isThirdParty() {
        return isThirdParty;
    }

    public void setThirdParty(boolean isThirdParty) {
        this.isThirdParty = isThirdParty;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isForce() {
        return isForce;
    }

    public void setForce(boolean isForce) {
        this.isForce = isForce;
    }

    public ArtifactDocumentForJava getJavaLanguage() {
        return javaLanguage;
    }

    public void setJavaLanguage(ArtifactDocumentForJava javaLanguage) {
        this.javaLanguage = javaLanguage;
    }

    public ArtifactDocumentForC getcLanguage() {
        return cLanguage;
    }

    public void setcLanguage(ArtifactDocumentForC cLanguage) {
        this.cLanguage = cLanguage;
    }

    public List<ArtifactDependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<ArtifactDependency> dependencies) {
        this.dependencies = dependencies;
    }

    public BuildContext getBuildContext() {
        return buildContext;
    }

    public void setBuildContext(BuildContext buildContext) {
        this.buildContext = buildContext;
    }
}
