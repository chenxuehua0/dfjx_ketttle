package org.seaboxdata.systemmng.utils;

import java.util.ArrayList;
import java.util.List;

import org.seaboxdata.systemmng.entity.ResourceEntity;

public class ResourceTreeUtils {

	// 把一个List转成树
	public static List<ResourceEntity> buidTree(List<ResourceEntity> list) {
		List<ResourceEntity> tree = new ArrayList<>();
		for (ResourceEntity node : list) {
			if (node.getParentId().equals("0")) {
				tree.add(findChild(node, list));
			}
		}
		return tree;
	}

	public static ResourceEntity findChild(ResourceEntity node, List<ResourceEntity> list) {
		for (ResourceEntity n : list) {
			if (n.getParentId().equals(node.getId())) {
				node.setLeaf(false);
				if (node.getChildren() == null) {
					node.setChildren(new ArrayList<ResourceEntity>());
				}
				node.getChildren().add(findChild(n, list));
			}
		}
		return node;
	}

}