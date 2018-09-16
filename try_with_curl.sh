curl --header "Content-Type: application/json" \
     --request POST \
     --data '{ "id": "b6148e9fa8d36d628753f2705e0be39f33b2f863", "internal": true, "senderPan": "1234567890123456", "receiverPan": "6123456789012345", "message": "Hello", "value": 10.00, "createdAt": "2018-09-16T17:42:42.264966Z[UTC]" }' \
http://localhost:8080/events
