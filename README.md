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
  .inCollections("people")
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

# Development

Tested with
* Marklogic 8.0.4
* Java 8

Assuming you have a Marklogic instance running on localhost:8010 (user/pass: admin/admindev), run :

``` shell
mvn clean verify
```

# TODO

* [ ] Writer for JSON
* [ ] Parameter validation (not nulls...)
* [ ] Parameters in `Transform`
* [ ] In `WriteOperation`, optional fields could be `Optional` instead of null

# DONE

* [x] Working DSL
* [x] String/Text writer
* [x] Transaction open/commit/rollback
* [x] In transaction, add the `HostId` cookie for load-balancing
* [x] Handle erroneous transaction response

# Future

* XML Support
* Config builder
* Choice of HTTP client implementation : not only Apache's HttpClient, consider using Google http-java-client to wrap other clients
* Select or build a Triples/RDF implementation (DIY, Jena, Sesame, Commons-rdf...)
* Fluent reading