import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
 
 public class OptimalCurrenSort {
	static byte[] buffer = new byte[3000000];
	static volatile int num;
	
 
	private static SortWork[] workers = new SortWork[15];
	static{
		for(int i=0;i<workers.length;i++){
			workers[i] = new SortWork();
		}
	}
	static ExecutorService executor = Executors.newFixedThreadPool(16);
	

	public static void main(String[] args) {
		long startTime = System.nanoTime();
		input_v8_n(args[0] );
	 	 
		System.out.println(" finish : "
				+ (System.nanoTime() - startTime) / 1000000 + " MS."  );
	}
	 
	 
	//字符范围 优化. 类似terasort里面的抽样优化,这里使用手工配置线程数
	public static void input_v8_n( String file){
		try { 	 
			FileInputStream in = new FileInputStream(file);
			num = in.read(buffer);//换行2个字符，从8开始
			int nthread = 15;
			Future<Integer>[] fus = new Future[nthread];
			
			workers[0].setAlph(num, 'a', 'a');
			workers[1].setAlph(num, 'b', 'b');
			workers[2].setAlph(num, 'c', 'c');
			workers[3].setAlph(num, 'd', 'd');
			workers[4].setAlph(num, 'e', 'e');
			workers[5].setAlph(num, 'f', 'g');
			workers[6].setAlph(num, 'h', 'i');
			workers[7].setAlph(num, 'j', 'l');
			workers[8].setAlph(num, 'm', 'm');
			workers[9].setAlph(num, 'n', 'o');
			workers[10].setAlph(num, 'p', 'p');
			workers[11].setAlph(num, 'q', 'r');
			workers[12].setAlph(num, 's', 's');
			workers[13].setAlph(num, 't', 'u');
			workers[14].setAlph(num, 'v', 'z');
		
			
			for(int i=0;i< nthread ; i++){
				fus[i] = executor.submit(workers[i]);
			}
			for(int i=0;i< nthread ; i++){
				fus[i].get();
				outf.write(workers[i].sortedbuff,0,workers[i].sbindex);
			}
	 	   
		 
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
			e.printStackTrace();
		}
	}
	 
	static class SortWork  implements Callable<Integer> {
		 
		 private Node_[] nodes = new Node_[70000];//600000
		
		 public byte[] sortedbuff = new byte[00000];//3000000
		 public int sbindex = 0;
		 
		 
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

		
		  private  void insert_v83(int start,int bufferlength){ 
				boolean isnew  = true;
				int slot = 0;
				Node_ slotnode = nodes[slot];
				
				for(int j= start; j< bufferlength; j++){
					byte bj = OptimalCurrenSort.buffer[j];
					if(isnew){
						if(( bj != this.start_alph )){
							while(OptimalCurrenSort.buffer[++j]!=13);
							j++;//跳过后空格10 
							continue;
						}else
							isnew = false;
					 }
					
					if( OptimalCurrenSort.buffer[j]!=13){
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
			}
		
		 
	   private  void insert_v82(int start,int bufferlength){ 
			boolean isnew  = true;
			int slot = 0;
			Node_ slotnode = nodes[slot];
			
			for(int j= start; j< bufferlength; j++){
				byte bj = OptimalCurrenSort.buffer[j];
				if(isnew){
					if(( bj < this.start_alph || bj > this.end_alph)){
						while(OptimalCurrenSort.buffer[++j]!=13);
						j++;//跳过后空格10 
						continue;
					}else
						isnew = false;
				 }
				
				if( OptimalCurrenSort.buffer[j]!=13){
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
		}
		
		public  void print(){
			 
				dfs(0);
		 
		}
		
		@Override
		public Integer call() throws Exception {
			
			if(this.start_alph==this.end_alph)
				insert_v83(8,this.bufferlength);
			else
				insert_v82(8,this.bufferlength);
			
			print();
			return 1;
		} 
	}

	static class Node_{
		int[] children = new int[28];
		int   wordnum = 0 ;
	}
}




