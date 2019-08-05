package folders;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WritableTreeFinder {

	private static final char FOLDER_SEPARATOR = '/';
	private static final String EMPTY_BASEPATH = "";

	public List<TreeItem> findWritableTree(List<TreeItem> rootFolders, List<String> readablePaths,
			List<String> writablePaths) {

		Set<String> readablePathSets = new HashSet<String>(readablePaths);
		Set<String> writablePathSets = new HashSet<String>(writablePaths);

		return findWritableTree(EMPTY_BASEPATH, rootFolders, readablePathSets, writablePathSets);

	}

	private List<TreeItem> findWritableTree(String basePath, List<TreeItem> baseFolders, Set<String> readablePaths,
			Set<String> writablePaths) {

		if (baseFolders == null) {
			return Collections.emptyList();
		}

		return baseFolders.stream()
				.map(folder -> findWritableTree(basePath, folder, readablePaths, writablePaths))
				.filter(item -> item != null).collect(Collectors.toList());
	}

	private TreeItem findWritableTree(String basePath, TreeItem currentFolder, Set<String> readablePaths,
			Set<String> writablePaths) {

		String fullPathForCurrentItem = basePath + FOLDER_SEPARATOR + currentFolder.getName();
		if (!readablePaths.contains(fullPathForCurrentItem)) {
			return null;
		}

		List<TreeItem> writableTree = findWritableTree(fullPathForCurrentItem, currentFolder.getChildren(),
				readablePaths,
				writablePaths);

		if (writableTree.isEmpty()) {
			if (writablePaths.contains(fullPathForCurrentItem)) {
				return new TreeItem(currentFolder.getName(), Collections.emptyList());
			} else {
				return null;
			}
		} else {
			return new TreeItem(currentFolder.getName(), writableTree);
		}

	}

}
