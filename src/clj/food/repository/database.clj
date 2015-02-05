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
      (let [rs (select entries)]
        (println "retrieve-entries rs=" rs)
        rs))))
