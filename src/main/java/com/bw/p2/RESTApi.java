package com.bw.p2;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RESTApi
{

    MongoClient m_mongoClient = null;
    MongoDatabase m_testDB = null;

    void connect()
    {
        if(m_mongoClient == null)
        {
            m_mongoClient = new MongoClient(new MongoClientURI("mongodb://p2db1:9AVX1Al9RkkoGKxxrjibBPmB5uQmnYprnqjL77M1dCik9zV3sD9eJArH0iuwckHsKGZ19wyucn71l1vNp5QofA==@p2db1.mongo.cosmos.azure.com:10255/?ssl=true&retrywrites=false&replicaSet=globaldb&maxIdleTimeMS=120000&appName=@p2db1@"));
            m_testDB = m_mongoClient.getDatabase("sample-database");
        }
    }

	@GetMapping("/delete_data")
	public String delete_data(
        @RequestParam(value = "collection", defaultValue = "default_collection") String collection
    ) {
        connect();

        MongoCollection<Document> numbersCollection = m_testDB.getCollection(collection);
        numbersCollection.drop();

        return "deleted collection " + collection;
    }

	@GetMapping("/get_data")
	public String get_data(
        @RequestParam(value = "msg", defaultValue = "") String msg,
        @RequestParam(value = "collection", defaultValue = "default_collection") String collection
    ) {
        connect();

        MongoCollection<Document> numbersCollection = m_testDB.getCollection(collection);

        if(msg.length() > 0)
        {
            Document doc = new Document("msg", msg);
            numbersCollection.insertOne(doc);
        }

        MongoCursor<Document> cursor = numbersCollection.find().iterator();

        String output = "";
        try {
            while (cursor.hasNext()) {
                output += cursor.next().toJson();
            }
        } finally {
            cursor.close();
        }

        return output;
    }
}