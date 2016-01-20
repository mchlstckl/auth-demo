# Authorization Demo

The aim of this repository is to:
 
- gain understanding of the OAuth2 space by implementing something
- demo 2-legged OAuth2 with OpenID Connect extensions. 

This is a work-in-progress of a 2-legged OAuth2 implementation currently using

- spring boot
- spring security
- spring security oauth2
- hand-crafted JWT RP and AS communication

Everything here can change without notice.

~~Currently using client secret for `grant_type=client_credentials` as we 
investigate how this can be done using JWT.~~

Now RP-nimbus uses JWT to request access token. AS is setup to look at the 
JWT and verify its authenticity. We're using asymmetric keys that were generated
beforehand.

## Authorization Service (AS)

Issues access tokens and introspects tokens. Currently, introspection is done
via the spring security defined endpoint `/check_token` which predates the 
introspection specification.

    localhost:8180/oauth/authorize
    localhost:8180/oauth/token
    localhost:8180/oauth/check_token

## Resource Service (RS)

Exposes Spring related quotes. Requires access token issued by AS.

    localhost:8280/quotes/:id
    localhost:8280/quotes/random
    
## Relying Party (RP)
 
An "external" services that collects random quotes from RS. It then exposes a 
query endpoint that allows the request to search for quotes containing a search
term.

In order to call RS endpoints, the service needs to retrieve an access token 
from AS and call RS using said token.

    localhost:8380/quotes/:term
    
## Run it

To run all dem tings, open a terminal an run the script `bootAll.sh` located in
the `/scripts` folder.

You should see a bunch of log statements flooding your terminal. This should 
slow down after a few seconds, say 20 seconds. Now you should be able to make
requests to `localhost:8380/quotes/:term` with a search term, e.g. 
`localhost:8380/quotes/boot`.

Hit `ENTER` to kill all the process.
    

 
 

