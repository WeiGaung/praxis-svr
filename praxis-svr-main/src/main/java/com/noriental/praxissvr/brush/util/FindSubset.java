package com.noriental.praxissvr.brush.util;

import java.util.*;

/**
 * 查找一个集合的子集
 * @author shengxian.xiao
 *
 */
public class FindSubset {
	private BitSet start;
	private BitSet end;
	private Set<Set<Object>> subsets;
	public FindSubset() {
		this.start = new BitSet();
		this.end = new BitSet();
		this.subsets = new HashSet<>();
	}
	public void execute(int n, Set<Object> items) {
		int size = items.size();
		for (int i = 0; i < n; i++) {
			start.set(i, true);
		}
		for (int i = n; i < size; i++) {
			start.set(i, false);
		}
		for (int i = size - 1; i > size - n; i--) {
			end.set(i, true);
		}
		for (int i = 0; i < size - n; i++) {
			end.set(i, false);
		}
		find(items);
		while (start != end) {
			boolean endBit = start.get(size - 1);
			int last10 = -1;
			int i;
			for (i = size - 1; i > 0; i--) {
				boolean former = start.get(i - 1);
				boolean last = start.get(i);
				if (former && !last) {
					last10 = i - 1;
					break;
				}
			}
			if (i == 0) {
				break;
			} else {
				if (!endBit) {
					start.set(last10, false);
					start.set(last10 + 1, true);
					find(items);
				} else {
					start.set(last10, false);
					start.set(last10 + 1, true);
					last10 += 1;
					for (int j = last10 + 1; j < size; j++) {
						if (start.get(j)) {
							start.set(j, false);
							start.set(last10 + 1, true);
							last10 += 1;
							find(items);
						}
					}
				}
			}
		}
	}
	public void find(Set<Object> items) {
		Set<Object> temp = new HashSet<>();
		Object elements[] = items.toArray();
		for (int i = 0; i < elements.length; i++) {
			if (start.get(i)) {
				temp.add(elements[i]);
			}
		}
		subsets.add(temp);
	}
	public Set<Set<Object>> getSubsets() {
		return this.subsets;
	}
	
	public void clearSubsets(){
		this.subsets.clear();
	}
	public static void main(String[] args) {
		Set<Object> items = new HashSet<>();
		items.add(1L);
		items.add(2L);
		items.add(4L);
		items.add(3L);
		
		FindSubset fs = new FindSubset();
		System.out.println(fs.getAllSortedSubSet(items));
//		List<List<Long>> list = new ArrayList<List<Long>>();
//		
//		FindSubset fs = new FindSubset();
//		for (int i = 0; i < items.size(); i++) {
//			fs.execute(i + 1, items);
//			Iterator<Set<Object>> iterator = fs.getSubsets().iterator();
//			while (iterator.hasNext()) {
//				List<Long> subsL  = new ArrayList<Long>(); 
//				Object[] subset = iterator.next().toArray();
//				List<Object> subs = Arrays.asList(subset);
//				for(int k=0;k<subs.size();k++){
//					subsL.add(Long.valueOf(subs.get(k).toString()));
//				}
//				Collections.sort(subsL);
//				list.add(subsL);
//			}			
//			fs.clearSubsets();
//		}
//		
//		System.out.println(list);
	}
	/**
	 * 子集从小到大排序按照,号分割
	 * @param items
	 * @return
	 */
	public List<String> getAllSortedSubSet(Set<Object> items){
		List<String> listStr = new ArrayList<>();
		List<List<Long>> list = new ArrayList<>();
		for (int i = 0; i < items.size(); i++) {
			this.execute(i + 1, items);
			for (Set<Object> objects : this.getSubsets()) {
				List<Long> subsL = new ArrayList<>();
				Object[] subset = objects.toArray();
				List<Object> subs = Arrays.asList(subset);
				for (Object sub : subs) {
					subsL.add(Long.valueOf(sub.toString()));
				}
				Collections.sort(subsL);
				list.add(subsL);
			}			
			this.clearSubsets();
		}
		for (List<Long> subList : list) {
			StringBuilder str = new StringBuilder();
			for (int k = 0; k < subList.size(); k++) {
				Long subL = subList.get(k);
				if (k == subList.size() - 1) {
					str.append(subL.toString());
				} else {
					str.append(subL.toString() + ",");
				}
			}
			listStr.add(str.toString());
		}
		return listStr;
	}
}
