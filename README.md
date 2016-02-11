# Marklogic Fluent Client

**Status: Work-In-Progress - Proof of concept**

Marklogic Fluent Client is a simpler way to write and read documents to and from Marklogic Database.

The official Marklogic Java Client has more than a dozen of methods to write and reads documents, 
mainly because it has to handle multiple optional parameters, like a transaction, a transformation, metadatas...

This project is a java fluent DSL on top of Marklogic REST Api for reading and writing documents. 

## Examples

Write :

``` java
client.write(person).toUri("/people/1.json").asJson();
```

Optional parameters :

``` java
client
  .write(person)
  .toUri("/people/1.json")
  .inCollection("people")
  .transformedBy(myTransform)
  .withTriples(rdfTriples)
  .inTransaction(tran)
  .asJson();
```

Writes in the same transaction :

``` java
new Client().inTransaction((client, tran) -> {
    client.write("{}").toUri("/foo").asString();
    client.write("{}").toUri("/bar").asString();
});
```
# TODO

* [ ] Configuration `Client` 
    * [ ] Writer for XML
    * [ ] Writer for JSON
    * [ ] Choice of HTTP client implementation : not only Apache's HttpClient, consider using Google http-java-client to wrap other clients
* [ ] Handle parameters in `Transform`
* [ ] Build/Choice of a Triples/RDF implementation (DIY, Apache Jena, Commons-rdf... ?)