package jimagemosaic.imageutils;

import java.awt.Color;

public interface IImageInfo {

	// Accessor to set the file name/path for the image.
	public abstract void setFileName(String fn);

	// Accessor to set the RBG value for the image.
	public abstract void setRBGVal(Color rbgv);

	// Accessor to retrieve the filename for the image.
	public abstract String getFileName();

	// Accessor to retrieve the RBG value for the image.
	public abstract Color getColor();

}