package jimagemosaic.imageutils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import jimagemosaic.plugins.ImageCache;

public class ImageCacheImpl implements ImageCache {
	public static Map<String, String> urlImageMap = new HashMap<String, String>();
	ExecutorService executor = Executors.newFixedThreadPool(30);
	
	@Override
	public Future<String> preFetch(final String url) {
		Future<String> cacheImage = executor.submit(new Callable<String>() {

			@Override
			public String call() throws Exception {
	    		URL u = new URL(url);  // Create a File from the path string.
	    		InputStream s = u.openStream();
	    		File source = File.createTempFile("tmpurl", ".jpg");

	            URLConnection con;  // represents a connection to the url we want to dl.
	            DataInputStream dis;  // input stream that will read data from the file.
	            FileOutputStream fos; //used to write data from inut stream to file.
	            byte[] fileData;  //byte aray used to hold data from downloaded file.

	    		con = u.openConnection(); // open the url connection.
	            dis = new DataInputStream(con.getInputStream()); // get a data stream from the url connection.
	            fileData = new byte[con.getContentLength()]; // determine how many byes the file size is and make array big enough to hold the data
	            for (int x = 0; x < fileData.length; x++) { // fill byte array with bytes from the data input stream
	                fileData[x] = dis.readByte();
	            }
	            dis.close(); // close the data input stream
	            fos = new FileOutputStream((File)source);  //create an object representing the file we want to save
	            fos.write(fileData);  // write out the file we want to save.
	            fos.close(); // close the output stream writer
	            return source.getAbsolutePath();
			}
			
		});
		return cacheImage;
	}
	
	@Override
	public File getImage(final String url) throws InterruptedException, ExecutionException {
		if ( urlImageMap.containsKey(url)) {
			return new File(urlImageMap.get(url));
		}
		Future<String> cacheImage = preFetch(url);
		String fileName = cacheImage.get();
		urlImageMap.put(url, fileName);
		return new File(fileName);
	}

	@Override
	public void add(String key, String string) {
		urlImageMap.put(key, string);
		
	}

}
