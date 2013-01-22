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

parameters:

* versionId (optional) - this can be any arbitrary version number (eg a git commit hash)
* mongoUri - the uri to the mongo db
* script_paths* - 1 or more relative paths to the mongo script folder to use for the migration

### Rollback

### Versions

## Installation

## Usage

You call the migrator on the command line specifying the parameters to the command:

    java -jar mongo-migrator-one-jar.jar migrate version_one mongodb://localhost:27017/cmd_db_test dbchanges/
    
(TODO - make a little shell wrapper script for this)
