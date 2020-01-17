# OfflineReverseGeocode
- An Offline Reverse Geocoding Java library  
- Forked from https://github.com/AReallyGoodName/OfflineReverseGeocode  
- This library provides offline reverse geocoding for [GeoNames](http://www.geonames.org/) placenames and postal codes 
given a lat/lon. It uses [k-d trees](https://en.wikipedia.org/wiki/K-d_tree) for extremely fast lookups.

## Usage

### Placenames Lookup
- First download a placenames file from http://download.geonames.org/export/dump/
- Allcountries.zip from that site is comprehensive however if you're on mobile try the cities1000.zip file. It's 1/80th of the size.
- Then simply  
  `ReverseGeoCode reverseGeoCode = new ReverseGeoCode(new FileInputStream("c:\\\\AU.txt"), true);`
  `System.out.println("Nearest to -23.456, 123.456 is " + reverseGeoCode.nearestPlace(-23.456, 123.456));`

### Postal Code lookup
- First download a postal code file from http://download.geonames.org/export/zip/
- Then simply  
  `ReversePostalCode reversePostalCode = new ReversePostalCode(new FileInputStream("c:\\\\AU.txt"));`
  `System.out.println("Nearest to -23.456, 123.456 is " + reversePostalCode.nearestPostalCode(-23.456, 123.456));`

## Configuration

### General configuration
This library does not have any direct configuration for connections.

## License
Licensed under The MIT License

## Ports
A C# port by Necrolis is available at https://github.com/Necrolis/GeoSharp
