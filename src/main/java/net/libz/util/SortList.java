package net.libz.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SortList {
	public static <T extends Comparable<T>> void concurrentSort(final List<T> key, List<?>... lists) {
		List<Integer> indices = new ArrayList<Integer>();
		for (int i = 0; i < key.size(); i++)
			indices.add(i);

		Collections.sort(indices, new Comparator<Integer>() {
			@Override
			public int compare(Integer i, Integer j) {
				return key.get(i).compareTo(key.get(j));
			}
		});

		Map<Integer, Integer> swapMap = new HashMap<Integer, Integer>(indices.size());
		List<Integer> swapFrom = new ArrayList<Integer>(indices.size()), swapTo = new ArrayList<Integer>(indices.size());
		for (int i = 0; i < key.size(); i++) {
			int k = indices.get(i);
			while (i != k && swapMap.containsKey(k))
				k = swapMap.get(k);

			swapFrom.add(i);
			swapTo.add(k);
			swapMap.put(i, k);
		}

		for (List<?> list : lists)
			for (int i = 0; i < list.size(); i++)
				Collections.swap(list, swapFrom.get(i), swapTo.get(i));
	}
}
