# TODO

- Registration page:
-- form validation
--- check whether username is already registered
-- redirect to login page on successful registation with username field filled in and successful registration flash message 

-- Tidy up server.clj, need to extract page handlers and templates

- Login page: 
-- form validation
-- error messages, how does friend deal with errors.

- Add register link on login page
- Add login link on registration page
- Add logout link on application page

- Client side validation on login in registration pages

- Authenticate user from db
-- retrieve-user just takes first result. What are error conditions, should we just allow an exception to be thrown, etc...
-- make username a unique column


- Error handling
-- Show error messge on the client side
-- Catch exception on DB access and add message to edn response
-- Ensure that all updated in the UI come from the app atom - properly decoupled UI

- use friend to check for https scheme in production only... i.e. (if is-dev? ...)
