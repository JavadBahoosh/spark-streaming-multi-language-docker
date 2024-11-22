FROM python:3.9-slim

# Set unbuffered mode for Python (disables output buffering)
ENV PYTHONUNBUFFERED=1

# Copy the Python script
COPY producer.py /producer.py

# Install dependencies
RUN pip install --no-cache-dir kafka-python redis

# Set the entrypoint to run the producer
ENTRYPOINT ["python", "/producer.py"]
