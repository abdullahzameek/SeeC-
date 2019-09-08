import requests, json

url = "http://127.0.0.1:5000/create-new-customer"
url1 = "http://127.0.0.1:5000/get-customer-data"
url2 = "http://127.0.0.1:5000/get-coupons"
url3 = "http://127.0.0.1:5000/get-customer-coupons"
payload5 = {
    "first_name": "Abdullah Zameek", 
    "email": "arz@nyu.edu"
}

payload6 = {
    "first_name": "Navya Suri", 
    "email": "ns@nyu.edu"
}

payload7 = {
    "cust_ID": "5d746e463c8c2216c9fcae7e"
}

payload8 = {
    "cust_ID": "5d747a0a3c8c2216c9fcae94"
}

#res = requests.post(url, data=json.dumps(payload5), headers={'Content-Type' : 'application/json'})
# res1 = requests.post(url, data=json.dumps(payload6), headers={'Content-Type' : 'application/json'})

# res3 = requests.get(url2)

res = requests.get(url3,data=json.dumps(payload8), headers={'Content-Type' : 'application/json'})
