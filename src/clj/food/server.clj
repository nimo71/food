(ns food.server
  (:require [clojure.java.io :as io]
            [food.dev :refer [is-dev? inject-devmode-html browser-repl start-figwheel]]
            [compojure.core :refer [GET PUT defroutes]]
            [compojure.route :refer [resources]]
            [compojure.handler :refer [api]]
            [net.cgrand.enlive-html :refer [deftemplate]]
            [ring.middleware.reload :as reload]
            [ring.middleware.edn :refer [wrap-edn-params]]
            [environ.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]]
            [food.app-context :refer [entry-repository]]
            [food.repository :refer [save-entry retrieve-entries]]))

(deftemplate page
  (io/resource "index.html") [] [:body] (if is-dev? inject-devmode-html identity))

(defn put-entry [entry]
  (let [saved (save-entry (entry-repository) entry)]
    {:status   200
     :headers  {"Content-Type" "application/edn"}
     :body     (pr-str {:id 1
                        :timestamp (:timestamp entry)})}))

(defn get-entries []
  (let [entries (retrieve-entries (entry-repository))]
    (println "get-entries entries=" entries)
    {:status   200
     :headers  {"Content-Type" "application/edn"}
     :body     (pr-str entries)}))

(defroutes routes
  (resources "/")
  (resources "/react" {:root "react"})
  (GET "/entries" [] (get-entries))
  (PUT "/entry" [timestamp] (put-entry {:timestamp timestamp}))
  (GET "/*" req (page)))

(def app
  (-> routes
      wrap-edn-params))

(def http-handler
  (if is-dev?
    (reload/wrap-reload (api #'app))
    (api app)))

(defn run [& [port]]
  (defonce ^:private server
    (do
      (if is-dev? (start-figwheel))
      (let [port (Integer. (or port (env :port) 10555))]
        (print "Starting web server on port" port ".\n")
        (run-jetty http-handler {:port port
                                 :join? false}))))
  server)

(defn -main [& [port]]
  (run port))
