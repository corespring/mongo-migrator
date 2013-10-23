# Mongo Migrator

A command line utility for migrating your mongo database.

## Why

There are other projects out there doing something similiar eg: 

* https://github.com/secondmarket/mongeez
* https://github.com/srohde/MongoUpgrade

However these utilities appear to be more suited to seeding a database for use in development - not
a db in production.


## Commands

### Migrate
Migrates the database

    mongo-migrator migrate [version_id] mongo_uri script_path_1 script_path2 ...

parameters:

* version_id (optional) - this can be any arbitrary version number (eg a git commit hash)
* mongo_uri - the uri to the mongo db
* script_paths* - 1 or more relative paths to the mongo script folder to use for the migration

What happens:
The script inspects the db to see if any scripts have already been run from the given file folder. Any that have been run are excluded from this migration.
The scripts are run and once complete a new version is saved with the scripts that were run.

### Versions
   
    mongo-migrator versions mongo_uri 
    
Prints out the versions in this db (with a version id if you specified one)

### Rollback

    mongo-migrator rollback version_id|object_id mongo_uri script_path_1 script_path2 ...

parameters:

* version_id|object_id - this can either by an arbitrary version id or an object id (you can see whats available by running versions).
* mongo_uri - the mongo uri
* script_path* - the relative paths to the script folder

What happens: 

The script finds the version specified, collates all the scripts from later versions and runs the *down* portion of each script in reverse order.

### Synch scripts

    mongo-migrator synch target versionId path_to_scripts
    //eg
    //synch the files to match whats in the db
    mongo-migrator synch files 1 scripts/
    //synch the db to match whats in the files
    mongo-migrator synch db 1 scripts/

Resynchs the contents in the db or local filesystem to be inline with the contents from the other source.


## Mongo URIs and Replica sets

If you need to work with a replica set dbs use the following format as the uri:

    my-replica-set|mongodb://server-one:111,server-two:2222/db

    aka

    replica_set|mongo_uri


## Script format
The migrator uses a simple convention for running up and down portions of the script.
You just need to define a function named 'up' or 'down' that takes no parameters.
Note: the 'down' function is optional - but the 'up' function isn't.

A up and down script:

    var x = "y";
    function up(a){ alert(x); }
    function down(a){ alert(x); }
    
    
## Differences between Files and Scripts
If when you run rollback - if there is a discrepency between the script contents in the db and the script contents in the local file, the command line will fail.


## Installation

At the moment we build a one-jar and use it with the command line.
Publishing/Hosting is do be done..

## Developing
you'll need to have sbt installed.
 
    sbt assembly # to build an all in one jar
    sbt compile # to compile
     

### Running tests

    sbt test

By default the tests run against a local single mongo db.
If you want to run against a replica set db you'll need to set the following env var: `MONGO_MIGRATOR_TEST_DB_URI`

There is a script here that will help you quickly set up a replica set:
https://github.com/edeustace/mongo-db-utils/tree/master/integration-test-env


### Release Notes:
- 0.2.2 : Fixed issue where Script equality wasn't working correctly.
- 0.2 : Added support for replica sets
- 0.1 : First version

