package ca.cmpt276.getdata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Data model for retrofit
 */

public class Resource {

    @SerializedName("cache_last_updated")
    @Expose
    private Object cacheLastUpdated;
    @SerializedName("package_id")
    @Expose
    private String packageId;
    @SerializedName("webstore_last_updated")
    @Expose
    private Object webstoreLastUpdated;
    @SerializedName("datastore_active")
    @Expose
    private Boolean datastoreActive;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("size")
    @Expose
    private Object size;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("hash")
    @Expose
    private String hash;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("format")
    @Expose
    private String format;
    @SerializedName("mimetype_inner")
    @Expose
    private Object mimetypeInner;
    @SerializedName("url_type")
    @Expose
    private String urlType;
    @SerializedName("mimetype")
    @Expose
    private Object mimetype;
    @SerializedName("cache_url")
    @Expose
    private Object cacheUrl;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("webstore_url")
    @Expose
    private Object webstoreUrl;
    @SerializedName("last_modified")
    @Expose
    private Object lastModified;
    @SerializedName("position")
    @Expose
    private Integer position;
    @SerializedName("revision_id")
    @Expose
    private String revisionId;
    @SerializedName("resource_type")
    @Expose
    private Object resourceType;

    public Object getCacheLastUpdated() {
        return cacheLastUpdated;
    }

    public void setCacheLastUpdated(Object cacheLastUpdated) {
        this.cacheLastUpdated = cacheLastUpdated;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public Object getWebstoreLastUpdated() {
        return webstoreLastUpdated;
    }

    public void setWebstoreLastUpdated(Object webstoreLastUpdated) {
        this.webstoreLastUpdated = webstoreLastUpdated;
    }

    public Boolean getDatastoreActive() {
        return datastoreActive;
    }

    public void setDatastoreActive(Boolean datastoreActive) {
        this.datastoreActive = datastoreActive;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getSize() {
        return size;
    }

    public void setSize(Object size) {
        this.size = size;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Object getMimetypeInner() {
        return mimetypeInner;
    }

    public void setMimetypeInner(Object mimetypeInner) {
        this.mimetypeInner = mimetypeInner;
    }

    public String getUrlType() {
        return urlType;
    }

    public void setUrlType(String urlType) {
        this.urlType = urlType;
    }

    public Object getMimetype() {
        return mimetype;
    }

    public void setMimetype(Object mimetype) {
        this.mimetype = mimetype;
    }

    public Object getCacheUrl() {
        return cacheUrl;
    }

    public void setCacheUrl(Object cacheUrl) {
        this.cacheUrl = cacheUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getWebstoreUrl() {
        return webstoreUrl;
    }

    public void setWebstoreUrl(Object webstoreUrl) {
        this.webstoreUrl = webstoreUrl;
    }

    public Object getLastModified() {
        return lastModified;
    }

    public void setLastModified(Object lastModified) {
        this.lastModified = lastModified;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(String revisionId) {
        this.revisionId = revisionId;
    }

    public Object getResourceType() {
        return resourceType;
    }

    public void setResourceType(Object resourceType) {
        this.resourceType = resourceType;
    }
}