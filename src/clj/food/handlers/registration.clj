(ns food.handlers.registration
  (:require [clojure.java.io :as io]
            [cemerick.friend.credentials :as creds]
            [ring.util.response :as response]
            [food.app-context :refer [user-repository]]
            [food.repository :as repo]
            [food.views.register :as view]))

(defn !valid [validity field property pred]
  (if pred
    (assoc validity field (conj (vec (field validity)) property))
    validity))

(defn !equal [validity field a b]
  (!valid validity field :equal (not= a b)))

(defn !empty [validity field val]
  (!valid validity field :empty (empty? val)))

(defn !length [validity field val shortest longest]
  (let [l (count val)]
    (!valid validity field :length (or (< l shortest) (> l longest)))))

(defn !email [validity field val]
  (!valid validity field :email (empty? (re-matches #".*@.*\..*" val))))

(defn validate-user-registration
  [{:keys [username confirm-username password confirm-password]}]
  (-> {}
      (!email :username username)
      (!equal :confirm-username username confirm-username)
      (!length :password password 4 20)
      (!equal :confirm-password password confirm-password)))

(def messages {:username         {:email "Enter a valid email address"}
               :confirm-username {:equal "Email confirmation does not match"}
               :password         {:length "Enter a password with 4 to 20 characters"}
               :confirm-password {:equal "Password confirmation does not match"}})

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
