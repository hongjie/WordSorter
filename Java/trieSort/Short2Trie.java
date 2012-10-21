



import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

 

public class Short2Trie {
	static int NODE_SIZE=32*32;
	static class SNode{
		int[] children = new int[NODE_SIZE]; 
 
		int   wordnum = 0 ; 
	}
	
	static byte[] buffer = new byte[3000000];//3000000
	static SNode[] nodes = new SNode[430000];
	static{
		for(int i= 0; i<nodes.length ; i++)
			nodes[i] = new SNode();
	}
	static int slotused = 0;
 
 
	
	private static void insert(int[] b,int start,int end){//2个byte成为一个int
		 
		int slot = 0;
		SNode slotnode = nodes[slot];
		int blength = end-start;
		
		for(int i=0;i < blength;i++){
			int c = b[start+i] ;
			slotnode = nodes[slot];
			if(slotnode.children[c] == 0)//o为未使用过的，子的位置1开始。 新slot
				slotnode.children[c] = ++slotused;
			slot = slotnode.children[c];
		}	
		nodes[slot].wordnum ++ ;
	}
	
	
	static int[] stack = new int[24];
	static int stkpos = 0;
	
 
    private static void dfs_2byte(int root){
		SNode slotnode = nodes[root];
		
		if(slotnode.wordnum!=0){
			//output stack
			out(slotnode.wordnum);
		}
			
		for(int i=1; i< NODE_SIZE ;i++){
				if(slotnode.children[i] != 0){
					int tmp = stkpos;
					stack[stkpos++] =  i ;
					dfs_2byte(slotnode.children[i]);
					stkpos = tmp;
				}
		}
		
	}
    
    
	private static void out(int times){
		byte[] resovle = new byte[2];
		for(int i=0;i<times;i++){
			for(int j=0;j< stkpos ;j++){
				if(decode(stack[j],resovle)==1){
		//			System.out.print((char)resovle[0]);
				}else{
		//			System.out.print(new String(resovle)); 
				}
		 		
			}
		 //	System.out.println("");
		}
			 count+=times;
	}
	
	static int count = 0;//单词数量
	public static void print(){
		dfs_2byte(0);
	}
 
	
	public static void input_mod(String file){
		try {
			FileInputStream in = new FileInputStream(file);
			int num = in.read(buffer);//换行2个字符，从8开始
			int start=0,end=0;
		// 	System.out.println(new String(buffer)+"\n--\n");
			start = 8;
			
			int[] small = new int[10];
			int smallindex = 0;
			
			int intbuff = 0;
			int byteindex = -1;//每3个
			
			for(int i = start; i< num;i++){
				
				byte tmp = buffer[i];
				
				if(tmp==13 && buffer[i+1]== 10){
					if(byteindex!=-1){//未输出
						 small[smallindex++]=intbuff;
						 if(intbuff>19683) System.out.println(intbuff);
						 intbuff = 0;
						 byteindex = -1;
					}
				 	insert(small,0,smallindex);
				 	//	decode_test(small,0,smallindex);
					start = i+2;
					i++;
					smallindex = 0;
					
				}else{//要插入small的byte
			//		System.out.println("input:"+tmp);
					tmp -= ('a'-1);
					byteindex ++ ;
					if(byteindex == 0){
						intbuff |= tmp << 5;
					}else if(byteindex == 1){
						intbuff |= tmp ;
					    small[smallindex++]=intbuff;//small 
					    //if(intbuff>19683) System.out.println(intbuff);
					    intbuff = 0;
					    byteindex = -1;
					} 
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
	
	private static void decode_test(int[] b,int start,int end){
		byte[] r = new byte[2*(end-start)];
		int ri = 0;
		for(int i=start; i<end ;i++){
			
			byte tmp = (byte) (b[i] >> 5);
			
			if(tmp!=0){
				r[ri++]=(byte) (tmp+'a'-1);// 要去掉a
			// 	System.out.println("output:"+(char)r[ri-1]);
				tmp = (byte) ((b[i]) & 0x1f); //11111
				if(tmp != 0){
					r[ri++]=(byte) (tmp+'a'-1);
			//		System.out.println("output:"+(char)r[ri-1]);
				 
				
				}
			}	 
		  }
		System.out.println(new String(r));
	}
	
	private static int decode(int b ,byte[] result){
		 
		int ri = 0;
	 
			
		byte tmp = (byte) (b >> 5);
		
		if(tmp!=0){
			result[ri++]=(byte)(tmp+'a'-1);
		//	System.out.println("output:"+tmp);
			tmp = (byte) ((b) & 0x1f);
			if(tmp != 0){
				result[ri++]=(byte)(tmp+'a'-1);
			}
		}	 
		  
		 return ri;
	}
	
	public static void main(String[] args) {
		System.err.println("start");
		long startTime = System.nanoTime();
		 
		input_mod("/home/foo/workspace/just-for-fun/WordSorter/Java/sowpods.txt");//
	  	print();
		System.out.println(" finish : "
				+ (System.nanoTime() - startTime) / 1000000 + " MS." + " count:"+count + " slot:"+slotused);
	}


}
