package test;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Random;
import net.smacke.jaydio.*;

public class Main {
	static final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
	
	public static void directFileAccess() throws IOException{
		System.out.println("--Jaydio test starting--");

		int bufferSize = 4*1024;//*1024; // Use 1 MiB buffers
		byte[] buf = new byte[bufferSize];
		System.out.println(bufferSize);
		
		DirectRandomAccessFile fin = new DirectRandomAccessFile(new File("big.txt"), "r", bufferSize);
		System.out.println("->FileIn created!");
		DirectRandomAccessFile fout = new DirectRandomAccessFile(new File("big_copy.txt"), "rw", bufferSize);
		System.out.println("->FileOut created!");
		
		Random randomGenerator = new Random();
		long random;
		
		System.out.println("->Copy Init!");
		long start = threadBean.getCurrentThreadCpuTime();
		
		//while (fin.getFilePointer() < fin.length()){
		for (int i=0; i<10; i++){
			random = (long)(randomGenerator.nextDouble()*fin.length());
			System.out.println("->Ramdom Number: " + random);
			fin.seek(random);
			int remaining = (int)Math.min(bufferSize,fin.length() - fin.getFilePointer());
			fin.read(buf,0,remaining);
			fout.write(buf,0,remaining);
			
		}
		long end = threadBean.getCurrentThreadCpuTime();
		System.out.println("->End of Copy!");
		
		//Tempo convertido de nanosegundos para milesegundos e mostrado
		System.out.println("Tempo de execução: " + (end-start) + " ns");
		System.out.println("Tempo de execução: " + (end-start)/1000 + " us");
		System.out.println("Tempo de execução: " + (end-start)/1000000 + " ms");
		fin.close();
		fout.close();
	}
	
	public static void main(String[] args) throws IOException  {
		directFileAccess();
	}

}
