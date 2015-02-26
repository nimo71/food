(ns food.handlers.login
  (:require [clojure.java.io :as io]
            [food.dev :refer [is-dev? inject-devmode-html]]
            [food.views.messages :as messages]
            [net.cgrand.enlive-html :as enlive :refer [deftemplate]]))

(deftemplate login-page (io/resource "login.html")
  [username prepend-errors prepend-flash]

  [:body]
  (enlive/do->
   (if is-dev? inject-devmode-html identity)
   (prepend-flash)
   (prepend-errors))

  [:#username]
  (enlive/set-attr :value username))

(def failed-login-message "Email or password not recognised, please try again.")

(defn build-errors [fail]
  #(if (seq fail)
    (enlive/prepend (messages/error-messages [failed-login-message]))
    identity))

(defn build-flash [flash]
  #(if (seq flash)
     (enlive/prepend (messages/flash-message [flash]))
    identity))

(defn login [{{username :username,
               fail     :login_failed} :params,
               {{flash :value} "flash"} :cookies}]

  (login-page username
              (build-errors fail)
              (build-flash flash)))
