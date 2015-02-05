(ns food.entry-log
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :as async :refer [chan <! sub put!]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [food.xhr :as xhr :refer [edn-xhr]]))

(defn get-entries [pub-chan]       ; move decoupled by channel
  (edn-xhr {                       ; what about timeout?
    :method       :get             ; error processing
    :url          "entries"
    :on-complete  (fn [res]
                    (put! pub-chan
                          {:topic :get-entries
                           :data  res}))}))

(defn put-entry [entry pub-chan]   ; move decoupled by channel
  (edn-xhr {                       ; what about timeout?
    :method      :put              ; error processing
    :url         "entry"
    :data        entry
    :on-complete (fn [res]
                   (put! pub-chan
                         {:topic :put-entry
                          :data  res}))}))

(defn entry-log [entries owner]
  (reify

    om/IDidMount
    (did-mount [_]
      (let [shared-state (om/get-shared owner)
            pub-chan     (:pub-chan shared-state)
            notif-chan   (:notif-chan shared-state)
            new-entries  (sub notif-chan :new-entry (chan))
            got-entries  (sub notif-chan :get-entries (chan))
            put-entries  (sub notif-chan :put-entry (chan))]

        (go
          (loop [e (<! new-entries)]
            (put-entry (:data e) pub-chan)
            (recur (<! new-entries))))

        (go
          (loop [es (<! got-entries)]
            (om/transact! entries #(:data es))
            (recur (<! got-entries))))

        (go
          (loop [es (<! put-entries)]
            (get-entries pub-chan)
            (recur (<! put-entries))))

        (get-entries pub-chan)))

    om/IRender
    (render [_]
      (println "rendering entries=" entries)
      (apply dom/ul nil
        (map #(dom/li nil (:when %)) entries)))))
