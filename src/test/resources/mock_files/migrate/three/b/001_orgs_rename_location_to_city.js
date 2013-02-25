function up() {
    print("--->mongo: " + db.organizations.count({}));
    db.organizations.find({}).forEach(function (o) {
        o.city = o.Location;
        delete o.Location;
        db.organizations.save(o);
        printjson(o);
    });
}
