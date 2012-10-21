import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

 class SortWork  implements Callable<Integer> {
	 
	 private Node_[] nodes = new Node_[600000];
	
	 public byte[] sortedbuff = new byte[3000000];
	 public int sbindex = 0;
	 
	 private int start;
	 private int end;
	 private byte start_alph;
	 private byte end_alph;
	 private int bufferlength;
	 
	 byte[] stack = new byte[24];
	 int stkpos = 0;
	 int count = 0;
	
	{
		for(int i= 0; i<nodes.length ; i++)
			nodes[i] = new Node_();
	}
	
	int slotused = 0;

	
	private  void insert(byte[] b,int start,int end){
		//System.out.println(new String(b,start,end-start));
		int slot = 0;
		Node_ slotnode = nodes[slot];
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
	
	 
	private  void insert_v82(int start,int bufferlength){ 
	 
		
		boolean isnew  = true;
		int slot = 0;
		Node_ slotnode = nodes[slot];
		
		for(int j= start; j< bufferlength; j++){
			byte bj = CurrenSortStack.buffer[j];
			if(isnew){
				if(( bj < this.start_alph || bj > this.end_alph)){
					while(CurrenSortStack.buffer[++j]!=13);
					j++;//跳过后空格10 
					continue;
				}else
					isnew = false;
			 }
			
			if( bj!=13){
				int c = bj - 'a' + 1;
				slotnode = nodes[slot];
				if(slotnode.children[c] == 0)//o为未使用过的，子的位置1开始。 新slot
					slotnode.children[c] = ++slotused;
				slot = slotnode.children[c];
			}else{	
				nodes[slot].wordnum ++ ;
				j++;
				slot = 0;
				isnew = true;
			}
		}
		 System.out.println("ok");
	}
	
	public void set(int start,int end){
		this.start = start;
		this.end = end;
	}
	public void setAlph(int bufferlength,char start,char end){
		this.bufferlength = bufferlength;
		this.start_alph = (byte)start;
		this.end_alph = (byte)end;
	}
    private  void dfs(int root){
    	Node_ slotnode = nodes[root];
		
		if(slotnode.wordnum!=0){
			//output stack
			out(slotnode.wordnum);
		}
			
		for(int i=1; i<28;i++){
				if(slotnode.children[i] != 0){
					int tmp = stkpos;
					stack[stkpos++] = (byte)(i-1+'a');
					dfs(slotnode.children[i]);
					stkpos = tmp;
				}
		}
		
	}
    private  void dfs_stack(int root){
 
		
		Node_[] nodestack = new Node_[18];
		int ndindex = -1;
		Node_ cur_node = null;
		
		int[] istack   = new int[18];
		int iindex  = -1;
		int cur_i  = 0;
		
		int[] posstack = new int[18];
		int posindex  = -1;
//		int cur_pos = 0;
		
		cur_node= nodes[root];
		cur_i   = 1;
	//	cur_pos = stkpos;
		
lable1:	while(true ){
			
	     	while(cur_i < 28){
					if( cur_node.children[cur_i] == 0)
						cur_i ++;
					else{
						if(stkpos==24){
							System.out.println();
						}
						stack[stkpos++] = (byte)(cur_i-1+'a');
						//压栈
						istack[++iindex]    = cur_i ;
						nodestack[++ndindex]= cur_node;
						posstack[++posindex]= stkpos-1;
						
						//更新当前
						cur_node = nodes[cur_node.children[cur_i]];
						cur_i = 1; 
						
						if(cur_node.wordnum != 0){
							//output stack
							out(cur_node.wordnum);
						}
						continue lable1;
					}
			 }
				
			 
			//弹栈
			if(ndindex <= -1) break lable1;
			
			cur_i    = istack[iindex--] + 1; 
			cur_node = nodestack[ndindex--];
			stkpos   = posstack[posindex--];
		}
			
		
	}
    
	private  void out_1(int times){
		for(int i=0;i<times;i++){
			for(int j=0;j< stkpos ;j++){		 
			 	sortedbuff[sbindex++]=stack[j];
		 	//	System.out.print((char)stack[j]);
			}
		
		 	sortedbuff[sbindex++]='\n';
		//	System.out.println('\n');
		}
			 count+=times;
	}
	
	private  void out(int times){
		for(int i=0;i<times;i++){
			
			 
			System.arraycopy(stack, 0, sortedbuff, sbindex, stkpos);
		 
			sbindex += stkpos;
			sortedbuff[sbindex++]='\n';
		}
			 count+=times;
	}
	
	public  void print(){
		 
		dfs_stack(0);
	 
	}
	
	@Override
	public Integer call() throws Exception {
		// TODO Auto-generated method stub
 
	
	//	long startTime = System.nanoTime();//注释掉
	 	 
		insert_v82(8,this.bufferlength);
		
		print();
		
	// 	System.out.println(" slot "+slotused + " count:"+count + " " +(char)start_alph+" "+(char)end_alph + " finish : "+ (System.nanoTime() - startTime) / 1000000 + " MS."  );
		
		return 1;
	} 
}

 
 
 
 public class CurrenSortStack {
	static byte[] buffer = new byte[3000000];//3000000
	static volatile int num;
	private static SortWork[] workers = new SortWork[16];
	static{
		for(int i=0;i<workers.length;i++){
			workers[i] = new SortWork();
		}
	}
	static ExecutorService executor = Executors.newFixedThreadPool(16);
	
	 
	public static void input_v8_n(int nthread , String file){//字符范围 5线程 330ms
		try { 	 
			FileInputStream in = new FileInputStream(file);
			num = in.read(buffer);//换行2个字符，从8开始
			Future<Integer>[] fus = new Future[nthread];
			for(int i=0;i< nthread ; i++){
				char start = (char)(1 + 'a'+(1.0f * i/nthread)*('z'-'a'));
				char end   = (char)('a'+(1.0f * (i+1)/nthread)*('z'-'a'));
				if(i == 0)
					start --;
				workers[i].setAlph(num,start,end);
				
				fus[i] = executor.submit(workers[i]);
			}
			for(int i=0;i < nthread ; i++){
				fus[i].get();
				outf.write(workers[i].sortedbuff,0,workers[i].sbindex);
			}
	 	   
		 
			executor.shutdown();
			
			try {
				for(int i=0;i< nthread ; i++){
					 
			//		outf.write(workers[i].sortedbuff,0,workers[i].sbindex);
				}
		 
				outf.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
 
	public static FileOutputStream outf; 
	
	static{
		try {
			outf = new FileOutputStream("output.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	 
 
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long startTime = System.nanoTime();
		
		input_v8_n(Integer.valueOf(args[0]),args[1]);
	 	 
		System.out.println(" finish : "
				+ (System.nanoTime() - startTime) / 1000000 + " MS."  );
	}

}

class Node_{
	int[] children = new int[28];
	
	int   wordnum = 0 ;

}


