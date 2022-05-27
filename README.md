[![build](https://github.com/bridgedb/bridgedb-webservice/actions/workflows/build.yml/badge.svg)](https://github.com/bridgedb/bridgedb-webservice/actions/workflows/build.yml)
[![docker stars](https://img.shields.io/docker/stars/bigcatum/bridgedb.svg?style=flat-square)](https://hub.docker.com/r/bigcatum/bridgedb)
[![docker pulls](https://img.shields.io/docker/pulls/bigcatum/bridgedb.svg?style=flat-square)](https://hub.docker.com/r/bigcatum/bridgedb)

# BridgeDb Webservice

The BridgeDb Webservice provides a REST service to access identifier mapping data. It uses the [BridgeDb Java](https://github.com/bridgedb/bridgedb)
library and RESTlet technologies to make the webservice available. There is a [BridgeDb Docker](https://github.com/bridgedb/docker) available for easy use.

## Compiling the webservice

A jar with all dependencies is created with the following command:

```shell
mvn clean package
```

### Downloading BridgeDb ID mapping databases

BridgeDb ID mapping databases are found on [this website](https://bridgedb.github.io/data/gene_database/).
The location of the downloaded files is needed for the below described `gdb.config` file.

### Installing the Derby libraries

Because when embedding Derby in the BridgeDb Server jar makes them impossible to find,
you still need them locally. Copy the Derby jars (derbyclient.jar) to this
folder.

### Starting the webservice

The service can be run with (only HTTP is supported):

```shell
sh startServer.sh
```

Run the server at a different port with:

```shell
sh startServer.sh -p 8082
```

This will look for a local `gdb.config` file that has a tab-separated values content, linking species
to BridgeDb Derby files, for example:

```
Human   /path/to/databases/Hs_Derby_Ensembl_105.bridge
```

### Testing the webservice

Test the service by asking for the properties of a species for which you downloaded ID mapping
databases, e.g. http://localhost:8183/Human/properties
