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


## Installation

## Usage

You call the migrator on the command line specifying the parameters to the command:

    java -jar mongo-migrator-one-jar.jar migrate version_one mongodb://localhost:27017/cmd_db_test dbchanges/
    
(TODO - make a little shell wrapper script for this)
