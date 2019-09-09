import requests, json

url = "https://seecseec.appspot.com//create-new-customer"
url1 = "http://127.0.0.1:5000/get-customer-data"
url2 = "http://127.0.0.1:5000/get-coupons"
url3 = "http://127.0.0.1:5000/get-customer-coupons"
url4 = "http://127.0.0.1:5000/add-balance"
url5 = "http://127.0.0.1:5000/make-purchase"

payload8 = {
    "cust_ID": "5d74ef393c8c2216c9fcaf3c",
    "id" : 3
}

res = requests.post(url5,data=json.dumps(payload8), headers={'Content-Type' : 'application/json'})
