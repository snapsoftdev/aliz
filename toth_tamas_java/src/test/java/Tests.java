import aliz.sample.TreeItem;
import aliz.sample.WritableDirectoryTreeBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class Tests {

    private WritableDirectoryTreeBuilder treeBuilder = new WritableDirectoryTreeBuilder();

    @Test
    public void basicTree() {
        Set<String> readable = new HashSet<>();
        readable.add("/a1/a2");
        readable.add("/a1");
        readable.add("/");
        readable.add("/b1");
        readable.add("/b1/b2");
        TreeItem writableDirectoryTree = treeBuilder.getWritableDirectoryTree(readable, readable);
        Assertions.assertEquals(2, writableDirectoryTree.getChildren().size());
        Assertions.assertEquals(1, writableDirectoryTree.getChildren().get(0).getChildren().size());
        Assertions.assertEquals(1, writableDirectoryTree.getChildren().get(1).getChildren().size());
    }

    @Test
    public void disjointTree() {
        Set<String> readable = new HashSet<>();
        readable.add("/a1/a2");
        readable.add("/a1");
        readable.add("/");
        readable.add("/b1");
        readable.add("/b1/b2");
        readable.add("/c1/c2");
        TreeItem writableDirectoryTree = treeBuilder.getWritableDirectoryTree(readable, readable);
        Assertions.assertEquals(2, writableDirectoryTree.getChildren().size());
        Assertions.assertEquals(1, writableDirectoryTree.getChildren().get(0).getChildren().size());
        Assertions.assertEquals(1, writableDirectoryTree.getChildren().get(1).getChildren().size());
    }

    @Test
    public void complexTree() {
        Set<String> readable = new HashSet<>();
        readable.add("/");
        readable.add("/a1");
        readable.add("/a1/a2");
        readable.add("/a1/b2");
        readable.add("/a1/c2");
        readable.add("/a1/c2/a3");
        readable.add("/a1/c2/b3");
        readable.add("/b1");
        readable.add("/b1/b2");
        readable.add("/c1");
        TreeItem writableDirectoryTree = treeBuilder.getWritableDirectoryTree(readable, readable);
        Assertions.assertEquals(3, writableDirectoryTree.getChildren().size());
        TreeItem a1 = writableDirectoryTree.getChildren().stream().filter(it -> it.getName().equals("a1")).findFirst().get();
        Assertions.assertEquals(3, a1.getChildren().size());
        TreeItem c2 = a1.getChildren().stream().filter(it -> it.getName().equals("c2")).findFirst().get();
        Assertions.assertEquals(2, c2.getChildren().size());
    }

    @Test
    public void writableLeaf() {
        Set<String> readable = new HashSet<>();
        readable.add("/a1/a2");
        readable.add("/a1");
        readable.add("/");
        readable.add("/a1/a2/a3/a4");
        readable.add("/a1/a2/a3");
        Set<String> writable = new HashSet<>();
        writable.add("/a1/a2/a3/a4");
        TreeItem writableDirectoryTree = treeBuilder.getWritableDirectoryTree(readable, writable);
        Assertions.assertFalse(writableDirectoryTree.isWritable()); //root
        Assertions.assertFalse(writableDirectoryTree.getChildren().get(0).isWritable()); //a1
        Assertions.assertFalse(writableDirectoryTree.getChildren().get(0).getChildren().get(0).isWritable()); //a2
        Assertions.assertFalse(writableDirectoryTree.getChildren().get(0).getChildren().get(0).getChildren().get(0).isWritable()); //a3
        Assertions.assertTrue(writableDirectoryTree.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).isWritable()); //a4
    }

    @Test
    public void treeShake() {
        Set<String> readable = new HashSet<>();
        readable.add("/");
        readable.add("/a1");
        readable.add("/a1/a2");
        readable.add("/a1/b2");
        readable.add("/a1/a2/a3");
        Set<String> writable = new HashSet<>();
        writable.add("/a1");
        TreeItem writableDirectoryTree = treeBuilder.getWritableDirectoryTree(readable, writable);
        TreeItem a0 = writableDirectoryTree.getChildren().get(0);
        Assertions.assertTrue(a0.isWritable());
        Assertions.assertEquals(0, a0.getChildren().size()); //a1
    }

    @Test
    public void readOnly() {
        Set<String> readable = new HashSet<>();
        readable.add("/a1");
        readable.add("/a1/a2");
        readable.add("/a1/b2");
        readable.add("/a1/a2/a3");
        Set<String> writable = new HashSet<>();
        TreeItem tree = treeBuilder.getWritableDirectoryTree(readable, writable);
        Assertions.assertFalse(tree.isWritable());
        Assertions.assertEquals(0, tree.getChildren().size()); //root
    }
}
