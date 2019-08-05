package folders;

import java.util.List;

public class TreeItem {
	private final String name;
	private final List<TreeItem> children;

	public TreeItem(String name, List<TreeItem> children) {
		this.name = name;
		this.children = children;
	}

	public List<TreeItem> getChildren() {
		return children;
	}

	public String getName() {
		return name;
	}
}
