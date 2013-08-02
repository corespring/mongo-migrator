function up(){

  db.orgs.find().forEach(function(o){
      o.organization = o.org;
      delete o.org;
      db.orgs.save(o);
  });

  //Down
  db.orgs.find().forEach(function(o){
      o.org = o.organization;
      delete o.org;
      db.orgs.save(o);
  });
}
