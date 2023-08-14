
curl -X POST http://localhost:8080/api/v1/orders/$1 -H 'accept: application/json' -H 'Content-Type: application/json' \
  -d '{ "customerID": "C01", "productID": "P01", "quantity": 10,  "destinationAddress": { "street": "1st horizon road", "city": "Hong Kong", "country": "HK", "zipcode": "95051" },"pickupAddress": { "street": "1st main street", "city": "San Francisco", "country": "USA", "state": "CA", "zipcode": "95051" }}'

