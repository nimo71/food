(ns food.handlers.registration
  (:require [clojure.java.io :as io]
            [cemerick.friend.credentials :as creds]
            [ring.util.response :as response]
            [food.app-context :refer [user-repository]]
            [food.repository :as repo]
            [food.views.register :as view]))

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
      (view/register {:username username
                 :confirm-username confirm-username
                 :errors (add-messages errors)})
      (do

        (repo/save-user (user-repository) {:username username
                                           :password (creds/hash-bcrypt password)})
        (response/redirect-after-post (str "/login?username=" username))))))
