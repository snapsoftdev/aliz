package ai.aliztech.test.folder.structure;

import ai.aliztech.test.folder.structure.model.TreeItem;

import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

public class Algorithm {

    private boolean isRootPath(String path) {
        return "/".equals(path);
    }

    private void addToTree(TreeItem root, Status status, String path) {
        if (isRootPath(path)) {
            root.setStatus(status);
            return;
        }

        TreeItem temp = root;
        boolean leaf = false;
        while (!leaf) {
            if (path.startsWith(temp.getAbsolutePath())) {
                String subPath = path.replaceFirst(temp.getAbsolutePath(), "");
                if (subPath.charAt(0) == '/') {
                    subPath = subPath.substring(1);
                }
                leaf = !subPath.contains("/");

                String nextFolderPart = leaf ? subPath : getNextFolderName(subPath);
                String absPathAtLevel = "/".equals(temp.getAbsolutePath()) ? "/" + nextFolderPart : temp.getAbsolutePath() + "/" + nextFolderPart;

                TreeItem child = getChildByAbsolutePath(temp, absPathAtLevel);
                if (child == null) {
                    TreeItem ti = new TreeItem(absPathAtLevel);
                    temp.getChildren().add(ti);
                    temp = ti;
                } else {
                    temp = child;
                }
                if (leaf) {
                    temp.setStatus(status);
                }
            } else {
                // create new child
                throw new IllegalStateException("Unknown state");
            }
        }
    }

    private TreeItem getChildByAbsolutePath(TreeItem parent, String absPath) {
        Optional<TreeItem> first = parent.getChildren().stream().filter(ti -> ti.getAbsolutePath().equals(absPath)).findFirst();
        if (first.isPresent()) {
            return first.get();
        } else {
            return null;
        }
    }

    private String getNextFolderName(String path) {
        return path.substring(0, path.indexOf("/"));
    }

    private TreeItem buildTree(List<String> readableFolderList, List<String> writableFolderList) {
        TreeItem root = new TreeItem("/");

        for (String writableFolder : writableFolderList) {
            addToTree(root, Status.WRITABLE, writableFolder);
        }

        for (String readableFolder : readableFolderList) {
            addToTree(root, Status.READABLE, readableFolder);
        }
        return root;
    }


    public TreeItem findWritableFolders(List<String> readableFolderList, List<String> writableFolderList) {
        readableFolderList.addAll(writableFolderList);
        TreeItem root = buildTree(readableFolderList, writableFolderList);
        Stack<TreeItem> foldersStack = new Stack<TreeItem>();
        foldersStack.push(root);
        while (!foldersStack.empty()) {
            TreeItem folder = foldersStack.peek();
            if (folder.getChildren().isEmpty()) {
                foldersStack.pop();
                folder.setVisited();
                if (folder.getStatus() != Status.WRITABLE) {
                    folder.invalidate();
                }
            } else if (!folder.isVisited()) {
                if (folder.getStatus() == Status.WRITABLE || folder.getStatus() == Status.READABLE) {
                    List<TreeItem> children = folder.getChildren();
                    for (TreeItem child : children) {
                        foldersStack.push(child);
                    }
                } else {
                    folder.invalidate();
                    foldersStack.pop();
                }
                folder.setVisited();
            } else {
                foldersStack.pop();
                List<TreeItem> children = folder.getChildren();
                List<TreeItem> invalidatedChildren = children.stream().filter(n -> n.isInvalidated()).collect(Collectors.toList());
                children.removeAll(invalidatedChildren);
                if (folder.getStatus() != Status.WRITABLE && children.isEmpty()) {
                    folder.invalidate();
                }
            }
        }
        if (root.isInvalidated()) {
            return null;
        } else {
            return root;
        }
    }


}
