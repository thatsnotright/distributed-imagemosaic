package edu.cu.csci5673.caete

import com.drew.metadata.Directory
import com.drew.metadata.Metadata
import com.drew.metadata.Tag
import com.drew.metadata.exif.ExifDirectory
import com.drew.imaging.jpeg.JpegMetadataReader
import com.drew.metadata.exif.GpsDirectory
import java.io.File
import jimagemosaic.plugins.defaultMapCreate
import jimagemosaic.plugins.radiusRenderRandom
import jimagemosaic.plugins.saveTypes.BMPSaveType
import jimagemosaic.imageutils.imageLoader
import java.awt.image.*
import javax.imageio.ImageReader

class ImageListController {

	def index = {
	}

	def upload = {
		def f = request.getFile('myFile')
		if(!f.empty) {
			def newFile = File.createTempFile("sourceimage",".${f.fileItem.name.split("\\.").last()}")
			f.transferTo( newFile )
			redirect(action:'mosaic', params:["file":newFile.name])
		}
		else {
			flash.message = 'file cannot be empty'
			redirect(action:'uploadForm')
		}
	}

	def mosaic = {
		def hTiles = 40
		def vTiles = 40
		def tileWidth = 20
		def tileHeight = 20
		def colorCorrectPercent = 50
		def tileRep = 5 // allow same tile adjacent for testing, set to > 1 for radius enforcement
		def minDistNum = 5 // distance between the same tile picture
		def tileOrder = 1 // keep this, it's "random" and the only one defined for the default
		
		// save as JPEG some how
		// multi-thread the stupid jimagemosaic code
		// fix exif reading
		
		def file = new File("/tmp/${params.file}")

		Metadata md = JpegMetadataReader.readMetadata(file);
		Directory exifDirectory = md.getDirectory(ExifDirectory.class);
		println exifDirectory.getString(GpsDirectory.TAG_GPS_LATITUDE)
		println exifDirectory.getString(GpsDirectory.TAG_GPS_LONGITUDE)

		WSImageList images = new WSImageList(4302021,-1052514,700)
		RenderedImage baseImage = imageLoader.getRenderedImage(file.path);
		def mapTask = new defaultMapCreate(baseImage, hTiles, vTiles);
		mapTask.go();
		def mapArray = mapTask.getMapArray();
		
		def renderTask = new radiusRenderRandom(mapArray,
			images,
			baseImage,
			hTiles,
			vTiles,
			tileWidth,
			tileHeight,
			colorCorrectPercent,
			tileRep,
			minDistNum,
			tileOrder);
		
		renderTask.go()
		
		def mosaicFile = File.createTempFile("mosaic",".bmp")//".${f.split("\\.").last()"}")
		BMPSaveType bst = new BMPSaveType(renderTask.getNewImage(), mosaicFile.path)
		bst.saveImageToFile();
		['file':file, 'mosaic':mosaicFile]
	}

	def getImage = {
		def fileName = params.file
		if ( fileName ==~ /[a-zA-Z0-9._-]+/) {
			def file = new File("/tmp/${fileName}")
			response.outputStream << file.getBytes()
		} else {
			response.status = 403
			render "Invalid file name, try again."
		}
	}
}
