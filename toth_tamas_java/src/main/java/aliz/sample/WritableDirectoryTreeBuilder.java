package aliz.sample;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WritableDirectoryTreeBuilder {

    public TreeItem getWritableDirectoryTree(Set<String> readableFolders, Set<String> writableFolders) {
        Set<String> sanitizedWritableFolders = writableFolders.stream().map(this::sanitizePath).collect(Collectors.toSet());
        TreeItem root = new TreeItem("/", writableFolders.contains("/"));
        readableFolders.stream()
                .map(this::sanitizePath)
                .map(path -> new FolderElement(path, sanitizedWritableFolders.contains(path)))
                .sorted(Comparator.comparingInt(fe -> fe.depth))
                .forEach(fe -> addToTree(root, fe));
        removeReadOnlyBranches(root);
        return root;
    }

    private String sanitizePath(String path) {
        assert path != null && !path.isEmpty(); //null or empty paths shouldn't be in input
        path = path.trim();
        if (path.startsWith("/")) {
            // we don't need starting /
            path = path.substring(1);
        } //relative paths treated as absolute
        return path;
    }

    private void addToTree(TreeItem item, FolderElement element) {
        String[] pathParts = element.pathParts;
        if (element.path.isEmpty()) return; //root dir is already added by default
        for (int i = 0; i < pathParts.length; i++) {
            String pathPart = pathParts[i];
            boolean lastPart = i == pathParts.length - 1;
            if (lastPart) {
                TreeItem newItem = new TreeItem(pathPart, element.writable);
                item.getChildren().add(newItem);
            } else {
                item = item.getChildren().stream().filter(ti -> ti.getName().equals(pathPart)).findFirst().orElse(null);
                if (item == null) {
                    // directory is not reachable view readable dirs
                    return;
                }
            }
        }
    }

    private boolean removeReadOnlyBranches(TreeItem item) {
        boolean hasWritableSubtree = false;
        for (Iterator<TreeItem> iterator = item.getChildren().iterator(); iterator.hasNext(); ) {
            TreeItem ti = iterator.next();
            boolean writable = removeReadOnlyBranches(ti);
            if (!writable) {
                iterator.remove();
            }
            hasWritableSubtree |= writable;
        }
        return hasWritableSubtree || item.isWritable();
    }

    private static class FolderElement {
        String[] pathParts;
        int depth;
        boolean writable;
        String path;

        public FolderElement(String path, boolean writable) {
            this.path = path;
            this.writable = writable;
            this.splitPath(path);
        }

        private void splitPath(String path) {
            this.pathParts = path.split("/");
            this.depth = pathParts.length;
        }
    }
}
