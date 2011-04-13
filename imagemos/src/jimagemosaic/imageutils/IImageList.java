package jimagemosaic.imageutils;

public interface IImageList {

	// Clears the list and sets the size to zero.
	public abstract void clear();

	// Adds an imageInfo record into the list at location "i".
	public abstract void put(IImageInfo i);

	// Returns an imageInfo record based on a particular file name/path.
	public abstract IImageInfo get(String f);

	// Returns an imageInfo record basaed on its index in the list.
	public abstract IImageInfo get(int i);

	// Returns the file name/path of an image based on its index in the list.
	public abstract String getName(int i);

	// Returns the file name, without path, of an image based on its index.
	public abstract String getNameNoPath(int i);

	// Removes an imageInfo record at index "i".
	public abstract void remove(int i);

	// Changes the imageInfo record that is in a specific location.
	public abstract void replace(int i, IImageInfo img);

	// This is used for debugging purposes.  It echos the list out to std_out.
	public abstract void printList();

	// Return the size of the list.
	public abstract int getSize();

}