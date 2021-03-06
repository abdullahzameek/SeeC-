import flask
from flask import request
from datetime import date
import firebase_admin
from firebase_admin import db
from firebase_admin import credentials

import requests
import json

capitalOneAPIKey = '' #did stuff
app = flask.Flask(__name__)

cred = credentials.Certificate("seec-pennapps-firebase-adminsdk-7zh6f-41c2a963c2.json")
firebase_admin.initialize_app(cred, {
    'databaseURL' : 'https://seec-pennapps.firebaseio.com/'
})

CUSTSTATS = db.reference('custstats')
COUPONS = db.reference('coupons')

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

@app.route("/create-new-customer", methods=['POST'])
def createCustomerCapitalOne():
    print(request.json)
    # for key, val in request.json: print(key, val)
    firstName = request.json['first_name']
    email = request.json['email']
    url = 'http://api.reimaginebanking.com/customers?key={}'.format(capitalOneAPIKey)
    payload = {
    "first_name": firstName,
    "last_name": " ",
    "address": {
        "street_number": "string",
        "street_name": "string",
        "city": "string",
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
    createCustAccount(custID)

    payload = {
        "custID" : custID,
        "first_name": firstName,
        "last_name": " ",
        "coupons" : [{
            "id":5,
            "code":"ki98GTB56fg",
            "image":"https://firebasestorage.googleapis.com/v0/b/seec-pennapps.appspot.com/o/Screenshot%20from%202019-09-08%2001-43-55%20Cropped%20(2).png?alt=media&token=c54cb559-0d4e-483c-850b-f234da18b4f2",
            "message":"SIGN UP BONUS! Have 30% off on us!",
            "price":11,
            "vendor":"Starbucks"
        }],
        "email" : email,
        "total_credits" : "0",
        "current_balance": "0"     
    }

    cust = CUSTSTATS.push(payload)
    return custID

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
    #print(json_data)


    if response.status_code == 404:
	    print('Couldnt retrieve the account data')
    return(json_data)

def getCustfromAcc(accountID):
    res = getAccountByID(accountID)
    return res['customer_id']


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

@app.route("/get-customer-data", methods=['POST'])
def getCustomerData():
    print(request.json)
    custID = request.json['cust_ID']
    response = CUSTSTATS.order_by_child('custID').equal_to(custID).get()
    for key, value in response.items():
        print(value)
    return(value)   

@app.route("/get-coupons", methods=['GET'])
def getCoupons():
    response = COUPONS.get()
    return(json.dumps(response))

@app.route("/get-customer-coupons", methods=['GET', 'POST'])
def getCustomerCoupons():
    print(request.json)
    custID = request.json['cust_ID']
    response = CUSTSTATS.order_by_child('custID').equal_to(custID).get()
    print(response)
    for key, value in response.items(): 
        coupons = value['coupons']
    return(json.dumps(coupons))


def getFireBaseID(custID):
    fireBaseID = ""
    response = CUSTSTATS.order_by_child('custID').equal_to(custID).get()
    for key, value in response.items(): 
        fireBaseID = key
        print("the key is ",key)
    return fireBaseID


def getAccountID(custID):
    url = 'http://api.reimaginebanking.com/customers/{}/accounts?key={}'.format(custID,capitalOneAPIKey)
    response = requests.get(url)
    json_data = json.loads(response.text)
    print(json_data[0]['_id'])

    if response.status_code == 404:
	    print('Couldnt retrieve ID')
    return json_data[0]['_id']

@app.route("/add-balance", methods=['POST'])
def addCustBalanceOne():
    print(request.json)
    custID = request.json['cust_ID']
    amount = request.json['amount']

    accountID = getAccountID(custID)

    url = 'http://api.reimaginebanking.com/accounts/{}/deposits?key={}'.format(accountID,capitalOneAPIKey)
    payload = {
        "medium": "balance",
        "transaction_date": str(date.today()),
        "status": "completed",
        "amount" : int(amount),
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
    
    custID = getCustfromAcc(accountID)
    print("the customer id is ", custID)
    fireBaseID = getFireBaseID(custID)
    print("the firebase id is ", fireBaseID)
    

    response = CUSTSTATS.order_by_child('custID').equal_to(custID).get()
    print(response)
    for key, value in response.items(): 
        cur_bal = value['current_balance']
        print("the current balance is ",value['current_balance'])
        finalamount = int(amount) + int(cur_bal)
    CUSTSTATS.child(fireBaseID).update({"current_balance": str(finalamount)})
    CUSTSTATS.child(fireBaseID).update({"total_credits": str(finalamount)})
    #response = CUSTSTATS.order_by_child('custID').equal_to(custID).get()
    # print(response)
    return("Adding to customer account")


def getCouponValue(couponID):
    response = COUPONS.order_by_child('id').equal_to(couponID).get()
    print(response)
    for key, value in response.items(): 
        v = value['price']
    return(v)

def getCoupon(couponID):
    response = COUPONS.order_by_child('id').equal_to(couponID).get()
    print("This is rhe repsonse")
    i = str(next(iter(response)))
    print(response[i])

    return response[i]


@app.route("/make-purchase", methods=['POST'])
def subCustBalanceOne():
    print(request.json)
    custID = request.json['cust_ID']
    couponID = request.json['id']
    print("The coupon code is ", couponID)
    amount = int(getCouponValue(couponID))
    print("The amount code is ", amount)
    accountID  = getAccountID(custID)
    newCoupon = getCoupon(couponID)
    print("The new coupon code is ", newCoupon)

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
    
    custID = getCustfromAcc(accountID)
    print("the customer id is ", custID)
    firebaseId = getFireBaseID(custID)
    #print("the firebase id is ", fireBaseID)
    

    response = CUSTSTATS.order_by_child('custID').equal_to(custID).get()
    print(response)
    for key, value in response.items(): 
        cur_bal = value['current_balance']
        coupons = value['coupons']
        coupons.append(newCoupon)
        print("the current balance is ",value['current_balance'])
        finalamount = int(cur_bal) - int(amount)
    CUSTSTATS.child(firebaseId).update({"current_balance": str(finalamount)})
    CUSTSTATS.child(firebaseId).update({"coupons": coupons})

    return("Subtracted and added coupons")

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
    



#if __name__ == "__main__":
#    createCustomerCapitalOne("Abdullah", "Zameek", "arz268@nyu.edu")
#     #createCustAccount("5d7413923c8c2216c9fcadfe")
#    getAllAccounts()
#     #addCustBalanceOne("5d7413b23c8c2216c9fcae00", 100000)
#     # addCustBalanceOne("5d7413b23c8c2216c9fcae00", 150000)
#     # addCustBalanceOne("5d74182c3c8c2216c9fcae1b", 250000)
#     createCustAccount("5d7414453c8c2216c9fcae04")
#     addCustBalanceOne("5d74182c3c8c2216c9fcae1b", 475000)
#     subCustBalanceOne("5d74182c3c8c2216c9fcae1b", 250000)
#     addCustBalanceOne("5d74182c3c8c2216c9fcae1b", 2500)
#     subCustBalanceOne("5d74182c3c8c2216c9fcae1b", 35000)
#getAllAccounts()
# if __name__ == "__main__":
#     getCoupons()
