<html>
<body>
<?php
date_default_timezone_set('America/Denver');
include("inc/funcs.php");
require_once('inc/nusoap.php');

function selectTiles($xblocks, $yblocks, $colormap, $imagelist) {
	$tiles = array();
	for($x = 0; $x < (int)$xblocks; $x++) {
		for($y = 0; $y < (int)$yblocks; $y++) {
			$match = findMatch($x, $y, $colormap[$x][$y], $imagelist, $tiles);
			$tiles[$x][$y] = $match;
		}
	}
	return $tiles;
}

// http://www.talkphp.com/script-giveaway/3620-pixelate-algorithm-using-gd.html
function pixelize($im, $width, $height, $blockw, $blockh) {
$colorMap = array();
	for($x = 0; $x < $width; $x += $blockw)
            {
                for($y = 0; $y < $height; $y += $blockh)
                {
                    $colors = Array(
                            'red'       => 0, 
                            'green'     => 0, 
                            'blue'      => 0, 
			    'total'	=> 0,
                            );
                    for($cx = 0; $cx < $blockw; ++$cx)
                    {
                        for($cy = 0; $cy < $blockh; ++$cy)
                        {
                            if($x + $cx >= $width || $y + $cy >= $height)
                            {
                                continue;
                            }

                            $rgb = imagecolorat($im, $x + $cx, $y + $cy);
			    $r = ($rgb >> 16) & 0xFF;
			    $g = ($rgb >> 8) & 0xFF;
			    $b = $rgb & 0xFF;
                            $colors['red']        += $r;
                            $colors['green']    += $g;
                            $colors['blue']        += $b;
			    ++$colors['total'];
                        }
                    }
		    $colorMap[$x/$blockw][$y/$blockh] = array((int)($colors['red']/$colors['total']),(int)($colors['green']/$colors['total']),(int)($colors['blue']/$colors['total']));
		}
	}
	return $colorMap;
}


function checkUsed($x, $y, $url, $tiles) {
	$radius = 16;
	for($row=$x-$radius;$row<$x+$radius;$row++) {
		for($col=$y-$radius; $col<$y+$radius;$col++) {
			if ( isset($tiles[$row][$col]) && $tiles[$row][$col] == $url )
				return true;
		}
	}
	return false;
}

function findMatch($x, $y, $rgb, $imagelist, $tiles) {
	$closesturl = '';
	$closest = 1000;

	foreach($imagelist as $image) {
		// val * 256 / 32768  = val * 0.0078125
		$dif = abs($image['red']*0.007-$rgb[0])+abs($image['green']*0.007-$rgb[1])+abs($image['blue']*0.007-$rgb[2]);
		if ( $dif < $closest && !checkUsed($x, $y, $image['url'], $tiles) )
		{			
			$closest = $dif;
			$closesturl = $image['url'];
		}
	}
	return $closesturl;
}

function showpixmap($width, $height, $pixmap, $xblocksize, $yblocksize, $xblocks, $yblocks) {
	$pixim = imagecreatetruecolor($width, $height);
	for($x=0;$x<$xblocks;$x++) {
		for($y=0;$y<$yblocks;$y++) {
			$color = imagecolorallocate($pixim, $pixmap[$x][$y][0],$pixmap[$x][$y][1],$pixmap[$x][$y][2]);
			imagefilledrectangle($pixim, $x*$xblocksize, $y*$yblocksize, $x*$xblocksize+$xblocksize, $y*$yblocksize+$yblocksize, $color);
			imagecolordeallocate($pixim, $color);
		}
	}
$fname = tempnam(sys_get_temp_dir(), "cache_");
imagejpeg($pixim, $fname);
$split = preg_split("/cache_/", $fname);
$last = $split[count($split)-1];
echo "<img src='image.php?image=".$last."' />";
/*	ob_start();
	imagejpeg($pixim);
	$contents =  ob_get_contents();
	ob_end_clean();
	echo "<img src='data:image/jpeg;base64,".base64_encode($contents)."' /><br/>";*/
	imagedestroy($pixim);
}

if ($_FILES["file"]["type"] != "image/jpeg"
  && $_FILES["file"]["size"] > 2000000) {
echo "nope";
}

