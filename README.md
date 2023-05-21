Assumptions
--------

This is checkout-service, which is responsible for calculation final order price 
according to promotional rules:
* If you spend over £60, then you get 10% off your purchase
* If you buy 2 or more travel card holders then the price drops to £8.50.

Here is sample of the products used in implementation:
 
    Product code | Name                   | Price
    -------------------------------------------------------
    001          | Travel Card Holder     | £9.25
    002          | Personalised cufflinks | £45.00
    003          | Kids T-shirt           | £19.95
    -------------------------------------------------------

Here are some assumptions I have made during implementation:

1) We want our discounts application logic work consistent, 
which means we always want to get same final price with same
items list in a basket (for example after application of the 
rule "2 or more travel card holders" cost of items in total
can become less than £60 pounds and second promotional rule
condition won't be met, but if we apply rules in different order,
customer will be able to get 10% off the purchase).
In this case promotional rules should
be applied with some priority to avoid inconsistency.
I assume we want to provide minimal possible discount for 
the customer, so that I will set higher priority for 
"2 or more travel card holders" and lower priority for the
rule "spend over £60, then you get 10% off".

2) I assume that idea of test was to concentrate on service layer, so the
focus here should be on modelling the domain in a maintainable and flexible way.
This is why I haven't implemented any View or Persistence layers.
If I had to, I would use spring MVC, Spring Boot, Spring JPA. I would store
Product metadata (together with User, Order etc) in relational DB,
product pictures in something like S3, Promotional Rules in NoSql, 
probably like Mongo DB as promotional rules have different nature and structure.


3) Another assumption is that we don't need to re-calculate total price each 
time on-flight when customer adds or removes product from order, we only
need to calculate total price and apply all promotions when method
`Checkout.total();` is being called. 

4) One more assumption is that we don't need reduced product price 
to be displayed in case "buy 2 or more travel card holders then the price 
drops to £8.50" is applied. As far as in test task api only returns calculated 
total price I won't implement any Order decorator which will have new price.
So that when any promotion is applied i wil only change total price.

5) I assume it's requirement to return total price as Double when `Checkout.total()`
is being called. I use BigDecimal for more accurate calculations, but convert final
result to Double as that was in test task requirements.
Same for all API. I haven't changed any method names, proposed in test, assumed 
this is requirement to stick with that API.

6) There is an assumption that it's impossible, for example 2 threads to add items to 
the order as in real world there would be one user session in browser for the customer,
order (order builder in my case) will be created for each new user session
and customer is the only one who can add items to the order. Concurrently we 
only would need to modify available products, but in this task I assumed we have 
unlimited products number.


Tests:
------

1. I have covered functionality with Unit tests using mock +
added one test, called `CheckoutEndToEndTest` which tests
api with test cases proposed in test description.

2. In `Main` class I also initialized classes, so we can test
new test cases there 







