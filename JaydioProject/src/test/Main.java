package test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Random;
import net.smacke.jaydio.*;

public class Main {
	static final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
	
	public static void mapFile(byte[] buf, int numTest) throws IOException{
		long init, timeCounter = 0;
		Random randomGenerator = new Random();
		
		System.out.println("-Mapping Files started-");
		for (int i=0; i<numTest; i++){
			//Create file object
	        File file = new File("urls/url_"+Integer.toString(randomGenerator.nextInt(100))+".pl");
	         
	        //Get file channel in read only mode
	        @SuppressWarnings("resource")
			FileChannel fileChannel = new RandomAccessFile(file, "r").getChannel();
	         
	        //Get direct byte buffer access using channel.map() operation
	        MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
	        
	        init = threadBean.getCurrentThreadCpuTime();
			buffer.position((int)randomGenerator.nextDouble()*buffer.limit());
			buffer.get(buf);
			timeCounter += (threadBean.getCurrentThreadCpuTime() - init);      
		}
		
		//Time in nanoseconds converted for micro/mili seconds
		System.out.println("Execution time: " + timeCounter + " ns");
		System.out.println("Execution time: " + timeCounter/1000 + " us");
		System.out.println("Execution time: " + timeCounter/1000000 + " ms");
				
		System.out.println("-Mapping Files ended-\n");
	}
	
	public static void directFileAccess(byte[] buf, int numTests, int readSize) throws IOException{
		System.out.println("--Jaydio test starting--");
		
		long random, init, timeCounter = 0;
		Random randomGenerator = new Random();

		System.out.println("->Test Init!");	
		for (int j=0; j<numTests; j++){
			//Randomly choose between files
			//Directory with 100 files required (url_0.pl ... url_100.pl)
			DirectRandomAccessFile dfin = new DirectRandomAccessFile(
					new File("urls/url_"+Integer.toString(randomGenerator.nextInt(100))+".pl"), "r", buf.length);
			random = (long)(randomGenerator.nextDouble()*dfin.length());
			
			init = threadBean.getCurrentThreadCpuTime();
			dfin.seek(random);
			int remaining = (int)Math.min(readSize, dfin.length() - dfin.getFilePointer());
			dfin.read(buf,0,remaining);
			timeCounter += (threadBean.getCurrentThreadCpuTime() - init);
			
			dfin.close();
		}
		System.out.println("->End of Test!");
		
		//Time in nanoseconds converted for micro/mili seconds
		System.out.println("Execution time: " + timeCounter + " ns");
		System.out.println("Execution time: " + timeCounter/1000 + " us");
		System.out.println("Execution time: " + timeCounter/1000000 + " ms");
		
		System.out.println("-Jaydio test ending-\n");
	}

	public static void randomFileAccess(byte[] buf, int numTests, int readSize) throws IOException{
		System.out.println("-RandomAccess test starting-");
		
		long random, init, timeCounter = 0;
		Random randomGenerator = new Random();
		
		System.out.println("->Test Init!");	
		for (int j=0; j<numTests; j++){
			//Randomly choose between files: url_0.pl ... url_100.pl
			RandomAccessFile rfin = new RandomAccessFile(
					"urls/url_"+Integer.toString(randomGenerator.nextInt(100))+".pl", "r");
			random = (long)(randomGenerator.nextDouble()*rfin.length());
			
			init = threadBean.getCurrentThreadCpuTime();
			rfin.seek(random);
			rfin.read(buf);
			timeCounter += (threadBean.getCurrentThreadCpuTime() - init);
			
			rfin.close();
		}
		System.out.println("->End of Test!");
		
		//Time in nanoseconds converted for micro/mili seconds
		System.out.println("Execution time: " + timeCounter + " ns");
		System.out.println("Execution time: " + timeCounter/1000 + " us");
		System.out.println("Execution time: " + timeCounter/1000000 + " ms");
		
		System.out.println("-RandomAccess test ending-\n");
	}
	
	public static void main(String[] args) throws IOException  {
		System.out.println("--RawAccess-Vs-MMF test starting--\n");
		
		int numTests = 1000, readSize = 512;
		int bufferSize = 4*1024;//*1024; // Use 4 KiB buffers
		byte[] buf = new byte[bufferSize];
		System.out.println("Application Buffer size: "+bufferSize+"\n");

		//Mapped-Files Test Init
		mapFile(buf, numTests);
		
		//Random IO Test Init
		randomFileAccess(buf, numTests, readSize);

		//Direct IO Test Init
		directFileAccess(buf, numTests, readSize);
		
		System.out.println("--RawAccess-Vs-MMF test Ending--");
	}
}