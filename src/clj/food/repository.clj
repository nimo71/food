(ns food.repository)

(defprotocol EntryRepository
  (save-entry [this entry])
  (retrieve-entries [this]))
