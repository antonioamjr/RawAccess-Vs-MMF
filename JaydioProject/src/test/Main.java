package test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Random;
import net.smacke.jaydio.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Main {
	static final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
	
	public static MappedByteBuffer[] mapFile() throws IOException{
		MappedByteBuffer buffer0 = null,buffer1 = null,buffer2 = null,buffer3 = null,buffer4 = null,buffer5 = null,
				buffer6 = null,buffer7 = null,buffer8 = null,buffer9 = null;
		MappedByteBuffer[] buffers = {buffer0,buffer1,buffer2,buffer3,buffer4,buffer5,buffer6,buffer7,buffer8,buffer9};
		
		System.out.println("-Mapping Files started-");
		for (int i=0; i<buffers.length; i++){
			//Create file object
			//Directory with 10 Big Files required
	        File file = new File("files/bigFile"+Integer.toString(i)+".tar.gz");
	         
	        //Get file channel in read only mode
	        @SuppressWarnings("resource")
			FileChannel fileChannel = new RandomAccessFile(file, "r").getChannel();
	         
	        //Get direct byte buffer access using channel.map() operation
	        buffers[i] = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
	         
		}
		System.out.println("-Mapping Files ended-\n");
		return buffers;
	}
	
	public static void directFileAccess(byte[] buf, int numTests, int readSize) throws IOException{
		System.out.println("--Jaydio test starting--");
		
		long random, init, timeCounter = 0;
		Random randomGenerator = new Random();

		System.out.println("->Test Init!");	
		for (int j=0; j<numTests; j++){
			//Randomly choose between files
			//Directory with 100 files required (url_0.pl ... url_100.pl)
			DirectRandomAccessFile dfin = new DirectRandomAccessFile(new File("urls/url_"+Integer.toString(randomGenerator.nextInt(100))+".pl"), "r", buf.length);
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

	public static void randomFileAccess(byte[] buf, int readSize, int numTests) throws IOException{
		System.out.println("-RandomAccess test starting-");
		
		long random, init, timeCounter = 0;
		Random randomGenerator = new Random();
		
		System.out.println("->Test Init!");	
		for (int j=0; j<numTests; j++){
			//Randomly choose between files: url_0.pl ... url_100.pl
			RandomAccessFile rfin = new RandomAccessFile("urls/url_"+Integer.toString(randomGenerator.nextInt(100))+".pl", "r");
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
		
		//Mapping Files (only for RAM occupation test)
		MappedByteBuffer[] buffers = mapFile();
		
		int numTests = 1000, readSize = 512;
		int bufferSize = 4*1024;//*1024; // Use 4 KiB buffers
		byte[] buf = new byte[bufferSize];
		System.out.println("Application Buffer size: "+bufferSize+"\n");

		//Random IO Test Init
		randomFileAccess(buf, readSize, numTests);

		//Direct IO Test Init
		directFileAccess(buf, numTests, readSize);
		
		System.out.println("--RawAccess-Vs-MMF test Ending--");
	}
}