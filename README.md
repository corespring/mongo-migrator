# Mongo Migrator

A command line utility for migrating your mongo database.

## Why

There are other projects out there doing something similiar eg: mongeez and
https://github.com/srohde/MongoUpgrade

However these utilities appear to suited to seeding a database for use in development - not
a db in production.


## Commands

### Migrate

### Rollback

### Versions

## Installation

## Usage

You call the migrator on the command line specifying the parameters to the command:

    java -jar mongo-migrator-one-jar.jar migrate version_one mongodb://localhost:27017/cmd_db_test dbchanges/
    
(TODO - make a little shell wrapper script for this)
