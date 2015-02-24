(ns lobos.migrations
  (:refer-clojure :exclude [alter drop
                            bigint boolean char double float time])
  (:use (lobos [migration :only [defmigration]] core schema
               config helpers)))

(defmigration add-entries-table
  (up [] (create
          (tbl :entries
            (varchar :when 50))))
  (down [] (drop (table :entries))))

(defmigration add-users-table
  (up [] (create
         (tbl :users
               (varchar :username 256 :unique)
               (varchar :password 60)
               (check :password (> (length :password) 3)))))
  (down [] (drop (table :users))))
