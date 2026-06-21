#!/bin/bash
set -e

BASE_URL="http://localhost:8088/api"
echo "=========================================================="
echo "          AnnDaan Backend Integration Test Flow"
echo "=========================================================="

echo -n "1. Registering Restaurant... "
REST_REG=$(curl -s -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "tasty_bites",
    "email": "contact@tastybites.com",
    "password": "password123",
    "role": "RESTAURANT",
    "name": "Tasty Bites Buffet",
    "phoneNumber": "+1234567890",
    "address": "456 Main St, Foodville",
    "latitude": 12.9716,
    "longitude": 77.5946
  }')
echo "Done."

echo -n "2. Registering Customer... "
CUST_REG=$(curl -s -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "cheap_eater",
    "email": "eater@anndaan.org",
    "password": "password123",
    "role": "CUSTOMER",
    "name": "John Doe",
    "phoneNumber": "+1987654321",
    "address": "789 Pine Rd, Foodville",
    "latitude": 12.9816,
    "longitude": 77.6046
  }')
echo "Done."

echo -n "3. Registering Rider... "
RIDER_REG=$(curl -s -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "speedy_rider",
    "email": "rider@anndaan.org",
    "password": "password123",
    "role": "RIDER",
    "name": "Delivery Agent Jack",
    "phoneNumber": "+1555444333",
    "address": "Rider Hub 1, Foodville",
    "latitude": 12.9616,
    "longitude": 77.5846
  }')
echo "Done."

echo -n "4. Logging in users to acquire JWT tokens... "
REST_TOKEN=$(curl -s -X POST "$BASE_URL/auth/login" -H "Content-Type: application/json" -d '{"username": "tasty_bites", "password": "password123"}' | jq -r .token)
CUST_TOKEN=$(curl -s -X POST "$BASE_URL/auth/login" -H "Content-Type: application/json" -d '{"username": "cheap_eater", "password": "password123"}' | jq -r .token)
RIDER_TOKEN=$(curl -s -X POST "$BASE_URL/auth/login" -H "Content-Type: application/json" -d '{"username": "speedy_rider", "password": "password123"}' | jq -r .token)
echo "Done."

echo -n "5. Restaurant posts leftover Veg Thali (Original: \$12.00, Discounted: \$3.00)... "
FOOD1_RESP=$(curl -s -X POST "$BASE_URL/food-items" \
  -H "Authorization: Bearer $REST_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Leftover Veg Thali",
    "description": "5 standard lunch thalis remaining from corporate event catering.",
    "originalPrice": 12.00,
    "discountedPrice": 3.00,
    "quantity": 5,
    "expiryTime": "2026-12-31T23:59:59",
    "pickupLocation": "Tasty Bites Buffet Back-gate",
    "latitude": 12.9716,
    "longitude": 77.5946
  }')
FOOD1_ID=$(echo "$FOOD1_RESP" | jq -r .id)
echo "Created Item ID: $FOOD1_ID"

echo -n "6. Restaurant posts leftover Cupcakes (Original: \$8.00, Discounted: \$2.00)... "
FOOD2_RESP=$(curl -s -X POST "$BASE_URL/food-items" \
  -H "Authorization: Bearer $REST_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Fresh Dessert Cupcakes",
    "description": "Excess cupcakes from today'\''s batch.",
    "originalPrice": 8.00,
    "discountedPrice": 2.00,
    "quantity": 10,
    "expiryTime": "2026-12-31T23:59:59",
    "pickupLocation": "Tasty Bites Buffet Back-gate",
    "latitude": 12.9716,
    "longitude": 77.5946
  }')
FOOD2_ID=$(echo "$FOOD2_RESP" | jq -r .id)
echo "Created Item ID: $FOOD2_ID"

echo ""
echo "7. Customer queries active leftover items:"
curl -s -X GET "$BASE_URL/food-items/available" -H "Authorization: Bearer $CUST_TOKEN" | jq .

echo ""
echo -n "8. Customer places order for 2 Veg Thalis and 3 Cupcakes... "
ORDER_RESP=$(curl -s -X POST "$BASE_URL/orders" \
  -H "Authorization: Bearer $CUST_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {"foodItemId": "'$FOOD1_ID'", "quantity": 2},
      {"foodItemId": "'$FOOD2_ID'", "quantity": 3}
    ],
    "deliveryAddress": "Flat 302, Green Apartments",
    "latitude": 12.9816,
    "longitude": 77.6046
  }')
ORDER_ID=$(echo "$ORDER_RESP" | jq -r .id)
TOTAL_AMOUNT=$(echo "$ORDER_RESP" | jq -r .totalAmount)
echo "Created Order ID: $ORDER_ID (Total Amount: \$$TOTAL_AMOUNT)"

echo -n "9. Customer pays for the order... "
PAY_RESP=$(curl -s -X POST "$BASE_URL/orders/$ORDER_ID/pay" -H "Authorization: Bearer $CUST_TOKEN")
echo "Paid. Status: $(echo "$PAY_RESP" | jq -r .status)"

echo ""
echo "10. Rider lists available paid deliveries:"
curl -s -X GET "$BASE_URL/deliveries/available" -H "Authorization: Bearer $RIDER_TOKEN" | jq .

echo ""
echo -n "11. Rider claims the delivery... "
CLAIM_RESP=$(curl -s -X POST "$BASE_URL/deliveries/$ORDER_ID/claim" -H "Authorization: Bearer $RIDER_TOKEN")
DELIVERY_ID=$(echo "$CLAIM_RESP" | jq -r .id)
echo "Claimed. Delivery ID: $DELIVERY_ID. Status: $(echo "$CLAIM_RESP" | jq -r .status)"

echo -n "12. Rider updates location... "
curl -s -X POST "$BASE_URL/deliveries/$DELIVERY_ID/location" \
  -H "Authorization: Bearer $RIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"latitude": 12.9740, "longitude": 77.5980}' > /dev/null
echo "Updated."

echo -n "13. Rider marks delivery as PICKED_UP... "
PICK_RESP=$(curl -s -X POST "$BASE_URL/deliveries/$DELIVERY_ID/pickup" -H "Authorization: Bearer $RIDER_TOKEN")
echo "Picked up. Order Status: $(echo "$PICK_RESP" | jq -r .orderStatus)"

echo -n "14. Rider marks delivery as DELIVERED... "
DELIV_RESP=$(curl -s -X POST "$BASE_URL/deliveries/$DELIVERY_ID/deliver" -H "Authorization: Bearer $RIDER_TOKEN")
echo "Delivered! Order Status: $(echo "$DELIV_RESP" | jq -r .orderStatus)"

echo ""
echo "15. Customer views final delivery tracking receipt:"
curl -s -X GET "$BASE_URL/deliveries/$DELIVERY_ID" -H "Authorization: Bearer $CUST_TOKEN" | jq .

echo ""
echo "16. Negative security checks:"
echo -n " - Customer tries to post food (expecting HTTP 403)... "
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/food-items" \
  -H "Authorization: Bearer $CUST_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title": "Illegal", "originalPrice": 10.00, "discountedPrice": 0.50, "quantity": 1, "expiryTime": "2026-12-31T23:59:59", "pickupLocation": "X"}')
echo "Response Status: $HTTP_CODE"

if [ "$HTTP_CODE" -eq 403 ]; then
  echo "Security check PASSED."
else
  echo "Security check FAILED."
  exit 1
fi

echo "=========================================="
echo "          Tests completed successfully!"
echo "=========================================="
