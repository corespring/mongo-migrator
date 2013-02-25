function up() {
    db.organizations.find({city: "NYC"}).forEach(function(o){
        o.city = "New York";
        db.organizations.save(o);
    });
}