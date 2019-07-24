import ai.aliztech.test.folder.structure.Algorithm;
import ai.aliztech.test.folder.structure.Status;
import ai.aliztech.test.folder.structure.model.TreeItem;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;

public class Tests {

    @Test
    public void basicTree() {
        List<String> readable = new ArrayList<>();
        readable.add("/a1/a2");
        readable.add("/a1");
        readable.add("/");
        readable.add("/b1");
        readable.add("/b1/b2");
        TreeItem writableDirectoryTree = new Algorithm().findWritableFolders(readable, readable);
        Assertions.assertEquals(2, writableDirectoryTree.getChildren().size());
        Assertions.assertEquals(1, writableDirectoryTree.getChildren().get(0).getChildren().size());
        Assertions.assertEquals(1, writableDirectoryTree.getChildren().get(1).getChildren().size());
    }

    @Test
    public void disjointTree() {
        List<String> readable = new ArrayList<>();
        readable.add("/a1/a2");
        readable.add("/a1");
        readable.add("/");
        readable.add("/b1");
        readable.add("/b1/b2");
        readable.add("/c1/c2");
        TreeItem writableDirectoryTree = new Algorithm().findWritableFolders(readable, readable);
        Assertions.assertEquals(2, writableDirectoryTree.getChildren().size());
        Assertions.assertEquals(1, writableDirectoryTree.getChildren().get(0).getChildren().size());
        Assertions.assertEquals(1, writableDirectoryTree.getChildren().get(1).getChildren().size());
    }

    @Test
    public void complexTree() {
        List<String> readable = new ArrayList<>();
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
        TreeItem writableDirectoryTree = new Algorithm().findWritableFolders(readable, readable);
        Assertions.assertEquals(3, writableDirectoryTree.getChildren().size());
        TreeItem a1 = writableDirectoryTree.getChildren().stream().filter(it -> it.getAbsolutePath().equals("/a1")).findFirst().get();
        Assertions.assertEquals(3, a1.getChildren().size());
        TreeItem c2 = a1.getChildren().stream().filter(it -> it.getAbsolutePath().equals("/a1/c2")).findFirst().get();
        Assertions.assertEquals(2, c2.getChildren().size());
    }

    @Test
    public void writableLeaf() {
        List<String> readable = new ArrayList<>();
        readable.add("/a1/a2");
        readable.add("/a1");
        readable.add("/");
        readable.add("/a1/a2/a3/a4");
        readable.add("/a1/a2/a3");
        List<String> writable = new ArrayList<>();
        writable.add("/a1/a2/a3/a4");
        TreeItem writableDirectoryTree = new Algorithm().findWritableFolders(readable, writable);
        Assertions.assertFalse(Status.WRITABLE.equals(writableDirectoryTree.getStatus())); //root
        Assertions.assertFalse(Status.WRITABLE.equals(writableDirectoryTree.getChildren().get(0).getStatus())); //a1
        Assertions.assertFalse(Status.WRITABLE.equals(writableDirectoryTree.getChildren().get(0).getChildren().get(0).getStatus())); //a2
        Assertions.assertFalse(Status.WRITABLE.equals(writableDirectoryTree.getChildren().get(0).getChildren().get(0).getChildren().get(0).getStatus())); //a3
        Assertions.assertTrue(Status.WRITABLE.equals(writableDirectoryTree.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getStatus())); //a4
    }

    @Test
    public void treeShake() {
        List<String> readable = new ArrayList<>();
        readable.add("/");
        readable.add("/a1");
        readable.add("/a1/a2");
        readable.add("/a1/b2");
        readable.add("/a1/a2/a3");
        List<String> writable = new ArrayList<>();
        writable.add("/a1");
        TreeItem writableDirectoryTree = new Algorithm().findWritableFolders(readable, writable);
        TreeItem a0 = writableDirectoryTree.getChildren().get(0);
        Assertions.assertTrue(Status.WRITABLE.equals(a0.getStatus()));
        Assertions.assertEquals(0, a0.getChildren().size()); //a1
    }

    @Test
    public void readOnly() {
        List<String> readable = new ArrayList<>();
        readable.add("/a1");
        readable.add("/a1/a2");
        readable.add("/a1/b2");
        readable.add("/a1/a2/a3");
        List<String> writable = new ArrayList<>();
        TreeItem writableDirectoryTree = new Algorithm().findWritableFolders(readable, writable);
        Assertions.assertNull(writableDirectoryTree);
    }

    @Test
    public void treeTest(){
        List<String> readable = new ArrayList<>();

        readable.add("/var");
        readable.add("/var/log");
        readable.add("/etc/init.d");
        readable.add("/");

        List<String> writable = new ArrayList<>();
        writable.add("/var/lib");
        writable.add("/etc/init.d/temp/x");

        TreeItem writableDirectoryTree = new Algorithm().findWritableFolders(readable, writable);
        Assertions.assertNotNull(writableDirectoryTree);
        Assertions.assertEquals(1, writableDirectoryTree.getChildren().size());
        Assertions.assertEquals("/var", writableDirectoryTree.getChildren().get(0).getAbsolutePath());
        Assertions.assertEquals(1, writableDirectoryTree.getChildren().get(0).getChildren().size());
        Assertions.assertEquals("/var/lib", writableDirectoryTree.getChildren().get(0).getChildren().get(0).getAbsolutePath());
        Assertions.assertEquals(0, writableDirectoryTree.getChildren().get(0).getChildren().get(0).getChildren().size());

    }
}
