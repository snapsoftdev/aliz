package folders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class WritableTreeFinderTest {
	WritableTreeFinder treeFinder = new WritableTreeFinder();

	@Test
	public void everythingReadableWritableWorks() {
		List<TreeItem> rootFolders = buildSampleTree();

		List<String> writablePaths = new ArrayList<String>();
		writablePaths.add("/A");
		writablePaths.add("/A/B");
		writablePaths.add("/A/C");
		writablePaths.add("/A/C/D");

		List<String> readablePaths = writablePaths;
		List<TreeItem> result = treeFinder.findWritableTree(rootFolders, readablePaths, writablePaths);
		assertTree(buildSampleTree(), result);
	}

	@Test
	public void onlyOneWritableWorks() {
		List<TreeItem> rootFolders = buildSampleTree();

		List<String> readablePaths = new ArrayList<String>();
		readablePaths.add("/A");
		readablePaths.add("/A/B");
		readablePaths.add("/A/C");
		readablePaths.add("/A/C/D");

		List<String> writablePaths = new ArrayList<String>();
		writablePaths.add("/A");
		writablePaths.add("/A/C/D");

		List<TreeItem> result = treeFinder.findWritableTree(rootFolders, readablePaths, writablePaths);
		List<TreeItem> expectedList = buildSampleTree();
		expectedList.get(0).getChildren().remove(0);

		assertTree(expectedList, result);
	}

	@Test
	public void noWritableReturnsEmptyList() {
		List<TreeItem> rootFolders = buildSampleTree();

		List<String> readablePaths = new ArrayList<String>();
		readablePaths.add("/A");
		readablePaths.add("/A/B");
		readablePaths.add("/A/C");
		readablePaths.add("/A/C/D");

		List<String> writablePaths = Collections.emptyList();

		List<TreeItem> result = treeFinder.findWritableTree(rootFolders, readablePaths, writablePaths);

		assertTree(Collections.emptyList(), result);
	}

	@Test
	public void writableChildWithoutReadableParentIsNotReturned() {
		List<TreeItem> rootFolders = buildSampleTree();

		List<String> readablePaths = new ArrayList<String>();
		readablePaths.add("/A");
		readablePaths.add("/A/B");
		readablePaths.add("/A/C/D");

		List<String> writablePaths = new ArrayList<String>();
		writablePaths.add("/A");
		writablePaths.add("/A/B");
		writablePaths.add("/A/C/D");

		List<TreeItem> result = treeFinder.findWritableTree(rootFolders, readablePaths, writablePaths);
		List<TreeItem> expectedList = buildSampleTree();
		expectedList.get(0).getChildren().remove(1);

		assertTree(expectedList, result);
	}

	@Test
	public void readableUnderWritableNotReturned() {
		List<TreeItem> rootFolders = buildSampleTree();

		List<String> readablePaths = new ArrayList<String>();
		readablePaths.add("/A");
		readablePaths.add("/A/B");
		readablePaths.add("/A/C");
		readablePaths.add("/A/C/D");

		List<String> writablePaths = new ArrayList<String>();
		writablePaths.add("/A");
		writablePaths.add("/A/B");
		writablePaths.add("/A/C");

		List<TreeItem> result = treeFinder.findWritableTree(rootFolders, readablePaths, writablePaths);
		List<TreeItem> expectedList = buildSampleTree();
		expectedList.get(0).getChildren().get(1).getChildren().remove(0);

		assertTree(expectedList, result);
	}

	@Test
	public void deepTreeWorks() {

		TreeItem item = new TreeItem("0", new ArrayList<TreeItem>());
		final TreeItem rootItem = item;
		String currentName = "/0";
		List<String> readablePaths = new ArrayList<String>();
		List<String> writablePaths = new ArrayList<String>();
		readablePaths.add(currentName);
		for (int i = 1; i < 300; i++) {
			TreeItem nextLevelItem = new TreeItem(i + "", new ArrayList<TreeItem>());
			item.getChildren().add(nextLevelItem);

			currentName += "/" + i;
			readablePaths.add(currentName);
			if (i == 299) {
				writablePaths.add(currentName);
			}
			item = nextLevelItem;
		}

		List<TreeItem> rootFolders = new ArrayList<TreeItem>();
		rootFolders.add(rootItem);

		List<TreeItem> result = treeFinder.findWritableTree(rootFolders, readablePaths, writablePaths);
		List<TreeItem> expectedList = rootFolders;

		assertTree(expectedList, result);
	}

	@Test
	public void deepTreeWithoutWritableWorks() {

		TreeItem item = new TreeItem("0", new ArrayList<TreeItem>());
		final TreeItem rootItem = item;
		String currentName = "/0";
		List<String> readablePaths = new ArrayList<String>();
		List<String> writablePaths = new ArrayList<String>();
		readablePaths.add(currentName);
		for (int i = 1; i < 300; i++) {
			TreeItem nextLevelItem = new TreeItem(i + "", new ArrayList<TreeItem>());
			item.getChildren().add(nextLevelItem);

			currentName += "/" + i;
			readablePaths.add(currentName);
			item = nextLevelItem;
		}

		List<TreeItem> rootFolders = new ArrayList<TreeItem>();
		rootFolders.add(rootItem);

		List<TreeItem> result = treeFinder.findWritableTree(rootFolders, readablePaths, writablePaths);
		List<TreeItem> expectedList = Collections.emptyList();

		assertTree(expectedList, result);
	}

	@Test
	public void randomTreeCorrect() {
		List<String> writable = new ArrayList<String>();
		List<String> readable = new ArrayList<String>();
		TreeItem randomTree = buildRandomTree(readable, writable);
		ArrayList<TreeItem> tree = new ArrayList<TreeItem>();
		tree.add(randomTree);
		List<TreeItem> result = treeFinder.findWritableTree(tree, readable, writable);
		boolean valid = checkValid(result, new HashSet<>(readable), new HashSet<>(writable), "");
		Assert.assertTrue(valid);
	}

	private boolean checkValid(List<TreeItem> result, Set<String> readable, Set<String> writable, String path) {
		for (TreeItem treeItem : result) {
			if (!checkValid(treeItem, readable, writable, path)) {
				return false;
			}
		}
		return true;

	}

	private boolean checkValid(TreeItem treeItem, Set<String> readable, Set<String> writable, String path) {
		String newPath = path + "/" + treeItem.getName();
		if (treeItem.getChildren() == null || treeItem.getChildren().isEmpty()) {
			return writable.contains(newPath);
		} else {
			return checkValid(treeItem.getChildren(), readable, writable, newPath) && readable.contains(newPath);
		}

	}

	Random random = new Random(1L);

	private TreeItem buildRandomTree(List<String> readable, List<String> writable) {

		int levels = 5;

		return buildTree(levels, writable, readable, "");

	}

	private TreeItem buildTree(int levels, List<String> writable, List<String> readable, String currentPath) {
		String folderName = (nameCounter++) + "";
		String fullPath = currentPath + "/" + folderName;

		double readableChance = random.nextDouble();
		double writableChance = random.nextDouble();
		boolean addToWritable = writableChance > 0.2;
		if (addToWritable) {
			writable.add(fullPath);
		}
		if (addToWritable || readableChance < 0.85) {
			readable.add(fullPath);
		}
		TreeItem result = new TreeItem(folderName, new ArrayList<TreeItem>());

		if (levels > 0) {
			for (int i = 0; i < 10; i++) {
				TreeItem nextLevelItem = buildTree(levels - 1, writable, readable, fullPath);
				result.getChildren().add(nextLevelItem);
			}
		}

		return result;
	}

	int nameCounter = 0;

	private List<TreeItem> buildSampleTree() {
		TreeItem root = treeItemWithName("A");

		TreeItem b = treeItemWithName("B");
		TreeItem c = treeItemWithName("C");
		TreeItem d = treeItemWithName("D");

		root.getChildren().add(b);
		root.getChildren().add(c);
		c.getChildren().add(d);

		List<TreeItem> rootFolders = new ArrayList<>();
		rootFolders.add(root);
		return rootFolders;
	}

	private TreeItem treeItemWithName(String name) {
		return new TreeItem(name, new ArrayList<TreeItem>());
	}

	private void assertTree(TreeItem expected, TreeItem actual) {
		Assert.assertEquals(expected.getName(), actual.getName());
		List<TreeItem> expectedList = expected.getChildren();
		if (expectedList == null) {
			Assert.assertNull(actual.getChildren());
		} else {
			List<TreeItem> actualList = actual.getChildren();
			assertTree(expectedList, actualList);
		}
	}

	private void assertTree(List<TreeItem> expectedList, List<TreeItem> actualList) {
		Assert.assertEquals(expectedList.size(), actualList.size());
		for (int i = 0; i < expectedList.size(); i++) {
			assertTree(expectedList.get(i), actualList.get(i));
		}
	}
}
