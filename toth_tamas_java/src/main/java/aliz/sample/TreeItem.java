package aliz.sample;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TreeItem {
    private String name;
    private final List<TreeItem> children = new LinkedList<>();
    private boolean writable;

    public TreeItem(String name, boolean writable) {
        this.name = name;
        this.writable = writable;
    }

    public String getName() {
        return name;
    }

    public boolean isWritable() {
        return writable;
    }

    public List<TreeItem> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return name;
    }
}
