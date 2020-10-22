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

db.items.insertMany([
  {
    name: 'Sandwich',
    type: 'food',
    price: 4.5
  },
  {
    name: 'Pastry',
    type: 'food',
    price: 2.3
  },
  {
    name: 'Coffee',
    type: 'drink',
    price: 0.6
  },
  {
    name: 'Coca-Cola',
    type: 'drink',
    price: 1.0
  }
]);
