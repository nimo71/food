(ns food.repository.database
  (:use [korma.db]
        [korma.core])
  (:require [food.repository :as repo]))

(defdb food-db (postgres {:db "food"
                          :username "food"
                          :password "foody"}))

(declare entries)

(defentity entries
  (pk :id)
  (table :entries)
  (database food-db)
  (entity-fields :when))

(defn entry-repository []
  (reify
    repo/EntryRepository
    (save-entry [this entry]
      (let [ts (:timestamp entry)]
        (insert entries (values {:when ts}))))
    (retrieve-entries [this]
      (select entries))))

(declare users)

(defentity users
  (pk :id)
  (table :users)
  (database food-db)
  (entity-fields :username :password))

(defn user-repository []
  (reify
    repo/UserRepository
    (save-user [this user]
      (insert users (values user)))
    (retrieve-user [this username]
      (first (select users (where {:username username}))))))
