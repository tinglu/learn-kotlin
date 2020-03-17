# Testing Strategies in a Microservice Architecture 

https://martinfowler.com/articles/microservice-testing/

## `Unit testing`:

- solitary
- sociable
       
Both styles of unit testing play an important role inside a microservice：

If a piece of coordination logic requires too many doubles, it is usually a good indicator that some concept should be extracted and tested in isolation.

As the size of a service decreases the ratio of plumbing and coordination logic to complex domain logic increases.

It is important to constantly question the value a unit test provides versus the cost it has in maintenance or the amount it constrains your implementation. By doing this, it is possible to keep the test suite small, focussed and high value. Similarly, some services will contain entirely plumbing and coordination logic such as adapters to different technologies or aggregators over other services. In such cases comprehensive unit testing may not pay off. Other levels of testing such as component testing can provide more value.


## `Integration Testing`

An integration test verifies the communication paths and interactions between components to detect interface defects.

Whilst tests that integrate components or modules can be written at any granularity, in microservice architectures they are typically used to verify interactions between
layers of integration code and the external components to which they are integrating.

Examples of the kinds of external components against which such integration tests can be useful include other microservices, data stores and caches.
