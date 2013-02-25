function up(){
    print("---> up");
    db.mongo_migration_test.insert({ name: "Ed"});
}