from flask import Flask
from datetime import date
import requests
import json

capitalOneAPIKey = 'f990b904d48c2277e4b75f9ddd8ed3c9'
app = Flask(__name__)
currentLast = "5d7324c3322fa016762f2fce"

################################### Capital One Related Stuff #######################################

def getAllCustomers():
    url = 'http://api.reimaginebanking.com/customers?key=f990b904d48c2277e4b75f9ddd8ed3c9'
    response = requests.get(url)
    json_data = json.loads(response.text)
    print(json_data)


    if response.status_code == 404:
	    print('Couldnt retrieve all the customer data')

def getAllAccounts():
    url = 'http://api.reimaginebanking.com/accounts?key=f990b904d48c2277e4b75f9ddd8ed3c9'
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
    

def createCustomerCapitalOne(firstName, lastName, stNo, stName, city, stateCode,zipCode):
    url = 'http://api.reimaginebanking.com/customers?key={}'.format(capitalOneAPIKey)
    payload = {
    "first_name": firstName,
    "last_name": lastName,
    "address": {
        "street_number": stNo,
        "street_name": stName,
        "city": city,
        "state": stateCode,
        "zip": zipCode
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


##################################### Firebase Stuff ###################################################

def createCustomerFB():
    pass

def addCustMiles():
    pass



if __name__ == "__main__":
    #createCustomerCapitalOne("Navya", "Suri", "42", "Jinqiao", "Shanghai", "SH", "00780")
    #createCustAccount("5d7393563c8c2216c9fcad2f")
    #getAllCustomers()
    #getAllAccounts()
    viewCustBalance("5d7393563c8c2216c9fcad2f")