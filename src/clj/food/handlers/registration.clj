(ns food.handlers.registration
  (:require [clojure.java.io :as io]
            [cemerick.friend.credentials :as creds]
            [ring.util.response :as response]
            [food.app-context :refer [user-repository]]
            [food.repository :as repo]
            [food.views.register :as view]))

(defn !equal [validity name a b]
  (if (not= a b)
    (assoc validity name (conj (vec (name validity)) :equal))
    validity))

(defn !empty [validity name val]
  (if (empty? val)
    (assoc validity name (conj (vec (name validity)) :empty))
    validity))

(defn !length [validity name val shortest longest]
  (let [l (count val)]
    (if (or (< l shortest) (> l longest))
      (assoc validity name (conj (vec (name validity)) :length))
      validity)))

(defn validate-user-registration
  [{:keys [username confirm-username password confirm-password]}]
  (-> {}
      (!empty :username username)
      (!empty :confirm-username confirm-username)
      (!equal :confirm-username username confirm-username)
      (!empty :password password)
      (!length :password password 4 20)
      (!empty :confirm-password confirm-password)
      (!equal :confirm-password password confirm-password)))

(def messages {:username         {:empty "Enter a username"}
               :confirm-username {:empty "Enter username confimation"
                                  :equal "Username confirmation doesn't match"}
               :password         {:empty "Enter a password"
                                  :length "Password must have 4 to 20 characters"}
               :confirm-password {:empty "Enter password confirmation"
                                  :equal "Password confirmation doesn't match"}})

(defn field-messages [field errors]
  (for [msg-key (field errors)]
    (-> messages field msg-key)))

(defn add-messages [errors]
  (if (seq errors)
    (into {:page ["Check your inputs and try again!"]}
          (for [field (keys errors)
                :let [field-error {field (field-messages field errors)}]]
            field-error))
    errors))

(defn register-user
  [{:keys [username password] :as reg-form}]
  (let [errors (validate-user-registration reg-form)]
    (if (seq errors)
      (view/register (assoc reg-form :errors (add-messages errors)))
      (do
        (repo/save-user (user-repository) {:username username
                                           :password (creds/hash-bcrypt password)})
        (response/redirect-after-post (str "/login?username=" username))))))
