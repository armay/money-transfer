curl --header "Content-Type: application/json" \
     --request POST \
     --data '{ "internal": true, "senderPan": "1234567890123456", "receiverPan": "6123456789012345", "message": "Hello", "value": 10.00, "createdAt": "2018-09-17T03:45:15.057826Z[UTC]" }' \
http://localhost:8080/events
