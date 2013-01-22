db.users.find().forEach(function(u){
    u.firstName = u.name;
    delete u.name;
    db.users.save(u);
});

//Down
db.users.find().forEach(function(u){
    u.name = u.firstName;
    delete u.firstName;
    db.users.save(u);
});

