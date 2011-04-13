package jimagemosaic.plugins;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import jimagemosaic.imageutils.IImageList;

public interface ImageCache {
	public File getImage(final String url) throws InterruptedException, ExecutionException;

	Future<String> preFetch(String url);

	public void add(String key, String string);
}
