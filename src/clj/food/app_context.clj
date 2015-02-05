(ns food.app-context
  (:require [food.repository.database :as db]))

(defn entry-repository []
  (db/entry-repository))
