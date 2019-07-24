package ai.aliztech.test.folder.structure;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private static List<String> getReadableFolderList(List<String> writableFolderList) {
        List<String> readableFolderList = new ArrayList<String>();
        readableFolderList.addAll(writableFolderList);
        readableFolderList.add("/var");
        readableFolderList.add("/var/log");
        readableFolderList.add("/etc/init.d");
        readableFolderList.add("/");
        return readableFolderList;
    }

    private static List<String> getWritableFolderList() {
        List<String> writableFolderList = new ArrayList<String>();
        writableFolderList.add("/var/lib");
        writableFolderList.add("/etc/init.d/temp/x");
        return writableFolderList;
    }


    public static void main(String[] args) {
        Algorithm a = new Algorithm();
        List<String> writableFolderList = getWritableFolderList();
        a.findWritableFolders(getReadableFolderList(writableFolderList), writableFolderList);
    }

}
