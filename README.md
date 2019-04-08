# WatchList

A Technical Task Implementation of a WatchList service

## Summary
This is a Scala and Play! based microservice API, which provides a simple implementation of a
watchlist service that allows a set of content to be added. deleted or viewed for a customer.
The resultant set of content after these actions forms the 'watchlist' for the customer

##Development Assumptions

The APIs will access a customer's watchlist through a non-shareable customer authorisation token which has a simplistic
implementationn here, but in practice would be in session memeory and part of a comprehensive authentication and
authorisation process.

The Watchlist service component is an implementation of a ContentService trait  which allows for multiple watchlist
service components across different customer or content segments.

No healthcheck service has been provided, but in practice one wopuld be

Application configuration has been minimal and so a customised configuration component has not been provided, but in practice
this would not be the case.

Client messaging text has been produced in-situ rather than via a messages file

Error handling has been provided at source via package object shareable functionality. This could as well have been via 
HttpErrorHandler(onClientError, onServerError)

Tests have only been provided for the main controller and endpoints for validation of the basic functionality.
In practice all controllers, services and models would have tests to ensure very high code coverage.
  



## Acceptance Criteria
- Customer can add contentIDs to their Watchlist
- Customer can delete contentIDs from their Watchlist
- Customer can see contents they added in their Watchlist
- Customer cannot see another customer’s Watchlist
- Each customer is represented by a unique 3 digit alphanumeric string
- The Watchlist items should be stored in memory
- The API should produce and consume JSON

##Examples:
- Given a customer with id ‘123’ and an empty Watchlist
When the customer adds ContentIDs ‘zRE49’, ‘wYqiZ’, ‘15nW5’, ‘srT5k’, ‘FBSxr’ to their watchlist
Then their Watchlist is returned it should only include ContentIDs ‘zRE49’, ‘wYqiZ’, ‘15nW5’, ‘srT5k’,‘FBSxr’

- Given a customer with id ‘123’ and a Watchlist containing ContentIDs ‘zRE49’, ‘wYqiZ’, ‘15nW5’, ‘srT5k’,
‘FBSxr’ When they remove ContentID ‘15nW5’ from their Watchlist
Then their Watchlist should only contain ContentIDs ‘zRE49’, ‘wYqiZ’, ‘srT5k’, ‘FBSxr’

- Given two customers, one with id ‘123’ and one with id ‘abc’
And corresponding Watchlists containing ContentIDs ‘zRE49’, ‘wYqiZ’, ‘srT5k’, ‘FBSxr’ and ‘hWjNK’,
’U8jVg’, ‘GH4pD’, ’rGIha’ respectively
When customer with id ‘abc’ views their Watchlist they should only see ContentIDs ‘hWjNK’, ’U8jVg’,
‘GH4pD’, ’rGIha’

##Install and run
```
cmd > git clone https://github.com/edemorama/watchlist.git
cmd > cd watchlist
cmd > sbt run
```

The server runs on the default Play! port 9000 as follows:

###Auth
A customer must first be authorised with aa token to access this service

GET http://localhost:9000/watchlist/authorise?custId=123

returns: 
```
[token-for-customer]
```

###Add
Content can be added for a customer

POST http://localhost:9000/watchlist/add?authtoken=token-for-customer
```
content-type: application/json
content: [{"id":"zRE49"}, {"id":"wYqiZ"}, {"id":"15nW5"}, {"id":"srT5k"}, {"id":"FBSxr"}]
```
returns:
```
[{"id":"zRE49"}, {"id":"wYqiZ"}, {"id":"15nW5"}, {"id":"srT5k"}, {"id":"FBSxr"}]
```

###Delete
Content can be removed for a customer

POST http://localhost:9000/watchlist/delete?authtoken=token-for-customer
```
content-type: application/json
content: [{"id":"srT5k"}, {"id":"FBSxr"}]
```
returns:
```
[{"id":"zRE49"}, {"id":"wYqiZ"}, {"id":"15nW5"}]
```

##Get
Content can be viewed for a customer

GET http://localhost:9000/watchlist/get?authtoken=token-for-customer

returns: 
```
[{"id":"zRE49"}, {"id":"wYqiZ"}, {"id":"15nW5"}]
```

###UnAuth
remove customer authorisation from access to this service

GET http://localhost:9000/watchlist/unauthorise?custId=123

returns: 
```
[token-for-customer OR NOT_AUTHORISED]
```
