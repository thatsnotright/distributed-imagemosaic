<?php

$seq = $_GET['image'];

$file = sys_get_temp_dir()."/cache_".escapeshellcmd($seq);
if ( !file_exists($file) )
	exit(0);
header('Content-Type: image/jpeg');
$image = imagecreatefromjpeg($file);
imagejpeg($image);
imagedestroy($image);
