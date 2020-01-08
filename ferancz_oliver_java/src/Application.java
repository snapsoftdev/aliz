import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Application {
    /**
     * arg0 is the root directory
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Running cleanup from: " + args[0]);
        ArrayList<File> files = new ArrayList<>(Arrays.asList(Objects.requireNonNull(new File(args[0]).listFiles())));
        cleanUp(files);
    }
    /**
     *  Empty folders that are not emptied by the cleanup process are kept on purpose
     */
    private static boolean cleanUp(ArrayList<File> files) {
        ArrayList<File> filesDeleted = new ArrayList<>();
        files.parallelStream().forEach(file -> {
            if (file.isDirectory()) {
                if (cleanUp(new ArrayList<>(Arrays.asList(Objects.requireNonNull(file.listFiles()))))) {
                    addToArrayAndDelete(filesDeleted, file);
                }
            } else if (file.getName().endsWith(".bak")) {
                if (files.size() == 1) {
                    addToArrayAndDelete(filesDeleted, file);
                } else {
                    int matchesFound = files.parallelStream()
                            .filter(f -> !f.isDirectory())
                            .filter(f -> !f.equals(file))
                            .filter(file1 -> getFileNameByStringHandling(file.getName()).equals(getFileNameByStringHandling(file1.getName()))).toArray().length;
                    if (matchesFound == 0) {
                        addToArrayAndDelete(filesDeleted, file);
                    }
                }
            }
        });
        return filesDeleted.size() > 0;
    }

    /**
     * filesDeleted array is used to determine if the file system was changed by the cleanup method
     * @param filesDeleted
     * @param file
     */
    private static void addToArrayAndDelete(ArrayList<File> filesDeleted, File file) {
        if (file.delete()) {
            filesDeleted.add(file);
        }
    }

    /**
     * It wass assumed that no .doc.bak formats will be used, only simple filename.extension format
     * @param filename
     * @return
     */
    private static String getFileNameByStringHandling(String filename) {
        if (filename.length() > 0 && filename.contains(".")) {
            return filename.substring(0, filename.lastIndexOf("."));
        }
        return null;
    }
}
