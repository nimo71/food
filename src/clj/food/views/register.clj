(ns food.views.register
  (:require [clojure.java.io :as io]
            [net.cgrand.enlive-html :as enlive :refer [deftemplate defsnippet]]
            [food.dev :refer [is-dev? inject-devmode-html]]))

(defsnippet error-messages (io/resource "messages.html") [:#error-messages]
  [messages]

  [:li.error-message]
  (enlive/clone-for [msg messages]
    (enlive/content msg)))

(deftemplate register (io/resource "register.html")
  [{:keys [username confirm-username errors]}]

  [:body]
  (if is-dev? inject-devmode-html identity)

  [:body enlive/first-child]
  (if (and (seq errors) (seq (:page errors)))
    (enlive/prepend (error-messages (:page errors)))
    identity)

  [:#username]
  (enlive/do->
   (enlive/set-attr :value username)
   (if (and (seq errors) (seq (:username errors)))
     (enlive/after (error-messages (:username errors)))
     identity))

  [:#confirm-username]
  (enlive/do->
   (enlive/set-attr :value confirm-username)
   (if (and (seq errors) (seq (:confirm-username errors)))
     (enlive/after (error-messages (:confirm-username errors)))
     identity))

  [:#confirm-password]
  (if (and (seq errors) (seq (:confirm-password errors)))
    (enlive/after (error-messages (:confirm-password errors)))
    identity))
