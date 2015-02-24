(ns food.views.messages
  (:require [clojure.java.io :as io]
            [net.cgrand.enlive-html :as enlive :refer [defsnippet]]))

(defsnippet error-messages (io/resource "messages.html") [:#error-messages]
  [messages]

  [:li.error-message]
  (enlive/clone-for [msg messages]
    (enlive/content msg)))

(defsnippet flash-message (io/resource "messages.html") [:#flash-message]
  [message]

  [:#flash-message]
  (enlive/content (first message)))
