import flask
from datetime import date
import firebase_admin
from firebase_admin import db
from firebase_admin import credentials

import requests
import json

capitalOneAPIKey = 'f990b904d48c2277e4b75f9ddd8ed3c9'
app = flask.Flask(__name__)

cred = credentials.Certificate("seec-pennapps-firebase-adminsdk-7zh6f-41c2a963c2.json")
firebase_admin.initialize_app(cred, {
    'databaseURL' : 'https://seec-pennapps.firebaseio.com/'
})

CUSTSTATS = db.reference('custstats')

currentLast = "5d7324c3322fa016762f2fce"


################################### Capital One Related Stuff #######################################

def getAllCustomers():
    url = 'http://api.reimaginebanking.com/customers?key={}'.format(capitalOneAPIKey)
    response = requests.get(url)
    json_data = json.loads(response.text)
    print(json_data)

    if response.status_code == 404:
	    print('Couldnt retrieve all the customer data')

def getLastCustomer():
    url = 'http://api.reimaginebanking.com/customers?key={}'.format(capitalOneAPIKey)
    response = requests.get(url)
    json_data = json.loads(response.text)
    return json_data[-1]['_id']

def createCustomerCapitalOne(firstName, lastName, email):
    url = 'http://api.reimaginebanking.com/customers?key={}'.format(capitalOneAPIKey)
    payload = {
    "first_name": firstName,
    "last_name": lastName,
    "address": {
        "street_number": "defaultNum",
        "street_name": "defaultStreetName",
        "city": "defaultCity",
        "state": "SC",
        "zip": "00000"
        }
    }

    response = requests.post( 
	    url, 
	    data=json.dumps(payload),
	    headers={'content-type':'application/json'},
	)
    print(response.status_code)
    if response.status_code == 201:
	    print('account created')

    custID = getLastCustomer()

    payload = {
        "custID" : custID,
        "first_name": firstName,
        "last_name": lastName,
        "email" : email,
        "total_credits" : "0",
        "current_balance": "0"
    }
    cust = CUSTSTATS.push(payload)


def getAllAccounts():
    url = 'http://api.reimaginebanking.com/accounts?key={}'.format(capitalOneAPIKey)
    response = requests.get(url)
    json_data = json.loads(response.text)
    print(json_data)


    if response.status_code == 404:
	    print('Couldnt retrieve all the account data')

def getAccountByID(accountID):
    url = 'http://api.reimaginebanking.com/accounts/{}?key={}'.format(accountID,capitalOneAPIKey)
    response = requests.get(url)
    json_data = json.loads(response.text)
    print(json_data)


    if response.status_code == 404:
	    print('Couldnt retrieve the account data')
    

def createCustAccount(customerID):
    url = 'http://api.reimaginebanking.com/customers/{}/accounts?key={}'.format(customerID,capitalOneAPIKey)
    payload = {
        "type": "Checking",
        "nickname": "CarbonCredits",
        "rewards": 0,
        "balance": 0,
    }
    response = requests.post( 
	    url, 
	    data=json.dumps(payload),
    	headers={'content-type':'application/json'},
	)

    if response.status_code == 201:
	    print('account created')

def addCustBalanceOne(accountID, amount):
    url = 'http://api.reimaginebanking.com/accounts/{}/deposits?key={}'.format(accountID,capitalOneAPIKey)
    payload = {
        "medium": "balance",
        "transaction_date": str(date.today()),
        "status": "completed",
        "amount" : amount,
        "description": "Amount earned in Carbon Credits"
    }
    response = requests.post( 
	    url, 
	    data=json.dumps(payload),
    	headers={'content-type':'application/json'},
	)
    if response.status_code == 201:
	    print('deposit made')
    else:
        print("Error in deposit")

def subCustBalanceOne(accountID,amount):
    url = 'http://api.reimaginebanking.com/accounts/{}/withdrawals?key={}'.format(accountID,capitalOneAPIKey)
    payload = {
        "medium": "balance",
        "transaction_date": str(date.today()),
        "status": "completed",
        "amount": amount,
        "description": "Amount of CC spent"
    }
    response = requests.post( 
	    url, 
	    data=json.dumps(payload),
    	headers={'content-type':'application/json'},
	)
    if response.status_code == 201:
	    print('spend made')
    else:
        print("Error in spend")


def viewCustBalance(customerID):
    url = 'http://api.reimaginebanking.com/customers/{}/accounts?key={}'.format(customerID,capitalOneAPIKey)
    response = requests.get(url)
    json_data = json.loads(response.text)
    print(json_data[0]['balance'])


    if response.status_code == 404:
	    print('Couldnt retrieve balance')
    return json_data[0]['balance']


def getAllMerchants():
    url = 'http://api.reimaginebanking.com/merchants?key={}'.format(capitalOneAPIKey)
    response = requests.get(url)
    json_data = json.loads(response.text)
    print(json_data)


    if response.status_code == 404:
	    print('Couldnt retrieve all the merchant data')

def createNewMerchant(name):
    url = 'http://api.reimaginebanking.com/merchants?key={}'.format(capitalOneAPIKey)
    payload = {
    "name": name,
    }

    response = requests.post( 
	    url, 
	    data=json.dumps(payload),
	    headers={'content-type':'application/json'},
	)
    print(response.status_code)
    if response.status_code == 201:
	    print('account created')
    else:
        print("failed to create merchant")

def getMerchantByID(merchantID):
    url = 'http://api.reimaginebanking.com/merchants/{}?key={}'.format(merchantID,capitalOneAPIKey)
    response = requests.get(url)
    json_data = json.loads(response.text)
    print(json_data)


    if response.status_code == 404:
	    print('Couldnt retrieve the merchant data')
    



if __name__ == "__main__":
    #createCustomerCapitalOne("Navyuh", "S", "24", "Songlin Lu", "Shanghai", "SH", "00780")
    #createCustomerCapitalOne("kertu", "k")
    createCustomerCapitalOne("Abucfewf", "ewefef", "arzvew68@nyu.edu")
    #createCustAccount("5d7393563c8c2216c9fcad2f")
    getAllCustomers()
    #getLastCustomer()
    #getAllAccounts()
    # createNewMerchant("vendorA")
    # getAllMerchants()
    # viewCustBalance("5d7393563c8c2216c9fcad2f")
    # getMerchantByID("5d7398573c8c2216c9fcad35")