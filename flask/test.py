import requests, json

url = "http://127.0.0.1:5000/create-new-customer"
url1 = "http://127.0.0.1:5000/get-customer-data"
url2 = "http://127.0.0.1:5000/get-coupons"
url3 = "http://127.0.0.1:5000/get-customer-coupons"
url4 = "http://127.0.0.1:5000/add-balance"
url5 = "http://127.0.0.1:5000/make-purchase"

payload8 = {
    "cust_ID": "5d7476863c8c2216c9fcae8f",
    "id" : 1
}

res = requests.post(url5,data=json.dumps(payload8), headers={'Content-Type' : 'application/json'})
