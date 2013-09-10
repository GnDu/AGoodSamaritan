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
			int sa, ra_k_val, rank, curr_suf, prev_suf;
			RankedComparison pair_comp = new RankedComparison();
			//O(log n) for this loop.
			//The complexity is pretty much log n(n log n + n) which pretty much
			//is n(log 2)^2
			for(int k=1; k<len; k*=2){
				
				//Create ranking pairs and sort them.
				//O(n)
				// for(int i=0; i<len; i++){
				// 	//Pretty much SA[i], RA[ SA[i] ] and RA[ SA[i] + k ]
				// 	sa = suffix_arr[i];
				// 	//if it's out sa[i] + k is out of bounds, assign it a zero
				// 	ra_k_val = (sa+k<len) ? (int)ranked_arr[sa+k] : 0; 
				// 	rp_arr[i] = new RankedPair(suffix_arr[i], (int)ranked_arr[sa], ra_k_val);
				// }

				//O(n log n)
				//Arrays.sort(rp_arr, pair_comp);
				//Using counting sort
				countingSort(k, len);
				countingSort(0, len);

				//Using counting sort

				//Put the suffix back and rank or rerank the Paired integers
				//O(n)
				rank=0;
				ranked_arr[0] = 0;
				int curr_k, prev_k;
				int[] tempRA = new int[len];
				for(int i=1; i<len; i++){
					//suffix_arr[i] = rp_arr[i].suffix;
					curr_suf = suffix_arr[i];
					prev_suf = suffix_arr[i-1];
					//Equvalent to RA[SA[i]]
					// if(rp_arr[i].equals(rp_arr[i-1]))
					// 	//if the rank is the same, 
					// 	ranked_arr[curr_suf] = rank;
					// else
					// 	ranked_arr[curr_suf] = ++rank;
					curr_k = (curr_suf+k<len) ? curr_suf+k : 0;
					prev_k = (prev_suf+k<len) ? prev_suf+k : 0;
					if(ranked_arr[curr_suf]==ranked_arr[prev_suf] &&
						ranked_arr[curr_k]==ranked_arr[prev_k]){
						tempRA[curr_suf] = rank;
					}
					else{
						tempRA[curr_suf] = ++rank;
					}
				}
				ranked_arr = tempRA;	
			}			
		}
		public String toString(){
			//return Arrays.toString(suffix_arr);
			String actual = "";
			for(int i=0; i<suffix_arr.length; i++){
				actual = actual.concat(word.substring(suffix_arr[i])).concat("\n");
			}
			return actual;
		}

	    public void countingSort(int k, int len){
		      int size = Math.max(len, 300);
		      int[] freq_table = new int[size]; 
		      int[] temp_suffix_arr = new int[len];
		      int rank;
		      //create frequency table
		      for(int i=0; i<len; i++){
		        rank = (i+k<len) ? ranked_arr[i+k] : 0;
		        freq_table[ rank ]++;
		      }
		      
		      //Accumulate them along the freq_table
		      int sum = 0;
		      for(int i=0; i<size; i++){
		        int temp = freq_table[i];
		        freq_table[i] = sum;
		        sum+=temp;
		      }   
		      //System.out.println(Arrays.toString(freq_table));
		      //start 'sorting'
		      int index;
		      for(int i=0; i<len; i++){
		        if(suffix_arr[i] + k < len)
		          rank = ranked_arr[ suffix_arr[i] +k ]; //get the rank
		        else
		          rank=0;

		        index = freq_table[rank]; //get the slot where 
		                                       //the index is supposed to go
		        //System.out.println(toString());
		        //System.out.println(index);
		        temp_suffix_arr[index] = suffix_arr[i];
		        freq_table[rank]++;
		      }
		      suffix_arr = temp_suffix_arr;    
	    }

		public boolean find(String substr){
			int low=0, high=suffix_arr.length-1, mid;
			String suffix;
			System.out.println("Finding");
			while(low<high){
				mid = (low+high)/2;
				suffix = word.substring(suffix_arr[mid]);
				if(suffix.startsWith(substr))
					return true;
				else if(substr.compareTo(suffix)>0)
					low = mid+1;
				else
					high = mid;
			}
			return false;
		}
	}

	public void run() throws Exception{
		//input and output

	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    PrintWriter pr = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
	    
	    //String input = br.readLine();
	    ArrayList<SuffixArray> words = new ArrayList<SuffixArray>();
	    SuffixArray sa;
	    String haha = "ROBERT$";
	    sa = new SuffixArray(haha, haha.length());
	    pr.println(sa);
	    // while(!input.equals("#")){
	    // 	System.out.println(input);
	    // 	input = input.concat("$").toUpperCase();
	    // 	sa = new SuffixArray(input, input.length());
	    // 	words.add(sa);
	    // 	input = br.readLine();
	    // }
	    // int count = 0;
	    // for(SuffixArray word:words){
	    // 	if(word.find("ER"))
	    // 		count++;
	    // }
	    // pr.println(count);
	    pr.close();

	}
	//An simple implementation of suffix array
	public static void main(String[] args) throws Exception{
		SuffixArrayDemo sad = new SuffixArrayDemo();
		sad.run();
	}

}