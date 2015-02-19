(ns food.server
  (:require [clojure.java.io :as io]
            [food.dev :refer [is-dev? inject-devmode-html browser-repl start-figwheel]]
            [compojure.core :refer [GET PUT defroutes context]]
            [compojure.route :refer [resources]]
            [compojure.handler :refer [api site]]
            [net.cgrand.enlive-html :refer [deftemplate]]
            [ring.middleware.reload :as reload]
            [ring.middleware.edn :refer [wrap-edn-params]]
            [environ.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.util.response :as resp]
            [cemerick.friend :as friend]
            [cemerick.friend.workflows :as workflows]
            [cemerick.friend.credentials :as creds]
            [food.app-context :refer [entry-repository user-repository]]
            [food.repository :as repo]))

(deftemplate login
  (io/resource "login.html") [] [:body] (if is-dev? inject-devmode-html identity))

(deftemplate page
  (io/resource "index.html") [] [:body] (if is-dev? inject-devmode-html identity))

(defn put-entry [entry]
  (let [saved (repo/save-entry (entry-repository) entry)]
    {:status   200
     :headers  {"Content-Type" "application/edn"}
     :body     (pr-str {:id 1
                        :timestamp (:timestamp entry)})}))

(defn get-entries []
  (let [entries (repo/retrieve-entries (entry-repository))]
    (println "get-entries entries=" entries)
    {:status   200
     :headers  {"Content-Type" "application/edn"}
     :body     (pr-str entries)}))

(defroutes routes
  (GET "/login" req (login))
  (GET "/logout" req (friend/logout* (resp/redirect (str (:context req) "/"))))
  (GET "/entries" [] (friend/authenticated (get-entries)))
  (PUT "/entry" [timestamp] (friend/authenticated (put-entry {:timestamp timestamp})))
  (GET "/" req (friend/authenticated (page)))

  (resources "/")
  (resources "/react" {:root "react"}))

(def credential-fn
  (partial creds/bcrypt-credential-fn
               (fn [id]
                 (when-let [{:keys [username password]}
                            (repo/retrieve-user (user-repository) id)]
                   (println "credential-fn: username=" username ", password=" password)
                   {:username username :password password}))))

(def app
  (-> routes
      (friend/authenticate {:allow-anon? true
                            :login-uri "/login"
                            :default-landing-uri "/"
                            :credential-fn #(creds/bcrypt-credential-fn (partial repo/retrieve-user (user-repository)) %)
                            :workflows [(workflows/interactive-form)]})
      wrap-edn-params))

(def http-handler
  (if is-dev?
    (reload/wrap-reload (site #'app))
    (site app)))

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
