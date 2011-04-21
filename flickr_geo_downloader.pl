#!/opt/local/bin/perl            

use lib "/Library/Perl/5.10.0/";

#use Image::ExifTool qw(:Public);
use LWP::Simple;
use Data::Dumper;
use HTML::LinkExtor;
use Flickr::API;
use Flickr::API::Request;
use Digest::MD5 qw(md5 md5_hex md5_base64);
use XML::Parser::Lite::Tree::XPath;
use Image::Magick;
use SOAP::Lite;

sub push_to_server{
    print "Pushing to Server RC: ".SOAP::Lite
	->proxy('http://mosaicgrailsapp.elasticbeanstalk.com/services/imageHeader?wsdl')
	->saveImage(
	SOAP::Data->name('url')->value("$_[0]")->type('string'), 
	SOAP::Data->name('longitude')->value("$_[1]")->type('int'),
	SOAP::Data->name('latitude')->value("$_[2]")->type('int'),
	SOAP::Data->name('red')->value("$_[3]")->type('int'),
	SOAP::Data->name('blue')->value("$_[4]")->type('int'),
	SOAP::Data->name('green')->value("$_[5]")->type('int'))."\n";
}



my $api = new Flickr::API({'key' => '014ce860b583a2be9ccc5c0b516af489',
                           'secret' => '5d90a8e2ee8adc73'});

@time = localtime(time); 
$time = $time[4].$time[3].$time[2].$time[1].$time[0]."\n";


$pull_latitude=$ARGV[0];
$pull_longitude=$ARGV[1];
open LOG, '>', "flickr_geo_downloader_log_$time";

#while($pull_latitude ){
for($longitude_counter=0; $longitude_counter<360; $longitude_counter+=.33){
    for($latitude_counter=0; $latitude_counter<180; $latitude_counter+=.33){
	$pull_latitude=$ARGV[0]+$latitude_counter;
	$pull_longitude=$ARGV[1]+$longitude_counter;

	if($pull_latitude > 90){
	    $pull_latitude-=180;
	}
	if($pull_longitude > 180){
	    $pull_longitude-=360;
	}


	print "Pulled from Lat: $pull_latitude / Lon: $pull_longitude\n";
	print LOG "Pulled from Lat: $pull_latitude / Lon: $pull_longitude\n";

	$newsig = md5_hex("5d90a8e2ee8adc73api_key014ce860b583a2be9ccc5c0b516af489auth_token72157625888021173-670056b58aecc13amethodflickr.photos.search");
	$response = $api->execute_method('flickr.photos.search', {
	    'api_key' => '014ce860b583a2be9ccc5c0b516af489',
	    'auth_token' => '72157625888021173-670056b58aecc13a',
	    'lat' => "$pull_latitude",
	    'lon' => "$pull_longitude",
	    'min_upload_date' => '0',
	    'radius' => '32km',
	    'signature' => "$newsig",
					 });





	my $xpath = new XML::Parser::Lite::Tree::XPath($response->{tree});
	my @nodes = $xpath->select_nodes('/photos');

	$Data::Dumper::Indent = 3;

	@temp = @{$nodes[0]}[0];


	$i=1;
	while($temp[0]->{children}[$i]){
	    my $id = $temp[0]->{children}[$i]->{attributes}->{id};
	    my $secret = $temp[0]->{children}[$i]->{attributes}->{secret};
	    my $server = $temp[0]->{children}[$i]->{attributes}->{server};
	    my $farm = $temp[0]->{children}[$i]->{attributes}->{farm};
	    print "http://farm${farm}.static.flickr.com/$server/${id}_${secret}.jpg\n";
	    $url = "http://farm${farm}.static.flickr.com/$server/${id}_${secret}.jpg";
	    LWP::Simple::getstore("http://farm${farm}.static.flickr.com/$server/${id}_${secret}.jpg", "test$i.jpg");

	    if($Visited{$url}){
		print "Previously seen $url";
		$i+=2;
		next;
	    }else{
		$Visited{$url} = 1;
		print LOG "Processing $url\n";
	    }

	    $newsig = md5_hex("5d90a8e2ee8adc73api_key014ce860b583a2be9ccc5c0b516af489auth_token72157625888021173-670056b58aecc13amethodflickr.photos.geo.getLocation");
	    $response = $api->execute_method('flickr.photos.geo.getLocation', {
		'api_key' => '014ce860b583a2be9ccc5c0b516af489',
		'photo_id', => $id,
					     });
	    my $xpath = new XML::Parser::Lite::Tree::XPath($response->{tree});    
	    my @nodes2 = $xpath->select_nodes('/photo');
	    @temp2 = @{$nodes2[0]}[0];
	    my $latitude = $temp2[0]->{children}[1]->{attributes}->{latitude};
	    my $longitude = $temp2[0]->{children}[1]->{attributes}->{longitude};
	    print "$latitude $longitude\n";

	    if($latitude =~ m/^(\d+)?\./){
		if($1 < 10){
		    $latitude="00".$latitude;
		}elsif($1 < 100){
		    $latitude="0".$latitude;
		}
	    }
	    if($longitude =~ m/^(\d+)?\./){
		if($1 < 10){
		    $longitude="00".$longitude;
		}elsif($1 < 100){
		    $longitude="0".$longitude;
		}
	    }
	    $latitude =~s/\.//;
	    $longitude =~s/\.//;

	    for($j=length($latitude);$j<10;$j++){
		$latitude=$latitude."0"
	    }
	    for($j=length($longitude);$j<10;$j++){
		$longitude=$longitude."0"
	    }

	    $total_red = 0;
	    $total_green = 0;
	    $total_blue = 0;
	    $total_count = 0;
	    $IM = Image::Magick->new;
	    $image = $IM->Read("test$i.jpg");
	    my(@histogram) = $IM->Histogram();
	    while(@histogram){
		($red, $green, $blue, $opacity, $count) = splice(@histogram, 0, 5);
		$total_red +=$red;
		$total_green +=$green;
		$total_blue +=$blue;
		$total_count +=$count;
	    }
	    print "TR $total_red, TG $total_green, TB $total_blue TC $total_count\n";
	    if($total_count == 0){
		next;
	    }

	    $average_red = $total_red / $total_count;
	    $average_blue = $total_blue / $total_count;
	    $average_green = $total_green /$total_count;
	    print sprintf "Averages: R: 0x%04x. G: 0x%04x. B: 0x%04x.\n", $average_red, $average_blue, $average_green;    


	    @tmp = split(/\./, $average_red);
	    $average_red = $tmp[0];
	    @tmp = split(/\./, $average_green);
	    $average_green = $tmp[0];
	    @tmp = split(/\./, $average_blue);
	    $average_blue = $tmp[0];
	    
	    print "Pushing $url, $longitude, $latitude, $average_red, $average_blue, $average_green\n";
	    push_to_server($url, $longitude, $latitude, $average_red, $average_blue, $average_green);
	    $i+=2;

	    sleep(5);
	}

    }
}
