# TODO

- Registration page:
-- redirect to login page on successful registation with username field filled in and successful registration flash message 

-- restrict db tables 
--- unique username
--- password length 4..20 chars

-- Tidy up server.clj, need to extract page handlers and templates

- Login page: 
-- form validation
-- error messages, how does friend deal with errors.
--- no user retrieved

- Confirmation email after registration 
-- includes confirmation link 
-- use to verify provided email is valid, an error in sending because of invalid address should show validation error on registration form.

- HTML5 style sections and navigation, header, footer, etc...
- Client side validation on login in registration pages

- Error handling
-- Show error messge on the client side
-- Catch exception on DB access and add message to edn response
-- Ensure that all updated in the UI come from the app atom - properly decoupled UI

- use friend to check for https scheme in production only... i.e. (if is-dev? ...)
