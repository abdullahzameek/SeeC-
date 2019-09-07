from flask import Flask
import requests
import json

capitalOneAPIKey = 'f990b904d48c2277e4b75f9ddd8ed3c9'
app = Flask(__name__)
customerID = ''

################################### Capital One Related Stuff #######################################

def getAllCustomers():
    url = 'http://api.reimaginebanking.com/customers?key=f990b904d48c2277e4b75f9ddd8ed3c9'
    response = requests.get(url)
    print(response.text)

    if response.status_code == 404:
	    print('oopsy whoopsy we made a fucky wucky')
    
def getNewCustID():
    pass

def createCustomerOne():
    pass

def addCustBalanceOne():
    pass

def subCustBalance():
    pass

def viewCustBalance():
    pass


##################################### Firebase Stuff ###################################################

def createCustomerFB():
    pass

def addCustMiles():
    pass



if __name__ == "__main__":
    getAllCustomers()