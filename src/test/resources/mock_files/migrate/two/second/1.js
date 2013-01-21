db.migrateTest.find().forEach(function(n){
    n.firstName = n.name;
    delete n.name;
    db.migrateTest.save(n);
});