(ns food.server
  (:require [clojure.java.io :as io]
            [food.dev :refer [is-dev? inject-devmode-html browser-repl start-figwheel]]
            [compojure.core :refer [GET PUT POST defroutes context]]
            [compojure.route :refer [resources]]
            [compojure.handler :refer [api site]]
            [net.cgrand.enlive-html :as enlive :refer [deftemplate defsnippet]]
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

(defsnippet error-messages (io/resource "messages.html") [:#error-messages]
  [messages]

  [:li.error-message]
  (enlive/clone-for [msg messages]
    (enlive/content msg)))

(deftemplate login (io/resource "login.html") []
  [:body]
  (if is-dev? inject-devmode-html identity))

(deftemplate register (io/resource "register.html")
  [{:keys [username confirm-username error]}]

  [:body]
  (if is-dev? inject-devmode-html identity)

  [:body enlive/first-child]
  (if error
    (enlive/prepend (error-messages [[error]]))
    identity)

  [:#username]
  (enlive/set-attr :value username)

  [:#confirm-username]
  (enlive/set-attr :value confirm-username))

(defn valid-user-registration?
  [{:keys [username confirm-username password confirm-password] :as reg-form}]
  false)

(defn register-user
  [{:keys [username confirm-username password confirm-password] :as reg-form}]
  (if (valid-user-registration? reg-form)
    (repo/save-user (user-repository) {:username username
                                       :password (creds/hash-bcrypt password)})
    ;; redirect to login page here with username filled in
    (register {:username username
               :confirm-username confirm-username
               :error "Check your inputs and try again!"})))

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
    {:status   200
     :headers  {"Content-Type" "application/edn"}
     :body     (pr-str entries)}))

(defroutes routes
  (GET "/login" req (login))
  (GET "/logout" req (friend/logout* (resp/redirect (str (:context req) "/"))))
  (GET "/register" req (register {}))
  (POST "/register" {registration-form :params} (register-user registration-form))
  (GET "/entries" [] (friend/authenticated (get-entries)))
  (PUT "/entry" [timestamp] (friend/authenticated (put-entry {:timestamp timestamp})))
  (GET "/" req (friend/authenticated (page)))

  (resources "/")
  (resources "/react" {:root "react"}))

(def users (partial repo/retrieve-user (user-repository)))

(def app
  (-> routes
      (friend/authenticate {:allow-anon? true
                            :login-uri "/login"
                            :default-landing-uri "/"
                            :credential-fn #(creds/bcrypt-credential-fn users %)
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
