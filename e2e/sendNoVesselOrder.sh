curl -X POST http://localhost:8080/api/v1/orders -H 'accept: application/json' -H 'Content-Type: application/json' \
  -d '{ "customerID": "C03", "productID": "P16", "quantity": 10, "pickupDate": "2023/9/1", "destinationAddress": { "street": "1st horizon road", "city": "Hong Kong", "country": "HK", "state": "S1", "zipcode": "95051" },"pickupAddress": { "street": "1st main street", "city": "San Francisco", "country": "USA", "state": "CA", "zipcode": "95051" }}'