echo "######################"
echo " Verify Vessels topic content"


docker  exec -ti kafka bash -c "/opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic vessels --from-beginning"