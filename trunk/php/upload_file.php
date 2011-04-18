<?php
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
	$radius = 6;
	for($row=$x-$radius;$row<$x+$radius;$row++) {
		for($col=$y-$radius; $col<$y+$radius;$col++) {
			if ( isset($tiles[$row][$col]) && $tiles[$row][$col] == $url )
				return true;
		}
	}
	return false;
}

function findMatch($x, $y, $rgb, $imagelist, $tiles) {
	$r = ($rgb >> 16) & 0xFF;
	$g = ($rgb >> 8) & 0xFF;
	$b = $rgb & 0xFF;
	$closesturl = '';
	$closest = 1000;

	foreach($imagelist as $image) {
		// val * 256 / 32768  = val * 0.0078125
		$dif = abs($image['red']*0.0078125-$r)+abs($image['green']*0.0078125-$g)+abs($image['blue']*0.0078125-$b);
		if ( $dif <= $closest && !checkUsed($x, $y, $image['url'], $tiles) )
		{			
			$closest = $dif;
			$closesturl = $image['url'];
		}
	}
	return $closesturl;
}

if (!((($_FILES["file"]["type"] == "image/gif")
|| ($_FILES["file"]["type"] == "image/jpeg")
|| ($_FILES["file"]["type"] == "image/pjpeg"))
&& ($_FILES["file"]["size"] < 2000000))) {
echo "nope";
}

if ($_FILES["file"]["error"] > 0)
  {
  echo "Error: " . $_FILES["file"]["error"] . "<br />";
  }
else if (isset($_GET['id'])) {

}
else
  {
$type = $_FILES["file"]["type"];
$filename = $_FILES["file"]["tmp_name"];
$exif = exif_read_data($filename);
$lon = getGps($exif["GPSLongitude"], $exif['GPSLongitudeRef']);
$lat = getGps($exif["GPSLatitude"], $exif['GPSLatitudeRef']);
$lon = (int)($lon * 1000000);
$lat = (int)($lat * 1000000);

$size = getimagesize($filename);
$width = $size[0];
$height = $size[1];
$tilecount = (int)($width/10*$height/10);

if ( $type == "image/jpeg" )
  $im = imagecreatefromjpeg($filename);
$xblocksize = 40;
$yblocksize = (int)($xblocksize*$width/$height);
$xblocks = (int)($width/$xblocksize);
$yblocks = (int)($height/$yblocksize);
$pixmap = pixelize($im, $width, $height, $xblocksize, $yblocksize);

$client = new nusoap_client('http://mosaicgrailsapp.elasticbeanstalk.com/services/imageHeader?wsdl','wsdl');
$result = $client->call('selectImagesNearLocation', array('longitude'=>(int)$lon, 'latitude'=>(int)$lat, 'numImagesToSelect'=>(int)$tilecount));

$imagelist = $result['return'];

$tileMap = selectTiles($xblocks, $yblocks, $pixmap, $imagelist);
$image = imagecreatetruecolor($width, $height);

// make a cache of images
$imagecache = array();
$uniquelist = array();
for($x=0;$x<$xblocks;$x++) {
	for($y=0; $y<$yblocks;$y++) {
		$uniquelist[$tileMap[$x][$y]] = $tileMap[$x][$y];
	}
}

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
header("Content-Type: image/png");
//header("Content-Length: " . filesize($name));

for($x=0;$x<$xblocks;$x++) {
	for($y=0; $y<$yblocks;$y++) {
		$img = imagecreatefromjpeg($imagecache[$tileMap[$x][$y]]);
		$img_size = getimagesize($imagecache[$tileMap[$x][$y]]); // probably inefficient
		imagecopyresampled($image, $img, $x*$xblocksize, $y*$yblocksize, 0, 0, $xblocksize, $yblocksize, $img_size[0], $img_size[1]);
		imagedestroy($img);
	}
}
imagepng($image);
  }

