# used to create multiple jobs for demo purposes

{
  for i in {1..5}; do
    curl -s "https://randomuser.me/api/?results=5000&inc=email&noinfo" \
      | grep -o '"email":"[^"]*"' | cut -d'"' -f4
  done
} | while read email; do
  curl -s -X POST "http://localhost:8080/subscribe?email=$email" > /dev/null
  if (( RANDOM % 10 < 9 )); then
    curl -s -X POST "http://localhost:8080/confirm?email=$email" > /dev/null
  fi
done
echo "Done"