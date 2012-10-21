

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.Callable;

public class StringSort {
	static byte[] buffer = new byte[3000000];
	static Node[] nodes = new Node[600000];
	static{
		for(int i= 0; i<nodes.length ; i++)
			nodes[i] = new Node();
	}
	static int slotused = 0;
	
	private static void insert(byte[] b){
		int slot = 0;
		Node slotnode = nodes[slot];
		
		for(int i=0;i< b.length;i++){
			int c = b[i] - 'a' + 1;
			slotnode = nodes[slot];
			if(slotnode.children[c] == 0)//o为未使用过的，子的位置1开始。 新slot
				slotnode.children[c] = ++slotused;
			slot = slotnode.children[c];
		}	
		nodes[slot].wordnum ++ ;
	}
	
	
	private static void insert(byte[] b,int start,int end){
		//System.out.println(new String(b,start,end-start));
		int slot = 0;
		Node slotnode = nodes[slot];
		int blength = end-start;
		for(int i=0;i< blength;i++){
			int c = b[start+i] - 'a' + 1;
			slotnode = nodes[slot];
			if(slotnode.children[c] == 0)//o为未使用过的，子的位置1开始。 新slot
				slotnode.children[c] = ++slotused;
			slot = slotnode.children[c];
		}	
		nodes[slot].wordnum ++ ;
	}
	
	
	private static void insert_v2(byte[] b,int start,int end){
		//System.out.println(new String(b,start,end-start));
		int slot = 0;
		Node slotnode = nodes[slot];
		int blength = end-start;
		for(int i=0;i< blength;i++){
			int c = b[start+i] - 'a' + 1;
			slotnode = nodes[slot];
			
			slot = slotnode.children[c];
			if(slot==0){
				slot = slotnode.children[c] = ++slotused;
			}
			 
		}	
		nodes[slot].wordnum ++ ;
	}
	
	private static void insert_v3(int bufferlength){//无调用者的bufer循环。比较难看
		int slot = 0;
		Node slotnode = nodes[slot];
		int start = 8;
		for(int j= start; j< bufferlength; j++){
			if( buffer[j]!=13){
				int c = buffer[j] - 'a' + 1;
				slotnode = nodes[slot];
				if(slotnode.children[c] == 0)//o为未使用过的，子的位置1开始。 新slot
					slotnode.children[c] = ++slotused;
				slot = slotnode.children[c];
			}else{	
				nodes[slot].wordnum ++ ;
				j++;
				slot = 0;
			}
		}

	}
	
	static char[] stack = new char[24];
	static int stkpos = 0;
    private static void dfs(int root){
		Node slotnode = nodes[root];
		
		if(slotnode.wordnum!=0){
			//output stack
			out(slotnode.wordnum);
		}
			
		for(int i=1; i<28;i++){
				if(slotnode.children[i] != 0){
					int tmp = stkpos;
					stack[stkpos++] = (char)(i-1+'a');
					dfs(slotnode.children[i]);
					stkpos = tmp;
				}
		}
		
	}
	private static void out(int times){
		for(int i=0;i<times;i++){
			for(int j=0;j< stkpos ;j++){
		//		System.out.print(stack[j]);
			}
		//	System.out.println("");
		}
			 count+=times;
	}
	static int count = 0;
	public static void print(){
		dfs(0);
	}
	public static void input(String file){
		try {
			 
			 
			FileInputStream in = new FileInputStream(file);
			int num = in.read(buffer);//换行2个字符，从8开始

		 

			int start=0,end=0;
//取单词数量
			/*for(int i = 0; i< num;i++){
				if(buffer[i]==13 && buffer[i+1]== 10){
					start = i+2;
					break;
				}
			}*/
			
			start = 8;
			for(int i = start; i< num;i++){
				if(buffer[i]==13 && buffer[i+1]== 10){
					insert(buffer,start,i);
					start = i+2;
					i++;
				}
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void input_byteArr(String file){
		try {
			FileInputStream in = new FileInputStream(file);
			int num = in.read(buffer);//换行2个字符，从8开始
			int start=0,end=0;
		//	System.out.println((int)buffer[6]);
		//	System.out.print((int)buffer[7]);
			/*for(int i = 0; i< num;i++){
				if(buffer[i]==13 && buffer[i+1]== 10){
					start = i+2;
					break;
				}
			}*/
			start = 8;
			byte[] small = new byte[20];
			for(int i = start; i< num;i++){
				byte tmp = buffer[i];
				if(tmp==13 && buffer[i+1]== 10){
					insert_v2(small,0,i-start);
					start = i+2;
					i++;
				}else
				small[i-start]=tmp;
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void input_v3(String file){
		try { 	 
			FileInputStream in = new FileInputStream(file);
			int num = in.read(buffer);//换行2个字符，从8开始
 
			insert_v3(num); 
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
 
	
	
	
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long startTime = System.nanoTime();
		//StringSort instance = new StringSort();
		input("/home/foo/workspace/just-for-fun/WordSorter/Java/sowpods.txt");
	 	print();
		System.out.println(" finish : "
				+ (System.nanoTime() - startTime) / 1000000 + " MS." + " count:"+count);
	}

}

class Node{
	int[] children = new int[28];
	
	int   wordnum = 0 ;

}


 