(ns web.hello.core
  (:require [ring.adapter.jetty :refer [run-jetty]]))

(defn hello [req]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello World"})

(defn wrap-server [handler]
  (fn [request]
    (assoc-in (handler request) [:headers "Server"] "My Web Server(1.0)")))

(defn run-nrepl []
  (try
    (when-let [nrepl-main (requiring-resolve 'nrepl.cmdline/-main)]
      (nrepl-main "--port" "8888" "--middleware" "[cider.nrepl/cider-middleware]"))
    (catch Exception e
      (prn :error (.getMessage e)))))

(defn -main []
  (run-jetty (-> hello wrap-server) {:port 3000 :join? false})
  (run-nrepl))
