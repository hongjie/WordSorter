
 

 

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
 
 public class CurrenSort {
	static byte[] buffer = new byte[3000000];
	static volatile int num;
	
 
	private static SortWork[] workers = new SortWork[1];
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
				if(i == 0) start --;
				
				workers[i].setAlph(num, start,end);
				
				fus[i] = executor.submit(workers[i]);
			}
			for(int i=0;i< nthread ; i++){
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
	
	public static void input_v8_1(String file, String start,String end ){//字符范围 5线程 330ms
		try { 	 
			FileInputStream in = new FileInputStream(file);
			num = in.read(buffer);//换行2个字符，从8开始
			Future<Integer>[] fus = new Future[1];
			 
		 
			workers[0].setAlph(num, 
						(char)start.charAt(0), 
						(char)end.charAt(0));
				
			fus[0] = executor.submit(workers[0]);
		 
			fus[0].get();
			outf.write(workers[0].sortedbuff,0,workers[0].sbindex);
			 
	 	   
		 
			executor.shutdown();
			
			try {
			 
		 
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
			outf = new FileOutputStream("/tmp/output.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	 
 
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long startTime = System.nanoTime();
		input_v8_1(args[0],args[1] ,  args[2] );
	 	 
		System.out.println(" finish : "
				+ (System.nanoTime() - startTime) / 1000000 + " MS."  );
	}
	
	
	
	static class SortWork  implements Callable<Integer> {
		 
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

		
	 
		
		 
	   private  void insert_v82(int start,int bufferlength){ 
		 
			
			boolean isnew  = true;
			int slot = 0;
			Node_ slotnode = nodes[slot];
			
			for(int j= start; j< bufferlength; j++){
				byte bj = CurrenSort.buffer[j];
				if(isnew){
					if(( bj < this.start_alph || bj > this.end_alph)){
						while(CurrenSort.buffer[++j]!=13);
						j++;//跳过后空格10 
						continue;
					}else
						isnew = false;
				 }
				
				if( CurrenSort.buffer[j]!=13){
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
		private  void out(int times){
			for(int i=0;i<times;i++){
				
				 
				System.arraycopy(stack, 0, sortedbuff, sbindex, stkpos);
			 
				sbindex += stkpos;
				sortedbuff[sbindex++]='\n';
			}
				 count+=times;
		}
		
		public  void print(){
			 
				dfs(0);
		 
		}
		
		@Override
		public Integer call() throws Exception {
			// TODO Auto-generated method stub
		//	insert_v4(start,end-start);
		//	insert_v5(start,end-start);
		
			long startTime = System.nanoTime();
		 	 
			insert_v82(8,this.bufferlength);
			
			print();
			
			System.out.println(" slot "+slotused + " count:"+count + " " +(char)start_alph+" "+(char)end_alph + " finish : "+ (System.nanoTime() - startTime) / 1000000 + " MS."  );
			return 1;
		} 
	}
	static class Node_{
		int[] children = new int[28];
		
		int   wordnum = 0 ;

	}
}




