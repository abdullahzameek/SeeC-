import requests, json

url = "https://seecseec.appspot.com/create-new-customer"
url1 = "http://127.0.0.1:5000/get-customer-data"
url2 = "http://127.0.0.1:5000/get-coupons"
url3 = "http://127.0.0.1:5000/get-customer-coupons"
url4 = "http://127.0.0.1:5000/add-balance"
url5 = "http://127.0.0.1:5000/make-purchase"

payload8 = {
    "first_name": "fqefq",
    "email" : "fqef@f.v"
}

res = requests.post(url,data=json.dumps(payload8), headers={'Content-Type' : 'application/json'})
