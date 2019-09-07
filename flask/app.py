from flask import Flask
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



def getNewLastCustomer():
    url = 'http://api.reimaginebanking.com/customers?key=f990b904d48c2277e4b75f9ddd8ed3c9'
    response = requests.get(url)
    json_data = json.loads(response.text)
    print(json_data)
    #When we need to add a new customer to the database, we need to assign an ID. To do this, we first need the ID of the current last customer and add one to it  
    currentLast = str(hex(int(("0x" + json_data[-1]['_id']),16)+1))[2:]

    if response.status_code == 404:
	    print('Couldnt retrieve all the customer data')
    return currentLast
    

def createCustomerCapitalOne(firstName, lastName, stNo, stName, city, stateCode,zipCode):
    customerID = getNewLastCustomer()
    print(customerID)
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
    # getNewLastCustomer()
    # createCustomerCapitalOne("Navya", "Suri", "42", "Jinqiao Road", "Shanghai", "SH", "10003")
    # getAllCustomers()
    #createCustAccount("5d7324c3322fa016762f2fce")
    #getAllAccounts()
    getAccountByID("5d735e383c8c2216c9fcac5d")