(ns food.entry-form
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :as async :refer [put!]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(defn entry-form [data owner]
  (reify
    om/IRender
    (render [_]
      (dom/div nil
         (dom/label #js {:for "timestamp"} "Time")
         (dom/input #js {:id "timestamp" :ref "timestamp" :type "text"})
         (dom/a #js {:className "button"
                     :href "#"
                     :onClick #(put! (:pub-chan (om/get-shared owner))
                                     {:topic :new-entry
                                      :data {:timestamp (.-value (om/get-node owner "timestamp"))}})}
                "Add")))))
