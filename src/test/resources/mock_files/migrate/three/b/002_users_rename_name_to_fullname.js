function up() {
    db.users.find().forEach(function (u) {
        u.fullName = u.Name;
        delete u.Name;
        db.users.save(u);
    })
}