(ns web.hello.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.util.response :as res]
            [clojure.pprint :refer [pprint]]
            [hiccup2.core :as h]))

(defn hello [{:keys [session]}]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (str (when-some [greeting (:greeting session)]
                (str "<p>Greeting: " greeting "</p>"))
              "<form action=\"/say\" method=\"post\">"
              "<input name=\"greeting\"/>"
              "<input type=\"submit\" value=\"Say\"/>"
              "</form>")})

(defn hello2 [{:keys [session]}]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (str (h/html [:div
                       (when-some [greeting (:greeting session)]
                         [:p "Greeting: " greeting])
                       [:form {:action "/say" :method "post"}
                        [:input {:name "greeting" :placeholder "Say something..."}]
                        [:input {:type "submit" :value "Say"}]]]))})

(defn info [req]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (with-out-str (pprint req))})

(defn say-hello-handler [{:keys [request-method params session]}]
  (when-not (= request-method :post)
    (throw (Exception. "Not allowed method")))
  (when-not (= request-method :post)
    (throw (Exception. "greeting parameter is needed.")))
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "OK"
   :session (assoc session :greeting (get params "greeting"))})

(defn router [{:keys [uri] :as req}]
  (case uri
    "/" (hello2 req)
    "/info" (info req)
    "/say" (say-hello-handler req)))

(defn wrap-server [handler]
  (fn [request]
    (assoc-in (handler request) [:headers "Server"] "My Web Server(1.0)")))

(defonce server (atom nil))

(def app
  (-> router
      wrap-params
      wrap-session
      wrap-server))

(defn- start-server []
  (reset! server (run-jetty (fn [ctx] (app ctx)) {:port 3000 :join? false})))

(defn- restart-server []
  (when @server
    (.stop @server)
    (start-server)))

(defn run-nrepl []
  (try
    (when-let [nrepl-main (requiring-resolve 'nrepl.cmdline/-main)]
      (nrepl-main "--port" "8888" "--middleware" "[cider.nrepl/cider-middleware]"))
    (catch Exception e
      (prn :error (.getMessage e)))))

(defn -main []
  (start-server)
  (run-nrepl))
