package test;

import java.util.ArrayList;

public class Test 
{
	public static void main(String[] args)
	{
		int[] array = {2,1,5,10,11,3};
		
		InsertionSort(array);
		for(Integer i: array)
		{
			System.out.print(i + ", ");
		}
		
	}
	public static void InsertionSort( int [ ] num)
	{
	     int j;                     // the number of items sorted so far
	     int key;                // the item to be inserted
	     int i;  

	     for (j = 1; j < num.length; j++)    // Start with 1 (not 0)
	    {
	           key = num[ j ];
	           for(i = j - 1; (i >= 0) && (num[ i ] < key); i--)   // Smaller values are moving up
	          {
	                 num[ i+1 ] = num[ i ];
	          }
	         num[ i+1 ] = key;    // Put the key in its proper location
	     }
	}
}


//   429,032  2,977,006   2,430,684
// 2,111,954  19,590,298  51,971,498
