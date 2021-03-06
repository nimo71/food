(ns food.views.register
  (:require [clojure.java.io :as io]
            [net.cgrand.enlive-html :as enlive :refer [deftemplate defsnippet]]
            [food.dev :refer [is-dev? inject-devmode-html]]
            [food.views.messages :as messages]))

(deftemplate register (io/resource "register.html")
  [{:keys [username confirm-username password confirm-password errors]}]

  [:body]
  (enlive/do->
   (if is-dev? inject-devmode-html identity)
   (if (and (seq errors) (seq (:page errors)))
     (enlive/prepend (messages/error-messages (:page errors)))
     identity))

  [:#username]
  (enlive/do->
   (enlive/set-attr :value username)
   (if (and (seq errors) (seq (:username errors)))
     (enlive/after (messages/error-messages (:username errors)))
     identity))

  [:#confirm-username]
  (enlive/do->
   (enlive/set-attr :value confirm-username)
   (if (and (seq errors) (seq (:confirm-username errors)))
     (enlive/after (messages/error-messages (:confirm-username errors)))
     identity))

  [:#password]
  (enlive/do->
   (enlive/set-attr :value password)
   (if (and (seq errors) (seq (:password errors)))
     (enlive/after (messages/error-messages (:password errors)))
     identity))

  [:#confirm-password]
  (enlive/do->
   (enlive/set-attr :value confirm-password)
   (if (and (seq errors) (seq (:confirm-password errors)))
     (enlive/after (messages/error-messages (:confirm-password errors)))
     identity)))
