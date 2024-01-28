#!/bin/bash

echo "Waiting for MongoDB to be ready..."
until mongo --eval "printjson(rs.isMaster())" | grep ismaster | grep true > /dev/null 2>&1; do
  echo "Waiting for MongoDB to be ready..."
  sleep 5
done

echo "MongoDB is ready. Initializing the replica set..."

# Initialize the replica set
mongo --eval 'rs.initiate(
  {
    _id: "rs0",
    members: [
      { _id: 0, host: "mongodb1:27017" },
      { _id: 1, host: "mongodb2:27017" },
      { _id: 2, host: "mongodb3:27017" }
    ]
  }
)'
