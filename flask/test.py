import requests, json

url = "http://127.0.0.1:5000/create-new-customer"
url1 = "http://127.0.0.1:5000/get-customer-data"
url2 = "http://127.0.0.1:5000/get-coupons"

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

#res = requests.post(url, data=json.dumps(payload5), headers={'Content-Type' : 'application/json'})
#res1 = requests.post(url1, data=json.dumps(payload7), headers={'Content-Type' : 'application/json'})

res3 = requests.get(url2)