(ns food.handlers.registration
  (:require [clojure.java.io :as io]
            [cemerick.friend.credentials :as creds]
            [net.cgrand.enlive-html :as enlive :refer [deftemplate defsnippet]]
            [ring.util.response :as response]
            [food.app-context :refer [user-repository]]
            [food.dev :refer [is-dev? inject-devmode-html]]
            [food.repository :as repo]))

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

(defn validate-equal [validity name a b]
  (if (not= a b)
    (let [errors (vec (name validity))]
      (assoc validity name (conj errors :equal)))
    validity))

(defn validate-user-registration
  [{:keys [username confirm-username password confirm-password]}]
  (let [e (validate-equal {} :confirm-username username confirm-username)]
    (validate-equal e :confirm-password password confirm-password)))

(def messages {:username {}
               :confirm-username {:equal "Username confirmation doesn't match."}
               :password {}
               :confirm-password {:equal "Password confirmation doesn't match."}})

(defn field-messages [field errors]
  (println "field-messages, field=" field ", errors=" errors)
  (for [msg-key (field errors)]
    (-> messages field msg-key)))

(defn add-messages [errors]
  (println "add-messages, errors=")
  (if (seq errors)
    (into {:page ["Check your inputs and try again!"]}
          (for [field (keys errors)
                :let [field-error {field (field-messages field errors)}]]
            field-error))
    errors))

(defn register-user
  [{:keys [username confirm-username password] :as reg-form}]
  (let [errors (validate-user-registration reg-form)]
    (if (seq errors)
      (register {:username username
                 :confirm-username confirm-username
                 :errors (add-messages errors)})
      (do

        (repo/save-user (user-repository) {:username username
                                           :password (creds/hash-bcrypt password)})
        (response/redirect-after-post (str "/login?username=" username))))))
