import random
import time
import json
from kafka import KafkaProducer

def create_kafka_producer():
    """Create a Kafka producer with JSON serialization."""
    try:
        producer = KafkaProducer(
            bootstrap_servers=['kafka:9092'],  # Specify as a list
            value_serializer=lambda v: json.dumps(v).encode('utf-8')
        )
        print("Kafka producer created successfully.")
        return producer
    except Exception as e:
        print(f"Error creating Kafka producer: {e}")
        raise

def main():
    """Main function to produce random data to Kafka."""
    producer = create_kafka_producer()

    while True:
        try:
            # Generate random data
            data = {'id': random.randint(1, 100), 'value': random.randint(1, 1000)}

            # Send data to the Kafka topic
            producer.send('random-data', value=data)
            print(f"Sent: {data}")

            # Sleep for 1 second
            # time.sleep(1)
        except Exception as e:
            print(f"Error while sending data: {e}")
            break

if __name__ == "__main__":
    main()
