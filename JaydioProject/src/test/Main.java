package test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Random;
import net.smacke.jaydio.*;

public class Main {
	static final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
	
	public static void directFileAccess(byte[] buf,int bs, int nt, int rs) throws IOException{
		System.out.println("--Jaydio test starting--");
		
		DirectRandomAccessFile dfin = new DirectRandomAccessFile(new File("url_0.pl"), "r", bs);
		System.out.println("->File for Raw Access created!");
		
		Random randomGenerator = new Random();
		long random;
		
		System.out.println("->Test Init!");
		long start = threadBean.getCurrentThreadCpuTime();

		for (int i=0; i<nt; i++){
			random = (long)(randomGenerator.nextDouble()*dfin.length());
			dfin.seek(random);
			int remaining = (int)Math.min(rs, dfin.length() - dfin.getFilePointer());
			dfin.read(buf,0,remaining);
		}
		
		long end = threadBean.getCurrentThreadCpuTime();
		System.out.println("->End of Test!");
		
		//System.out.println("->Writes Counter: " + counter);
		
		//Tempo convertido de nanosegundos para microsegundos/milesegundos e mostrado
		System.out.println("Tempo de execução: " + (end-start) + " ns");
		System.out.println("Tempo de execução: " + (end-start)/1000 + " us");
		System.out.println("Tempo de execução: " + (end-start)/1000000 + " ms");
		dfin.close();
		
		System.out.println("-Jaydio test ending-\n");
	}

	public static void randomFileAccess(byte[] buf, int nt) throws IOException{
		System.out.println("-RandomAccess test starting-");
		
		RandomAccessFile rfin = new RandomAccessFile("url_0.pl", "r");
		System.out.println("->File for Random Access created!");
		
		Random randomGenerator = new Random();
		long random;
		
		System.out.println("->Test Init!");
		long start = threadBean.getCurrentThreadCpuTime();

		for (int i=0; i<nt; i++){
			random = (long)(randomGenerator.nextDouble()*rfin.length());
			rfin.seek(random);
			rfin.read(buf);
		}
		
		long end = threadBean.getCurrentThreadCpuTime();
		System.out.println("->End of Test!");
		
		//Tempo convertido de nanosegundos para microsegundos/milesegundos e mostrado
		System.out.println("Tempo de execução: " + (end-start) + " ns");
		System.out.println("Tempo de execução: " + (end-start)/1000 + " us");
		System.out.println("Tempo de execução: " + (end-start)/1000000 + " ms");
		rfin.close();
		
		System.out.println("-RandomAccess test ending-\n");
	}
	
	public static void main(String[] args) throws IOException  {
		System.out.println("--RawAccess-Vs-MMF test starting--");
		
		int numTests = 1000;
		int readSize = 512;
		int bufferSize = 4*1024;//*1024; // Use 1 MiB buffers
		byte[] buf = new byte[bufferSize];
		System.out.println("App Buffer size: "+bufferSize+"\n");
		
		//Random IO Test Init
		randomFileAccess(buf, numTests);
		
		//Direct IO Test Init
		directFileAccess(buf, bufferSize, numTests, readSize);
		
		System.out.println("--RawAccess-Vs-MMF test Ending--");
	}
}