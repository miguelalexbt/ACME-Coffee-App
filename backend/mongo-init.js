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

db.users.insert([
  {
    _id: '62fc8181-dfe4-4b3b-abe6-61175416486f',
    name: 'John Doe',
    username: 'johndoe',
    password: '$2b$10$IaPlqcuqYi3ZV1lPpFlHQuUIsMWKevQ9GHvjmslX3Q8j7BFMo.NRm'
  }
]);

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
  },
  {
    name: 'Latte',
    type: 'drink',
    price: 3.99
  },
  {
    name: 'Mochaccino',
    type: 'drink',
    price: 4.99
  },
  {
    name: 'Cappuccino',
    type: 'drink',
    price: 2.99
  },
  {
    name: 'Croissant',
    type: 'drink',
    price: 2.50
  },
  {
    name: 'Water',
    type: 'drink',
    price: 0.99
  },
  {
    name: 'Expresso',
    type: 'drink',
    price: 0.70
  }
]);
