# TODO

- Forgotten password links
- Confirmation email after registration 
-- use to verify provided email is valid, an error in sending because of invalid address should show validation error on registration form.
-- includes confirmation link 

- HTML5 style sections and navigation, header, footer, etc...
- Flash message should be added to the top of the main section, to be displayed under the navigation menu and above any other content.

- Add form to record weight
-- timestamp
-- weight in kg, lb or st and lb
--- store weight in kg

- Display weight entries in log

- User profile page
-- change email and password 
-- preferences eg weight units

- Error handling
-- Show error messge on the client side
-- Catch exception on DB access and add message to edn response
-- Ensure that all updated in the UI come from the app atom - properly decoupled UI
-- Client side error checking in registration and login form

- use friend to check for https scheme in production only... i.e. (if is-dev? ...)

## Group weight
- Orgainser create team 
-- Add people 
- Email invitation
- Email to request weight, link to add weight
- Email to notify all weights in and result
-- link to results page 
--- graph
--- winner
--- comments
