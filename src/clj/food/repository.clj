(ns food.repository)

(defprotocol EntryRepository
  (save-entry [this entry])
  (retrieve-entries [this]))

(defprotocol UserRepository
  (save-user [this user])
  (retrieve-user [this username password]))
