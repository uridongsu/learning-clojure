(ns hello.core)

(defn hello [{:keys [name]}]
  (prn :Hello name))

(defn -main
  [& [name]]
  (hello {:name name})
  ;; run nrepl
  (try
    (when-let [nrepl-main (requiring-resolve 'nrepl.cmdline/-main)]
      (nrepl-main "--port" "8888" "--middleware" "[cider.nrepl/cider-middleware]"))
    (catch Exception e
      (prn :error (.getMessage e)))))
