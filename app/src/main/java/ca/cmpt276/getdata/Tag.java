package ca.cmpt276.getdata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Data model for retrofit
 */

public class Tag {

    @SerializedName("vocabulary_id")
    @Expose
    private Object vocabularyId;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("display_name")
    @Expose
    private String displayName;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;

    public Object getVocabularyId() {
        return vocabularyId;
    }

    public void setVocabularyId(Object vocabularyId) {
        this.vocabularyId = vocabularyId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