if ($_FILES["file"]["error"] > 0)
{
  echo "Error: " . $_FILES["file"]["error"] . "<br />";
}
else
  {
$type = $_FILES["file"]["type"];
$filename = $_FILES["file"]["tmp_name"];
$exif = exif_read_data($filename);
$lon = getGps($exif["GPSLongitude"], $exif['GPSLongitudeRef']);
$lat = getGps($exif["GPSLatitude"], $exif['GPSLatitudeRef']);
echo "This image was taken at ".$lon." longitude, ".$lat." latitude.<br/>";
$lon = (int)($lon * 1000000);
$lat = (int)($lat * 1000000);

$size = getimagesize($filename);
$width = $size[0];
$height = $size[1];

$im = imagecreatefromjpeg($filename);
if ( $width > 600 || $height > 600 ) {
	$ow = $width;
	$oh = $height;
	$ratio = $height/$width;
	$width = 600;
	$height = (int)($ratio*$width);
	$oldim = $im;
	$im = imagecreatetruecolor($width, $height);
	imagecopyresampled($im, $oldim, 0, 0, 0, 0, $width, $height, $ow, $oh);
	imagedestroy($oldim);
}

$fname = tempnam(sys_get_temp_dir(), "cache_");
imagejpeg($im, $fname);
$split = preg_split("/cache_/", $fname);
$last = $split[count($split)-1];
echo "<img src='image.php?image=".$last."' />";


$xblocksize = $_POST['tilesize'];
if(isset($_POST['keepsize'])) {
	$image = imagecreatetruecolor($width, $height);
	$yblocksize = (int)($xblocksize);
	$xblocks = (int)($width/$xblocksize);
	$yblocks = (int)($height/$yblocksize);
	$tilecount = (int)($xblocks*$yblocks)*2;
	$pixmap = pixelize($im, $width, $height, $xblocksize, $yblocksize);
	showpixmap($width, $height, $pixmap, $xblocksize, $yblocksize, $xblocks, $yblocks);
} else {
	$xblocksize = 10;
	$yblocksize = (int)($xblocksize);
	$xblocks = (int)($width/$xblocksize);
	$yblocks = (int)($height/$yblocksize);
	$tilecount = (int)($xblocks*$yblocks)*2;

	$pixmap = pixelize($im, $width, $height, 10, 10);	
	showpixmap($width, $height, $pixmap, $xblocksize, $yblocksize, $xblocks, $yblocks);

	$xblocksize = $_POST['tilesize'];
	$yblocksize = (int)($xblocksize);
	$width = $xblocksize * $xblocks;
	$height = $yblocksize * $yblocks;
	$image = imagecreatetruecolor($width, $height);
}
imagedestroy($im);
echo "Final image size will be ".$width."x".$height." pixels<br/>";

$client = new nusoap_client('http://mosaicgrailsapp.elasticbeanstalk.com/services/imageHeader?wsdl','wsdl');
$client->setDebugLevel(0);
$client->use_curl = true;
$result = $client->call('selectImagesNearLocation', array('longitude'=>(int)$lon, 'latitude'=>(int)$lat, 'numImagesToSelect'=>(int)$tilecount));

$imagelist = $result['return'];

$tileMap = selectTiles($xblocks, $yblocks, $pixmap, $imagelist);


// make a cache of images
$imagecache = array();
$uniquelist = array();
for($x=0;$x<$xblocks;$x++) {
	for($y=0; $y<$yblocks;$y++) {
		$uniquelist[$tileMap[$x][$y]] = $tileMap[$x][$y];
	}
}
$dtime_start = microtime(true);
$curl_arr = array();
$master = curl_multi_init();
$i=0;
foreach($uniquelist as $url)
{
	$curl_arr[$i][0] = curl_init($url);
	$curl_arr[$i][1] = $url;
	curl_setopt($curl_arr[$i][0], CURLOPT_RETURNTRANSFER, true);
	curl_multi_add_handle($master, $curl_arr[$i][0]);
	$i++;
}

do {
    curl_multi_exec($master,$running);
} while($running > 0);
$node_count = count($curl_arr);
for($i = 0; $i < $node_count; $i++)
{
	$results = curl_multi_getcontent  ( $curl_arr[$i][0]  );
	$fname = tempnam(sys_get_temp_dir(), "cache_");
	$imagecache[$curl_arr[$i][1]] = $fname;
	$file = fopen($fname, "wb");
	fwrite($file, $results);
	fseek($file, 0);
	fclose($file);
	curl_multi_remove_handle($master, $curl_arr[$i][0]);;
}
curl_multi_close($master);

$dtime_end = microtime(true);
$dtime = $dtime_end - $dtime_start;

$time_start = microtime(true);
for($x=0;$x<$xblocks;$x++) {
	for($y=0; $y<$yblocks;$y++) {
		$img = imagecreatefromjpeg($imagecache[$tileMap[$x][$y]]);
		$img_size = getimagesize($imagecache[$tileMap[$x][$y]]); // probably inefficient
		imagecopyresized($image, $img, $x*$xblocksize, $y*$yblocksize, 0, 0, $xblocksize, $yblocksize, $img_size[0], $img_size[1]);
		imagedestroy($img);
	}
}
$time_end = microtime(true);
$time = $time_end - $time_start;

$totalsize=0;
foreach($imagecache as $file) {
	$totalsize += filesize($file);
}
echo "<map name=\"sourcemap\">";
for($x=0;$x<$xblocks;$x++) {
	for($y=0; $y<$yblocks;$y++) {
		$coordstr = ($x*$xblocksize).",".($y*$yblocksize).",".($x*$xblocksize+$xblocksize).",".($y*$yblocksize+$yblocksize);
		echo "<area shape=\"rect\" coords=\"".$coordstr."\" href=\"".$tileMap[$x][$y]."\" target=\"_blank\"/>";
	}
}
echo "</map>";

/*ob_start();
imagejpeg($image);
$contents =  ob_get_contents();
ob_end_clean();


echo "<img src='image.php?image=' usemap=\"#sourcemap\"/>";*/
$fname = tempnam(sys_get_temp_dir(), "cache_");
imagejpeg($image, $fname);
$split = preg_split("/cache_/", $fname);
$last = $split[count($split)-1];
echo "<img src='image.php?image=".$last."' />";
imagedestroy($image);
echo "Downloaded ".count($imagecache)." images in ".$dtime." seconds, total of ".$totalsize." bytes for ".$totalsize/$dtime." bytes/sec<br/>";
echo "Took ".$time." seconds to create the output image.<br/>";

foreach($imagecache as $file) {
	unlink($file);
}
  }
?>
</body>
</html>
