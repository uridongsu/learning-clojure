(ns web.hello.core
  (:require [ring.adapter.jetty :refer [run-jetty]]))

(defn hello [req]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello"})

(defn wrap-server [handler]
  (fn [request]
    (assoc-in (handler request) [:headers "Server"] "My Web Server(1.0)")))

(defonce server (atom nil))

(defn- start-server []
  (reset! server (run-jetty (-> hello wrap-server) {:port 3000 :join? false})))

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
