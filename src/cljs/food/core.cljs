(ns food.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :as async :refer [chan put! <! pub sub]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [food.entry-form :refer [entry-form]]
            [food.entry-log :refer [entry-log get-entries]]))

(defonce app-state (atom {:text "Health Log"
                          :new-entry {}
                          :entries []}))

(defn main []
  (let [req-chan   (chan)
        pub-chan   (chan)
        notif-chan (pub pub-chan :topic)]

    ;; server loop
    ;;(go
    ;;  (while true
    ;;    (serve (<! req-chan))))


    ;; *******************************************************
    ;; TODO:
    ;; - added timestamp on to :pub-chan
    ;;   - need go loop to read :new-entry topics and add to a log
    ;; - create git repo
    ;; *******************************************************

    (om/root
      (fn [app owner]
        (reify

;          om/IDidMount
;          (did-mount [_]
;            (get-entries (:pub-chan (om/get-shared owner))))

          om/IRender
          (render [_]
            (dom/div nil
              (dom/h1 nil "Health Log")
              (om/build entry-form (:new-entry app))
              (om/build entry-log (:entries app))))))
      app-state
      {:shared {:req-chan   req-chan
                :notif-chan notif-chan
                :pub-chan   pub-chan}
       :target (. js/document (getElementById "app"))})))
