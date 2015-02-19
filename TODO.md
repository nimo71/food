# TODO

- Add registration page

- Authenticate user from db
-- retrieve-user just takes first result. What are error conditions, should we just allow an exception to be thrown, etc...
-- make username a unique column

- Error handling
-- Show error messge on the client side
-- Catch exception on DB access and add message to edn response

- use friend to check for https scheme in production only... i.e. (if is-dev? ...)
