package noh.jinil.app.anytime.utils;

import java.util.ArrayList;

import android.util.Log;

public class ArrayUtils {
	public static void parseLongsToArray(ArrayList<Long> list, long[] ids) {		
		for(int i=0;i<ids.length;i++) {
			list.add(ids[i]);
		}
	}
	
	public static void parseArrayToLongs(ArrayList<Long> list, long[] ids) {		
		for(int i=0;i<list.size();i++) {
			ids[i] = list.get(i);
		}
	}
	
	public static long[] checkDuplicate(long[] cmpA, long[] cmpB) {
		ArrayList<Long> arrayList = new ArrayList<Long>();
		Log.e("ArrayUtils", "1quickSort:"+cmpB.length);
		quickSort(cmpA, 0, cmpA.length-1);
		
		for(int i=0;i<cmpB.length;i++) {
			if(!binarySearch(cmpA, cmpB[i])) {
				arrayList.add(cmpB[i]);
			}
		}
		
		long[] newList = new long[arrayList.size()];
		parseArrayToLongs(arrayList, newList);
		Log.e("ArrayUtils", "2quickSort:"+newList.length);
		return newList;
	}
	
	private static int partition(long arr[], int left, int right) {
		long pivot = arr[(left + right) / 2];

		while (left < right) {
			while ((arr[left] < pivot) && (left < right))
				left++;
			while ((arr[right] > pivot) && (left < right))
				right--;

			if (left < right) {
				long temp = arr[left];
				arr[left] = arr[right];
				arr[right] = temp;
			}
		}

		return left;
	}

	public static void quickSort(long arr[], int left, int right) {
		if (left < right) {
			int pivotNewIndex = partition(arr, left, right);

			quickSort(arr, left, pivotNewIndex - 1);
			quickSort(arr, pivotNewIndex + 1, right);
		}
	}
	
	public static boolean binarySearch(long data[], long key) {
		int low = 0;
		int high = data.length - 1;
		 
		while(high >= low) {
		    int middle = (low + high) / 2;
		    if(data[middle] == key) {
		        return true;
		    }
		    if(data[middle] < key) {
		        low = middle + 1;
		    }
		    if(data[middle] > key) {
		        high = middle - 1;
		    }
		}
		return false;
	}
}
