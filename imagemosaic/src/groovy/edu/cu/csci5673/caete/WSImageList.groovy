package edu.cu.csci5673.caete

import java.awt.Color;

import jimagemosaic.imageutils.IImageInfo;
import jimagemosaic.imageutils.IImageList;
import groovyx.net.ws.WSClient

class WSImageList implements IImageList {
	
	List images 
	
	WSImageList(int lon, int lat, int max) {
		def proxy = new WSClient("http://mosaicgrailsapp.elasticbeanstalk.com/services/imageHeader?wsdl", this.class.classLoader)
		proxy.initialize()
		images = proxy.selectImagesNearLocation(lon, lat, max)
	}

	@Override
	public void clear() {
		images.clear()
	}
	
	@Override
	public IImageInfo get(String arg0) {
		images.find { it.url.endsWith arg0 }
	}

	@Override
	public IImageInfo get(int arg0) {
		def image = images.get(arg0)
		return new IImageInfo() {
			
			@Override
			public void setRBGVal(Color rbgv) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setFileName(String fn) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public String getFileName() {
				return image.url
			}
			
			@Override
			public Color getColor() {
				return new Color(image.red/32767, image.green/32767, image.blue/32767);
			}
		};
	}

	@Override
	public int getSize() {
		images.size()
	}

	@Override
	public void printList() {
		images.each {
			println it.url
		}

	}

	@Override
	public void put(IImageInfo arg0) {
		images += arg0
	}

	@Override
	public void remove(int arg0) {
		images.remove(arg0)
	}

	@Override
	public void replace(int arg0, IImageInfo arg1) {
		images[arg0] = arg1
	}
	
	@Override
	public String getNameNoPath(int arg0) {
		images[arg0].name
	}

	@Override
	public String getName(int arg0) {
		images[arg0].name
	}
}
