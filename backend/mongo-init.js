db.createUser(
    {
        user: 'user',
        pwd: 'Porto123*',
        roles: [
          { role: 'readWrite', db: 'app' }
        ]
    }
);

db.users.drop();
db.items.drop();
