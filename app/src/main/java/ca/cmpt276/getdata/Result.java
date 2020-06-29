package ca.cmpt276.getdata;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Data model for retrofit
 */

public class Result {

    @SerializedName("more_information")
    @Expose
    private String moreInformation;
    @SerializedName("license_title")
    @Expose
    private String licenseTitle;
    @SerializedName("maintainer")
    @Expose
    private String maintainer;
    @SerializedName("relationships_as_object")
    @Expose
    private List<Object> relationshipsAsObject = null;
    @SerializedName("private")
    @Expose
    private Boolean _private;
    @SerializedName("maintainer_email")
    @Expose
    private String maintainerEmail;
    @SerializedName("num_tags")
    @Expose
    private Integer numTags;
    @SerializedName("update_frequency")
    @Expose
    private String updateFrequency;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("metadata_created")
    @Expose
    private String metadataCreated;
    @SerializedName("metadata_modified")
    @Expose
    private String metadataModified;
    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("author_email")
    @Expose
    private String authorEmail;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("creator_user_id")
    @Expose
    private String creatorUserId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("resources")
    @Expose
    private List<Resource> resources = null;
    @SerializedName("num_resources")
    @Expose
    private Integer numResources;
    @SerializedName("tags")
    @Expose
    private List<Tag> tags = null;
    @SerializedName("attribute_details")
    @Expose
    private String attributeDetails;
    @SerializedName("groups")
    @Expose
    private List<Group> groups = null;
    @SerializedName("license_id")
    @Expose
    private String licenseId;
    @SerializedName("relationships_as_subject")
    @Expose
    private List<Object> relationshipsAsSubject = null;
    @SerializedName("organization")
    @Expose
    private Organization organization;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("isopen")
    @Expose
    private Boolean isopen;
    @SerializedName("coordinate_system")
    @Expose
    private String coordinateSystem;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("notes")
    @Expose
    private String notes;
    @SerializedName("owner_org")
    @Expose
    private String ownerOrg;
    @SerializedName("is_geospatial")
    @Expose
    private String isGeospatial;
    @SerializedName("license_url")
    @Expose
    private String licenseUrl;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("revision_id")
    @Expose
    private String revisionId;
    @Expose
    private List<Extra> extras = null;


    public String getMoreInformation() {
        return moreInformation;
    }

    public void setMoreInformation(String moreInformation) {
        this.moreInformation = moreInformation;
    }

    public String getLicenseTitle() {
        return licenseTitle;
    }

    public void setLicenseTitle(String licenseTitle) {
        this.licenseTitle = licenseTitle;
    }

    public String getMaintainer() {
        return maintainer;
    }

    public void setMaintainer(String maintainer) {
        this.maintainer = maintainer;
    }

    public List<Object> getRelationshipsAsObject() {
        return relationshipsAsObject;
    }

    public void setRelationshipsAsObject(List<Object> relationshipsAsObject) {
        this.relationshipsAsObject = relationshipsAsObject;
    }

    public Boolean getPrivate() {
        return _private;
    }

    public void setPrivate(Boolean _private) {
        this._private = _private;
    }

    public String getMaintainerEmail() {
        return maintainerEmail;
    }

    public void setMaintainerEmail(String maintainerEmail) {
        this.maintainerEmail = maintainerEmail;
    }

    public Integer getNumTags() {
        return numTags;
    }

    public void setNumTags(Integer numTags) {
        this.numTags = numTags;
    }

    public String getUpdateFrequency() {
        return updateFrequency;
    }

    public void setUpdateFrequency(String updateFrequency) {
        this.updateFrequency = updateFrequency;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMetadataCreated() {
        return metadataCreated;
    }

    public void setMetadataCreated(String metadataCreated) {
        this.metadataCreated = metadataCreated;
    }

    public String getMetadataModified() {
        return metadataModified;
    }

    public void setMetadataModified(String metadataModified) {
        this.metadataModified = metadataModified;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCreatorUserId() {
        return creatorUserId;
    }

    public void setCreatorUserId(String creatorUserId) {
        this.creatorUserId = creatorUserId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public Integer getNumResources() {
        return numResources;
    }

    public void setNumResources(Integer numResources) {
        this.numResources = numResources;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getAttributeDetails() {
        return attributeDetails;
    }

    public void setAttributeDetails(String attributeDetails) {
        this.attributeDetails = attributeDetails;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public String getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }

    public List<Object> getRelationshipsAsSubject() {
        return relationshipsAsSubject;
    }

    public void setRelationshipsAsSubject(List<Object> relationshipsAsSubject) {
        this.relationshipsAsSubject = relationshipsAsSubject;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsopen() {
        return isopen;
    }

    public void setIsopen(Boolean isopen) {
        this.isopen = isopen;
    }

    public String getCoordinateSystem() {
        return coordinateSystem;
    }

    public void setCoordinateSystem(String coordinateSystem) {
        this.coordinateSystem = coordinateSystem;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getOwnerOrg() {
        return ownerOrg;
    }

    public void setOwnerOrg(String ownerOrg) {
        this.ownerOrg = ownerOrg;
    }

    public String getIsGeospatial() {
        return isGeospatial;
    }

    public void setIsGeospatial(String isGeospatial) {
        this.isGeospatial = isGeospatial;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(String revisionId) {
        this.revisionId = revisionId;
    }

    public List<Extra> getExtras() {
        return extras;
    }

    public void setExtras(List<Extra> extras) {
        this.extras = extras;
    }
}
