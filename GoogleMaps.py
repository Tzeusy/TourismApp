import requests
import json

class GoogleMaps:
    API_KEY = "  AIzaSyDKvwXnlAQ6velp7HzzHqBbPAMvik7N5y4  "
    GEOCODE_API = " AIzaSyCfwFQHj-v03_4juOrRcF4cKSQwOpznu3Y "
    def getDistanceData(self, origin, destination):
		url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + origin + "&destinations=" + destination + "&key=" + self.API_KEY
		
		r = requests.get(url)
		
		return json.loads(r.text)
	
    def getDistanceDataBus(self, origin, destination):
		url = "https://maps.googleapis.com/maps/api/distancematrix/json?mode=transit&origins=" + origin + "&destinations=" + destination + "&key=" + self.API_KEY
		
		r = requests.get(url)
		
		return json.loads(r.text)
	
    def getDistanceDataWalk(self, origin, destination):
		url = "https://maps.googleapis.com/maps/api/distancematrix/json?mode=walking&origins=" + origin + "&destinations=" + destination + "&key=" + self.API_KEY
		
		r = requests.get(url)
		
		return json.loads(r.text)
  
    def getLatLong(self, address):
        url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=" + self.GEOCODE_API
        
        r = requests.get(url)
        
        return json.loads(r.text)
