/*
Author: 
Du Zhiyuan

Description:
A simple implementation of Suffix Array as explained in Steven Halim's Competitive 
Programming book (or The Little Green Book)

Visualisation:
http://www.comp.nus.edu.sg/~stevenha/visualization/suffixarray.html
*/

import java.io.*;
import java.util.*;


class SuffixArrayDemo{
	//We need to create additional classes so we could use Arrays.sort
	public class RankedPair{
		//For sorting suffixes.
		//OOP convention be damned! 
		int first, second, suffix;
		public RankedPair(int pt, int f, int s){
			first = f;
			second = s;
			//We would need to store the corresponding suffix index when we sort around
			suffix = pt;
		}
		public boolean equals(Object obj){
			if(obj instanceof RankedPair){
				RankedPair sec = (RankedPair) obj;
				return first==sec.first && second == sec.second;
			}else
				return false;		
		}
		public String toString(){
			return suffix + ": (" + first + "," + second + ")";
		}
	}
	public class RankedComparison implements Comparator<RankedPair>{
		//The Comparator to be used for Arrays.sort
		public int compare(RankedPair pair_1st, RankedPair pair_2nd){
			
			if(pair_1st==null || pair_2nd==null)
				return 0;
			
			//(10,5) > (10,2) > (9,5) > (3,7) == (3,7)
			if(pair_1st.first > pair_2nd.first || (pair_1st.first==pair_2nd.first &&
													pair_1st.second>pair_2nd.second))
				return 1;
			else if(pair_1st.first<pair_2nd.first || (pair_1st.first==pair_2nd.first &&
														pair_1st.second<pair_2nd.second))
				return -1;
			else
				return 0;
		}
	}

	public class SuffixArray{

		//suffix_arr to store all the indexes of the suffix
		//ranked_arr to store all the rankings.
		//rp_arr Array to sort
		//word for printing purpose
		private int[] suffix_arr, ranked_arr;
		private RankedPair[] rp_arr;
		String word;
		
		public SuffixArray(String input, int len){

			suffix_arr = new int[len];	
			ranked_arr = new int[len];
			rp_arr = new RankedPair[len];
			word = input;
			
			//Initialising the array
			for(int i=0; i<len; i++){
				suffix_arr[i] = i;
				ranked_arr[i] = (int) input.charAt(i);
			}
			suffix_sort(len);

		}
		public void suffix_sort(int len){
			//Start the sorting
			//A slew of other variables
			int sa, ra_k_val, rank, curr_suf;
			RankedComparison pair_comp = new RankedComparison();
			//O(log n) for this loop.
			//The complexity is pretty much log n(n log n + n) which pretty much
			//is n(log 2)^2
			for(int k=1; k<len; k*=2){
				
				//Create ranking pairs and sort them.
				//O(n)
				for(int i=0; i<len; i++){
					//Pretty much SA[i], RA[ SA[i] ] and RA[ SA[i] + k ]
					sa = suffix_arr[i];
					//if it's out sa[i] + k is out of bounds, assign it a zero
					ra_k_val = (sa+k<len) ? (int)ranked_arr[sa+k] : 0; 
					rp_arr[i] = new RankedPair(suffix_arr[i], (int)ranked_arr[sa], ra_k_val);
				}

				//O(n log n)
				Arrays.sort(rp_arr, pair_comp);

				//Put the suffix back and rank or rerank the Paired integers
				//O(n)
				rank=0;
				for(int i=0; i<len; i++){
					suffix_arr[i] = rp_arr[i].suffix;
					curr_suf = suffix_arr[i];
					//Equvalent to RA[SA[i]]
					if(i==0)
						ranked_arr[curr_suf] = 0;
					else if(rp_arr[i].equals(rp_arr[i-1]))
						//if the rank is the same, 
						ranked_arr[curr_suf] = rank;
					else
						ranked_arr[curr_suf] = ++rank;
				}
				//System.out.println("k=" + k);
			}			
		}
		public String toString(){
			String actual = "";
			for(int i=0; i<suffix_arr.length; i++){
				actual = actual.concat(word.substring(suffix_arr[i])).concat("\n");
			}
			return actual;
		}
	}

	public void run() throws Exception{
		//input and output

	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    PrintWriter pr = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
	    
	    String input = br.readLine();
	    SuffixArray sa;

	    while(!input.equals("#")){
	    	input = input.concat("$").toUpperCase();
	    	sa = new SuffixArray(input, input.length());
	    	pr.println(sa.toString());
	    }
	    pr.close();

	}
	//An simple implementation of suffix array
	public static void main(String[] args) throws Exception{
		SuffixArrayDemo sad = new SuffixArrayDemo();
		sad.run();
	}

}