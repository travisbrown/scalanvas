Scalanvas
=========

Scalanvas is a Scala library for creating
[Shared Canvas](http://www.shared-canvas.org/)
manifests from other document representations (TEI, etc.).
It's built on the [W3C](http://www.w3.org/)'s
[banana-rdf](https://github.com/w3c/banana-rdf) library,
and uses [jsonld-java](https://github.com/jsonld-java/jsonld-java)
for [JSON-LD](http://json-ld.org/) serialization.

Setup
-----

Once you've checked out this repository, all you need is an installation
of the Java Runtime Environment (version 6 or newer). Running `./sbt`
and then typing `project scalanvas-core` in the console will load a shell
that will allow you to interactively use these libraries.

Usage
-----

See the [`edu.umd.mith.sga.frankenstein.Builder`](https://github.com/umd-mith/scalanvas/blob/master/core/src/main/scala/sga/frankenstein/builder.scala)
object for sample programmatic usage.

Configuration
-------------

Copy the `./examples/config/application.conf` file to `./core/src/main/resources/`
and edit it to reflect the paths to the TEI directories on your local system.

Temporary Whitman Archive Manifest Generation Instructions
-----------------------------------------------------------------

Run `./sbt assembly` to compile the project, edit `shelfmarks/wwa.txt` as desired,
and then run the application:

``` bash
java -jar core/target/scala-2.10/scalanvas-core-assembly-0.0.0-SNAPSHOT.jar shelfmarks/wwa.txt
```

The manifests will be generated in the `output` directory.

Temporary Shelley-Godwin Archive Manifest Generation Instructions
-----------------------------------------------------------------

If you just need to build the Shelley-Godwin Archive manifests,
see the `builder.scala` file linked in the preceding subsection.

Run `./sbt` to launch the SBT console.
At the prompt, type `project scalanvas-core`, and then `run`. You'll
be asked whether you want the development or production manifests:

```
> run

Multiple main classes detected, select one to run:

[1] edu.umd.mith.sga.frankenstein.DevelopmentBuilder
[2] edu.umd.mith.sga.frankenstein.ProductionBuilder

Enter number:
```

Type `1` or `2` as appropriate and hit enter. After a few minutes you'll have your
files in the `./output` directory.

For production we want to compact the JSON by removing unnecessary
whitespace and then compress the files with Gzip to allow Nginx to
avoid compressing the files on every request. This process is taken
care of by the `scripts/compact.sh` script, which you can run from the
project root, and which will give you a `compact.tgz` file ready for
deployment.

I know this process is a mess, but we're under the gun, and it'll be
easy to streamline in the near future.

Experimental
------------

This repository also includes
[a demonstration](https://github.com/umd-mith/scalanvas/blob/master/generators/src/main/scala/generators/PrefixGenerator.scala)
of how Scala 2.10's
macro system can be used to parse RDF schemas at compile time to produce
Scala bindings. So, for example, instead of writing something like this:

``` scala
class OREPrefix[Rdf <: RDF](ops: RDFOps[Rdf])
  extends PrefixBuilder("ore", "http://www.openarchives.org/ore/terms/")(ops) {
  val aggregates = apply("aggregates")
  val describes = apply("describes")
  val isDescribedBy = apply("isDescribedBy")
  val Aggregation = apply("Aggregation")
  val ResourceMap = apply("ResourceMap")
  // And so on...
}

object OREPrefix {
  def apply[Rdf <: RDF](implicit ops: RDFOps[Rdf]) = new OREPrefix[Rdf](ops)
}
```

And then eventually:

``` scala
val ore = OREPrefix[Rdf]
```

We can just write the following:

``` scala
val ore = PrefixGenerator.fromSchema(
  "http://www.openarchives.org/ore/terms/",
  "ore",
  "/edu/umd/mith/scalanvas/schemas/oreterms.rdf"
)
```

Now the RDF schema at the indicated resource path will be parsed at
compile time, and we'll have an anonymous subclass of `Prefix` with
all of the members of our `OREPrefix` above added for us. This approach
is less tedious, less error-prone, and much easier to maintain in the
case of changing schemas.

