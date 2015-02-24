(ns food.handlers.login
  (:require [clojure.java.io :as io]
            [food.dev :refer [is-dev? inject-devmode-html]]
            [food.views.messages :as messages]
            [net.cgrand.enlive-html :as enlive :refer [deftemplate]]))

(deftemplate login-page (io/resource "login.html") [username error flash]
  [:body]
  (enlive/do->
   (if is-dev? inject-devmode-html identity)

   (if (seq flash)
    (enlive/prepend (messages/flash-message flash))
    identity)

   (if (seq error)
     (enlive/prepend (messages/error-messages error))
     identity))

  [:#username]
  (enlive/set-attr :value username))

(def failed-login-message "Email or password not recognised, please try again.")

(defn login [{{username :username,
               fail     :login_failed} :params,
               {{flash :value} "flash"} :cookies}]

  (let [errors (if (seq fail)
                 [failed-login-message]
                 nil)]
    (login-page username errors flash)))
