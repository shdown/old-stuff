package util;

public class SortUtils {
	protected static void quickSort(Object[] objects, Comparator cmp, int lowerIndex, int higherIndex) {
		if(higherIndex <= lowerIndex)
			return;
		int i = lowerIndex;
		int j = higherIndex;
		Object pivot = objects[lowerIndex+(higherIndex-lowerIndex)/2];
		while(i <= j) {
			while(cmp.compare(objects[i], pivot) < 0)
				i++;
			while(cmp.compare(objects[j], pivot) > 0)
				j--;
			if(i <= j) {
				Object tmp = objects[j];
				objects[j] = objects[i];
				objects[i] = tmp;
				i++;
				j--;
			}
		}
		if(lowerIndex < j)
			quickSort(objects, cmp, lowerIndex, j);
		if(i < higherIndex)
			quickSort(objects, cmp, i, higherIndex);
	}
	
	public static void quickSort(Object[] objects, Comparator cmp) {
		quickSort(objects, cmp, 0, objects.length-1);
	}	
}