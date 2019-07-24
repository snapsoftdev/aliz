package ai.aliztech.test.folder.structure.model;

import ai.aliztech.test.folder.structure.Status;

import java.util.ArrayList;
import java.util.List;

public class TreeItem {

    private String absolutePath;
    private List<TreeItem> children = new ArrayList<TreeItem>();
    private Status status;
    private boolean invalidated;
    private boolean visited;

    public TreeItem(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public void setStatus(Status status) {
        if (this.status == null || this.status != Status.WRITABLE) {
            this.status = status;
        }
    }

    public Status getStatus() {
        return status;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public List<TreeItem> getChildren() {
        return children;
    }

    public boolean isInvalidated() {
        return invalidated;
    }

    public void setVisited() {
        visited = true;
    }

    public boolean isVisited() {
        return visited;
    }

    public void invalidate() {
        invalidated = true;
    }

}
